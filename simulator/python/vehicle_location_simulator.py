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
# 这些位置是当前 5 辆车的"当前位置"，每次循环沿当前方向微移 ±0.0005 度模拟行驶
INITIAL_POSITIONS = {
    '860123456789001': {'lng': 120.40, 'lat': 31.55, 'dir_lng': 1, 'dir_lat': 0},
    '860123456789002': {'lng': 120.45, 'lat': 31.58, 'dir_lng': -1, 'dir_lat': 0},
    '860123456789003': {'lng': 120.38, 'lat': 31.52, 'dir_lng': 0, 'dir_lat': 1},
    '860123456789004': {'lng': 120.42, 'lat': 31.60, 'dir_lng': 1, 'dir_lat': 1},
    '860123456789005': {'lng': 120.36, 'lat': 31.56, 'dir_lng': 0, 'dir_lat': -1},
}

# 无锡大致 bounds：120.05-120.60, 31.36-31.73（贴原大屏 WUXI_BOUNDS）
# 留 0.02 度缓冲避免超出
BOUNDS = {
    'lng_min': 120.07, 'lng_max': 120.58,
    'lat_min': 31.38, 'lat_max': 31.71,
}

# 速度范围（km/h，模拟城市道路）
SPEED_MIN = 20
SPEED_MAX = 60

# Redis key 前缀（与 RedisKeyConfig.VehicleInfoPrefix 一致）
VEHICLE_KEY_PREFIX = 'vaas:vehicle:info:'


def clamp(val, lo, hi):
    return max(lo, min(hi, val))


def move_position(state):
    """按当前方向偏移 ±0.0005 度，碰 bounds 反向"""
    # 随机小步长 (0.0001 ~ 0.0006)
    step_lng = random.uniform(0.0001, 0.0006) * state['dir_lng']
    step_lat = random.uniform(0.0001, 0.0006) * state['dir_lat']

    new_lng = state['lng'] + step_lng
    new_lat = state['lat'] + step_lat

    # 碰 lng 边界反向
    if new_lng < BOUNDS['lng_min'] or new_lng > BOUNDS['lng_max']:
        state['dir_lng'] *= -1
        new_lng = clamp(new_lng, BOUNDS['lng_min'], BOUNDS['lng_max'])

    # 碰 lat 边界反向
    if new_lat < BOUNDS['lat_min'] or new_lat > BOUNDS['lat_max']:
        state['dir_lat'] *= -1
        new_lat = clamp(new_lat, BOUNDS['lat_min'], BOUNDS['lat_max'])

    state['lng'] = new_lng
    state['lat'] = new_lat
    return state


def build_payload(state):
    """构造 CachedVehiclePosition JSON（与后端 Java 字段名一致）"""
    now_ms = int(time.time() * 1000)
    speed = round(random.uniform(SPEED_MIN, SPEED_MAX), 1)
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