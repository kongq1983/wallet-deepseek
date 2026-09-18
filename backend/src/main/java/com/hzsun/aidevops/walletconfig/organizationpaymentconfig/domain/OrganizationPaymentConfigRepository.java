package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain;

import java.util.List;
import java.util.Optional;

/**
 * 机构支付参数仓储接口。
 *
 * <p>可消费身份以独立的关联表持久化，保存时全量覆盖。</p>
 */
public interface OrganizationPaymentConfigRepository {

    /**
     * 保存新的机构支付参数。
     *
     * @param config 机构支付参数聚合根
     */
    void save(OrganizationPaymentConfig config);

    /**
     * 更新已存在的机构支付参数。
     *
     * @param config 机构支付参数聚合根
     */
    void update(OrganizationPaymentConfig config);

    /**
     * 按机构 ID 查询机构支付参数。
     *
     * @param tenantId       租户 ID
     * @param organizationId 机构 ID
     * @return 机构支付参数聚合根
     */
    Optional<OrganizationPaymentConfig> findByOrganizationId(Long tenantId, Long organizationId);

    /**
     * 查询租户下全部机构支付参数。
     *
     * @param tenantId 租户 ID
     * @return 机构支付参数列表
     */
    List<OrganizationPaymentConfig> findAll(Long tenantId);

    /**
     * 移除各机构可消费身份中对指定身份的引用。
     *
     * @param tenantId   租户 ID
     * @param identityId 被删除的身份 ID
     */
    void removeIdentityReference(Long tenantId, Long identityId);
}
