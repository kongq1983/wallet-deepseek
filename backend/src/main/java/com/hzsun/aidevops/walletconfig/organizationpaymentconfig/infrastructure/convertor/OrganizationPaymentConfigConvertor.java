package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.convertor;

import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfig;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po.OrganizationIdentityPo;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po.OrganizationPaymentConfigPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 机构支付参数持久化对象与领域对象转换器。
 *
 * <p>可消费身份以关联表承载，由仓储实现负责按机构维度组装与覆盖。</p>
 */
@Mapper(componentModel = "spring")
public interface OrganizationPaymentConfigConvertor {

    /**
     * 聚合根转为持久化对象。
     *
     * @param config 机构支付参数聚合根
     * @return 持久化对象
     */
    @Mapping(target = "workWalletNo",
            expression = "java(config.getWorkWalletNo() == null ? null : config.getWorkWalletNo().value())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrganizationPaymentConfigPo toPo(OrganizationPaymentConfig config);

    /**
     * 构造机构可消费身份持久化对象。
     *
     * @param id             系统内部 ID
     * @param tenantId       租户 ID
     * @param organizationId 机构 ID
     * @param identityId     身份 ID
     * @return 持久化对象
     */
    default OrganizationIdentityPo toIdentityPo(Long id, Long tenantId, Long organizationId, Long identityId) {
        OrganizationIdentityPo po = new OrganizationIdentityPo();
        po.setId(id);
        po.setTenantId(tenantId);
        po.setOrganizationId(organizationId);
        po.setIdentityId(identityId);
        return po;
    }
}
