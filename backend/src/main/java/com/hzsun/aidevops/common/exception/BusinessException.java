package com.hzsun.aidevops.common.exception;

import lombok.Getter;

/**
 * 业务异常。
 *
 * <p>用于表达可预期的业务失败（参数越界、规则冲突、资源不存在等）。
 * 禁止使用裸 RuntimeException 表达业务分支；异常信息必须可直接展示给终端用户，且不泄露内部实现细节。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务错误码，取值来自 {@link ErrorCodes}。 */
    private final int errorCode;

    /**
     * 以错误码与可展示文案构造业务异常。
     *
     * @param errorCode 业务错误码
     * @param message   可直接展示给终端用户的文案
     */
    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
