

import redis
import json
import numpy as np
import datetime

def update_GPS_by_sn_and_timestamp(event, GPS_key_name):
    GPS_list = cache.zrange(name="vaas:vehicle:location:25", start=0, end=-1, withscores=True)
    coordinates = [json.loads(json.loads(item[0])) for item in GPS_list]
    scores = [float(item[1]) for item in GPS_list]
    scores_array = np.array(scores)

    # if event_ts > max(scores_array):
    #     index = -1
    #     logger.info('cannot find right coordinates in GPS !, event_ts > max(scores_array)')
    # elif event_ts < min(scores_array):
    #     index = 0
    #     logger.info('cannot find right coordinates in GPS !, event_ts < min(scores_array)')
    # else:
    #     index = find_closest(scores_array, event_ts)
    # #logger.info(GPS_list[result_index])
    # logger.info(coordinates[index])
    # coordinates_info_with_time = coordinates[index]
    # event['coordinates'] = [float(coordinates[index]["longitude"]), float(coordinates[index]['latitude'])]
    # logger.info(event)

def find_closest(array, ts):
    return np.searchsorted(array, ts)




def test_pub():
    dt = datetime.datetime.now()
    timestamp = dt.timestamp() * 1000
    event = {'status': 1,
              'event_type': 'bumpy_event',
              'event_time': "2025-05-25 11:12:40.278",
              'event_timestamp': timestamp,
              'coordinates': [120.46029645,31.59237275],
              'longitude':120.46029645,
              'latitude':31.59237275,
              "road_name": "123",
              "dongle_id": "vaas:25",
              "in_xidong": "yes",
              }


    event_to_publish_json = json.dumps(event, ensure_ascii=False).encode('utf8')
    print("publish event to redis")
    print(event_to_publish_json)
    cache.publish('vaas:event:topic', event_to_publish_json)

    current_event_timestamp = event['event_timestamp']
    dumped_bumpy_event = json.dumps(event, ensure_ascii=False).encode('utf8')
    mapping = {dumped_bumpy_event: current_event_timestamp}

    if 'vaas:event:bump:24h' not in cache.keys():
        cache.zadd('vaas:event:bump:24h', mapping=mapping)
        print('adding wit bumpy event')
    else:
        # 24 hours in milliseconds = 1000 * 24 * 60 * 60 = 86,400,000 milliseconds
        # cache.zremrangebyscore(name=self.AA_last24h_bumpy_event_key, min=0, max=current_event_timestamp-86400000)
        cache.zadd('vaas:event:bump:24h', mapping=mapping)
        print('adding wit bumpy event')
    return

def delete_key():
    cache.delete('vaas:event:bump:24h')

if __name__ == "__main__":

    cache = redis.Redis(host='192.168.112.17', port=6379, decode_responses=True, max_connections=2)
    # #cache.delete("vaas:vehicle:location:25")
    #update_GPS_by_sn_and_timestamp()
    test_pub()
    #delete_key()



