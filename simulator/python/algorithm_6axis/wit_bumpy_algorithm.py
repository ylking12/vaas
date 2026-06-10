# credits to Niu lijuan

import copy
import datetime

import numpy as np
from scipy import signal


class BumpyIdentificationBasedSensor:
    def __init__(self):
        self.adjacent_slippery_point = {}
        self.status_flag = {}
        self.zero_spd_count = {}
        self.data_cache = {}

    @staticmethod
    def format_time(t):
        return datetime.datetime.strptime(t, '%Y-%m-%d %H:%M:%S.%f')

    @staticmethod
    def convert_to_timestamp(t):

        time_format = '%Y-%m-%d %H:%M:%S.%f'

        timestamp = int(datetime.datetime.strptime(t, time_format).timestamp() * 1000)
        return timestamp

    def update_data_cache(self, data, sensor_id):
        data_dict = {'datetime': data['datetime'], 'ax': data['ax'], 'ay': data['ay'], 'az': data['az'],
                     'wx': data['wx']}

        # ignore abnormal data
        if data_dict['az'] > 2.5:
            data_observe = copy.deepcopy(self.data_cache[sensor_id])
            return data_observe

        self.data_cache[sensor_id].append(data_dict)
        # when the ax equal to 0 more than 3 times, clear the data_cache
        if data_dict['wx'] == 0:
            self.zero_spd_count[sensor_id] = self.zero_spd_count[sensor_id] + 1
            if self.zero_spd_count[sensor_id] > 3:
                self.data_cache[sensor_id] = []
                self.zero_spd_count[sensor_id] = 0
        else:
            self.zero_spd_count[sensor_id] = 0

        # Each observe interval contains 10 pieces of data
        data_observe = copy.deepcopy(self.data_cache[sensor_id])
        if len(self.data_cache[sensor_id]) == 7:
            self.data_cache[sensor_id] = self.data_cache[sensor_id][1:]
        return data_observe

    @staticmethod
    def get_peak_valley_diff(data):
        data = np.array(data)
        to_delete = []
        for i in range(len(data) - 1):
            if data[i] == data[i + 1]:
                to_delete.append(i)
        data = np.delete(data, to_delete)

        max_index = signal.argrelextrema(data, np.greater)
        local_max = data[max_index]
        min_index = signal.argrelextrema(data, np.less)
        local_min = data[min_index]
        extreme_vals = []
        if len(data) >= 2:
            if data[0] > data[1]:
                extreme_vals = local_max
                for i in range(len(local_min)):
                    extreme_vals = np.insert(extreme_vals, i * 2, local_min[i])
            else:
                extreme_vals = local_min
                for i in range(len(local_max)):
                    extreme_vals = np.insert(extreme_vals, i * 2, local_max[i])
        extreme_vals = np.append(extreme_vals, data[-1])
        extreme_vals = np.insert(extreme_vals, 0, data[0])
        peak_valley_diff = np.diff(extreme_vals)
        return peak_valley_diff

    def feature_statis(self, data):
        ay = [item['ay'] for item in data]
        az = [item['az'] for item in data]
        peak_valley_ay = self.get_peak_valley_diff(ay)
        peak_valley_az = self.get_peak_valley_diff(az)

        res = {'start_time': data[0]['datetime'], 'end_time': data[-1]['datetime'],
               'peak_valley_ay_0.2': len(peak_valley_ay[abs(peak_valley_ay) > 0.2]),
               'peak_valley_az_0.25': len(peak_valley_az[abs(peak_valley_az) > 0.25])}
        return res

    def identify_bumpy_event(self, data):
        sensor_id = str(data['sensor_id'])
        if sensor_id not in self.data_cache.keys():
            self.data_cache[sensor_id] = [data]
            self.status_flag[sensor_id] = 0
            self.zero_spd_count[sensor_id] = 0
            self.adjacent_slippery_point[sensor_id] = []
            return

        data_observe = self.update_data_cache(data, sensor_id)
        if len(data_observe) >= 7:
            feature = self.feature_statis(data_observe)
            if feature['peak_valley_ay_0.2'] >= 1 and feature['peak_valley_az_0.25'] >= 1 and \
                    (feature['peak_valley_ay_0.2'] + feature['peak_valley_az_0.25']) >= 3:

                result = {'status': 1,
                          'sensor_time': feature['end_time'],
                          'coordinates': []
                          }
                self.status_flag[sensor_id] = 1
                self.adjacent_slippery_point[sensor_id].append(result)
                self.data_cache[sensor_id] = []
                return
            else:
                self.status_flag[sensor_id] = self.status_flag[sensor_id] - 1
                if len(self.adjacent_slippery_point[sensor_id]) == 0:
                    self.status_flag[sensor_id] = 0
                    return
                elif self.status_flag[sensor_id] < 0:
                    first_sensor_time = self.adjacent_slippery_point[sensor_id][0]['sensor_time']
                    result = {'status': 1,
                              'event_type': 'bumpy_event',
                              'car_type': 'xiangdao_rongweiEI5',
                              'event_time': first_sensor_time,
                              'event_timestamp': self.convert_to_timestamp(first_sensor_time),
                              'coordinates': [],
                              "road_name": "xxxx",
                              "device_number": sensor_id,
                              }
                    self.adjacent_slippery_point[sensor_id] = []
                    self.status_flag[sensor_id] = 0
                    return result
                else:
                    return
