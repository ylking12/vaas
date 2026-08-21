package com.etas.vaas.backend.cron;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * SOURCE: 新增（演示保活辅助，非原版还原产物） | STATUS: Added-for-demo
 *
 * 用途：向 Redis 颠簸事件 ZSet(bumpEventKey) 注入一条常驻演示点位，定时刷新 score，
 *      对抗 {@link CleanEventZSet} 的 24h 清理，使该点位在大屏上"一直展示"。
 *      仅用于演示/验收，生产环境可通过 demo.bump.keepalive.enabled=false 关闭。
 *
 * 机制（方案 A：运行期间 member 固定，只刷 score）：
 *   - 启动时注入一条 bump 事件（member JSON 固定），score = now
 *   - 每 6h 对同一 member 重新 ZADD，仅刷新 score（ZSet 天然去重，运行期间不累积）
 *   - member 固定 -> 保活期间 eventTimestamp 不变，展示时间停在首次注入（启动）时刻
 *   - 重启时 eventTimestamp 取新启动时刻，member 随之改变；故启动注入前先按固定
 *     eventId 清理旧 demo member，防止重启累积出多个点位
 *   - 直接调 redisUtils.addToZSet，不走 VehicleEventService.persistEvent，不写 MySQL
 *   - 不调 publishEvent，不影响 SSE 实时事件链路
 *
 * 前端：不改。score=now 始终落在默认查询窗口 [now-23h, now] 内，刷新页面/切图层/拖
 *      时间轴触发 loadMapEvents 即可查到并画 marker。
 *
 * 配置：demo.bump.keepalive.{enabled,lng,lat,level,road-name}
 */
@Component
public class DemoBumpEventKeepAlive {
    private static final Logger log = LoggerFactory.getLogger(DemoBumpEventKeepAlive.class);

    /** 固定 eventId，用于启动时识别并清理旧 demo member，避免重启累积 */
    private static final String FIXED_EVENT_ID = "DEMO_BUMP_KEEPALIVE";

    /** 保活间隔：6h（远小于 CleanEventZSet 的 24h 清理周期） */
    private static final long KEEPALIVE_INTERVAL_MS = 6L * 3600_000L;

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;

    @Value("${demo.bump.keepalive.enabled:true}")
    private boolean enabled;
    @Value("${demo.bump.lng:120.379123}")
    private double lng;
    @Value("${demo.bump.lat:31.585633}")
    private double lat;
    @Value("${demo.bump.road-name:团结路庄桥路}")
    private String roadName;
    /** 颠簸等级，可选；不配置则为 null（marker tooltip 不显示等级） */
    @Value("#{${demo.bump.level:null}}")
    private Integer level;

    /** 固定 member JSON，保活时复用，仅刷新 score */
    private String memberJson;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        if (!enabled) {
            log.info("demo bump keepalive disabled, skip injection");
            return;
        }
        cleanupOldDemoMember();
        memberJson = buildMemberJson(System.currentTimeMillis());
        refreshScore();
        log.info("demo bump keepalive injected: lng={}, lat={}, roadName={}, level={}", lng, lat, roadName, level);
    }

    @Scheduled(fixedRate = KEEPALIVE_INTERVAL_MS)
    public void keepAlive() {
        if (!enabled || memberJson == null) {
            return;
        }
        refreshScore();
        log.debug("demo bump keepalive refreshed");
    }

    private void refreshScore() {
        String key = redisKeyConfig.getInstance().getBumpEventKey();
        redisUtils.addToZSet(key, memberJson, (double) System.currentTimeMillis());
    }

    /**
     * 启动时清理可能残留的旧 demo member（按固定 eventId 匹配）。
     * member 中 eventTimestamp 每次启动取当前时刻，若不清理会导致重启后 ZSet 内残留
     * 多条 demo member（24h 内会显示多个点位），故启动注入前先清理。
     */
    private void cleanupOldDemoMember() {
        String key = redisKeyConfig.getInstance().getBumpEventKey();
        Set<String> all = redisUtils.getAllFromZSet(key);
        if (all == null || all.isEmpty()) {
            return;
        }
        int removed = 0;
        for (String m : all) {
            VehicleEventDao dao = JsonUtils.parseJson(m, VehicleEventDao.class);
            if (dao != null && FIXED_EVENT_ID.equals(dao.getEventId())) {
                redisUtils.removeFromZSetByMember(key, m);
                removed++;
            }
        }
        if (removed > 0) {
            log.info("cleaned {} stale demo bump member before re-inject", removed);
        }
    }

    private String buildMemberJson(long eventTimestamp) {
        VehicleEventDao dao = new VehicleEventDao();
        dao.setEventId(FIXED_EVENT_ID);
        dao.setEventType(EventType.BUMP);
        // deviceId 必须非 null：getAlarmList 会用它查 FleetManagementComponent 的
        // ConcurrentHashMap（不允许 null key），get(null) 会 NPE 导致整个告警列表 500。
        // 用一个不存在的占位 imei，get 返回 null -> 走默认车牌"苏B*****"。
        dao.setDeviceId("DEMO_DEVICE");
        dao.setLongitude(lng);
        dao.setLatitude(lat);
        dao.setRoadName(roadName);
        dao.setEventTimestamp(eventTimestamp);
        dao.setInArea(Boolean.TRUE);
        dao.setSimulated(Boolean.TRUE);
        if (level != null) {
            dao.setLevel(level);
        }
        return JsonUtils.toJson(dao);
    }
}
