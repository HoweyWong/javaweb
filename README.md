# Spring Boot 数据同步系统

该示例项目展示了如何使用 Spring Boot 构建一个简单的“源到目标”数据同步系统。源数据和目标数据分别存储在内存 Map 中，通过 REST API、定时任务和服务层协同完成数据同步。

## 功能概览

- `GET /api/sync/preview`：查看源数据与目标数据之间的差异。
- `POST /api/sync`：触发一次全量同步，将所有差异应用到目标端。
- `POST /api/sync/{key}`：按键同步单条记录。
- `GET /api/sync/status`：查看最近同步时间、待同步数量以及近期操作日志。
- 内置定时任务（默认 30 秒一次）自动执行全量同步，可在 `application.yml` 中配置开关和周期。

系统在启动时可选地加载演示数据，便于快速体验，配置项为 `sync.demo-data-enabled`。

## 本地运行

```bash
mvn spring-boot:run
```

应用启动后，可使用 `curl` 或 Postman 访问上述接口，例如：

```bash
# 查看差异
curl http://localhost:8080/api/sync/preview

# 手动执行一次全量同步
curl -X POST http://localhost:8080/api/sync

# 同步单条记录
curl -X POST http://localhost:8080/api/sync/customer-1001

# 查看同步状态
curl http://localhost:8080/api/sync/status
```

## 测试

```bash
mvn test
```

## 配置项

`src/main/resources/application.yml` 提供了默认配置，核心选项如下：

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `sync.demo-data-enabled` | `true` | 启动时是否加载示例数据 |
| `sync.scheduling.enabled` | `true` | 是否启用定时同步 |
| `sync.scheduling.fixed-delay` | `30s` | 定时同步的间隔 |

## 扩展方向

- 将内存 Map 替换为真实的数据源，如数据库或外部系统 API。
- 根据业务需求扩展同步策略，例如冲突解决、增量同步、删除同步等。
- 将同步结果写入审计日志或指标系统，便于监控。
