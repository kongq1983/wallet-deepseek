package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;

/**
 * 机构支付参数聚合根工厂。
 *
 * <p>以静态方法提供业务创建入口，不实例化。</p>
 */
public final class OrganizationPaymentConfigFactory {

    private OrganizationPaymentConfigFactory() {
    }

    /**
     * 创建机构支付参数聚合根。
     *
     * @param idGenerator    内部 ID 生成器
     * @param tenantId       归属租户 ID
     * @param organizationId 归属机构 ID
     * @return 新建的机构支付参数聚合根
     */
    public static OrganizationPaymentConfig create(IdGenerator idGenerator, Long tenantId, Long organizationId) {
        return OrganizationPaymentConfig.create(idGenerator.nextId(), tenantId, organizationId);
    }
}
