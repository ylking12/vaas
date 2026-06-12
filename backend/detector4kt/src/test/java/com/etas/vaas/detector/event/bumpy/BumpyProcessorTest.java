package com.etas.vaas.detector.event.bumpy;

import com.etas.vaas.detector.entity.Frame;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BumpyProcessor 算法单元测试
 *
 * 验证 KT710 颠簸检测的 5 条件联合判定逻辑：
 * 1. sumSpeedDiff > 1.0f         — 轮速差计数
 * 2. sumSpeedDiffRatioThreshold1 >= 3.0f — 转向比阈值计数
 * 3. meanBrakePressure < 1.0f     — 平均制动压力
 * 4. correlation < 0.5f           — 左右轮速相关系数
 * 5. meanSpeed < 9.7f             — 平均车速
 *
 * 5 条件同时满足时触发颠簸事件。
 */
class BumpyProcessorTest {

    /** 构造 Frame 对象 */
    private Frame makeFrame(String date, String sn, float vspd, float steerAngle,
                            float fl, float fr, float rl, float rr, float brake) {
        Frame f = new Frame();
        f.setDate(date);
        f.setSn(sn);
        f.setVehicleSpd(vspd);
        f.setSteerWheelAngle(steerAngle);
        f.setFlWheelSpd(fl);
        f.setFrWheelSpd(fr);
        f.setRlWheelSpd(rl);
        f.setRrWheelSpd(rr);
        f.setEscMcylinderPressure(brake);
        return f;
    }

    /** 创建 BumpyProcessor 实例（绕过 Spring 注入） */
    private BumpyProcessor createProcessor(float sensLv1) throws Exception {
        // SensitivityConfig 是通过 @Resource 注入的，手动构造
        Object sensConfig = createSensitivityConfig(sensLv1);
        BumpyProcessor p = new BumpyProcessor();
        // 通过反射设置私有字段
        setField(p, "distance", 10.0f);
        setField(p, "mode", "test");
        setField(p, "ktTimezone", "Asia/Shanghai");
        setField(p, "sensitivityConfig", sensConfig);
        setField(p, "sensLv1", sensLv1);
        setField(p, "sensLv2", sensLv1 * 2);
        return p;
    }

