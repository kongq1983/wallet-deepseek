package com.hzsun.aidevops.common.specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合规约抽象类。
 *
 * <p>自定义规约类应继承本类并实现 {@link #validate(Object)}。
 * 本类同时提供 {@code and} 的 FAIL_FAST 组合实现，供 {@link Specification} 的默认方法复用。</p>
 *
 * @param <T> 单一校验对象类型
 */
public abstract class CompositeSpecification<T> implements Specification<T> {

    @Override
    public abstract ValidationResult validate(T value);
}

/**
 * 与组合规约：按策略收集或短路返回失败结果。
 *
 * <p>构造时对内部的与组合做扁平化，保证 {@code withStrategy} 调整策略后仍能收集到全部原子规约的错误。</p>
 *
 * @param <T> 校验对象类型
 */
final class AndSpecification<T> extends CompositeSpecification<T> {

    private final List<Specification<T>> specifications;

    private final ValidationStrategy strategy;

    @SuppressWarnings("unchecked")
    AndSpecification(List<Specification<T>> specifications, ValidationStrategy strategy) {
        List<Specification<T>> flattened = new ArrayList<>();
        for (Specification<T> specification : specifications) {
            if (specification instanceof AndSpecification) {
                flattened.addAll(((AndSpecification<T>) specification).specifications);
            } else {
                flattened.add(specification);
            }
        }
        this.specifications = List.copyOf(flattened);
        this.strategy = strategy;
    }

    @Override
    public ValidationResult validate(T value) {
        List<String> errors = new ArrayList<>();
        for (Specification<T> specification : specifications) {
            ValidationResult result = specification.validate(value);
            if (result.isValid()) {
                continue;
            }
            if (strategy == ValidationStrategy.FAIL_FAST) {
                return result;
            }
            errors.addAll(result.getErrors());
        }
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
}

/**
 * 或组合规约：任一通过即通过。
 *
 * @param <T> 校验对象类型
 */
final class OrSpecification<T> extends CompositeSpecification<T> {

    private final Specification<T> left;

    private final Specification<T> right;

    OrSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public ValidationResult validate(T value) {
        ValidationResult leftResult = left.validate(value);
        if (leftResult.isValid()) {
            return ValidationResult.valid();
        }
        ValidationResult rightResult = right.validate(value);
        if (rightResult.isValid()) {
            return ValidationResult.valid();
        }
        return rightResult;
    }
}

/**
 * 取反规约：原规约失败即通过。
 *
 * @param <T> 校验对象类型
 */
final class NotSpecification<T> extends CompositeSpecification<T> {

    private final Specification<T> specification;

    NotSpecification(Specification<T> specification) {
        this.specification = specification;
    }

    @Override
    public ValidationResult validate(T value) {
        ValidationResult result = specification.validate(value);
        return result.isValid() ? ValidationResult.invalid("规则不满足") : ValidationResult.valid();
    }
}
