# Phase 9.2 线上配置与运行参数备份（脱敏）

> **用途**：Phase 9 后台服务替换前的配置基线备份。任一服务替换失败时，可据此恢复原配置与原二进制。
> **采集方式**：SSH 只读采样（2026-07-17），密码/凭据已脱敏（`***MASKED***`），敏感值只记录来源不入库。
> **任务索引**：[TASK_TRACKING.md](../TASK_TRACKING.md) 9.2 ｜ 操作日志 [phase9-replacement-log.md](./phase9-replacement-log.md)
> **采集时服务状态**：receiver@16、detector4kt@15、detector4motion@15 均 active running（已运行约 1 个月，自 2026-06-16）

---

## 0. 关键结论：外部 config.yaml 不被加载

receiver@16、detector4kt@15、detector4motion@15 工作目录下均存在外部 `config.yaml`，但：

- 还原代码中**无任何加载机制**：无 `@PropertySource`、无 `spring.config.import`、无 `config.yaml` 字符串引用、无 `spring.config.name=config`
- Spring Boot 默认只加载 `application.properties/yml/yaml`，**不加载 `config.yaml`**

**判定：外部 config.yaml 不被加载，是遗留文件。各服务 active 配置 = application-prod.\*（receiver/detector4kt 为 JAR 内置，detector4motion 为外部）。**

> 9.1 盘点"receiver/detector4kt 无外部 yaml"结论需更正：**有外部 config.yaml，但不被加载**。

---

## 1. receiver @ 192.168.112.16（port 50412）

### 1.1 systemd unit（`/etc/systemd/system/receiver.service`，密码脱敏）

```ini
[Unit]
Description=VaaS-Receiver
After=network.target

[Service]
User=root
Group=root
Environment="DB_HOST=192.168.112.17"
Environment="DB_USER=vaas"
Environment="DB_PASSWD=***MASKED***"          # 来源：systemd unit Environment=（指向17的MySQL vaas库）
Environment="REDIS_HOST=192.168.112.17"
WorkingDirectory=/data/etas/vaas/receiver
ExecStart=/usr/bin/java -Dspring.profiles.active=prod -jar receiver.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

- **配置加载方式**：`-Dspring.profiles.active=prod`，JAR 内置 application-prod.yaml（无外部 application-prod）
- **无 `-Duser.timezone`** -> 日志为 UTC（与 9.1 一致）
- unit 位置：`/etc/systemd/system/`（注：9.1 表写 /etc/systemd/system，一致）

### 1.2 JAR（回滚依据）

| 项 | 值 |
|---|---|
| 工作目录 | `/data/etas/vaas/receiver` |
| active JAR | `receiver.jar` -> `receiver-0eb93458.jar`（symlink） |
| 大小 | 43,508,592 B |
| sha256 | `e15de9344b6cb7900a62c367268d4c660baf8bf478dc63a9c9a9988d45405a7d` |
| 历史版本（同目录） | `receiver-4fd4ef6d.jar`(43503298B)、`receiver.jar.bak`(43482527B) |
| 外部文件 | `config.yaml`(1908B，不加载)、`logback-spring.xml`(3436B)、`logs/`、`start_local.sh`、`start.sh.bak` |

### 1.3 active 配置（JAR 内置 `BOOT-INF/classes/application-prod.yaml`，密码脱敏）

```yaml
spring:
  application:
    name: receiver
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT:6379}
      database: 0
      lettuce:
        pool:
          max-active: 16
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT:3306}/vaas?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USER}
    password: ***MASKED***
    driver-class-name: com.mysql.cj.jdbc.Driver
  flyway:
    enabled: true
    encoding: UTF-8
    locations: classpath:db/migration
    validate-on-migrate: true
    baseline-on-migrate: true
    clean-disabled: true
server:
  port: 50412
