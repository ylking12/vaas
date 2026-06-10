-- ============================================================================
-- VaaS 数据库 DDL
-- 数据库: vaas
-- 来源: 从 Entity 类反编译还原
-- 生成日期: 2026-06-10
-- 注意事项: 索引和部分约束可能不完整，需与详细设计文档核对
-- ============================================================================

CREATE DATABASE IF NOT EXISTS vaas DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vaas;

-- ----------------------------------------------------------------------------
-- 1. 事件表 - 存储所有检测到的路面事件
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS event (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    event_id            VARCHAR(64)     NOT NULL                 COMMENT '事件唯一ID',
    event_type          VARCHAR(32)     NOT NULL                 COMMENT '事件类型: BUMP(颠簸)/SLIP(湿滑)/PONDING(积水)/ICE(结冰)/LOW_FRICTION(低附着)',
    source_id           VARCHAR(64)     NOT NULL                 COMMENT '来源ID(设备ID/车辆SN)',
    source_type         VARCHAR(32)     NOT NULL                 COMMENT '来源类型: kt710/motionSensor/weatherSensor',
    road_name           VARCHAR(128)    DEFAULT NULL             COMMENT '路段名称',
    longitude           DOUBLE          DEFAULT NULL             COMMENT '经度',
    latitude            DOUBLE          DEFAULT NULL             COMMENT '纬度',
    in_area             TINYINT(1)      DEFAULT NULL             COMMENT '是否在监控区域内',
    event_time          DATETIME        NOT NULL                 COMMENT '事件发生时间',
    received_time       DATETIME        DEFAULT NULL             COMMENT '接收时间',
    perception_time     DATETIME        DEFAULT NULL             COMMENT '感知时间',
    duplicated          TINYINT(1)      DEFAULT 0                COMMENT '是否重复',
    level               INT             DEFAULT NULL             COMMENT '事件等级',
    simulated           TINYINT(1)      DEFAULT 0                COMMENT '是否为模拟数据',
    h3_hash             VARCHAR(32)     DEFAULT NULL             COMMENT 'H3地理哈希索引',
    PRIMARY KEY (id),
    INDEX idx_event_time (event_time),
    INDEX idx_event_type (event_type),
    INDEX idx_source_id (source_id),
    INDEX idx_h3_hash (h3_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路面事件表';


-- ----------------------------------------------------------------------------
-- 2. 车队管理表 - 车辆与设备绑定关系
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fleet_management (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    imei                VARCHAR(64)     NOT NULL                 COMMENT '设备IMEI号',
    kt710_id            VARCHAR(64)     DEFAULT NULL             COMMENT 'KT710设备ID(SN)',
    plate               VARCHAR(32)     DEFAULT NULL             COMMENT '车牌号',
    data_type           VARCHAR(16)     NOT NULL                 COMMENT '数据类型: kt710/6a',
    group_id            INT             NOT NULL                 COMMENT '分组ID',
    bump_enable         TINYINT(1)      DEFAULT 0                COMMENT '是否启用颠簸检测',
    slip_enable         TINYINT(1)      DEFAULT 0                COMMENT '是否启用湿滑检测',
    sim_id              VARCHAR(64)     DEFAULT NULL             COMMENT 'SIM卡ID',
    brand_model         VARCHAR(64)     DEFAULT NULL             COMMENT '品牌型号',
    reject              TINYINT(1)      DEFAULT 0                COMMENT '是否拒绝',
    update_at           BIGINT          DEFAULT NULL             COMMENT '更新时间戳',
    phone_number        VARCHAR(32)     DEFAULT NULL             COMMENT '电话号码',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_imei (imei),
    INDEX idx_kt710_id (kt710_id),
    INDEX idx_plate (plate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车队管理表(车辆-设备绑定)';


-- ----------------------------------------------------------------------------
-- 3. Redis 配置表 - Redis key 映射配置
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS redis_key (
    id                          INT             NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    vehicle_speed_key           VARCHAR(128)    DEFAULT NULL             COMMENT '车辆速度Redis Key',
    vehicle_location_key_prefix VARCHAR(128)    DEFAULT NULL             COMMENT '车辆位置key前缀',
    vehicle_last_online_timestamp_key VARCHAR(128) DEFAULT NULL          COMMENT '车辆最后在线时间key',
    bump_counter_key            VARCHAR(128)    DEFAULT NULL             COMMENT '颠簸事件计数器key',
    slip_counter_key            VARCHAR(128)    DEFAULT NULL             COMMENT '湿滑事件计数器key',
    bump_event_key              VARCHAR(128)    DEFAULT NULL             COMMENT '颠簸事件缓存key',
    slip_event_key              VARCHAR(128)    DEFAULT NULL             COMMENT '湿滑事件缓存key',
    ice_event_key               VARCHAR(128)    DEFAULT NULL             COMMENT '结冰事件缓存key',
    ponding_event_key           VARCHAR(128)    DEFAULT NULL             COMMENT '积水事件缓存key',
    low_attachment_event_key    VARCHAR(128)    DEFAULT NULL             COMMENT '低附着事件缓存key',
    road_segment_coordinates_key VARCHAR(128)   DEFAULT NULL             COMMENT '路段坐标key',
    road_segment_map_key        VARCHAR(128)    DEFAULT NULL             COMMENT '路段映射key',
    event_topic                 VARCHAR(128)    DEFAULT NULL             COMMENT '事件推送PubSub主题',
    motion_topic                VARCHAR(128)    DEFAULT NULL             COMMENT '运动数据PubSub主题',
    motion_queue                VARCHAR(128)    DEFAULT NULL             COMMENT '运动数据队列key',
    kt_topic                    VARCHAR(128)    DEFAULT NULL             COMMENT 'KT710数据PubSub主题',
    kt_queue                    VARCHAR(128)    DEFAULT NULL             COMMENT 'KT710数据队列key',
    vehicle_info_prefix         VARCHAR(128)    DEFAULT NULL             COMMENT '车辆信息key前缀',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Redis Key配置表';


-- ----------------------------------------------------------------------------
-- 4. 品牌型号表
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS brand_model (
    model_id            INT             NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    model_name          VARCHAR(128)    DEFAULT NULL             COMMENT '型号名称',
    model_value         VARCHAR(128)    DEFAULT NULL             COMMENT '型号值',
    PRIMARY KEY (model_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆品牌型号表';


-- ----------------------------------------------------------------------------
-- 5. 气象数据表
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS weather (
    id                  INT             NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    district_name       VARCHAR(64)     DEFAULT NULL             COMMENT '区域名称',
    district_id         VARCHAR(32)     DEFAULT NULL             COMMENT '区域ID',
    request_time        DATETIME        DEFAULT NULL             COMMENT '请求时间',
    update_time         DATETIME        DEFAULT NULL             COMMENT '更新时间',
    obs_time            DATETIME        DEFAULT NULL             COMMENT '观测时间',
    temp                FLOAT           DEFAULT NULL             COMMENT '温度',
    feels_like          INT             DEFAULT NULL             COMMENT '体感温度',
    icon                VARCHAR(32)     DEFAULT NULL             COMMENT '天气图标',
    text                VARCHAR(64)     DEFAULT NULL             COMMENT '天气描述',
    wind360             INT             DEFAULT NULL             COMMENT '风向360度',
    wind_dir            VARCHAR(16)     DEFAULT NULL             COMMENT '风向描述',
    wind_scale          INT             DEFAULT NULL             COMMENT '风力等级',
    wind_speed          INT             DEFAULT NULL             COMMENT '风速(km/h)',
    humidity            INT             DEFAULT NULL             COMMENT '相对湿度(%)',
    precip              FLOAT           DEFAULT NULL             COMMENT '降水量(mm)',
    PRIMARY KEY (id),
    INDEX idx_district_name (district_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='气象数据表';


-- ----------------------------------------------------------------------------
-- 6. 传感器节点数据表 - 气象站传感器实时数据
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sensor_node_data (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    sensor_id           INT             NOT NULL                 COMMENT '传感器ID',
    sensor_type         VARCHAR(32)     DEFAULT NULL             COMMENT '传感器类型: Station/RoadCondition/Atmospheric',
    coordinate_type     TINYINT         DEFAULT NULL             COMMENT '坐标类型',
    latitude            FLOAT           DEFAULT NULL             COMMENT '纬度',
    longitude           FLOAT           DEFAULT NULL             COMMENT '经度',
    node_id             INT             DEFAULT NULL             COMMENT '节点ID',
    record_time         DATETIME        DEFAULT NULL             COMMENT '记录时间',
    analog1             FLOAT           DEFAULT NULL             COMMENT '模拟量1',
    analog2             FLOAT           DEFAULT NULL             COMMENT '模拟量2',
    float_data          FLOAT           DEFAULT NULL             COMMENT '浮点数据',
    unsigned_int32_data BIGINT          DEFAULT NULL             COMMENT '32位无符号整数',
    road_conditions     INT             DEFAULT NULL             COMMENT '路面状态(1干燥/2潮湿/3湿滑/4积水/7结冰)',
    ponding_depth       FLOAT           DEFAULT NULL             COMMENT '积水深度(mm)',
    ice_thickness       FLOAT           DEFAULT NULL             COMMENT '结冰厚度(mm)',
    road_surface_temp   FLOAT           DEFAULT NULL             COMMENT '路面温度(°C)',
    PRIMARY KEY (id),
    INDEX idx_sensor_id (sensor_id),
    INDEX idx_record_time (record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感器节点数据表';
