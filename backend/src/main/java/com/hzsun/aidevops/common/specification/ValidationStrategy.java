package com.hzsun.aidevops.common.specification;

/**
 * 规约组合的校验策略。
 *
 * <p>默认使用 {@link #FAIL_FAST}；仅当需要一次性返回全部错误时才使用 {@link #ALL_ERRORS}，
 * 此时调用方必须读取 {@link ValidationResult#getErrors()}。</p>
 */
public enum ValidationStrategy {

    /** 遇到第一条失败规则立即返回。 */
    FAIL_FAST,

    /** 收集全部失败规则后统一返回。 */
    ALL_ERRORS
}
