import datetime
import requests
import time
import json
import random


with open("50_cars_path_data.json") as file:
    path = json.load(file)

def simulate_one_event():
    random_int = random.randint(1, 6999)
    event_time = datetime.datetime.now()
    event_timestamp = round(time.time() * 1000)
    coordinates = path[random_int]
    result = {'status': 1,
              'event_type': 'bumpy_event',
              'car_type': 'xiangdao_rongweiEI5',
              'car_id': "rcs15",
              'event_time': str(event_time),
              'event_timestamp': event_timestamp,
              'receive_time': str(event_time),
              'received_timestamp': event_timestamp,
              'latitude': coordinates[1],
              'longitude': coordinates[0],
              'road_name': "",
              'in_xidong': "yes"
              }
    url = 'http://localhost:50410/spring/v1/post_simulated_event'

    response = requests.post(url, json=result)
    print(response.text)

if __name__ == "__main__":
    while True:
        simulate_one_event()
        #每隔45分钟发一次event
        time.sleep(45*60)
