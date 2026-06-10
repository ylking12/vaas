import datetime
import time
import requests

# points = [
#     [120.4382000589521, 31.584368338687984, "吼山南路"],
#     [120.43847576497572, 31.58433515310284,"吼山南路"],
#     [120.44607160981434,31.589253947247876,"山河路"],
#     [120.44631967595785,31.58925048316855,"山河路"],
#     [120.4275891440434,31.584885507332235, "新锡路"],
#     [120.42785824034104,31.584747667329314, "新锡路"],
# ]

trues = [
    [120.36289552,31.53677911,"先锋东路"],
    [120.35791911, 31.53264283,"先锋东路"],
    [120.43473916,31.58456929,"先锋东路"],
]

def simulate_one_event():

    for i in trues:
        event_time = datetime.datetime.now()
        event_timestamp = round(time.time() * 1000)

        result = {'status': 1,
              'event_type': 'bumpy_event',
              'car_type': 'xiangdao_rongweiEI5',
              'car_id': "rcs15",
              'event_time': str(event_time),
              'event_timestamp': event_timestamp,
              'receive_time': str(event_time),
              'received_timestamp': event_timestamp,
              'latitude': i[1],
              'longitude': i[0],
              'road_name': i[2],
              'in_xidong': "yes"
              }
        url = 'http://localhost:50410/spring/v1/post_simulated_event'
        #url = 'https://vaas.wx-iov.com:444/spring/v1/post_simulated_event'
        response = requests.post(url, json=result)
        print(response.text)

# def get():
#     url = 'http://localhost:50410/spring/v1/rear-mirror/get-last24h-event?minute=1440'
#
#     response = requests.get(url)
#     print(response.text)

if __name__ == "__main__":

    simulate_one_event()
