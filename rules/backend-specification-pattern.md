# 后端规约模式规范

## 1. 适用范围
- 适用于 `backend/` 下所有领域业务规则、业务准入规则、状态流转前置规则的 Java 代码。
- 业务规则必须使用规约模式表达，禁止把领域业务判断直接散落在聚合根、领域服务、应用服务、触发层或基础设施层的 `if`/`else` 中。
- 规约只承载领域规则判断，默认放在对应聚合的 `domain/specification/` 包下。
- 非业务性的空值防御、格式校验、接口入参约束不属于领域业务规则，不应强行封装为领域规约。

## 2. 集成基线
- 新增公共规约能力时，优先使用 `com.hzsun:hzsun-common-starter-specification-pattern` 组件，版本与团队公共组件基线保持一致。
- 组件核心类型位于 `com.hzsun.common.starter.specification.pattern` 包：
  - `Specification<T>`：规约接口。
  - `CompositeSpecification<T>`：组合规约抽象类。
  - `ValidationResult`：校验结果。
  - `ValidationStrategy`：校验策略，包含 `FAIL_FAST` 与 `ALL_ERRORS`。
- 若当前模块尚未引入组件，不得在业务包内复制一套并行规约框架；应先评估并引入公共组件。

依赖示例：

```xml
<dependency>
    <groupId>com.hzsun</groupId>
    <artifactId>hzsun-common-starter-specification-pattern</artifactId>
    <version>${hzsun.common.version}</version>
</dependency>
```

## 3. 使用边界
- 适合使用规约的场景：
  - 领域业务规则、业务准入规则、状态流转前置规则。
  - 同一业务规则会被多个领域行为、应用服务或测试复用。
  - 多个业务规则需要通过 `and`、`or`、`not` 组合表达。
  - 校验失败原因需要稳定返回给应用层，再转换为业务异常或接口错误。
- 不适合使用规约的场景：
  - Controller 入参格式校验、必填校验、长度校验等边界校验；这类规则优先使用 Bean Validation 或触发层显式校验。
  - 查询条件拼装、数据库过滤、MyBatis 条件构造；规约不得替代 Mapper 查询条件。
  - 跨聚合编排、事务控制、远程调用、数据库读取；这些职责属于应用层或基础设施层。

## 4. 目录与命名
- 规约类必须放在对应聚合的 `domain/specification/` 包下，例如：

```text
backend/src/main/java/com/hzsun/aidevops/<bounded_context>/<aggregate>/domain/specification/
```

- 类名必须使用 `{业务规则}Specification`，业务规则要表达被校验的领域事实或业务准入能力。
- 原子规约类名必须优先使用以下句式：
  - `{业务主体}Must{业务约束}Specification`，例如 `RechargeAmountMustBePositiveSpecification`。
  - `{业务主体}MustNot{禁止条件}Specification`，例如 `RechargeAmountMustNotExceedLimitSpecification`。
- 组合规约类名必须优先使用 `{业务主体}Can{业务动作}Specification`，例如 `WalletCanBeRechargedSpecification`；只有业务语言中已有明确“允许/准入”概念时，才允许使用 `{业务动作}AllowedSpecification`。
- `Must` / `MustNot` 句式默认只表达一个原子业务事实；`Can` 句式可以组合多个原子规约，但类名必须表达组合后的业务能力。
- 类名中的业务主体按以下优先级选择：
  - 优先使用被校验对象的业务名，例如 `RechargeAmountMustBePositiveSpecification`。
  - 校验聚合状态或聚合行为准入时，使用聚合根名，例如 `WalletCanBeRechargedSpecification`。
  - 需要多个事实参与校验时，先封装有业务语义的值对象、实体或校验上下文对象，再使用对应业务名；禁止使用 `Context`、`Param`、`Request` 等技术容器名充当业务主体。
- 规约类名不得窄于实际校验内容；若实现同时判断多个事实，应拆成原子规约，或命名为明确的组合业务能力规约。
- 允许使用领域集合主体，例如 `RechargeItemsMustUseSameCurrencySpecification`；禁止使用 `List`、`Map`、`Collection` 等技术集合容器作为类名主体。
- 禁止使用 `Check*`、`Validate*`、`Verify*`、`Common*`、`Base*`、`Default*`、`General*`、`*Spec`、`*Validator`、`*Checker`、`*Rule` 等泛化或职责混淆命名；`Abstract*` 仅在确实定义抽象基类时使用。
- 组合后的规约变量名必须表达业务语义，例如 `rechargeAllowedSpec`、`passwordPolicySpec`，禁止使用 `spec1`、`finalSpec`。
- 同一聚合内的规约只服务该聚合语义；跨聚合复用前必须先确认该规则是否真的是共享内核规则。
- 同一聚合下的规约默认不按原子规约、组合规约拆分子包；只有规约数量和职责复杂到影响定位时，才评估拆分子包并同步更新本规范。

