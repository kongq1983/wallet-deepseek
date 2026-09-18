package com.hzsun.aidevops.common.specification;

import java.util.List;

/**
 * 规约校验结果。
 *
 * <p>规约的 {@code validate} 只能返回本类型，禁止在规约内部直接抛业务异常；
 * 失败结果由调用方转换为 {@code BusinessException} 与明确的业务错误码。</p>
 *
 * <p>注意：本类型是团队公共组件 {@code hzsun-common-starter-specification-pattern} 的等价最小实现，
 * 组件可获取后应删除本地实现并切换依赖。</p>
 */
public final class ValidationResult {

    /** 校验错误信息列表，为空表示校验通过。 */
    private final List<String> errors;

    private ValidationResult(List<String> errors) {
        this.errors = errors;
    }

    /**
     * 构造校验通过结果。
     *
     * @return 通过结果
     */
    public static ValidationResult valid() {
        return new ValidationResult(List.of());
    }

    /**
     * 构造单条错误的校验失败结果。
     *
     * @param error 错误信息
     * @return 失败结果
     */
    public static ValidationResult invalid(String error) {
        return new ValidationResult(List.of(error));
    }

    /**
     * 构造多条错误的校验失败结果。
     *
     * @param errors 错误信息列表
     * @return 失败结果
     */
    public static ValidationResult invalid(List<String> errors) {
        return new ValidationResult(List.copyOf(errors));
    }

    /**
     * 判断校验是否通过。
     *
     * @return true 表示通过
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * 获取第一条错误信息，仅在 FAIL_FAST 策略或单错误场景下使用。
     *
     * @return 第一条错误信息，校验通过时为 null
     */
    public String getError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    /**
     * 获取全部错误信息。
     *
     * @return 错误信息列表
     */
    public List<String> getErrors() {
        return errors;
    }
}
