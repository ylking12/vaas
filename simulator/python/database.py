import redis
import platform


# def connect_to_redis():
#     if platform.system() == 'Linux':
#         # 二期服务器里redis的地址为 10.100.1.176:6379
#         redis_connection = redis.Redis(host='10.100.1.176', port=6379, password='Wxyd@20210813', decode_responses=True,
#                                        max_connections=2)
#     else:
#         # 本地windows测试：
#         redis_connection = redis.Redis(host='localhost', port=6379, decode_responses=True, max_connections=2)
#     return redis_connection

cache = redis.Redis(host='192.168.112.17', port=6379, decode_responses=True, max_connections=2)