## 5. 规约实现
- 自定义规约类应继承 `CompositeSpecification<T>` 并实现 `validate(T value)`。
- `T` 必须是单一校验对象类型；组件不支持多个入参。需要多个事实参与校验时，必须封装为有业务语义的值对象、实体或校验上下文对象，禁止使用 `Map`、数组或无语义元组凑参数。
- `validate` 必须返回 `ValidationResult.valid()` 或 `ValidationResult.invalid("错误信息")`，禁止在规约内部直接抛出 `BusinessException`。
- 规约必须是纯内存判断，不得依赖 Spring Bean、Mapper、Repository 实现、HTTP 客户端、配置中心或当前登录上下文。
- 规约不应修改被校验对象状态，不应发送领域事件，不应产生审计日志、数据库写入等副作用。

示例：

```java
import com.hzsun.common.starter.specification.pattern.CompositeSpecification;
import com.hzsun.common.starter.specification.pattern.ValidationResult;
import com.hzsun.aidevops.common.domainshared.Money;

/**
 * 充值金额必须大于零的领域规约。
 */
public class RechargeAmountMustBePositiveSpecification extends CompositeSpecification<Money> {

    @Override
    public ValidationResult validate(Money amount) {
        if (amount == null || amount.equalZero()) {
            return ValidationResult.invalid("充值金额必须大于0");
        }
        return ValidationResult.valid();
    }
}
```

## 6. 规约组合
- 同一组合链中的规约泛型必须一致，否则不得组合。
- 默认使用 `ValidationStrategy.FAIL_FAST`，用于命令处理中的领域规则校验。
- 只有在前端需要一次性展示全部错误、批量导入需要返回多条错误、或测试明确覆盖多错误场景时，才使用 `ValidationStrategy.ALL_ERRORS`。
- 使用 `ALL_ERRORS` 时，调用方必须读取 `ValidationResult.getErrors()`；默认 `getError()` 只表达单条错误。

示例：

```java
Specification<Money> rechargeAmountSpec = new RechargeAmountMustBePositiveSpecification()
    .and(new RechargeAmountMustNotExceedLimitSpecification(limit));

ValidationResult result = rechargeAmountSpec.validate(amount);
```

## 7. 调用与异常转换
- 聚合根或领域服务可以直接调用规约，并根据 `ValidationResult` 决定是否继续执行业务行为。
- 应用层调用规约时，只能用于进入领域行为前的业务前置判断；不得绕过聚合根，把规约校验成功当作状态变更已经完成。
- 调用规约时应在使用点通过 `new XxxSpecification()` 创建实例，禁止在聚合根、领域服务、应用服务中定义 `private static final XxxSpecification` 单例字段。
- 规约失败转换为项目业务异常时，应在调用方完成，错误码必须使用 `ErrorCodes` 中的明确业务错误码。

示例：

```java
ValidationResult result = new RechargeAmountMustBePositiveSpecification().validate(amount);
if (!result.isValid()) {
    throw new BusinessException(ErrorCodes.WALLET_INVALID_RECHARGE_AMOUNT, result.getError());
}
```

## 8. 测试要求
- 每条新增领域业务规则必须有对应规约测试；每个新增规约至少覆盖通过与失败两个分支。
- 组合规约必须覆盖组合语义，例如 `and` 任一失败即失败、`or` 任一通过即通过、`not` 反转结果。
- 使用 `ValidationStrategy.ALL_ERRORS` 时，测试必须断言错误列表内容和顺序，避免调用方误用 `getError()`。

## 9. 禁止行为
- 禁止在规约中访问数据库、缓存、远程服务、文件系统或 Spring 容器。
- 禁止在规约中开启事务、发布领域事件或修改领域对象状态。
- 禁止把 Controller 参数校验、Mapper 查询条件、权限鉴权封装成领域规约。
- 禁止把领域业务规则以内联 `if`/`else`、私有校验方法或通用 `Validator`/`Checker` 类替代规约。
- 禁止把规约实例缓存为 `static final` 单例字段后复用。
- 禁止在业务代码中复制 `Specification`、`CompositeSpecification`、`ValidationResult` 等组件类型。

## 10. 自检清单
- 该规则是否是领域业务规则，而不是接口参数格式或数据库查询条件。
- 领域业务规则是否已经使用规约模式表达。
- 规约是否放在对应聚合的 `domain/specification/` 包下。
- 规约命名是否准确表达业务事实。
- `validate` 是否只返回 `ValidationResult`，没有直接抛业务异常。
- 规约是否无副作用、无外部依赖、无跨层调用。
- 调用方是否将失败结果转换为明确的业务错误码。

## 11. 参考资料
- 组件介绍：`https://dochub.hzlinks.net/public/common/hzsun-common/specification-pattern/introduce.html`
- 快速开始：`https://dochub.hzlinks.net/public/common/hzsun-common/specification-pattern/quick-start.html`
