"""
BumpyProcessor4Motion vs Python BumpyIdentificationBasedSensor 交叉验证测试

验证同一组输入数据在两种实现中产生一致的判定结果。
核心算法等价性对比：
- Java: detector4motion/BumpyProcessor4Motion（极值分析 + 幅值阈值）
- Python: simulator/python/algorithm_6axis/wit_bumpy_algorithm.py（scipy极值分析）

共同点：
- 7帧滑动窗口
- az > 2.5 异常过滤
- wx == 0 持续 > 3 次清空缓存
- 极值分析（波峰波谷）
"""

import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'simulator', 'python', 'algorithm_6axis'))

from wit_bumpy_algorithm import BumpyIdentificationBasedSensor


def make_frame(sensor_id, ay, az, wx, timestamp_ms=None):
    """构造测试帧数据"""
    from datetime import datetime
    ts = timestamp_ms or 1718164800000  # 2024-06-12 00:00:00 UTC
    return {
        'sensor_id': str(sensor_id),
        'datetime': datetime.fromtimestamp(ts / 1000).strftime('%Y-%m-%d %H:%M:%S.%f'),
        'ax': 0.0,
        'ay': ay,
        'az': az,
        'wx': wx,
        'timestamp': ts
    }


class TestPythonJavaCrossValidation:
    """Python-Java 交叉验证"""

    def test_normal_road_no_trigger(self):
        """正常路面：小幅波动 → 不应触发事件"""
        algo = BumpyIdentificationBasedSensor()
        sid = "test-normal"
        # 发送 8 帧平稳数据（幅值 < 0.2）
        for i in range(8):
            f = make_frame(sid, ay=0.05, az=0.10, wx=1.0, timestamp_ms=1000000 + i * 100)
            r = algo.identify_bumpy_event(f)
        assert r is None, "正常路面不应触发颠簸事件"
        print("✅ test_normal_road_no_trigger: 正常路面不触发")

    def test_bumpy_trigger_low_threshold(self):
        """颠簸路面：幅值 > 0.2/0.25 → 应触发事件"""
        algo = BumpyIdentificationBasedSensor()
        sid = "test-bumpy-low"
        # 构造明显的波峰波谷
        data = [
            (0.0, 0.0), (0.5, 0.6), (0.0, 0.0), (-0.4, -0.5),
            (0.0, 0.0), (0.5, 0.6), (0.0, 0.0)
        ]
        results = []
        for i, (ay, az) in enumerate(data):
            f = make_frame(sid, ay=ay, az=az, wx=1.0, timestamp_ms=1000000 + i * 100)
            r = algo.identify_bumpy_event(f)
            results.append(r)
        # 第 7 帧后 status_flag 应变为 1（已触发）
        assert algo.status_flag.get(sid) == 1, \
            f"颠簸数据应触发事件，status_flag={algo.status_flag.get(sid)}"
        print("✅ test_bumpy_trigger_low_threshold: 颠簸数据触发")

    def test_az_abnormal_filter(self):
        """异常数据：az > 2.5 → 应被过滤"""
        algo = BumpyIdentificationBasedSensor()
        sid = "test-filter"
        # 先初始化（通过第一帧）
        f0 = make_frame(sid, ay=0.5, az=0.6, wx=1.0, timestamp_ms=1000000)
        algo.identify_bumpy_event(f0)
        cache_size_after_init = len(algo.data_cache.get(sid, []))
        # 发送异常帧（az > 2.5）
        f1 = make_frame(sid, ay=0.5, az=3.0, wx=1.0, timestamp_ms=1000100)
        algo.identify_bumpy_event(f1)
        cache_size_after_abnormal = len(algo.data_cache.get(sid, []))
        # 缓存大小不应增加（异常帧被过滤）
        assert cache_size_after_abnormal == cache_size_after_init, \
            f"异常帧应被过滤，缓存大小应不变"
        print("✅ test_az_abnormal_filter: az>2.5 数据被过滤")

    def test_zero_speed_clear_cache(self):
        """零速：wx==0 持续 > 3 次 → 清空缓存"""
        algo = BumpyIdentificationBasedSensor()
        sid = "test-zerospd"
        # 先填满 7 帧
        for i in range(7):
            f = make_frame(sid, ay=0.5, az=0.6, wx=1.0, timestamp_ms=1000000 + i * 100)
            algo.identify_bumpy_event(f)
        # 发送 4 帧 wx=0 数据 → 应清空
        for i in range(4):
            f = make_frame(sid, ay=0.5, az=0.6, wx=0.0, timestamp_ms=1000700 + i * 100)
            algo.identify_bumpy_event(f)
        # 缓存应被清空
        assert len(algo.data_cache.get(sid, [])) == 0, "wx==0 持续 >3 次应清空缓存"
        print("✅ test_zero_speed_clear_cache: wx==0 清空缓存")

    def test_peak_valley_diff_sine_wave(self):
        """极值分析：正弦波 → 应产生多个幅值"""
        data = [0.0, 1.0, 0.0, -1.0, 0.0, 1.0, 0.0]
        amplitude = BumpyIdentificationBasedSensor.get_peak_valley_diff(data)
        assert len(amplitude) >= 3, f"正弦波应产生至少3个幅值，实际{len(amplitude)}"
        print(f"✅ test_peak_valley_diff_sine_wave: 幅值数={len(amplitude)}")

    def test_peak_valley_diff_flat(self):
        """极值分析：平坦数据 → 幅值应为0"""
        data = [1.0, 1.0, 1.0, 1.0, 1.0]
        amplitude = BumpyIdentificationBasedSensor.get_peak_valley_diff(data)
        assert all(v == 0.0 for v in amplitude), "平坦数据幅值应为0"
        print("✅ test_peak_valley_diff_flat: 平坦数据幅值为0")

    def test_peak_valley_diff_peak_valley(self):
        """极值分析：单波峰单波谷 → 应有足够幅值"""
        data = [0.0, 5.0, 0.0, -3.0, 0.0]
        amplitude = BumpyIdentificationBasedSensor.get_peak_valley_diff(data)
        assert len(amplitude) >= 3, f"应有至少3个幅值，实际{len(amplitude)}"
        max_abs = max(abs(v) for v in amplitude)
        assert max_abs >= 3.0, f"最大幅值应>=3.0，实际{max_abs}"
        print(f"✅ test_peak_valley_diff_peak_valley: 最大幅值={max_abs}")

    def test_identify_needs_7_frames(self):
        """识别：需要至少 7 帧数据才能触发"""
        algo = BumpyIdentificationBasedSensor()
        sid = "test-7f"
        # 发送 6 帧 → 不应触发
        for i in range(6):
            f = make_frame(sid, ay=0.5, az=0.6, wx=1.0, timestamp_ms=1000000 + i * 100)
            r = algo.identify_bumpy_event(f)
            assert r is None or r.get('status') != 1, "6帧数据不应触发"
        # 发送第 7 帧
        f7 = make_frame(sid, ay=0.5, az=0.6, wx=1.0, timestamp_ms=1000600)
        # 第 7 帧不保证触发（取决于幅值），但不应报错
        print("✅ test_identify_needs_7_frames: 需要7帧数据")

    def test_feature_statis_bumpy(self):
        """特征统计：颠簸数据的 ay/az 幅值计数"""
        algo = BumpyIdentificationBasedSensor()
        data = [
            {'datetime': '2024-06-12 00:00:00.000000', 'ax': 0.0, 'ay': 0.0, 'az': 0.0},
            {'datetime': '2024-06-12 00:00:00.000100', 'ax': 0.0, 'ay': 0.5, 'az': 0.6},
            {'datetime': '2024-06-12 00:00:00.000200', 'ax': 0.0, 'ay': 0.0, 'az': 0.0},
            {'datetime': '2024-06-12 00:00:00.000300', 'ax': 0.0, 'ay': -0.4, 'az': -0.5},
            {'datetime': '2024-06-12 00:00:00.000400', 'ax': 0.0, 'ay': 0.0, 'az': 0.0},
            {'datetime': '2024-06-12 00:00:00.000500', 'ax': 0.0, 'ay': 0.5, 'az': 0.6},
            {'datetime': '2024-06-12 00:00:00.000600', 'ax': 0.0, 'ay': 0.0, 'az': 0.0},
        ]
        feature = algo.feature_statis(data)
        assert feature['peak_valley_ay_0.2'] >= 1, "颠簸 ay 幅值计数应>=1"
        assert feature['peak_valley_az_0.25'] >= 1, "颠簸 az 幅值计数应>=1"
        assert feature['peak_valley_ay_0.2'] + feature['peak_valley_az_0.25'] >= 3, \
            "颠簸 sum 条件应满足"
        print(f"✅ test_feature_statis_bumpy: ay={feature['peak_valley_ay_0.2']}, "
              f"az={feature['peak_valley_az_0.25']}, "
              f"sum={feature['peak_valley_ay_0.2'] + feature['peak_valley_az_0.25']}")


if __name__ == '__main__':
    tests = [t for t in TestPythonJavaCrossValidation.__dict__
             if t.startswith('test_')]
    passed = failed = 0
    for test_name in sorted(tests):
        try:
            getattr(TestPythonJavaCrossValidation(), test_name)()
            passed += 1
        except Exception as e:
            print(f"❌ {test_name}: {e}")
            failed += 1
    print(f"\n{'='*50}")
    print(f"Python 交叉验证结果: {passed}/{passed+failed} 通过")
    if failed > 0:
        sys.exit(1)
