package com.hzsun.aidevops.common.exception;

import com.hzsun.aidevops.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理器。
 *
 * <p>负责把异常统一映射为 HTTP 200 + {code,message,data} 的标准响应（非 200 状态码不返回响应体）。
 * Controller 不得自行拼装异常返回体。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常：记录 warn 日志并返回业务错误码与可展示文案。
     *
     * @param exception 业务异常
     * @return 标准响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        log.warn("业务处理失败, errorCode={}, message={}", exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.ok(ApiResponse.failure(exception.getErrorCode(), exception.getMessage()));
    }

    /**
     * 处理请求体字段校验失败（Bean Validation）。
     *
     * @param exception 校验异常
     * @return 标准响应，文案取第一条字段错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("请求参数不合法");
        log.warn("请求参数校验失败, message={}", message);
        return ResponseEntity.ok(ApiResponse.failure(ErrorCodes.COMMON_FAILURE, message));
    }

    /**
     * 处理方法参数与路径变量校验失败。
     *
     * @param exception 校验异常
     * @return 标准响应，文案取第一条约束错误
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("请求参数不合法");
        log.warn("请求参数约束校验失败, message={}", message);
        return ResponseEntity.ok(ApiResponse.failure(ErrorCodes.COMMON_FAILURE, message));
    }

    /**
     * 处理未知异常：返回通用失败文案，保留完整错误日志用于定位。
     *
     * @param exception 未知异常
     * @return 标准响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        log.error("系统异常, api=未知", exception);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.failure(ErrorCodes.COMMON_FAILURE, null));
    }
}
