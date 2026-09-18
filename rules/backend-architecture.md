# 后端架构规范

## 1. 适用范围
- 适用于 `backend/` 下所有 Java 代码。
- 后端架构必须遵循 DDD + CQRS 分层模型。

## 2. 目录与分层

### 2.1 目录结构
```text
backend/src/main/java/com/hzsun/aidevops/
├── bootstrap/               # [启动与装配] Spring Boot 启动类、全局配置装配
├── common/                  # [通用横切] 通用模型、异常、工具类、配置常量、扩展点契约与适配框架
├── api/                     # [HTTP触发层] 对外暴露 HTTP 接口，按端侧分类
│   ├── user/                #   用户端 Controller，路径前缀统一为 /api/user
│   └── admin/               #   管理端 Controller，路径前缀统一为 /api/admin
└── <bounded_context>/       # [限界上下文] 按业务边界划分（示例：order）
    └── <aggregate>/         # [聚合根] 聚合内的唯一入口和生命周期管理
        ├── cmdservice/      # [命令端] 负责写操作（CQRS-C）
        │   ├── cmd/         #   入参命令对象：XxxCmd
        │   ├── dto/         #   出参传输对象：XxxDto
        │   ├── assembler/   #   XxxCmdAssembler，MapStruct转换器：聚合根转化为Dto、Cmd转化为实体和值对象
        │   └── XxxCmdService.java    #   命令服务，进行对业务的编排
        ├── qryservice/      # [查询端] 负责读操作（CQRS-Q）
        │   ├── qry/         #   入参查询对象：XxxQry
        │   ├── dto/         #   出参传输对象：XxxDto
        │   ├── assembler/   #   XxxQryAssembler，MapStruct转换器：Po持久化对象转化为Dto
        │   └── XxxQryService.java    #   查询服务
        ├── domain/          # [领域层] 纯内存逻辑，不依赖任何外部框架
        │   ├── event/       #   领域事件定义
        │   ├── specification/ # 领域规约（业务规则)
        │   ├── strategy/    #   策略模式接口与实现
        │   ├── valueobject/ #   值对象
        │   ├── Xxx.java     #   实体/聚合根
        │   ├── XxxDomainService.java # 领域服务
        │   ├── XxxFactory.java      # 聚合根业务创建工厂
        │   └── XxxRepository.java   # 仓储接口（仅接口）
        ├── infrastructure/ # [基础设施层] 技术实现细节
        │   ├── mapper/      #   MyBatis-Plus Mapper 接口
        │   ├── convertor/   #   XxxConvertor，MapStruct转换器：Po持久化对象 与 领域对象互转
        │   ├── repository/  #   数据库仓储接口实现
        │   └── po/          #   持久化对象：XxxPo
        ├── eventlistener/   # [领域事件监听] 异步领域事件处理
        └── XxxJob.java     # 定时任务
```

### 2.2 限界上下文清单
| 上下文英文标识 | 中文名称 |
| --- | --- |
| `RequirementEngineering` | 需求工程 |
| `ArchitectureDesign` | 架构设计 |
| `CodeDevelopment` | 代码研发 |
| `QualityAssurance` | 质量保证 |
| `ReleaseDeployment` | 发布部署 |
| `DevWorkflow` | 研发流程编排 |
| `DomainKnowledge` | 领域知识管理 |
| `AgentProvider` | Agent 供应 |
| `ToolIntegration` | 工具集成 |
| `IdentityAccessManagement` | 身份与访问 |
| `SystemConfiguration` | 系统配置 |
| `TenantManagement` | 租户管理 |

- 新增后端业务代码时，必须先归属到上述限界上下文之一，再在该上下文内按聚合拆分目录；禁止创建跨上下文的混合目录。

