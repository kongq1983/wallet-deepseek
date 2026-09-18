package com.hzsun.aidevops.common.tenant;

/**
 * 租户公共常量。
 *
 * <p>当前阶段系统采用“单租户运行、多租户就绪”策略，默认业务租户固定为 {@link #DEFAULT_TENANT_ID}。
 * 业务限界上下文、聚合、仓储与监听器禁止重复定义默认租户常量。</p>
 */
public final class TenantConstants {

    /** 默认业务租户 ID。注意 0L 不是业务租户。 */
    public static final Long DEFAULT_TENANT_ID = 1L;

    private TenantConstants() {
    }
}