websocket:
  motion-path: /ws/motion
  location-path: /ws/location
  # 注：无 kt-path。WebSocketConfig 只绑定 motionPath/locationPath，WebSocketDispatcher 只注册 /ws/motion、/ws/location 两个 handler（无 /ws/kt）
redis:
  kt-max-queue-size: 80000
  motion-max-queue-size: 80000
  location-max-queue-size: 3000          # 5fps*60s*10min
  location-allow-max-overflow: 600        # 5fps*60s*2min
mybatis-plus:
  global-config:
    sequence:
      datacenter-id: 1
      worker-id: 1
```

JAR 内置其他 profile：application.yaml(`spring.profiles.active: prod`)、application-local.yaml(192.168.88.35)、application-dev.yaml(10.175.116.81)、application-test.yml(vaas_test, redis db15)

### 1.4 外部 config.yaml（不被加载，遗留文件，密码脱敏）

内容含 `websocket.kt-path: /ws/kt`（但代码不读）、`area:`（经度 120.31417995-120.59721142、纬度 31.44289605-31.7394908）、`dump:`（enable:true，含真实设备 IMEI）、`gps.clean-interval: 3600000`、`logging.config: file:./logback-spring.xml`。判定为遗留，替换时原样保留不依赖。

### 1.5 还原版对照（差异）

还原版 `backend/receiver/src/main/resources/application.yml`（无 application-prod.yaml、无 config.yaml、无 spring.profiles.active）：

| 配置项 | 还原版 | 线上 active | 影响 |
|---|---|---|---|
| redis 队列 key | `coordinate-max-queue-size: 80000` | `location-max-queue-size: 3000` + `location-allow-max-overflow: 600` | key 名不同，代码读哪个需确认（KtService 读 kt-max、MotionService 读 motion-max 已确认；location/coordinate 队列的读取类待查） |
| area / gps 块 | 有 | 无（不加载的 config.yaml 里才有） | 若代码读 area，还原版会启用而线上不启用 |
| flyway / mybatis | 缺 | 有 | flyway 可能走默认（依赖存在即 enabled）；mybatis sequence 缺失可能影响雪花 ID |
| spring.profiles.active | 缺 | prod（unit -D 传入） | 还原 JAR 无 application-prod，-D prod 找不到，回退 application.yml |
| management(actuator) | 有 | 未显式 | 还原版新增 P8-8.16，无害 |

**结论**：receiver 替换前需把还原版 application.yml 对齐线上 application-prod.yaml（队列 key、补 flyway/mybatis、去 area/gps 或确认代码不读）。

---

## 2. detector4kt @ 192.168.112.15（port 50413）

### 2.1 systemd unit（`/etc/systemd/system/detector4kt.service`，密码脱敏）

```ini
[Unit]
Description=VaaS-Detector4kt
After=network.target

[Service]
User=root
Group=root
Environment="DB_HOST=192.168.112.17"
Environment="DB_USER=vaas"
Environment="DB_PASSWD=***MASKED***"
Environment="REDIS_HOST=192.168.112.17"
WorkingDirectory=/opt/etas/vaas/detector4kt
ExecStart=/usr/bin/java -Dspring.profiles.active=prod -jar detector4kt.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

- **配置加载方式**：`-Dspring.profiles.active=prod`，JAR 内置 application-prod.yml（无外部 application-prod）
- **无 `-Duser.timezone`** -> 日志 UTC（9.1 已定：按还原原则保持 UTC）
- unit 位置：`/etc/systemd/system/`（与 detector4motion 的 /lib/systemd/system 不同）

### 2.2 JAR（回滚依据）