### 2.3 分层职责
- `Controller` 统一放在 `com.hzsun.aidevops.api.user` 或 `com.hzsun.aidevops.api.admin` 包下，只负责协议转换、参数校验与鉴权上下文读取。
- 用户端 `Controller` 必须放在 `api/user/` 目录下，HTTP 路径前缀必须以 `/api/user` 开头。
- 管理端 `Controller` 必须放在 `api/admin/` 目录下，HTTP 路径前缀必须以 `/api/admin` 开头。
- `Controller` 可以按业务语义调用对应限界上下文聚合内的 `cmdservice`/`qryservice`，但不得承载限界上下文目录结构。
- `eventlistener` 只负责事件订阅、事件去重/幂等校验、事件到 Command/Query 的转换与应用层调用。
- `XxxJob` 只负责调度触发、运行窗口计算、幂等键生成、Command/Query 构造与应用层调用。
- `cmdservice` 只处理写流程编排、事务边界与命令执行。
- `qryservice` 只处理读模型查询与查询结果组装。
- `domain` 只承载业务规则、实体、值对象、领域服务与规约，不依赖 Spring 或持久化框架。
- 领域业务规则、业务准入规则、状态流转前置规则必须使用 `domain/specification/` 下的规约表达；聚合根与领域服务只能调用规约并执行业务行为，禁止内联业务判断。
- `domain` 内不得使用 Spring 工具类或 stereotype 注解；通用工具类优先采用 Hutool，例如字符串判空使用 `CharSequenceUtil.isNotBlank/isBlank`，集合判空使用 `CollUtil.isNotEmpty/isEmpty`。
- 聚合根业务新建必须统一通过领域层 `XxxFactory` 完成；聚合根重建或还原必须统一通过 `XxxRepository` 获取，禁止应用服务、领域服务或其他调用方直接 `new Xxx(...)` 实例化聚合根，也禁止绕过仓储自行调用聚合根还原方法。
- 领域对象创建过程中的内部 ID 生成必须遵循 `@docs/rules/backend-id-generation.md`，领域层只能依赖 `IdGenerator` 接口，不得直接依赖具体生成器实现。
- `infrastructure` 只承载数据库仓储实现、Mapper、PO 与外部系统适配；生产仓储实现必须通过 Mapper/PO 访问数据库，禁止用内存集合、静态变量或缓存替代数据库持久化。

## 3. CQRS 与对象模型约束
- 写操作必须通过 Command 模型进入应用层。
- Command 执行链路必须经过领域层（聚合根/领域服务/领域工厂），禁止 Command 直接落到 Mapper。
- 读操作必须通过 Query 模型进入查询层。
- Query 查询链路由 `qryservice` 调用 MyBatis Mapper 接口直接查询，禁止读流程绕行领域层执行业务编排。
- 写侧 `cmdservice` 依赖领域层 `XxxRepository` 接口时，其生产实现必须位于 `infrastructure/repository`，并通过 MyBatis-Plus Mapper 与 PO 完成数据库读写。
- 内存仓储实现仅允许作为单元测试替身放在 `backend/src/test/`，禁止放入 `backend/src/main/`，也禁止被生产 Spring Bean 扫描或配置选中。
- Dto、Do、Po、领域对象必须分离，禁止跨层复用同一对象。
- Controller 类的入参必须使用入参命令对象 `XxxCmd` 或入参查询对象 `XxxQry`，出参返回对象必须使用 `XxxDto`。
- `Cmd`、`Dto`、`Po`、领域对象等模型默认使用 Lombok 注解减少样板代码，禁止手写重复的 getter/setter/构造器（存在明确业务语义的方法除外）。
- 对象转换必须集中在 Assembler/Convertor，且必须使用 MapStruct 实现，禁止在 Controller 或 Domain 中散落字段拷贝逻辑。
- 系统内部 ID 的类型、生成方式与 API 表达必须遵循 `@docs/rules/backend-id-generation.md`。
- 领域模型中凡是货币语义字段（如金额、余额、手续费、单价）必须使用 `Money` 值对象，禁止在 `Domain` 中直接使用 `BigDecimal`/`Long` 承载货币语义。
- `Cmd`/`Dto`/`Po` 可使用基础数值类型或字符串做传输/持久化，但进入领域层前必须在 `Assembler`/`Convertor` 完成与 `Money` 的双向转换。

## 4. 聚合与事务约束
- 聚合一致性规则必须在聚合内完成，禁止跨聚合直接写。
- 聚合一致性规则涉及业务判断时，必须先沉淀为规约，再由聚合根或领域服务调用后执行状态变更。
- 事务边界必须定义在应用层，禁止在 Mapper 或 Convertor 开启事务。
- 使用 `@Transactional` 时必须显式声明 `rollbackFor = Exception.class`，禁止仅依赖默认运行时异常回滚策略。
- 涉及补偿或重试的流程必须具备幂等键。

