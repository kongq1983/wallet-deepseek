# 数据库选型规范

## 1. 目标
- 本规范定义 AI 研发平台的数据库选型与单库治理边界；不定义 Liquibase 变更脚本写法与发布细则。
- 平台统一使用 PostgreSQL，不维护多数据库分支与方言兼容。

## 2. 选型决策
- 数据库：PostgreSQL（详见 `@docs/adr/0002-backend-tech-stack-and-database.md`）。
- AI 研发平台为内部研发平台，无国产化交付需求，不兼容达梦或其他数据库。
- 运行时数据库类型不可通过配置切换为其他数据库。

## 3. 变更约束
- Liquibase 变更脚本统一使用 PostgreSQL 方言，不需要按 `context` 隔离多数据库差异变更。
- 涉及 Liquibase 变更脚本写法、发布与回滚治理，统一遵循 `@docs/rules/backend-database-change-governance.md`。
- 禁止在业务层散落数据库专属 SQL 或方言分支。

## 4. 验证要求
- 默认 `test` 路径保持数据库无依赖。
- 必须有可执行的 PostgreSQL 真实数据库验证通道。
- 发布结论必须包含 PostgreSQL 验证证据。