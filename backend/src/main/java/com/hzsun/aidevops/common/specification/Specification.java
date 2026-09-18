package com.hzsun.aidevops.common.specification;

/**
 * 领域规约接口。
 *
 * <p>承载领域业务规则、业务准入规则与状态流转前置规则的纯内存判断。
 * 规约不得访问数据库、缓存、远程服务或 Spring 容器，不得修改被校验对象状态，不得产生副作用。</p>
 *
 * @param <T> 单一校验对象类型
 */
public interface Specification<T> {

    /**
     * 执行规则校验。
     *
     * @param value 被校验对象
     * @return 校验结果
     */
    ValidationResult validate(T value);

    /**
     * 与另一规约做与组合。
     *
     * @param other 另一规约
     * @return 组合规约
     */
    default Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(java.util.List.of(this, other), ValidationStrategy.FAIL_FAST);
    }

    /**
     * 与另一规约做或组合，任一通过即通过。
     *
     * @param other 另一规约
     * @return 组合规约
     */
    default Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    /**
     * 取反组合。
     *
     * @return 取反后的规约
     */
    default Specification<T> not() {
        return new NotSpecification<>(this);
    }

    /**
     * 在组合规约上应用校验策略。
     *
     * @param strategy 校验策略
     * @return 应用策略后的规约
     */
    default Specification<T> withStrategy(ValidationStrategy strategy) {
        return new AndSpecification<>(java.util.List.of(this), strategy);
    }
}
