-- VaaS 数据库初始化脚本
-- 用法: mysql -uroot < scripts/init-db.sql
-- 或:   bash scripts/init-db.sh

CREATE DATABASE IF NOT EXISTS `vaas` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `vaas`;

-- =============================================
-- 1. redis_key — Redis Key 配置
-- =============================================
DROP TABLE IF EXISTS `redis_key`;
CREATE TABLE `redis_key` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `vehicle_speed_key` varchar(128) DEFAULT NULL COMMENT '车辆速度Redis Key',
  `vehicle_location_key_prefix` varchar(128) DEFAULT NULL COMMENT '车辆位置key前缀',
  `vehicle_last_online_ts_key` varchar(128) DEFAULT NULL,
  `bump_counter_key` varchar(128) DEFAULT NULL COMMENT '颠簸事件计数器key',
  `slip_counter_key` varchar(128) DEFAULT NULL COMMENT '湿滑事件计数器key',
  `bump_event_key` varchar(128) DEFAULT NULL COMMENT '颠簸事件缓存key',
  `slip_event_key` varchar(128) DEFAULT NULL COMMENT '湿滑事件缓存key',
  `ice_event_key` varchar(128) DEFAULT NULL COMMENT '结冰事件缓存key',
  `ponding_event_key` varchar(128) DEFAULT NULL COMMENT '积水事件缓存key',
  `low_attach_event_key` varchar(128) DEFAULT NULL,
  `road_seg_co_key` varchar(128) DEFAULT NULL,
  `road_seg_map_key` varchar(128) DEFAULT NULL,
  `event_topic` varchar(128) DEFAULT NULL COMMENT '事件推送PubSub主题',
  `motion_topic` varchar(128) DEFAULT NULL COMMENT '运动数据PubSub主题',
  `motion_queue` varchar(128) DEFAULT NULL COMMENT '运动数据队列key',
  `kt_topic` varchar(128) DEFAULT NULL COMMENT 'KT710数据PubSub主题',
  `kt_queue` varchar(128) DEFAULT NULL COMMENT 'KT710数据队列key',
  `vehicle_info_key_prefix` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Redis Key配置表';

INSERT INTO `redis_key` (`id`, `vehicle_speed_key`, `vehicle_location_key_prefix`, `vehicle_last_online_ts_key`,
  `bump_counter_key`, `slip_counter_key`, `bump_event_key`, `slip_event_key`, `ice_event_key`,
  `ponding_event_key`, `low_attach_event_key`, `road_seg_co_key`, `road_seg_map_key`,
  `event_topic`, `motion_topic`, `motion_queue`, `kt_topic`, `kt_queue`, `vehicle_info_key_prefix`)
VALUES (1,
  'vaas:vehicle:info:', 'vaas:vehicle:location:', 'vaas:vehicle:last-online',
  'vaas:bump:counter', 'vaas:slip:counter',
  'vaas:bump:event', 'vaas:slip:event', 'vaas:ice:event', 'vaas:ponding:event', 'vaas:low-attachment:event',
  'vaas:road:segment:coordinates', 'vaas:road:segment:map',
  'vaas:event:topic', 'vaas:motion:notifier', 'vaas:motion:queue:', 'vaas:kt710:notifier', 'kt710:queue:',
  'vaas:vehicle:info:');

