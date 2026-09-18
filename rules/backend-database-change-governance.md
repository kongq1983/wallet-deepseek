# 后端数据库变更治理规范

## 1. 适用范围
- 本规范仅覆盖数据库变更发布治理（Liquibase 脚本、执行边界、回滚/前滚与审计）；不覆盖日常建模与运行时 CRUD 访问约束，也不定义多数据库语义一致性的验收细则。
- 后端数据库结构变更必须通过 Liquibase 管理，禁止在业务代码中新增或扩散 DDL 逻辑。
- 本规范覆盖变更脚本组织、执行边界、回滚策略与运维安全要求。

## 2. 目录与命名约定
- 主入口：`backend/src/main/resources/db/changelog/db.changelog-master.yaml`
- 开发态脚本根目录：`backend/src/main/resources/db/changelog/in-progress/<context>/<feature>/`，用于保存需求开发过程中的临时 SQL 轨迹。
- 发布态脚本根目录：`backend/src/main/resources/db/changelog/releases/<version>/`，按程序版本号建目录并在同一发布批次内持续维护。
- `<version>` 目录命名规则为 `{大版本 X.X}.{年份 YY}.{月日 MMdd}`，例如 `1.0.26.0901`。
- `db.changelog-master.yaml` 只 include `releases/` 下的发布态脚本，禁止 include `in-progress/`。
- 未发布版本的业务变更脚本应按上下文目录组织：`backend/src/main/resources/db/changelog/releases/<version>/<context>/`。
- 限界上下文权威清单为 `docs/strategic/bounded-context-list.md`；业务脚本只能归属到该清单列出的平台内限界上下文。
- `<context>` 目录名必须由限界上下文清单中的英文名称派生，统一使用小写连续命名，例如 `PointWallet` 对应 `pointwallet`、`LlmProviderIntegration` 对应 `llmproviderintegration`、`IdentityAccessManagement` 对应 `identityaccessmanagement`。
- 禁止新增未登记在限界上下文清单中的业务上下文目录；如工程包名存在缩写或历史命名差异，必须先更新限界上下文清单或补充映射说明，再新增对应数据库脚本目录。
- 平台级技术基础设施脚本放入 `backend/src/main/resources/db/changelog/releases/<version>/platform/`，仅用于 ID 生成、全局锁、迁移支撑表等跨上下文技术设施；禁止放入业务表、业务约束或业务历史数据修复。
- 不按数据库方言拆分目录；统一使用 PostgreSQL，变更脚本放在对应上下文目录下，文件名不携带方言后缀。
- 跨上下文数据修复不单独设置公共目录。能拆分时应拆到各自上下文脚本，由主入口控制执行顺序；不能拆分时放入主修改对象所属上下文，并在文件名或脚本注释中说明依赖的其他上下文。
- `in-progress/` 下的开发态脚本不要求最终编号稳定；每次新增或调整脚本时，必须同步折叠并更新 `releases/<version>/<context>/` 的目标态脚本。
- 已折叠进 `releases/` 的 `in-progress/` 脚本在发布前应保留用于过程审查；发布完成后应统一删除。确需留档时不得被任何 Liquibase 入口引用。
- 脚本文件名不携带版本号；统一使用 `<seq>-<intent>.yaml`，例如 `001-create-user-wallet.yaml`。`<seq>` 是当前版本目录内的发布顺序编号，建议使用三位数字。
- 首版全量策略：当系统处于首版上线且无需兼容历史增量迁移时，可直接维护“全量目标态脚本”（例如 `001-release-schema-mariadb.yaml`、`001-release-schema-dm.yaml`），将同一对象的多次变更折叠为最终 SQL 结果，避免保留仅用于过渡的中间 rename/alter 轨迹。
- `changeSet` 命名建议：`<yyyymmdd>-<seq>-<intent>`，例如 `20260505-002-create-admin-credentials`。
- 已发布的 `changeSet` 不可修改内容；修复通过新增 `changeSet` 完成。
- 统一使用 PostgreSQL，不维护多数据库方言分支，不需要按 `context` 隔离差异变更。
- `db.changelog-master.yaml` 必须显式 `include` 发布态脚本，禁止使用无序 `includeAll` 承载发布顺序；按真实依赖顺序排列。

