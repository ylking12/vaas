#!/usr/bin/env python3
"""
vehicle_location_simulator.py
==============================
网联车位置仿真器 — 给 Redis 5 个 vaas:vehicle:info:* key 循环推位置数据
让 LocationService.getOnlineVehicles() 能查到这 5 辆车（60s 内有效）

用法:
    python3 vehicle_location_simulator.py              # 默认 5s 推送，localhost:6379
    python3 vehicle_location_simulator.py --interval 5 # 自定义间隔

按 Ctrl+C 停止。后台跑用: nohup python3 ... > log 2>&1 &
"""
import argparse
import random
import time
import json
import redis


# 5 辆车的初始位置（与 fleet_management 表 imei 对应，沿无锡主干道分布）
# dir_axis='lng' 沿经度方向走；'lat' 沿纬度方向走
# 每辆车绑定一个轴向 + 初始方向，更像沿路直线行驶
INITIAL_POSITIONS = {
    '863842050000001': {'lng': 120.30, 'lat': 31.55, 'dir_axis': 'lng', 'dir_lng': 1,  'dir_lat': 0},
    '863842050000002': {'lng': 120.45, 'lat': 31.58, 'dir_axis': 'lng', 'dir_lng': -1, 'dir_lat': 0},
    '863842050000003': {'lng': 120.42, 'lat': 31.40, 'dir_axis': 'lat', 'dir_lng': 0,  'dir_lat': 1},
    '863842050000004': {'lng': 120.50, 'lat': 31.60, 'dir_axis': 'lng', 'dir_lng': -1, 'dir_lat': 0},
    '863842050000005': {'lng': 120.38, 'lat': 31.68, 'dir_axis': 'lat', 'dir_lng': 0,  'dir_lat': -1},
}

# 无锡大致 bounds：120.05-120.60, 31.36-31.73（贴原大屏 WUXI_BOUNDS）
# 留 0.02 度缓冲避免超出
BOUNDS = {
    'lng_min': 120.07, 'lng_max': 120.58,
    'lat_min': 31.38, 'lat_max': 31.71,
}

# 固定速度（30m/5s ≈ 21.6 km/h，按用户原话"5s 推 30m"）
SPEED_KMH = 21.6

# Redis key 前缀（与 RedisKeyConfig.VehicleInfoPrefix 一致）
VEHICLE_KEY_PREFIX = 'vaas:vehicle:info:'


def clamp(val, lo, hi):
    return max(lo, min(hi, val))


def move_position(state):
    """按当前方向偏移固定 30m，碰 bounds 反向
    30m 在无锡纬度：≈ 0.00032 经度 / 0.00027 纬度"""
    # 5s 移动 30m ≈ 21.6 km/h（合理城市道路速度）
    STEP_LNG = 0.00032   # ~30m 经度
    STEP_LAT = 0.00027   # ~30m 纬度

    # 每辆车用统一的轴向（不混向）：东/西 OR 南/北，更像沿路行驶
    # 通过 dir_axis 控制：'lng' = 沿经度方向走，'lat' = 沿纬度方向走
    if state['dir_axis'] == 'lng':
        new_lng = state['lng'] + STEP_LNG * state['dir_lng']
        new_lat = state['lat']
    else:
        new_lng = state['lng']
        new_lat = state['lat'] + STEP_LAT * state['dir_lat']

    # 碰 lng 边界反向（沿 lng 轴的车）
    if state['dir_axis'] == 'lng':
        if new_lng < BOUNDS['lng_min'] or new_lng > BOUNDS['lng_max']:
            state['dir_lng'] *= -1
            new_lng = clamp(new_lng, BOUNDS['lng_min'], BOUNDS['lng_max'])
    # 碰 lat 边界反向（沿 lat 轴的车）
    else:
        if new_lat < BOUNDS['lat_min'] or new_lat > BOUNDS['lat_max']:
            state['dir_lat'] *= -1
            new_lat = clamp(new_lat, BOUNDS['lat_min'], BOUNDS['lat_max'])

    state['lng'] = new_lng
    state['lat'] = new_lat
    return state


def build_payload(state):
    """构造 CachedVehiclePosition JSON（与后端 Java 字段名一致）
    speed 固定按 30m/5s = 21.6 km/h 算，符合'5s 推 30m'的视觉观感"""
    now_ms = int(time.time() * 1000)
    # 30m / 5s = 6 m/s = 21.6 km/h（用户要求 30m/5s 对应速度）
    speed = 21.6
    return {
        'longitude': round(state['lng'], 6),
        'latitude': round(state['lat'], 6),
        'speed': speed,
        'timestamp': now_ms,
    }


def main():
    parser = argparse.ArgumentParser(description='网联车位置仿真器')
    parser.add_argument('--host', default='localhost', help='Redis host (default: localhost)')
    parser.add_argument('--port', type=int, default=6379, help='Redis port (default: 6379)')
    parser.add_argument('--interval', type=float, default=5.0, help='推送间隔秒数 (default: 5)')
    args = parser.parse_args()

    print(f'[vehicle-sim] 连 Redis {args.host}:{args.port} ...', flush=True)
    r = redis.Redis(host=args.host, port=args.port, decode_responses=True)
    r.ping()
    print(f'[vehicle-sim] ✅ Redis 已连，开始推送 (间隔 {args.interval}s)', flush=True)
    print(f'[vehicle-sim] 车辆数: {len(INITIAL_POSITIONS)} (与 fleet_management.imei 对应)', flush=True)

    # 深拷贝初始位置（避免被修改污染 INITIAL_POSITIONS）
    states = {imei: dict(pos) for imei, pos in INITIAL_POSITIONS.items()}

    cycle = 0
    try:
        while True:
            cycle += 1
            now_ms = int(time.time() * 1000)
            for imei, state in states.items():
                move_position(state)
                payload = build_payload(state)
                key = VEHICLE_KEY_PREFIX + imei
                # RPUSH + LTRIM 保留最后 1 条（与 detector4kt 推送语义一致：List 只保留最新）
                r.rpush(key, json.dumps(payload))
                r.ltrim(key, -1, -1)
            if cycle % 6 == 0:
                print(f'[vehicle-sim] cycle {cycle}: 已推送 {len(states)} 辆车 ts={now_ms}', flush=True)
            time.sleep(args.interval)
    except KeyboardInterrupt:
        print(f'\n[vehicle-sim] ⏹  停止 (共 {cycle} 个周期)', flush=True)
    except redis.exceptions.ConnectionError as e:
        print(f'[vehicle-sim] ❌ Redis 连接失败: {e}', flush=True)
        raise


if __name__ == '__main__':
    main()