from database import cache
import numpy as np
from loguru import logger
import json

def find_closest(ts_array, ts):
    return np.searchsorted(ts_array, ts)


def update_GPS_by_sn_and_timestamp(event, GPS_key_name):

    GPS_list = cache.zrange(name=GPS_key_name, start=0, end=-1, withscores=True)
    coordinates = [json.loads(json.loads(item[0])) for item in GPS_list]
    scores = [float(item[1]) for item in GPS_list]
    scores_array = np.array(scores)

    if event["event_timestamp"] > max(scores_array):
        index = -1
        logger.info('cannot find right coordinates in GPS !, event_ts > max(scores_array)')
    elif event["event_timestamp"] < min(scores_array):
        index = 0
        logger.info('cannot find right coordinates in GPS !, event_ts < min(scores_array)')
    else:
        index = find_closest(scores_array, event_ts)
    #logger.info(GPS_list[result_index])
    logger.info(coordinates[index])
    coordinates_info_with_time = coordinates[index]
    event['coordinates'] = [float(coordinates[index]["longitude"]), float(coordinates[index]['latitude'])]
    logger.info(event)
    return event


#todo: 更新到整个无锡市范围内
max_lon, min_lon = 120.59721142, 120.31417995
max_lat, min_lat = 31.7394908, 31.44289605


def add_area_to_event(event: dict) -> tuple:
    coordinates = event['coordinates']
    max_lon_condition = coordinates[0] < max_lon
    min_lon_condition = coordinates[0] > min_lon
    max_lat_condition = coordinates[1] < max_lat
    min_lat_condition = coordinates[1] > min_lat

    if all([max_lon_condition, min_lon_condition, max_lat_condition, min_lat_condition]):
        event['in_xidong'] = 'yes'
        return event, True
    else:
        event['in_xidong'] = 'no'
        return event, False
