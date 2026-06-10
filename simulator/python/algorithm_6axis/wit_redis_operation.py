from database import cache
import json
from loguru import logger
from typing import List


# 业务需求：
# 积水路面个数，且只保留24小时内的数据。另外这个是路面个数，不是事件个数，路名去重后求和。len(set(road_name))
# 湿滑路面同理
# 颠簸路面同理


class RedisOperationForWit:
    def __init__(self):

        self.AA_last24h_bumpy_event_key = "vaas:event:bump:24h"

        self.event_stream_key = 'vaas:vehicle:event'

        self.AA_car_bumpy_event_count_key = 'vaas:bump:count'

        self.road_segment_coordinates_key = 'road_segment_coordinates'
        self.road_segment_map_key = "road_segment_map"
        # 根据coordinates找半径为2km的范围内找路名，如果找不到任何路名，返回unknown road
        self.radius = 2

    # def count_wit_car_event(self, event_data):
    #     event_type = event_data['event_type']
    #     #sn = event_data['serial_number']
    #     vehicle_number = event_data["vehicle_number"]
    #
    #     if not cache.hexists(self.AA_car_bumpy_event_count_key, vehicle_number):
    #         cache.hset(self.AA_car_bumpy_event_count_key, vehicle_number, 1)
    #     else:
    #         cache.hincrby(self.AA_car_bumpy_event_count_key, vehicle_number, 1)
    #         logger.info('incremented wit bumpy event count !')

    def get_road_name(self, coordinates: list) -> str:
        road_id = cache.georadius(self.road_segment_coordinates_key, longitude=coordinates[0],
                                  latitude=coordinates[1],
                                  radius=self.radius,
                                  unit='km', count=1, sort='ASC')
        if not road_id:
            return "unknown_road"
        road_name = cache.hmget(self.road_segment_map_key, road_id)[0]
        if road_name:
            return road_name
        return "unknown_road"

    def add_bumpy_event_to_redis(self, event) -> None:
        event["latitude"] = event["coordinates"][1]
        event["longitude"] = event["coordinates"][0]
        event_to_publish = {'bumpy_event': event}
        event_to_publish_json = json.dumps(event_to_publish, ensure_ascii=False).encode('utf8')
        logger.info("publish event to redis")
        cache.publish(self.event_stream_key, event_to_publish_json)

        current_event_timestamp = event['event_timestamp']
        dumped_bumpy_event = json.dumps(event, ensure_ascii=False).encode('utf8')
        mapping = {dumped_bumpy_event: current_event_timestamp}

        if self.AA_last24h_bumpy_event_key not in cache.keys():
            cache.zadd(self.AA_last24h_bumpy_event_key, mapping=mapping)
            logger.info('adding wit bumpy event')
        else:
            # 24 hours in milliseconds = 1000 * 24 * 60 * 60 = 86,400,000 milliseconds
            # cache.zremrangebyscore(name=self.AA_last24h_bumpy_event_key, min=0, max=current_event_timestamp-86400000)
            cache.zadd(self.AA_last24h_bumpy_event_key, mapping=mapping)
            logger.info('adding wit bumpy event')
        return

    def add_beyond_xidong_bumpy_event_to_redis(self, event):
        event_to_publish = {'bumpy_event': event}
        event_to_publish_json = json.dumps(event_to_publish, ensure_ascii=False).encode('utf8')
        cache.publish(self.event_stream_key, event_to_publish_json)

        current_event_timestamp = event['event_timestamp']
        dumped_bumpy_event = json.dumps(event, ensure_ascii=False).encode('utf8')
        mapping = {dumped_bumpy_event: current_event_timestamp}

        # if self.AA_beyond_xidong_last24h_bumpy_event_key not in cache.keys():
        #     cache.zadd(self.AA_beyond_xidong_last24h_bumpy_event_key, mapping=mapping)
        #     logger.info('wit adding beyond xidong wit bumpy event')
        # else:
        #     # 24 hours in milliseconds = 1000 * 24 * 60 * 60 = 86,400,000 milliseconds
        #     cache.zremrangebyscore(name=self.AA_beyond_xidong_last24h_bumpy_event_key, min=0,
        #                            max=current_event_timestamp - 86400000)
        #     cache.zadd(self.AA_beyond_xidong_last24h_bumpy_event_key, mapping=mapping)
        #     logger.info('wit adding beyond xidong wit bumpy event')
