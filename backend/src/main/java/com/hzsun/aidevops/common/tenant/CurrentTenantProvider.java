package com.hzsun.aidevops.common.tenant;

/**
 * 当前租户解析能力。
 *
 * <p>触发层负责从登录态解析当前用户归属租户，并以显式参数传入应用服务。
 * HTTP 请求体中的 Cmd/Qry 不得包含 tenantId 字段。</p>
 */
public interface CurrentTenantProvider {

    /**
     * 解析当前请求的归属租户。
     *
     * @return 归属租户 ID
     */
    Long currentTenantId();
}
