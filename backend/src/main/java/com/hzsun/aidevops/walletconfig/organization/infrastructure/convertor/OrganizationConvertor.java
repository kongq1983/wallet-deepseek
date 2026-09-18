package com.hzsun.aidevops.walletconfig.organization.infrastructure.convertor;

import com.hzsun.aidevops.walletconfig.organization.domain.Organization;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationType;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.po.OrganizationPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 机构持久化对象与领域对象转换器。
 */
@Mapper(componentModel = "spring")
public interface OrganizationConvertor {

    /**
     * 聚合根转为持久化对象。
     *
     * @param organization 机构聚合根
     * @return 持久化对象
     */
    @Mapping(target = "orgType", expression = "java(organization.getOrgType().name())")
    @Mapping(target = "superiorId", source = "superiorId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrganizationPo toPo(Organization organization);

    /**
     * 持久化对象还原为聚合根。
     *
     * @param po 持久化对象
     * @return 机构聚合根
     */
    default Organization toDomain(OrganizationPo po) {
        return Organization.reconstitute(
                po.getId(),
                po.getTenantId(),
                po.getName(),
                OrganizationType.valueOf(po.getOrgType()),
                po.getSuperiorId());
    }
}
