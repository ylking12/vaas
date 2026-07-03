import requests
from datetime import datetime

def simulate_one_event():

    event_time = datetime(2025, 5, 18, 15, 27, 35).isoformat()
    print(event_time)
    result = {
              "id": "10000001",
              "sourceId": "vehicle_001",
              "sourceType": "vehicle",
              'eventType': 'bumpy',
              'eventTime': event_time,
              'lon': 120.41859986,
              'lat': 31.5854597,
              'roadName': "锡沪路",
              }

    url = 'http://localhost:50410/spring/v1/post_simulated_event_to_database'

    print(result)

    r = requests.post(url, json=result)
    print(r.text)

def getEventSummary():
    url = 'http://localhost:50410/spring/v1/external/getEventSummary/2025-01-02T08:30:10/2025-05-25T08:30:10'
    result = requests.get(url)
    print(result.text)


if __name__ == "__main__":
    #simulate_one_event()
    getEventSummary()
