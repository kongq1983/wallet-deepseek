# AGENTS.md

## 1. 项目概述

AI 研发平台是一个以 AI 驱动软件研发全流程的内部平台，覆盖需求分析、架构设计、代码研发、测试、发布部署，在关键节点由人审批。平台通过接入主流 Coding Agent（Codex 等）执行研发任务，沉淀领域知识让 AI 越用越准。
本项目采用 Domain-Driven Design (DDD) 分层架构，后端技术栈基于 Java 17 + Spring Boot 3，前端技术栈基于 Vite + React + TypeScript + Tailwind CSS + shadcn/ui，采用 monorepo 结构管理，根目录固定为 `backend/` 与 `frontend/`。

## 2. 快速命令

| 场景     | 命令                                                  |
| -------- | ----------------------------------------------------- |
| 后端构建 | `./mvnw -f backend/pom.xml clean package -DskipTests` |
| 后端启动 | `./mvnw -f backend/pom.xml spring-boot:run`           |
| 后端测试 | `./mvnw -f backend/pom.xml test`                      |
| 前端构建 | `cd frontend && pnpm build`                           |
| 前端启动 | `cd frontend && pnpm dev`                             |
| 前端测试 | `cd frontend && pnpm test`                            |

## 3. 交互要求

1. 你在处理所有问题时，全程思考过程必须使用中文（包括需求分析、逻辑拆解、方案选择、步骤推导等所有内部推理环节）；
2. 最终输出的所有回答内容（包括文字解释、代码注释、步骤说明等）必须全部使用中文，仅代码语法本身的英文关键词除外。

## 4. 外部文件加载

极其重要：当遇到文件引用（例如 @rules/general.md）时，请使用 Read 工具按需加载。这些文件与当前的具体任务密切相关。
Instructions:

- 请勿提前加载所有引用--根据实际需求使用懒加载（lazy loading）
- 加载后，请将其内容视为必须遵守的指令，且该指令将覆盖默认规则
- 必要时，请递归追踪并加载引用文件

## 5. 开发指南

关于后端架构规范：@docs/rules/backend-architecture.md
关于前端架构规范：@docs/rules/frontend-architecture.md
关于数据库变更治理规范：@docs/rules/backend-database-change-governance.md
关于数据库建模与访问规范：@docs/rules/backend-database-modeling-and-access.md
关于后端ID生成规范：@docs/rules/backend-id-generation.md
关于后端租户上下文规范：@docs/rules/backend-tenant-context.md
关于数据库选型规范：@docs/rules/backend-database-multi-db-compatibility.md
关于领域事件开发规范：@docs/rules/backend-domain-event.md
关于异常处理规范：@docs/rules/backend-exception-handling.md
关于后端API设计规范：@docs/rules/backend-api-design.md
关于后端安全规范：@docs/rules/backend-security.md
关于后端日志与可观测性规范：@docs/rules/backend-log-observability.md
关于后端代码风格规范：@docs/rules/backend-code-style.md
关于后端规约模式规范：@docs/rules/backend-specification-pattern.md
关于前端代码风格规范：@docs/rules/frontend-code-style.md
关于前端 UI 样式规范：@docs/rules/frontend-ui-style.md
关于前端统一设计系统：@DESIGN.md
新增或修改前端页面、组件、样式、视觉资产时，必须先读取并遵循前端统一设计系统。
关于框架参考文档：
| 文档分类 | 文档网址 | 说明 |
|------------|------|-----------------------------------|
| SpringBoot | https://docs.spring.io/spring-boot/3.3/index.html | Java 快速开发框架 |
| SA-Token | https://sa-token.cc/doc.html#/ | 开源、免费、一站式 java 权限认证框架，让鉴权变得简单、优雅！ |
| MyBatis-Plus | https://baomidou.com/introduce/ | MyBatis 的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生 |
