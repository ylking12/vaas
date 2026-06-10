import datetime
import time

from loguru import logger
from sqlalchemy import create_engine, text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy import Column, Integer, String, Float
from urllib.parse import quote_plus

# CREATE THE TABLE MODEL TO USE IT FOR QUERYING
SQLALCHEMY_DATABASE_URL = 'mysql+pymysql://root:' + quote_plus('Wxyd@20210813') + '@10.100.1.67:3306/sris'

# create engine
# pool_pre_ping: tests connections for liveness, pool_recycle every 8 hours
engine = create_engine(SQLALCHEMY_DATABASE_URL, pool_pre_ping=True, pool_recycle=3600 * 8)

# create session
Session = sessionmaker(bind=engine, autocommit=False, autoflush=False)
# create base
Base = declarative_base()


class EVENT(Base):
    __tablename__ = 'EVENT'

    index = Column(Integer, primary_key=True, index=True)

    source_name = Column(String(64))  # source_name 事件源名称或车牌号
    source_type = Column(Integer)  # 事件源类型 0路端传感器 1网约车 2标定车
    road_name = Column(String(64))
    gps = Column(String(32))
    occur_time = Column(Float)
    readable_time = Column(String(32))  # 可读时间
    event_type = Column(Integer)  # '事件类型 0颠簸；1湿滑;2积水;3低附着系数;4结冰;5路拱',
    area_scope = Column(Integer)  # 事件发生范围 0不在锡东 1在锡东


# CREATE A SESSION OBJECT TO INITIATE QUERY IN DATABASE
session = Session()

event_scope_map = {'no': 0, 'yes': 1}


def add_wit_event_to_sql(event):
    event_to_mysql = EVENT(
        source_name=event['car_id'],
        source_type=1,  # social car = 1
        road_name=event['road_name'],
        gps=str(event['coordinates']),
        occur_time=event['event_timestamp'],
        readable_time=event['event_time'],
        event_type=0, # bumpy event = 0
        area_scope=event_scope_map[event['in_xidong']]
    )

    session.add(event_to_mysql)
    session.commit()
    session.refresh(event_to_mysql)
    logger.success(f'wit event {event} added to mysql successfully')
