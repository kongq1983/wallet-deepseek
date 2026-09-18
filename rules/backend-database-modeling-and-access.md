# 后端数据库建模与访问规范

## 1. 适用范围
- 本规范仅覆盖数据库建模结果与运行时数据访问（表/字段/约束命名、PO/Mapper/Repository 一致性、运行时 CRUD SQL 约束）；不覆盖数据库变更发布脚本治理与回滚策略。
- 适用于后端所有与数据库对象相关的开发活动：表结构设计（命名与语义约束）、PO 命名、MyBatis-Plus 映射、Mapper SQL 与连通性测试，不包含迁移脚本编排与发布流程。
- 涉及数据库变更发布、回滚与脚本编排，统一遵循 `@docs/rules/backend-database-change-governance.md`。

## 2. 核心原则
- **表命名单数化**：业务表统一使用单数蛇形命名（如 `user`、`admin`、`wallet_recharge_order`）。
- **PO 语义一致**：主实体表对应 PO 命名必须表达主实体语义（如 `UserPo`、`AdminPo`）。
- **代码与库结构同源**：`@TableName`、Mapper XML、运行时 SQL 与测试中的表名必须保持完全一致。
- **内部 ID 统一治理**：系统内部主键类型、生成策略与外部 ID 边界统一遵循 `@docs/rules/backend-id-generation.md`。
- **SQL 归位 XML**：自定义查询/更新 SQL 必须写在 Mapper XML 中，禁止在 Mapper 接口（含注解 SQL 与 default 方法）内直接编写 SQL 语句。
- **方言差异受控**：运行时 SQL 禁止在业务层散落数据库方言分支，差异处理遵循 `@docs/rules/backend-database-multi-db-compatibility.md`。

## 3. 命名约定

### 3.1 表名
- 禁止新增复数表名（如 `users`、`admins`、`*_orders`、`*_keys`、`*_credentials`）。
- 领域事件相关基础设施表按既有前缀规范保留（如 `de_domain_event`、`de_consume_record`）。

### 3.2 PO 命名
- 统一格式：`<Entity>Po`。
- 当表语义发生升级（如从凭证态转为主实体）时，必须同步重命名 PO，禁止只改表不改类名。

### 3.3 约束命名
- 主键约束建议：`pk_<table_name>`。
- 唯一约束建议：`uk_<table_name>_<column_or_business_key>`。
- 数据库模型不设计 `CHECK` 约束；字典值、状态流转、数值范围、非空文本、一致性校验等规则由领域模型、应用服务、触发层校验或字典配置承载。

## 4. 开发自检清单
- [ ] 新增/改造表名是否为单数。
- [ ] PO 命名是否与表语义一致。
- [ ] 系统内部主键是否遵循 `@docs/rules/backend-id-generation.md`，未新增 `INT`、`AUTO_INCREMENT`、`IDENTITY` 内部主键。
- [ ] 是否未新增数据库 `CHECK` 约束。
- [ ] `@TableName` 与 Mapper XML 是否全部同步。
- [ ] 是否无 Mapper 接口内 SQL（含注解 SQL 与 default 方法中的 SQL 字符串）。
- [ ] 运行时 CRUD SQL 是否避免在业务层散落数据库方言分支。
- [ ] 连通性测试与相关单测/集测是否同步更新并通过。