| 项 | 值 |
|---|---|
| 工作目录 | `/opt/etas/vaas/detector4kt` |
| active JAR | `detector4kt.jar` -> `detector4kt-4fd4ef6d.jar`（symlink） |
| 大小 | 36,536,467 B |
| sha256 | `28fa54952e5ca4985776ab3fdd61435568a79afdc93bf7c09d8b806427658fe0` |
| 历史版本 | `detector4kt.jar.bak`(36516972B) |
| 外部文件 | `config.yaml`(1775B，不加载)、`logs/`、`start_local.sh`、`start.sh`、异常文件名 `\`(1740B，9.1 已记) |

### 2.3 active 配置（JAR 内置 `BOOT-INF/classes/application-prod.yml`，密码脱敏）

```yaml
spring:
  application:
    name: detector4kt
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT:6379}
      database: 0
      lettuce: { pool: { max-active: 16 } }
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT:3306}/vaas?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USER}
    password: ***MASKED***
    driver-class-name: com.mysql.cj.jdbc.Driver
common:
  locationSearch:
    toleratedDeltaTime: 1500   # ms
  geo:
    roadNameSearchRange: 10    # km
    eventDeduplicationEnabled: true
    eventDeduplicationRange: 5 # m
    eventDeduplicationH3Resolution: 5
    eventBounds:
      longitude: { left: 120.05, right: 120.60 }
      latitude: { bottom: 31.36, top: 31.73 }
processor:
  debounce: true
  debounceInterval: 1000       # ms
  bumpy:
    distance: 3                # m
  slippery:
    speed-threshold: 1.389     # 5/1.6
    mu-threshold: 0.51
algorithm:
  kt:
    bump:
      enable: false            # ★ 线上颠簸检测关闭
      steer-ratio-diff-lv1: 0.1
      steer-ratio-diff-lv2: 0.05
      mean-break-pressure-threshold: 1
      sum-speed-ratio-threshold: 3
      correlation-threshold: 0.8   # 注释 # 0.5（旧值），active=0.8
      mean-speed-threshold: 9.7
    slip:
      enable: true
      mu-threshold:            # 空
mode: prod
kt-timezone: UTC
logging:
  level:
    root: info
```

JAR 内置其他 profile：application.yml(`spring.profiles.active: prod`)、application-local.yml、application-dev.yml、application-test.yml

### 2.4 还原版对照（差异，🔴 阻断）

还原版 `backend/detector4kt/src/main/resources/application.yml`。代码绑定确认：
- `Consumer4Kt`: `@Value("${algorithm.kt.bump.enable}")`（**无默认值**）、`@Value("${algorithm.kt.slip.enable}")`（无默认值）
- `BaseProcessor`: `@Value("${processor.debounceInterval}")`、`@Value("${processor.debounce}")`
- `BumpyProcessor`: `@Value("${processor.bumpy.distance}")`、`${mode}`、`${kt-timezone}`
- `SlipperyProcessor`: `${processor.slippery.speed-threshold}`、`${processor.slippery.mu-threshold}`、`${mode}`、`${kt-timezone}`
- `SensitivityConfig`: `@ConfigurationProperties(prefix="algorithm")`

| 配置项 | 还原版 | 线上 active | 代码读取 | 影响 |
|---|---|---|---|---|
| **algorithm.kt.bump.enable** | **true** | **false** | `@Value` 无默认 | 🔴 还原版会开颠簸检测，线上关着 -> 凭空多 KT710 颠簸事件 |
| processor.debounceInterval | 5000 | 1000 | `@Value` 无默认 | 去抖差 5 倍 |
| processor.bumpy.distance | 50 | 3 | `@Value` 无默认 | 距离阈值差 16 倍 |
| kt-timezone | Asia/Shanghai | UTC | `@Value` | 违反 9.1 还原原则（应保持 UTC） |
| common.geo 块 | 缺 | 有 | 待确认 | 缺事件去重/边界/H3 配置 |
| kt710 块 | 有 | 无 | 待确认 | 还原版多出的配置（疑似还原时新增） |
| spring.profiles.active | 缺 | prod | - | 还原 JAR 无 application-prod，回退 application.yml 用上表错误值 |

**🔴 阻断结论**：detector4kt 无外部 application-prod，只换 JAR 后 active 配置 = 还原 JAR 内置 application.yml，bump.enable=true 会错误开启颠簸检测。**替换前必须把还原版 application-prod.yml 对齐线上（bump.enable=false、debounceInterval=1000、bumpy.distance=3、kt-timezone=UTC、补 common.geo）。**

---

## 3. detector4motion @ 192.168.112.15（port 50414）

### 3.1 systemd unit（`/lib/systemd/system/vaas_detector4motion.service`，密码脱敏）

```ini
[Unit]
Description=VAAS Motion Detector Service
After=network.target

