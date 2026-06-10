import json
import time
from database import cache
from loguru import logger

#from .wit_mysql_operation import add_wit_event_to_sql
from .wit_bumpy_algorithm import BumpyIdentificationBasedSensor
from .wit_redis_operation import RedisOperationForWit
from .wit_utils import add_area_to_event, update_GPS_by_sn_and_timestamp
from wit import wit_decode_algorithm


# rom wit import wit_decode_func
def find_car_speed(redis_speed_key):
    try:
        raw_data = cache.hget("vaas:vehicle:speed", redis_speed_key)
        # example: raw_data = cache.hget('social_car_spd', 'social_car_spd_01')
        json_format_data = json.loads(raw_data)
        split_data = json_format_data.split(',')
        speed = split_data[-1]
        rounded_speed = int(float(speed))
        return rounded_speed
    except Exception as e:
        logger.error(f"error while retrieving speed from 6 axis")
        logger.error(f"error detail:{e}")
        return 0


def increment_AA_car_event(sn):
    if not cache.hexists('AA_car_bumpy_event_count', sn):
        cache.hset('AA_car_bumpy_event_count', sn, 1)
    else:
        cache.hincrby('AA_car_bumpy_event_count', sn, 1)


def wit_data_handler(redis_list_key_name):
    wit_decoder = wit_decode_algorithm.WitDecoderAW()
    wit_bumpy_identifier = BumpyIdentificationBasedSensor()
    wit_redis_ops = RedisOperationForWit()

    # while cache.llen(redis_list_key_name) > 0:
    while 1:
        single_raw_data_with_key = cache.blpop(redis_list_key_name, 1)
        # must check single_raw_data_with_key is not None
        if not single_raw_data_with_key:
            timestamp = str(round(time.time() * 1000))
            if timestamp[-1] == "0":
                logger.info("all 6 axis data are processed")

        if single_raw_data_with_key:
            # careful here, blpop returns a tuple: (key name, value), so need [1] to access value
            single_data = json.loads(single_raw_data_with_key[1])
            single_data = single_data.split(',')
            date_time = single_data[0]
            wit_serial_number = single_data[1]
            wit_hex_data = single_data[2]
            wit_byt_data = bytes.fromhex(wit_hex_data)
            wit_data = wit_decoder.due_data(wit_byt_data)
            if wit_data:
                timestamp = str(round(time.time() * 1000))
                if timestamp[-2] == "00":
                    logger.info(f"processing {wit_serial_number} data with data time :{date_time}")
                wit_data['datetime'] = date_time
                #wit_data['serial_number'] = wit_serial_number
                wit_data['sensor_id'] = wit_serial_number[3:5]
                bumpy_event = None
                try:
                   # logger.info(wit_data)
                    bumpy_event = wit_bumpy_identifier.identify_bumpy_event(wit_data)
                except Exception as e:
                    logger.exception(e)
                    pass
                if bumpy_event:
                    logger.info(bumpy_event)
                    try:
                        # wit_serial_number : rcs18, [3:]后是纯数子
                        car_number = wit_serial_number[3:]
                        # current_speed = find_car_speed(car_number)
                        # if current_speed == 0:
                        #     logger.info("{} 速度为0，剃掉", wit_serial_number)
                        #     continue
                        # elif current_speed > 35:
                        #     logger.info("{} 速度大于35，剃掉", wit_serial_number)
                        #     continue

                        # 根据 bumpy_event 时间戳去redis里相应的coordinates
                        gps_key_name = "vaas:vehicle:location:" + bumpy_event["device_number"]
                        bumpy_event = update_GPS_by_sn_and_timestamp(bumpy_event, gps_key_name)
                        # increment 事件数量
                        increment_AA_car_event(wit_serial_number)

                        # 看看事件是不是发生在锡东范围内
                        bumpy_event, is_in_xidong = add_area_to_event(bumpy_event)
                        # add event to mysql
                        #logger.success(bumpy_event)

                        if is_in_xidong:
                            # if the event occurred in xidong area
                            bumpy_event['road_name'] = wit_redis_ops.get_road_name(bumpy_event['coordinates'])
                            wit_redis_ops.add_bumpy_event_to_redis(bumpy_event)
                        else:
                            bumpy_event['road_name'] = '不在锡东范围内'
                            #wit_redis_ops.add_beyond_xidong_bumpy_event_to_redis(bumpy_event)
                        # todo: 加到数据库
                        #add_wit_event_to_sql(bumpy_event)
                    except Exception as e:
                        logger.exception(e)
                        pass
