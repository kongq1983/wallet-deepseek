# 后端领域事件组件规范

## 1. 适用范围
- 适用于 `backend/` 下所有需要跨聚合、跨流程异步解耦的业务场景。
- 同一服务内的领域事件发布与消费，统一使用 `hzsun-common-starter-domain-event` 组件。

## 2. 集成基线
- 依赖引入：必须引入 `com.hzsun:hzsun-common-starter-domain-event`，版本与团队基线保持一致。
- 数据库初始化：必须先执行组件提供的初始化 SQL（PostgreSQL）。
- 初始化后应存在以下核心表：`de_domain_event`（事件主表）、`de_consume_record`（消费记录表）。

## 3. 事件定义规范
- 领域事件类必须继承 `DomainEvent`。
- 领域事件类必须提供无参构造器（推荐使用 `@NoArgsConstructor`），用于 Jackson 反序列化。
- 事件有参构造器必须调用 `super(tenantId)` 传入租户编号。
- 事件属性包含对象时，对象类型也必须具备无参构造器。
- 事件属性若使用 `isXxx` 布尔字段，必须提供 `setXxx()` 以支持反序列化。

## 4. 事件发送规范
- 领域事件必须在聚合根的业务行为方法中使用 `com.hzsun.common.starter.domain.event.domain.produce.DomainEventProducer.send(...)` 发送，禁止自建并行消息发布通道。
- `DomainEventProducer.send(...)` 必须运行在 Spring 事务环境中，确保事务提交后再触发发布流程。
- 事件发送必须内聚在领域行为方法中（如 `UserCreatedEvent` 必须在 `User` 的创建行为中触发），严禁在聚合根外发送，严禁额外暴露 `sendXxxEvent` 这类“仅发送事件”的方法。
- 应用服务仅负责编排并调用聚合根，禁止在应用服务或 Controller 层直接发送业务事件。

## 5. 监听器开发规范
- 监听器必须实现 `DomainEventListener<T>` 并显式声明事件泛型。
- 监听器类名必须使用 `{动作名}On{事件名}EventListener` 格式，表达“发生什么事件时要做什么业务动作”。
- `{动作名}` 必须是业务动作且以动词开头，如 `Create`、`Sync`、`Grant`、`Freeze`、`Notify`、`Retry`，禁止使用 `Handle`、`Process` 等泛化动作名。
- `{事件名}` 使用被监听事件类名去掉 `Event` 后缀后的名称，避免出现 `EventEventListener`。
- 监听器命名应保持动作名短、准、业务化，只描述核心业务结果，禁止把命令方法名、技术过程或实现细节写进类名。
- 监听器必须标注为 Spring Bean（如 `@Component`），保证被容器扫描与注册。
- 消费逻辑失败时必须抛出异常，组件才会将该次消费标记为失败并触发后续重试。
- 禁止吞异常后返回成功，否则会导致事件被误判为消费完成。
- 监听器需确保由 Spring CGLIB 代理（例如涉及 `@Async` 时避免仅使用 JDK 代理导致监听失效）。

命名示例：
- `CreatePersonalPointWalletOnPlatformUserCreatedEventListener`
- `CreateProviderApiKeyOnPointWalletCreationInitiatedEventListener`
- `SyncProjectMembersOnResearchProjectCreatedEventListener`
- `GrantPointsOnPaymentSucceededEventListener`

## 6. 重试与清理规范
- 必须提供定时任务调用 `DomainEventService.republishIncompleteEvents()`，重发未完成事件。
- 必须提供定时任务调用 `DomainEventService.clearCompletedEvents()`，清理已完成且满足保留策略的历史事件。
- 组件默认策略为：重发近 7 天且发布次数不超过 3 次的未完成事件；清理 7 天前全部消费者成功的事件及其消费记录。
- 定时表达式可按业务调整，但必须保证“重试 + 清理”两个任务长期稳定执行。

## 7. 自定义 ID 生成器规范
- 默认 ID 策略为雪花算法；如业务需要可实现 `IdGenerator` 接口自定义生成规则。
- 自定义实现必须通过 Spring Bean 暴露 `IdGenerator`，由组件自动接管。
- 自定义 ID 必须满足全局唯一性、并发安全和可追踪性，不得引入重复 ID 风险。

## 8. 验证与观测要求
- 联调阶段至少验证一次完整链路：事件入库、监听消费、消费记录写入、失败重试、最终清理。
- 重点核查字段：`event_status`、`publish_count`、`consume_success_count`、`consume_fail_count`。
- 发布前需确认消费者幂等策略有效，避免重试场景下产生重复业务副作用。
- 评审时必须检查监听器类名是否同时表达被监听事件与监听后的业务动作。

## 9. 禁止行为
- 禁止绕过组件直接操作 `de_domain_event` 与 `de_consume_record` 作为业务逻辑主路径。
- 禁止在无事务上下文中发送领域事件。
- 禁止将“必须同步完成”的强一致流程误用为领域事件异步处理。

## 10. 参考资料
- 组件介绍：`https://dochub.hzlinks.net/public/common/hzsun-common/domain-event/introduce.html`
- 快速开始：`https://dochub.hzlinks.net/public/common/hzsun-common/domain-event/quick-start.html`
- 自定义 ID 生成器：`https://dochub.hzlinks.net/public/common/hzsun-common/domain-event/custom-id-generator.html`