[Service]
User=root
Group=root
Environment="DB_HOST=192.168.112.17"
Environment="DB_USER=vaas"
Environment="DB_PASSWD=***MASKED***"
Environment="REDIS_HOST=192.168.112.17"
WorkingDirectory=/opt/etas/vaas/vaas_detector4motion
ExecStart=/usr/bin/java -Dspring.profiles.location=application-prod.yaml -Duser.timezone=Asia/Shanghai -jar vaas_detector4motion.jar
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

- **配置加载方式**：`-Dspring.profiles.location=application-prod.yaml`（SB 3.5.3 不识别的无效遗留 flag）+ **外部 application.yml**(`spring.profiles.active: prod`) 激活 prod -> 外部 application-prod.yaml 为 active 配置
- **有 `-Duser.timezone=Asia/Shanghai`** -> 日志 Shanghai 时间（与 detector4kt 的 UTC 不同）
- unit 位置：`/lib/systemd/system/`

### 3.2 JAR（回滚依据）

| 项 | 值 |
|---|---|
| 工作目录 | `/opt/etas/vaas/vaas_detector4motion` |
| active JAR | `vaas_detector4motion.jar`（非 symlink，实体文件） |
| 大小 | 37,025,409 B |
| sha256 | `6b19e69b070b8ed2ef0a0ad12ea1d65187c9c6ec9cb97eb3d6e52587c317d45b` |
| 历史版本 | `detector4motion-prod-0917.jar`(37023863B) |
| 外部文件 | `application-prod.yaml`(1228B，**active**)、`application.yml`(40B)、`application-test.yml`(1367B)、`config.yaml`(1081B，不加载)、`logback-spring.xml`(3992B)、`nohup.out`(240MB，日志堆积，可清理) |

### 3.3 active 配置（外部 `application-prod.yaml`，密码脱敏）

```yaml
spring:
  application:
    name: detector4motion
  data:
    redis:
      host: ${REDIS_HOST:192.168.112.17}
      port: 6379
      database: 0
      lettuce: { pool: { max-active: 16 } }
  datasource:
    url: jdbc:mysql://${DB_HOST:192.168.112.17}:3306/vaas?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USER:vaas}
    password: ***MASKED***
    driver-class-name: com.mysql.cj.jdbc.Driver
common:
  locationSearch:
    toleratedDeltaTime: 1000   # ms
  geo:
    roadNameSearchRange: 10    # km
    eventDeduplicationRange: 5 # m
    eventDeduplicationH3Resolution: 13
    eventBounds:
      longitude: { left: 120.05, right: 120.60 }
      latitude: { bottom: 31.36, top: 31.73 }
motion-processor:
  debounce:
    enabled: true
    interval: 3000             # ms
  thresholds:
    level7: { yAmplitude: 0.44, zAmplitude: 0.25 }
    level5: { yAmplitude: 0.35, zAmplitude: 0.25 }
    level3: { yAmplitude: 0.27, zAmplitude: 0.23 }
logging:
  level:
    com.etas.vaas.detector4motion.consumer: info
    com.etas.vaas.detector4motion.processor: info
```

外部 application.yml(40B)：`spring: profiles: active: prod`

### 3.4 还原版对照（差异，🟡 不阻断但需修）