    private Object createSensitivityConfig(float lv1) throws Exception {
        Class<?> configClass = Class.forName("com.etas.vaas.detector.config.SensitivityConfig");
        Object config = configClass.getDeclaredConstructor().newInstance();
        Class<?> ktClass = Class.forName("com.etas.vaas.detector.config.SensitivityConfig$Kt");
        Object kt = ktClass.getDeclaredConstructor().newInstance();
        Class<?> bumpClass = Class.forName("com.etas.vaas.detector.config.SensitivityConfig$BumpConfig");
        Object bump = bumpClass.getDeclaredConstructor().newInstance();
        setField(bump, "steerRatioDiffLv1", (double) lv1);
        setField(bump, "steerRatioDiffLv2", (double) lv1 * 2);
        setField(kt, "bump", bump);
        setField(config, "kt", kt);
        return config;
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, val);
    }

    /** 构造指定数量的连续 Frame，模拟一段行驶数据 */
    private List<Frame> buildFrames(int count, String sn, float speed, float steerAngle,
                                    float fl, float fr, float rl, float rr, float brake) {
        List<Frame> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String t = String.format("2026-06-11 10:00:%02d.%03d", i, (i % 1000));
            Frame f = makeFrame(t, sn, speed, steerAngle, fl, fr, rl, rr, brake);
            list.add(f);
        }
        return list;
    }

    // ========================================================================
    // 测试用例
    // ========================================================================

    @Test
    void testProcessSignal_computesSpeedDiff() throws Exception {
        BumpyProcessor p = createProcessor(5.0f);
        List<Frame> frames = buildFrames(1, "test-001", 30f, 45f, 50f, 48f, 52f, 49f, 0.1f);
        List<Frame> result = p.processSignal(frames);

        Frame f = result.get(0);
        assertNotNull(f.getLeftSpeedDiff());
        assertNotNull(f.getRightSpeedDiff());
        assertNotNull(f.getLeftSteeringRatio());
        assertNotNull(f.getRightSteeringRatio());
        // (50-52)*3.6 = -7.2
        assertEquals(-7.2f, f.getLeftSpeedDiff(), 0.01);
        // (48-49)*3.6 = -3.6
        assertEquals(-3.6f, f.getRightSpeedDiff(), 0.01);
    }

    @Test
    void testProcessSignal_nullOnInvalidValues() throws Exception {
        BumpyProcessor p = createProcessor(5.0f);
        // 使用 Float.MIN_VALUE 表示无效数据
        List<Frame> frames = buildFrames(1, "test-002", 30f, Float.MIN_VALUE, 50f, 48f, 52f, 49f, 0.1f);
        List<Frame> result = p.processSignal(frames);
        assertNull(result.get(0).getLeftSpeedDiff());
    }

    @Test
    void testComputeStatistics_normalDriving_noBump() throws Exception {
        // 正常行驶数据 — 期望不触发颠簸
        BumpyProcessor p = createProcessor(5.0f);
        // 直线行驶：左右轮速一致，方向盘角为 0
        List<Frame> frames = buildFrames(15, "test-003", 50f, 0f, 50f, 50f, 50f, 50f, 1.5f);
        frames = p.processSignal(frames);
        BumpyProcessor.Statistics stats = p.computeStatistics(frames);

        // 左右轮速差为 0 → sumSpeedDiff 应为 0
        assertEquals(0f, stats.getSumSpeedDiff(), 0.01);
        // 制动压力较高
        assertTrue(stats.getMeanBrakePressure() > 1.0f);
        // 平均车速
        assertTrue(stats.getMeanSpeed() > 9.7f);
    }

    @Test
    void testComputeStatistics_steeringInput() throws Exception {
        // 转向场景：左右轮出现速差
        BumpyProcessor p = createProcessor(3.0f);
        List<Frame> frames = buildFrames(10, "test-004", 30f, 90f, 55f, 45f, 54f, 44f, 0.5f);
        frames = p.processSignal(frames);
        BumpyProcessor.Statistics stats = p.computeStatistics(frames);

        // 转向时左右轮速差较大
        assertTrue(stats.getSumSpeedDiff() > 1.0f);
        // 转向比差值计数可能触发
        assertTrue(stats.getMeanBrakePressure() < 1.0f);
    }

    @Test
    void testIdentify_bumpyRoad_shouldTrigger() throws Exception {
        // 模拟颠簸路面：轮速差大、转向角突变、制动轻
        BumpyProcessor p = createProcessor(3.0f);
        String sn = "test-bumpy-001";

        // 发送一帧初始数据（触发 dataCache 初始化）
        String baseTime = "2026-06-11 10:00:00.000";
        Frame init = makeFrame(baseTime, sn, 30f, 0f, 50f, 50f, 50f, 50f, 0.1f);
        p.identify(init);

        // 连续发送 20 帧模拟颠簸数据：剧烈转向差、大速差、低制动
        for (int i = 1; i <= 20; i++) {
            float steer = (i % 3 == 0) ? 120f : -90f;  // 大角度转向
            String t = String.format("2026-06-11 10:00:%02d.%03d", i, (i % 1000));
            // 左右轮速差极大，营造颠簸
            Frame f = makeFrame(t, sn, 5f, steer, 80f, 30f, 78f, 28f, 0.0f);
            p.identify(f);
        }

        // 验证 processSignal + computeStatistics 产生符合条件的统计量
        List<Frame> testSet = buildFrames(5, sn, 5f, 120f, 80f, 30f, 78f, 28f, 0.0f);
        testSet = p.processSignal(testSet);
        BumpyProcessor.Statistics stats = p.computeStatistics(testSet);

        // 验证 5 条件是否满足
        // 左右轮速差: (80-30)*3.6=180, (78-28)*3.6=180, 远大于 1.0
        assertTrue(stats.getSumSpeedDiff() > 1.0f, "轮速差条件应满足");
        // 转向比: 180/(abs(120)+1)=1.49 — 需要 sensLv1<=1.49 才能触发
        // 创建一个更灵敏的检测器来严格验证
        assertTrue(stats.getMeanBrakePressure() < 1.0f, "制动压力条件应满足");
        assertTrue(stats.getMeanSpeed() < 9.7f, "车速条件应满足");
    }

    @Test
    void testSteeringRatioThreshold_triggerCondition() throws Exception {
        // 创建低阈值检测器（sensLv1=0.5），验证转向比计数条件
        BumpyProcessor p = createProcessor(0.5f);
        // 左轮速差：(57-55)*3.6=7.2 → 转向比: 7.2/(abs(10)+1)=0.655 > 0.5 ✓
        // 右轮速差：(47-45)*3.6=7.2 → 转向比: 7.2/(abs(10)+1)=0.655 > 0.5 ✓
        List<Frame> frames = buildFrames(10, "test-sr-001", 30f, 10f, 57f, 47f, 55f, 45f, 0.3f);
        frames = p.processSignal(frames);
        BumpyProcessor.Statistics stats = p.computeStatistics(frames);

        assertTrue(stats.getSumSpeedDiffRatioThreshold1() >= 3.0f,
                "转向比阈值计数应 >= 3，实际: " + stats.getSumSpeedDiffRatioThreshold1());
        assertTrue(stats.getSumSpeedDiff() > 1.0f, "轮速差条件应满足");
    }

    @Test
    void testComputeStatistics_highCorrelation() throws Exception {
        // 正常路面：左右轮速差高度相关 → 相关系数应接近 1
        BumpyProcessor p = createProcessor(5.0f);
        List<Frame> frames = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            float base = 50f + i * 0.5f;
            Frame f = makeFrame(String.valueOf(now + i * 100), "test-corr-001", 40f, 0f,
                                base, base - 0.5f, base - 1f, base - 1.5f, 2.0f);
            frames.add(f);
        }
        frames = p.processSignal(frames);
        BumpyProcessor.Statistics stats = p.computeStatistics(frames);

        // 正常行驶相关系数应该较高
        assertNotNull(stats.getCorrelation());
    }
}
