---
name: db-release-consolidation
description: 整理发版前未发布的 Liquibase 数据库变更脚本，将 in-progress 开发态 SQL 与需求或设计反复调整产生的中间 SQL 折叠为最终目标态 changelog。Use when 用户提到“发版前数据库脚本合并”“合并中间 SQL”“折叠 changelog”“未发版 Liquibase 脚本整理”“数据库脚本目标态整理”“开发中 SQL 统一目录”。
---

# DB Release Consolidation

用于在版本发布前整理未发布的 Liquibase 脚本，把开发过程中的中间变更收敛为可审计、可发布的最终 SQL 变更记录。

## Hard Gate

- 必须先确认脚本是否已在共享环境、演示环境、测试库或长期本地库执行过。
- 只要任一环境已执行过，禁止移动或改写既有 `changeSet`，只能新增 forward-fix 脚本。
- 仅当用户明确说明“未发版、可不管历史数据/执行记录”时，才允许重排文件、合并中间脚本、改写未发布 `changeSet`。
- 执行前必须读取并遵循 `docs/rules/backend-database-change-governance.md`；涉及多数据库时还要读取多数据库兼容规范。
- 执行前必须读取 `docs/strategic/bounded-context-list.md`，业务脚本归属只能参考该清单中的平台内限界上下文。

## Quick Start

1. 盘点 `backend/src/main/resources/db/changelog/db.changelog-master.yaml`、`in-progress/` 与 `releases/<yyyy-mm-dd>/` 下所有脚本。
2. 按脚本涉及的表与 `docs/strategic/bounded-context-list.md` 判断归属上下文。
3. 在未发版前提下，把同一对象的 create/alter/rename/drop 中间过程折叠成最终目标态 SQL。
4. 按 `releases/<yyyy-mm-dd>/<context>/` 或 `releases/<yyyy-mm-dd>/platform/` 重排脚本，并更新 master 显式 include 顺序。
5. 校验每个数据库方言的执行链路、上下文归属、回滚或前滚说明。

## Directory Rules

- 开发态 SQL 放入 `in-progress/<context>/<feature>/`，用于保存需求开发过程中的临时 create/alter/rename/drop 轨迹。
- `in-progress/` 不进入 `db.changelog-master.yaml`；如需本地执行，只能通过开发专用入口或一次性可重建开发库执行。
- 整理后的发布态脚本按整合当天日期放入 `releases/<yyyy-mm-dd>/`，不要再按月份目录归档。
- 业务上下文脚本放入 `releases/<yyyy-mm-dd>/<context>/`。
- 限界上下文权威清单为 `docs/strategic/bounded-context-list.md`；业务脚本只能归属到该清单列出的平台内限界上下文。
- `<context>` 目录名必须由限界上下文清单中的英文名称派生，统一使用小写连续命名，例如 `PointWallet` 对应 `pointwallet`、`LlmProviderIntegration` 对应 `llmproviderintegration`、`IdentityAccessManagement` 对应 `identityaccessmanagement`。
- 禁止新增未登记在限界上下文清单中的业务上下文目录；如工程包名存在缩写或历史命名差异，必须先更新限界上下文清单或补充映射说明。
- `platform/` 只放跨上下文技术基础设施，例如 ID 生成、全局锁、迁移支撑表；不得放业务表或业务历史数据修复。
- 不创建 `crosscontext/`。跨上下文数据脚本能拆则拆到各自上下文；不能拆时放到主修改对象所属上下文，并在文件名或脚本注释说明依赖的其他上下文。
- 多数据库脚本不按方言分目录；同一业务变更的 `*-mariadb.yaml` 与 `*-dm.yaml` 放在同一上下文目录，通过文件名后缀与 `context: mariadb` / `context: dm` 区分。
- 脚本文件名不再携带日期；统一使用 `<seq>-<intent>-<dialect>.yaml`，例如 `001-create-user-table-mariadb.yaml`、`001-create-user-table-dm.yaml`。
- `<seq>` 是当前日期目录内的发布顺序编号，建议使用三位数字；同一逻辑变更的 MariaDB 与达梦脚本可使用相同编号并通过方言后缀区分。

## Consolidation Rules

- 未发布 schema 变更可以折叠为最终目标态：删除临时列、临时表、中间 rename、反复调整的约束与索引轨迹。
- 发版整合时优先从 `in-progress/` 收敛到 `releases/<yyyy-mm-dd>/`，`releases/` 只保留准备发布的目标态脚本。
- 已折叠进 `releases/` 的 `in-progress/` 脚本应删除；确需留档时不得被任何 Liquibase 入口引用。
- 已发布或可能已执行的 `changeSet` 不可折叠，不可改路径，不可改 SQL 内容。
- 数据修复脚本不要机械合并进 schema 脚本；若它表达真实历史数据校正，应保留为独立 `changeSet`。
- 仅当数据脚本只是未发布开发过程的临时过渡，且最终发布不需要审计该过程时，才可折叠或删除。
- 同时修改多个上下文的数据变更优先拆分；无法拆分时归到主业务目标上下文，并明确依赖顺序。
- `changes` 节点必须使用 `- sql:`，不得改成 Liquibase 结构化 DSL。

## Master Ordering

- `db.changelog-master.yaml` 继续使用显式 `include`，不要改成无序 `includeAll`。
- `db.changelog-master.yaml` 只 include `releases/` 下的发布态脚本，不 include `in-progress/`。
- 推荐按数据库方言分批：先列出 `mariadb` 执行链，再列出 `dm` 执行链。
- 每个方言内部按基础设施、上下文 schema、数据修复、后续依赖变更的真实依赖顺序排列。
- `platform` 中的 ID 生成等基础设施应早于依赖它的业务表或数据脚本。

## Review Checklist

- [ ] 已确认发版状态，未误改已执行 `changeSet`。
- [ ] 已读取数据库变更治理规范和必要的多数据库规范。
- [ ] 已读取限界上下文清单，业务脚本目录均来自清单中的英文名称。
- [ ] 开发态脚本均集中在 `in-progress/<context>/<feature>/`，未被 master include。
- [ ] 目录为 `releases/<yyyy-mm-dd>/<context>/` 或 `releases/<yyyy-mm-dd>/platform/`，日期为本次整合当天。
- [ ] 文件名不含日期，格式为 `<seq>-<intent>-<dialect>.yaml`。
- [ ] 中间 SQL 已折叠为最终目标态，仍需审计的数据修复保留独立记录。
- [ ] MariaDB 与达梦脚本同上下文放置，文件名和 `context` 清晰。
- [ ] master include 显式、有序，能解释每个跨上下文依赖。
