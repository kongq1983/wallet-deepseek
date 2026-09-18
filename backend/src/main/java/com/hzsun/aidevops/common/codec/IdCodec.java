package com.hzsun.aidevops.common.codec;

import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;

/**
 * 系统内部 ID 的传输编解码工具。
 *
 * <p>前端统一以字符串承载后端 Long 型 ID，边界层负责转换为 Long 并校验合法性，
 * 避免 JavaScript 大整数精度丢失。</p>
 */
public final class IdCodec {

    /**
     * 将字符串 ID 转换为 Long。
     *
     * @param value 字符串 ID
     * @return Long 型 ID
     * @throws BusinessException 当值为空或不是合法数字时抛出
     */
    public static Long toLong(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCodes.COMMON_FAILURE, "请求参数不合法");
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException exception) {
            throw new BusinessException(ErrorCodes.COMMON_FAILURE, "请求参数不合法");
        }
    }

    /**
     * 将 Long 型 ID 转换为字符串。
     *
     * @param value Long 型 ID
     * @return 字符串 ID，入参为 null 时返回 null
     */
    public static String toString(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    private IdCodec() {
    }
}
