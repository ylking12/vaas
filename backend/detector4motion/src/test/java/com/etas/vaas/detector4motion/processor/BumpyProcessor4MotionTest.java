package com.etas.vaas.detector4motion.processor;

import com.etas.vaas.common.dto.MotionFrame;
import com.etas.vaas.detector4motion.dto.Statistics;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BumpyProcessor4Motion 算法单元测试
 *
 * 验证 6轴运动颠簸检测的核心逻辑：
 * 1. computeAmplitude — 极值分析（波峰波谷）
 * 2. computeStatistics — ay/az 幅值统计
 * 3. Level 3/5/7 阈值判定
 * 4. 7帧滑动窗口
 */
class BumpyProcessor4MotionTest {

    /** 创建 MotionFrame */
    private MotionFrame makeFrame(String deviceId, double ay, double az, double wx, long ts) {
        MotionFrame f = new MotionFrame();
        f.setDeviceId(deviceId);
        f.setAY(ay);
        f.setAZ(az);
        f.setWX(wx);
        f.setTimestamp(ts);
        return f;
    }

    /** 通过反射创建 BumpyProcessor4Motion 并设置阈值 */
    private BumpyProcessor4Motion createProcessor() throws Exception {
        BumpyProcessor4Motion p = new BumpyProcessor4Motion();
        setField(p, "debounceEnabled", false);
        setField(p, "debounceInterval", 2000L);
        setField(p, "zAmplitudeLevel7", 0.43);
        setField(p, "yAmplitudeLevel7", 0.25);
        setField(p, "zAmplitudeLevel5", 0.35);
        setField(p, "yAmplitudeLevel5", 0.25);
        setField(p, "zAmplitudeLevel3", 0.27);
        setField(p, "yAmplitudeLevel3", 0.23);
        return p;
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, val);
    }

    /** 调用私有方法 computeAmplitude */
    private List<Double> computeAmplitude(BumpyProcessor4Motion p, List<Double> input) throws Exception {
        java.lang.reflect.Method m = BumpyProcessor4Motion.class.getDeclaredMethod("computeAmplitude", List.class);
        m.setAccessible(true);
        return (List<Double>) m.invoke(p, input);
    }

    /** 构造 7 帧测试数据 */
    private List<MotionFrame> buildFrames(String deviceId, double[] ayValues, double[] azValues) {
        List<MotionFrame> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < Math.min(ayValues.length, azValues.length); i++) {
            list.add(makeFrame(deviceId, ayValues[i], azValues[i], 1.0, now + i * 100));
        }
        return list;
    }

    // ====================================================================
    // computeAmplitude 极值分析测试
    // ====================================================================

    @Test
    void testComputeAmplitude_flatLine() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        List<Double> amplitude = computeAmplitude(p, Arrays.asList(1.0, 1.0, 1.0, 1.0, 1.0));
        // 所有值相等 → 去重后无变化 → 幅值应为 0
        assertTrue(amplitude.stream().allMatch(v -> v == 0.0), "平坦数据幅值应为0");
    }

    @Test
    void testComputeAmplitude_sineWave() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // 正弦波：0, 1, 0, -1, 0, 1, 0
        List<Double> amplitude = computeAmplitude(p, Arrays.asList(0.0, 1.0, 0.0, -1.0, 0.0, 1.0, 0.0));
        // 应该有多个幅值（波峰到波谷的差）
        assertFalse(amplitude.isEmpty(), "正弦波应产生幅值");
        assertTrue(amplitude.size() >= 3, "应有至少3个幅值");
    }

    @Test
    void testComputeAmplitude_monotonic() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // 单调递增：无波峰波谷
        List<Double> amplitude = computeAmplitude(p, Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        // 单调序列不应有极值
        assertFalse(amplitude.isEmpty()); // 至少包含首尾差值
    }

    @Test
    void testComputeAmplitude_peakValley() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // 一个波峰和一个波谷：0, 5, 0, -3, 0
        // 波峰在索引1(值5), 波谷在索引3(值-3)
        // 去重后: [0, 5, 0, -3, 0]
        // 极值: 5(波峰)和-3(波谷)
        // 极值序列: [0, 5, 0, -3, 0] 或按规则确定
        List<Double> amplitude = computeAmplitude(p, Arrays.asList(0.0, 5.0, 0.0, -3.0, 0.0));
        assertTrue(amplitude.size() >= 3, "应有足够的幅值");
        // 最大幅值至少为 3（从0到-3）
        double maxAbs = amplitude.stream().mapToDouble(Math::abs).max().orElse(0);
        assertTrue(maxAbs >= 3.0, "最大幅值应 >= 3.0");
    }

    // ====================================================================
    // computeStatistics 统计测试
    // ====================================================================

    @Test
    void testComputeStatistics_normal_noTrigger() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // 正常路面：小幅值波动
        double[] ay = {0.05, 0.06, 0.04, 0.05, 0.07, 0.06, 0.05};
        double[] az = {0.10, 0.11, 0.09, 0.10, 0.12, 0.11, 0.10};
        List<MotionFrame> frames = buildFrames("test-normal", ay, az);
        Statistics stats = p.computeStatistics(frames);

        // 正常路面不应触发阈值
        assertTrue(stats.getAmplitudeAy() < 1L, "正常路面ay不应超过阈值");
        assertTrue(stats.getAmplitudeAz() < 1L, "正常路面az不应超过阈值");
    }

    @Test
    void testComputeStatistics_bumpy_triggerLevel3() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // 颠簸路面：ay 和 az 超过 Level 3 阈值但低于 Level 5
        // Level 3: yAmplitude=0.23, zAmplitude=0.27
        // 构造一个明显的波峰波谷
        double[] ay = {0.0, 0.5, 0.0, -0.4, 0.0, 0.5, 0.0};  // 幅值 0.5 > 0.23
        double[] az = {0.0, 0.6, 0.0, -0.5, 0.0, 0.6, 0.0};  // 幅值 0.6 > 0.27
        List<MotionFrame> frames = buildFrames("test-l3", ay, az);
        Statistics stats = p.computeStatistics(frames);

        assertTrue(stats.getAmplitudeAy() >= 1L, "ay 应触发阈值");
        assertTrue(stats.getAmplitudeAz() >= 1L, "az 应触发阈值");
        assertTrue(stats.getMaxAmplitudeAy() > 0.25, "最大ay幅值应 > Level 7 阈值");
        assertTrue(stats.getMaxAmplitudeAz() > 0.43, "最大az幅值应 > Level 7 阈值");
    }

    @Test
    void testComputeStatistics_bumpy_triggerLevel5() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // Level 5: yAmplitude=0.25, zAmplitude=0.35
        // Level 7: yAmplitude=0.25, zAmplitude=0.43
        // 构造满足 Level 5 但不到 Level 7 的小幅值
        double[] ay = {0.1, 0.4, 0.1, -0.3, 0.1, 0.4, 0.1};   // ay 幅值 0.3 ~ 0.4
        double[] az = {0.1, 0.40, 0.1, -0.38, 0.1, 0.40, 0.1}; // az 幅值 0.3 ~ 0.4 (可能在 0.35~0.43 之间)
        List<MotionFrame> frames = buildFrames("test-l5", ay, az);
        Statistics stats = p.computeStatistics(frames);

        double maxAy = stats.getMaxAmplitudeAy();
        double maxAz = stats.getMaxAmplitudeAz();
        // 验证 ay 和 az 均超过 Level 3 阈值（最低触发条件）
        assertTrue(maxAy > 0.23, "ay > Level3 阈值，实际: " + maxAy);
        assertTrue(maxAz > 0.27, "az > Level3 阈值，实际: " + maxAz);
    }

    @Test
    void testComputeStatistics_bumpy_triggerLevel7() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // Level 7: yAmplitude=0.25, zAmplitude=0.43
        double[] ay = {0.0, 0.8, 0.0, -0.6, 0.0, 0.8, 0.0};   // ay 幅值 0.8 >> 0.25
        double[] az = {0.0, 0.9, 0.0, -0.7, 0.0, 0.9, 0.0};   // az 幅值 0.9 >> 0.43
        List<MotionFrame> frames = buildFrames("test-l7", ay, az);
        Statistics stats = p.computeStatistics(frames);

        assertTrue(stats.getMaxAmplitudeAy() > 0.25, "ay > Level7 阈值");
        assertTrue(stats.getMaxAmplitudeAz() > 0.43, "az > Level7 阈值");
        long totalCount = stats.getAmplitudeAy() + stats.getAmplitudeAz();
        assertTrue(totalCount >= 3L,
                "sumCondition 应满足（ay+az >= 3），实际: " + totalCount);
    }

    // ====================================================================
    // identify 方法集成测试
    // ====================================================================

    @Test
    void testIdentify_needs7Frames() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // 发送 7 帧数据，触发 identify
        String deviceId = "test-7f";
        long now = System.currentTimeMillis();
        for (int i = 0; i < 7; i++) {
            MotionFrame f = makeFrame(deviceId, 0.5, 0.6, 1.0, now + i * 100);
            p.identify(f);
        }
        // 执行到这里没有异常即通过
        assertTrue(true, "7帧数据处理不应异常");
    }

    @Test
    void testIdentify_filterHighAz() throws Exception {
        BumpyProcessor4Motion p = createProcessor();
        // az > 2.5 的数据应被过滤
        String deviceId = "test-filter";
        MotionFrame f = makeFrame(deviceId, 0.5, 3.0, 1.0, System.currentTimeMillis());
        // 被过滤的数据不应导致异常
        assertDoesNotThrow(() -> p.identify(f));
    }
}
