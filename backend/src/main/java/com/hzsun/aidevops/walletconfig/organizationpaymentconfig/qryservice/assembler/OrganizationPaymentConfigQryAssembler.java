package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice.assembler;

import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po.OrganizationPaymentConfigPo;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice.dto.OrganizationPaymentConfigDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 机构支付参数查询结果转换器。
 *
 * <p>负责持久化对象到出参的基础字段投影；机构名称、可消费身份名称与追扣钱包由查询服务补充。</p>
 */
@Mapper(componentModel = "spring")
public interface OrganizationPaymentConfigQryAssembler {

    /**
     * 持久化对象转为查询出参。
     *
     * @param po 持久化对象
     * @return 查询出参
     */
    @Mapping(target = "organizationId", source = "organizationId", qualifiedByName = "longToString")
    @Mapping(target = "workWalletNo", source = "workWalletNo", qualifiedByName = "intToString")
    @Mapping(target = "deductAllowed", source = "deductAllowed", qualifiedByName = "toBoolean")
    @Mapping(target = "identityRestricted", source = "identityRestricted", qualifiedByName = "toBoolean")
    @Mapping(target = "offlineAllowed", source = "offlineAllowed", qualifiedByName = "toBoolean")
    @Mapping(target = "configured", constant = "true")
    @Mapping(target = "organizationName", ignore = true)
    @Mapping(target = "deductWalletNo", ignore = true)
    @Mapping(target = "allowedIdentityIds", ignore = true)
    @Mapping(target = "allowedIdentityNames", ignore = true)
    OrganizationPaymentConfigDto toDto(OrganizationPaymentConfigPo po);

    /**
     * 构造未配置支付参数的出参。
     *
     * @param organizationId   机构 ID
     * @param organizationName 机构名称
     * @return 查询出参
     */
    default OrganizationPaymentConfigDto notConfigured(Long organizationId, String organizationName) {
        OrganizationPaymentConfigDto dto = new OrganizationPaymentConfigDto();
        dto.setOrganizationId(organizationId == null ? null : String.valueOf(organizationId));
        dto.setOrganizationName(organizationName);
        dto.setConfigured(false);
        return dto;
    }

    /**
     * Long 转字符串。
     *
     * @param value 原始值
     * @return 字符串值
     */
    @Named("longToString")
    default String longToString(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * Integer 转字符串。
     *
     * @param value 原始值
     * @return 字符串值
     */
    @Named("intToString")
    default String intToString(Integer value) {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 空布尔值归一化为 false。
     *
     * @param value 原始值
     * @return 归一化后的布尔值
     */
    @Named("toBoolean")
    default Boolean toBoolean(Boolean value) {
        return Boolean.TRUE.equals(value);
    }
}