代码 `BumpyProcessor4Motion` 绑定确认（@Value）：
- `${motion-processor.debounce.enabled:true}`、`${motion-processor.debounce.interval:2000}`
- `${motion-processor.thresholds.level7.yAmplitude:0.25}`、`${motion-processor.thresholds.level7.zAmplitude:0.43}`
- `${motion-processor.thresholds.level5.yAmplitude:0.25}`、`${motion-processor.thresholds.level5.zAmplitude:0.35}`
- `${motion-processor.thresholds.level3.yAmplitude:0.23}`、`${motion-processor.thresholds.level3.zAmplitude:0.27}`

还原版 application.yml 用的是 **`detector.motion.bump-level-3/5/7`**（y=0.14/0.16/0.20，z=0.175/0.20/0.25）—— **key 名错误，代码不读**。

| 项 | 还原版 | 线上 active | 代码 @Value 默认 |
|---|---|---|---|
| threshold key | `detector.motion.bump-level-N`（错） | `motion-processor.thresholds.levelN`（对） | 读 motion-processor.* |
| level7 y | 0.20（错 key，不生效） | 0.44 | 0.25 |
| level3 y | 0.14（错 key，不生效） | 0.27 | 0.23 |

**🟡 结论**：detector4motion 有外部 application-prod.yaml（active，提供正确 motion-processor.thresholds），只换 JAR 时外部配置不动 -> **能跑对**。但还原 JAR 内置 application.yml 是死配置（key 错），单独部署会回退到 @Value 默认值（与线上 0.44/0.25 仍不同）。**应修还原版 application.yml：改用 motion-processor.thresholds.levelN 键 + 线上值，并补 common.geo/motion-processor.debounce。**

---

## 4. 差异汇总与替换前必修项

| 服务 | 阻断？ | 必修内容 |
|---|---|---|
| detector4kt | 🔴 是 | 还原版补/改 application-prod.yml：bump.enable=false、debounceInterval=1000、bumpy.distance=3、kt-timezone=UTC、补 common.geo(eventBounds/H3=5/toleratedDeltaTime=1500)；确认 kt710 块是否代码需要 |
| receiver | 🟠 待确认 | 对齐队列 key（location-max-queue-size=3000 + overflow=600 或确认代码读 coordinate-max）、补 flyway/mybatis、确认 area/gps 是否代码读取 |
| detector4motion | 🟡 不阻断 | 修还原版 application.yml：改 motion-processor.thresholds.levelN 键 + 线上值(0.44/0.35/0.27)、补 common.geo/motion-processor.debounce（只换JAR 能跑，但还原度缺陷） |

**通用**：三个还原版 application.yml 均缺 `spring.profiles.active: prod` 且无 application-prod.* —— 建议补齐 application-prod.* 对齐线上，使还原 JAR 单独部署也能正确加载 prod 配置。

---

## 5. 回滚流程（任一服务替换失败时）

1. **停止还原版服务**：`systemctl stop <service>`
2. **恢复原 JAR**（原 JAR 在工作目录未删除，或从同目录历史版本/bak 恢复）：
   - receiver: `receiver.jar -> receiver-0eb93458.jar`（sha256 `e15de934…0505a7d`）
   - detector4kt: `detector4kt.jar -> detector4kt-4fd4ef6d.jar`（sha256 `28fa5495…658fe0`）
   - detector4motion: `vaas_detector4motion.jar`（sha256 `6b19e69b…317d45b`）
3. **外部配置不动**（替换时本就只换 JAR，外部 config.yaml/logback/application-prod.yaml 原样保留）
4. **systemd unit 不动**（替换时不改 unit）
5. `systemctl start <service>` + 健康检查（actuator/health + API/日志对照 9.4 基线）
6. 原版二进制 + 配置 sha256 见各服务 1.2/2.2/3.2，恢复后核对哈希确认回滚到位

> 注：实际替换时应先 `cp <jar> <jar>.bak-YYYYMM17` 备份原版再覆盖，本表的 sha256 用于校验备份完整性。
