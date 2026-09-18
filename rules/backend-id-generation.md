# 后端 ID 生成规范

## 1. 适用范围
- 本规范适用于系统内部实体 ID、聚合根 ID、聚合内实体 ID、流水/日志明细 ID。
- 本规范不适用于第三方业务号、外部系统编号、展示型业务单号、自然键。
- 领域事件组件自身 ID 策略遵循 `@docs/rules/backend-domain-event.md`，业务表内部 ID 策略遵循本文档。
- 本规范对应架构决策记录：`@docs/adr/0002-backend-tech-stack-and-database.md`。

## 2. ID 分类
- **内部 ID**：由 AI 研发平台系统生成，用于内部主键、实体引用、业务流水追踪，统一使用雪花算法。
- **外部 ID**：由第三方系统生成，如一卡通、科研、财务、大模型提供商返回的编号，必须原样保存，不使用本系统 `IdGenerator`。
- **业务展示单号**：面向用户展示、对账或外部沟通的编号，可独立设计生成规则，不等同于数据库主键。
- **自然键**：如人员编号、租户编码、第三方项目编号，通过唯一约束表达业务唯一性，不替代内部主键策略。

## 3. 类型规范
- Java 侧系统内部 ID 统一使用 `Long`。
- 数据库侧系统内部 ID 统一使用 `BIGINT`。
- HTTP API 与前端 TypeScript 中，所有后端 `Long` ID 统一以 `string` 表达。
- 禁止新增 `Integer` 类型的系统内部主键。
- 禁止新增 `INT` 类型的系统内部主键字段。
- 禁止在前端使用 `number` 承载后端 `Long` ID。

## 4. 生成策略
- 系统内部 ID 统一通过 `IdGenerator` 生成。
- `IdGenerator` 生产实现基于 Hutool `IdUtil.getSnowflake(workerId, datacenterId)`。
- `datacenterId` 固定为 `1`。
- Hutool 对时钟回拨抛出的异常必须视为基础设施故障。
- ID 生成失败不得被业务层吞掉，不得在业务层临时改用随机数、数据库自增或其他备用 ID。

## 5. DDD 分层约束
- `IdGenerator` 是领域层可依赖的接口，跨聚合统一接口应放在 `com.hzsun.aidevops.common.domainshared` 或其下级包。
- 聚合根的业务新建必须通过领域层 `XxxFactory` 完成；`XxxFactory` 的创建方法必须通过参数接收 `IdGenerator`，并在创建流程内生成聚合根自身 ID。
- 聚合根的持久化还原、历史状态重建等非业务创建场景必须通过 `XxxRepository` 获取；仓储还原过程只能使用既有 ID，禁止生成新的聚合根 ID。
- 领域层只依赖 `IdGenerator` 接口，不得直接依赖 Hutool、Spring、数据库、配置中心或 Mapper。
- Hutool 雪花实现、`workerId` 分配、数据库计数器访问必须放在基础设施或启动装配层。
- 聚合根必须在 `XxxFactory` 业务创建流程中获得 ID，禁止依赖数据库或 ORM 插入后回填内部主键。
- 聚合内实体如需独立引用、追踪、幂等或分页定位，可以使用同一个 `IdGenerator` 生成 ID。
- 值对象不得分配 ID。

## 6. 数据库建模约束
- 系统内部主键字段统一使用 `BIGINT NOT NULL`。
- 业务表内部主键不得使用 `AUTO_INCREMENT`、`IDENTITY`、数据库 `sequence` 作为默认生成策略。
- 未正式上线且无需保留存量数据时，允许重建数据库基线并移除既有自增主键。
- 纯关联表优先使用复合主键或复合唯一键，不因统一 ID 策略而强行增加代理主键。
- 第三方业务号应使用 `external_*`、`provider_*`、`source_*` 等字段命名，并按业务需要建立唯一约束。
- 数据库变更脚本仍需遵循 `@docs/rules/backend-database-change-governance.md`。

## 7. workerId 分配
- 应用启动时从数据库单行计数器获取 `workerId`。
- `workerId` 获取逻辑必须在事务内完成，确保并发启动时单行更新互斥。
- 数据库仅维护当前最大 `workerId`。
- 实例启动时将当前 `workerId` 加 `1` 后取出。
- 当自增结果超过 `31` 时，重置为 `0`。
- 当前策略不维护实例注册表，不做心跳续租，不检测仍在运行实例的 `workerId` 冲突。
- 当前策略接受频繁重启、滚动发布、蓝绿发布或并发扩缩容导致 `workerId` 冲突的风险。

## 8. 升级触发条件
出现以下任一情况时，必须重新评审 `workerId` 分配策略，并升级为租约池或显式部署配置：

- 系统进入正式生产运行且存在必须保留的数据。
- 单环境实例数接近或超过 30。
- 发布频率升高，出现频繁重启、滚动发布、蓝绿发布或自动扩缩容。
- 运行环境迁移到 Kubernetes 等会频繁重建实例的容器平台。
- 引入多机房、多校区、多活部署，或需要将 `datacenterId` 纳入唯一性边界。
- 任一环境出现主键冲突、疑似 ID 重复、时钟回拨导致的 ID 生成故障。

## 9. API 与前端协作约束
- 后端 DTO、Cmd、Qry 中承载系统内部 ID 时，Java 类型仍使用 `Long`。
- JSON 序列化到前端时，后端 `Long` ID 必须以字符串表达。
- 前端接口类型中，后端 `Long` ID 必须声明为 `string`。
- 前端传回后端的 ID 参数也必须以字符串承载，由后端在边界层转换为 `Long` 并校验合法性。

## 10. 禁止行为
- 禁止新增数据库自增内部主键。
- 禁止在领域层直接调用 Hutool `IdUtil`。
- 禁止把第三方业务号改造成系统内部 ID。
- 禁止 API 直接以数字表达后端 `Long` ID。
- 禁止用 `Integer` / `INT` 承载雪花 ID。
- 禁止将 `workerId` 循环复用策略描述为无冲突或生产安全策略。

## 11. 自检清单
- [ ] 新增内部主键是否为 Java `Long`、数据库 `BIGINT`。
- [ ] 聚合根的业务新建是否通过领域层 `XxxFactory` 完成，并通过 `IdGenerator` 获取 ID。
- [ ] 聚合根的还原或重建是否通过 `XxxRepository` 获取，且未生成新的聚合根 ID。
- [ ] 领域层是否未直接依赖 Hutool、Spring、数据库或 Mapper。
- [ ] 数据库脚本是否未新增 `AUTO_INCREMENT`、`IDENTITY`、`sequence` 内部主键。
- [ ] 第三方业务号是否作为外部字段保存，而非使用 `IdGenerator` 生成。
- [ ] API 与前端类型是否将后端 `Long` ID 表达为 `string`。
- [ ] 是否已确认当前 `workerId` 简化策略的冲突风险仍可接受。
