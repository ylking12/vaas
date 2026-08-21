package com.etas.vaas.detector4motion.processor;

import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.MotionFrame;
import java.lang.reflect.Field;

/**
 * Standalone bump-detection test for the ALIGNED detector4motion (2026-07-21, 9.10 前置验证).
 *
 * 直接 new BumpyProcessor4Motion + 反射注入线上 prod 阈值，喂 20 帧（7 bump + 13 flat），
 * 验证对齐后的处理器（移除 web/actuator/webflux 后）仍能正确产生 level7 颠簸事件。
 * 不依赖 Spring 容器 / Redis，绕过本地 Redis 3.2 不支持 LPOP count 的环境限制。
 *
 * 阈值来源：.15 /opt/etas/vaas/vaas_detector4motion/application-prod.yaml（线上 active 配置）
 * 帧序列来源：P5 BumpyProcessor4MotionTest 的 Level7 触发模式
 */
public class BumpStandaloneTest {

    public static void main(String[] args) throws Exception {
        BumpyProcessor4Motion p = new BumpyProcessor4Motion();
        // 线上 prod 阈值（与 application-prod.yaml 一致）
        setField(p, "debounceEnabled", true);
        setField(p, "debounceInterval", 3000L);
        setField(p, "yAmplitudeLevel7", 0.44);
        setField(p, "zAmplitudeLevel7", 0.25);
        setField(p, "yAmplitudeLevel5", 0.35);
        setField(p, "zAmplitudeLevel5", 0.25);
        setField(p, "yAmplitudeLevel3", 0.27);
        setField(p, "zAmplitudeLevel3", 0.23);

        String deviceId = "e2e-001";
        long now = 1753000000000L;
        double[] bumpAy = {0.0, 0.8, 0.0, -0.6, 0.0, 0.8, 0.0};
        double[] bumpAz = {0.0, 0.9, 0.0, -0.7, 0.0, 0.9, 0.0};

        VehicleEventDao event = null;
        // 帧 0-6：bump 模式（应记中间结果、statusFlag=1、缓存重置，返回 null）
        for (int i = 0; i < 7; i++) {
            event = p.identify(frame(deviceId, bumpAy[i], bumpAz[i], 1.0, now + i * 100));
        }
        System.out.println("[after 7 bump frames] event = " + event + "  (expect null: intermediate recorded, cache reset)");

        // 帧 7-19：平坦（statusFlag 递减 -> 某帧 <0 时生成事件）
        for (int i = 7; i < 20; i++) {
            event = p.identify(frame(deviceId, 0.05, 0.05, 1.0, now + i * 100));
            if (event != null) {
                System.out.println("[BUMP EVENT at frame " + i + "]");
                System.out.println("  eventType = " + event.getEventType());
                System.out.println("  level     = " + event.getLevel());
                System.out.println("  deviceId  = " + event.getDeviceId());
                System.out.println("  sourceType= " + event.getSourceType());
                System.out.println(">>> SUCCESS: aligned BumpyProcessor4Motion produced a bump event (level "
                    + event.getLevel() + ")");
                return;
            }
        }
        System.out.println(">>> NO bump event produced (unexpected - review frame sequence / state machine)");
    }

    static MotionFrame frame(String id, double ay, double az, double wx, long ts) {
        MotionFrame f = new MotionFrame();
        f.setDeviceId(id);
        f.setAY(ay);
        f.setAZ(az);
        f.setWX(wx);
        f.setTimestamp(ts);
        f.setReceivedTimestamp(ts);
        return f;
    }

    static void setField(Object o, String name, Object val) throws Exception {
        Field f = o.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(o, val);
    }
}
