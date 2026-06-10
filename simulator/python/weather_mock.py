"""
Open-Meteo 天气数据模拟服务
免费开源天气 API（无需 Key），用于替代物理气象站 SDK
来源: https://open-meteo.com/
"""
import requests
import json
import time
import pymysql
from datetime import datetime

# 无锡市区坐标
WUXI_LAT = 31.58
WUXI_LON = 120.30

# 5个路段的坐标
DISTRICTS = {
    "无锡市": (120.30, 31.58),  # 默认查询城市
    "锡山区": (120.45, 31.60),
    "惠山区": (120.31, 31.68),
    "滨湖区": (120.31, 31.50),
    "梁溪区": (120.30, 31.58),
    "新吴区": (120.43, 31.51),
}

def fetch_weather():
    """从 Open-Meteo 获取实时天气"""
    url = f"https://api.open-meteo.com/v1/forecast?latitude={WUXI_LAT}&longitude={WUXI_LON}&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m"
    try:
        r = requests.get(url, timeout=10)
        data = r.json()
        current = data.get("current", {})
        return {
            "temp": current.get("temperature_2m"),
            "humidity": current.get("relative_humidity_2m"),
            "precip": current.get("precipitation", 0),
            "wind_speed": current.get("wind_speed_10m"),
            "weather_code": current.get("weather_code"),
        }
    except Exception as e:
        print(f"[weather_mock] 获取天气失败: {e}")
        return None

def save_to_mysql(weather):
    """写入 MySQL weather 表"""
    try:
        conn = pymysql.connect(host="localhost", user="root", password="", database="vaas")
        cursor = conn.cursor()
        now = datetime.now()
        for district, (lon, lat) in DISTRICTS.items():
            sql = """INSERT INTO weather (district_name, request_time, update_time, obs_time, temp, humidity, 
                     precip, wind_speed, text, wind_dir) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
            cursor.execute(sql, (district, now, now, now, weather["temp"], weather["humidity"],
                                 weather["precip"], weather["wind_speed"], str(weather.get("weather_code","")), ""))
        conn.commit()
        conn.close()
        print(f"[weather_mock] 已写入 {len(DISTRICTS)} 个区域的天气数据")
        return True
    except Exception as e:
        print(f"[weather_mock] 写入MySQL失败: {e}")
        return False

if __name__ == "__main__":
    print("[weather_mock] 正在获取实时天气...")
    weather = fetch_weather()
    if weather:
        print(f"  温度: {weather['temp']}°C, 湿度: {weather['humidity']}%, 降雨: {weather['precip']}mm")
        save_to_mysql(weather)
    else:
        # 如果网络不可用，写入模拟数据
        print("  网络不可用，使用模拟数据")
        save_to_mysql({"temp": 28.5, "humidity": 65, "precip": 0, "wind_speed": 3.2, "weather_code": 0})