## 5. 领域工厂约束
- 工厂类（`XxxFactory`）只负责聚合根的业务创建，不得承担持久化还原、数据库读写、领域事件发布或跨聚合编排；聚合根重建或还原统一由 `XxxRepository` 完成。
- 工厂类必须使用 `final` 修饰，并显式声明 `private` 无参构造函数，禁止实例化。
- 工厂类不得注册为 Spring Bean，不得添加 `@Component`/`@Service` 等构造型注解，也不得定义任何实例字段或可变静态字段；工厂是无状态的。
- 创建聚合根的方法必须是 `public static`，命名统一为 `create`；同一聚合存在多种业务创建场景时可用 `createXxx` 区分。
- 创建方法必须通过参数接收 `IdGenerator` 并在方法内生成聚合根自身 ID，ID 生成细则遵循 `@docs/rules/backend-id-generation.md`。
- 调用方必须以 `XxxFactory.create(...)` 静态方式调用，禁止通过依赖注入获取工厂实例后再调用。
- 类注释必须说明“以静态方法提供业务创建入口，不实例化”。

## 6. 事件、任务与扩展点
- 领域事件定义放在 `domain/event/`，监听器放在 `eventlistener/`。
- 定时任务统一放在聚合根目录下的 `XxxJob.java`。
- HTTP 触发入口统一放在 `api/user/` 或 `api/admin/`，禁止在限界上下文或聚合目录下新增 `XxxController.java`。
- `Controller`、`eventlistener`、`XxxJob` 都属于触发层适配器，只能做入口适配、上下文提取、输入校验、幂等防重、命令/查询对象构造与应用层委派。
- 监听器处理领域事件时，必须把后续写操作封装为 `XxxCmd` 并调用 `cmdservice`；读取展示或判定所需数据时，必须封装为 `XxxQry` 并调用 `qryservice`。
- 定时任务执行周期性流程时，必须把每个业务动作委派给 `cmdservice` 或 `qryservice`；调度表达式、分页游标、批次大小、执行窗口和幂等键可以留在任务类中，业务规则和状态变更不得留在任务类中。
- 监听器中的“同步逻辑”不得直接写在监听器方法内；若同步代表业务状态推进、跨聚合写入、外部系统回调后的补偿或重试，必须沉淀到应用层编排，并通过领域模型完成业务规则校验。
- 新增扩展点必须先复用现有聚合能力，禁止重复建模同类能力。

## 7. 架构禁令
- 禁止在 Controller 编写核心业务规则。
- 禁止用内联 `if`/`else`、私有校验方法、通用 `Validator`/`Checker` 类替代领域业务规约。
- 禁止在 `eventlistener` 或 `XxxJob` 编写核心业务规则、状态流转、跨聚合写入、同步/补偿编排或直接持久化逻辑。
- 禁止在触发层直接调用 Mapper、Repository 实现、外部系统 SDK 完成业务动作；触发层必须先委派 `cmdservice`/`qryservice`。
- 禁止在 Domain 依赖数据库 Mapper、HTTP 客户端或配置中心 SDK。
- 禁止绕过仓储直接操作持久化对象。
- 禁止在生产代码中新增 `InMemoryXxxRepository`、基于 `Map/List/static` 的仓储实现，或把缓存当作事实数据源。

## 8. 自检清单
- 分层职责是否清晰且无越层调用。
- 聚合边界是否清晰且无跨聚合写操作。
- 事务、幂等与补偿策略是否完整。
- `@Transactional` 是否显式声明 `rollbackFor = Exception.class`。
- 生产仓储实现是否通过 Mapper/PO 持久化到数据库，且不存在被生产环境加载的内存仓储实现。
- 聚合根业务新建是否统一通过 `XxxFactory` 完成；聚合根重建或还原是否统一通过 `XxxRepository` 获取，且调用方不存在直接 `new Xxx(...)` 或自行调用还原方法。
- `XxxFactory` 是否为 `final` 且含私有构造、创建方法是否为 `public static`、是否未注册为 Spring Bean。
- 事件监听器是否只完成事件适配与应用层委派，未内嵌同步业务逻辑。
- 定时任务是否只完成调度适配与应用层委派，未内嵌任务业务逻辑。