-- =============================================
-- 2. fleet_management — 车队管理表
-- =============================================
DROP TABLE IF EXISTS `fleet_management`;
CREATE TABLE `fleet_management` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `imei` varchar(64) NOT NULL COMMENT '设备IMEI号',
  `kt710_id` varchar(64) DEFAULT NULL COMMENT 'KT710设备ID(SN)',
  `plate` varchar(32) DEFAULT NULL COMMENT '车牌号',
  `data_type` varchar(16) NOT NULL COMMENT '数据类型: kt710/6a',
  `group_id` int NOT NULL COMMENT '分组ID',
  `bump_enable` tinyint(1) DEFAULT '0' COMMENT '是否启用颠簸检测',
  `slip_enable` tinyint(1) DEFAULT '0' COMMENT '是否启用湿滑检测',
  `sim_id` varchar(64) DEFAULT NULL COMMENT 'SIM卡ID',
  `brand_model` varchar(64) DEFAULT NULL COMMENT '品牌型号',
  `reject` tinyint(1) DEFAULT '0' COMMENT '是否拒绝',
  `update_at` bigint DEFAULT NULL COMMENT '更新时间戳',
  `phone_number` varchar(32) DEFAULT NULL COMMENT '电话号码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_imei` (`imei`),
  KEY `idx_kt710_id` (`kt710_id`),
  KEY `idx_plate` (`plate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车队管理表';

-- =============================================
-- 3. brand_model — 车辆品牌型号
-- =============================================
DROP TABLE IF EXISTS `brand_model`;
CREATE TABLE `brand_model` (
  `model_id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_name` varchar(128) DEFAULT NULL COMMENT '型号名称',
  `model_value` varchar(128) DEFAULT NULL COMMENT '型号值',
  PRIMARY KEY (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车辆品牌型号表';

-- =============================================
-- 4. event — 路面事件表
-- =============================================
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id` varchar(64) DEFAULT NULL,
  `event_type` varchar(32) NOT NULL COMMENT '事件类型',
  `source_id` varchar(64) DEFAULT NULL,
  `source_type` varchar(32) NOT NULL COMMENT '来源类型',
  `road_name` varchar(128) DEFAULT NULL COMMENT '路段名称',
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `in_area` tinyint(1) DEFAULT NULL,
  `event_time` datetime DEFAULT NULL,
  `received_time` datetime DEFAULT NULL,
  `perception_time` datetime DEFAULT NULL,
  `duplicated` tinyint(1) DEFAULT '0',
  `level` int DEFAULT NULL COMMENT '事件等级',
  `simulated` tinyint(1) DEFAULT '0',
  `h3_hash` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_event_time` (`event_time`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_source_id` (`source_id`),
  KEY `idx_h3_hash` (`h3_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路面事件表';

-- =============================================
-- 5. sensor_node_data — 传感器节点数据
-- =============================================
DROP TABLE IF EXISTS `sensor_node_data`;
CREATE TABLE `sensor_node_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sensor_id` int NOT NULL COMMENT '传感器ID',
  `sensor_type` varchar(32) DEFAULT NULL COMMENT '传感器类型',
  `coordinate_type` tinyint DEFAULT NULL COMMENT '坐标类型',
  `latitude` float DEFAULT NULL COMMENT '纬度',
  `longitude` float DEFAULT NULL COMMENT '经度',
  `node_id` int DEFAULT NULL COMMENT '节点ID',
  `record_time` datetime DEFAULT NULL COMMENT '记录时间',
  `analog1` float DEFAULT NULL COMMENT '模拟量1',
  `analog2` float DEFAULT NULL COMMENT '模拟量2',
  `float_data` float DEFAULT NULL COMMENT '浮点数据',
  `unsigned_int32_data` bigint DEFAULT NULL COMMENT '32位无符号整数',
  `road_conditions` int DEFAULT NULL COMMENT '路面状态',
  `ponding_depth` float DEFAULT NULL COMMENT '积水深度(mm)',
  `ice_thickness` float DEFAULT NULL COMMENT '结冰厚度(mm)',
  `road_surface_temp` float DEFAULT NULL COMMENT '路面温度(°C)',
  PRIMARY KEY (`id`),
  KEY `idx_sensor_id` (`sensor_id`),
  KEY `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='传感器节点数据表';

-- =============================================
-- 6. weather — 天气数据表
-- =============================================
DROP TABLE IF EXISTS `weather`;
CREATE TABLE `weather` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `district_name` varchar(64) DEFAULT NULL COMMENT '区域名称',
  `district_id` varchar(32) DEFAULT NULL COMMENT '区域ID',
  `request_time` datetime DEFAULT NULL COMMENT '请求时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `obs_time` datetime DEFAULT NULL COMMENT '观测时间',
  `temp` float DEFAULT NULL COMMENT '温度(°C)',
  `feels_like` int DEFAULT NULL COMMENT '体感温度',
  `icon` varchar(32) DEFAULT NULL COMMENT '天气图标',
  `text` varchar(64) DEFAULT NULL COMMENT '天气描述',
  `wind360` int DEFAULT NULL COMMENT '风向角度',
  `wind_dir` varchar(16) DEFAULT NULL COMMENT '风向',
  `wind_scale` int DEFAULT NULL COMMENT '风力等级',
  `wind_speed` int DEFAULT NULL COMMENT '风速(km/h)',
  `humidity` int DEFAULT NULL COMMENT '相对湿度(%)',
  `precip` float DEFAULT NULL COMMENT '降水量(mm)',
  PRIMARY KEY (`id`),
  KEY `idx_district_name` (`district_name`),
  KEY `idx_obs_time` (`obs_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='天气数据表';

-- =============================================
-- 初始种子数据
-- =============================================

-- 车型
INSERT INTO `brand_model` (`model_name`, `model_value`) VALUES
  ('荣威EI5', 'rongwei_EI5');

-- 车队车辆（示例）
INSERT INTO `fleet_management` (`imei`, `kt710_id`, `plate`, `data_type`, `group_id`, `bump_enable`, `slip_enable`, `brand_model`) VALUES
  ('863842050000001', 'KT710001', '苏B0T001', 'kt710', 1, 1, 1, '荣威EI5'),
  ('863842050000002', 'KT710002', '苏B1T002', 'kt710', 1, 1, 1, '荣威EI5'),
  ('863842050000003', 'KT710003', '苏B3T003', 'kt710', 1, 1, 1, '荣威EI5'),
  ('863842050000004', 'KT710004', '苏B6T004', 'kt710', 1, 1, 1, '荣威EI5'),
  ('863842050000005', 'KT710005', '苏B7T005', 'kt710', 1, 1, 1, '荣威EI5');