## 3. 变更设计原则
- 变更脚本应保持可审计：一组逻辑变更对应一组明确 `changeSet`。
- 数据库变更同时包含受控的 DDL 与发布流程托管的 DML（如回填/修复）；禁止将日常业务写入混入变更脚本。
- 开发过程中每次新增或调整未发布、未执行的中间变更脚本时，必须同步整理，将 `in-progress/` 中需求或设计反复调整产生的临时 create/alter/rename/drop 轨迹折叠为 `releases/` 下的目标态 SQL。
- 只要某个 `changeSet` 已在共享稳定环境、发布回归环境、UAT、预发或生产执行过，就视为已发布处理，禁止移动文件路径、修改 `changeSet` 内容或合并删除；后续修复必须新增 forward-fix 脚本。
- 本地开发库、功能测试环境执行 `in-progress/` 仅用于需求验证，不纳入已发布冻结边界；该类环境必须可清库或重建，且不得作为正式发布路径验证证据。
- 数据修复脚本不得机械合并进 schema 脚本。若该脚本表达真实历史数据校正或发布过程需要审计的数据迁移，应保留为独立 `changeSet`。
- 高风险变更（删列、改类型、批量数据修复）必须提供前置校验与回滚/前滚补救方案。
- 生产故障默认采用 forward-fix（新增修复脚本）策略，避免修改历史脚本导致 checksum 漂移。
- `changeSet` 的 `changes` 节点必须统一使用 `- sql:` 声明变更语句（含必要方言 SQL）。
- 禁止在 `changes` 节点使用 `- createTable:`、`- addColumn:`、`- createIndex:` 等结构化 DSL 语法，避免双库语义漂移与方言细节丢失。
- Liquibase 脚本禁止编写 `rollback:` 节点；回滚/补救方案应在发布预案中说明，落库修复默认通过新增 forward-fix `changeSet` 完成。
- Liquibase SQL 禁止创建 `CHECK` 约束，包括 `CREATE TABLE` 内联 `CHECK` 与 `ALTER TABLE ... ADD CONSTRAINT ... CHECK`。字典值、状态流转、数值范围、非空文本、一致性校验等规则由领域模型、应用服务、触发层校验或字典配置承载，避免字典扩展或规则调整时必须修改历史数据库脚本。

## 4. 既有环境基线策略
- **空库/新库**：按常规 Liquibase 启动流程执行。
- **已存在兼容结构的存量库**：首轮发布按受控流程执行 `changelog-sync`，并留存执行人与验证证据。
- **结构不兼容**：必须中止发布，先修复结构差异再继续。

## 5. 运行与锁处理
- 迁移失败应阻断应用就绪，禁止“带错误继续启动”。
- 涉及 `DATABASECHANGELOGLOCK` 处理时，需先确认锁为陈旧锁，再执行解锁。
- 解锁与基线标记操作需有明确责任人和审计记录。

## 6. 安全与凭据要求
- 迁移账号与运行时账号应遵循最小权限原则（迁移具备必要 DDL 权限，运行时账号最小化）。
- 数据库凭据与加密密钥仅通过环境变量或密钥托管注入，不写入仓库。
- 故障日志必须避免泄露明文密码、密码哈希、完整连接密钥等敏感信息。

## 7. 验证要求
- 每次迁移变更需至少验证：
  - Liquibase 元数据表可用（`DATABASECHANGELOG` / `DATABASECHANGELOGLOCK`）。
  - 目标业务表结构满足读写契约。
  - 关键业务路径在迁移后无行为回退。
- 每次迁移需保留目标环境的迁移验证证据。

## 8. 写法示例（必须遵循）
- 正确示例（使用 `- sql:`）：

```yaml
databaseChangeLog:
  - changeSet:
      id: 20260506-001-create-user-wallet
      author: system
      changes:
        - sql: |
            CREATE TABLE user_wallet (
              id BIGINT PRIMARY KEY,
              user_id BIGINT NOT NULL,
              balance BIGINT NOT NULL DEFAULT 0
            );
```

- 错误示例（禁止使用结构化 DSL）：

```yaml
databaseChangeLog:
  - changeSet:
      id: 20260506-002-create-user-wallet
      author: system
      changes:
        - createTable:
            tableName: user_wallet
            columns:
              - column:
                  name: id
                  type: BIGINT
                  constraints:
                    primaryKey: true
```

- 错误示例（禁止 `rollback:` 与 `CHECK`）：

```yaml
databaseChangeLog:
  - changeSet:
      id: 20260506-003-add-user-status
      author: system
      changes:
        - sql:
            sql: |
              ALTER TABLE user
              ADD CONSTRAINT ck_user_status
              CHECK (status IN ('VALID', 'INVALID'))
      rollback:
        - sql:
            sql: |
              ALTER TABLE user
              DROP CONSTRAINT ck_user_status
```
