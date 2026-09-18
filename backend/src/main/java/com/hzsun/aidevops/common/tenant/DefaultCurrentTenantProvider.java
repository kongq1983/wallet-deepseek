package com.hzsun.aidevops.common.tenant;

import org.springframework.stereotype.Component;

/**
 * 当前租户解析的过渡实现。
 *
 * <p>当前阶段尚未接入登录模块，管理端登录态中不存在用户归属租户，因此统一返回默认业务租户。
 * 登录模块接入后，必须改为从登录态读取用户记录上的租户 ID，并删除本实现，
 * 禁止在业务服务、聚合、仓储或监听器中散落硬编码租户。</p>
 */
@Component
public class DefaultCurrentTenantProvider implements CurrentTenantProvider {

    @Override
    public Long currentTenantId() {
        return TenantConstants.DEFAULT_TENANT_ID;
    }
}
