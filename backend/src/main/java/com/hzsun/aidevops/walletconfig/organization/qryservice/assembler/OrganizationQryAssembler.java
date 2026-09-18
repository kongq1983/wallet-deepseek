package com.hzsun.aidevops.walletconfig.organization.qryservice.assembler;

import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationType;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.po.OrganizationPo;
import com.hzsun.aidevops.walletconfig.organization.qryservice.dto.OrganizationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 机构查询结果转换器。
 *
 * <p>把持久化对象投影为查询出参，并把类型枚举名转换为中文含义、把 ID 与层级转换为字符串。</p>
 */
@Mapper(componentModel = "spring")
public interface OrganizationQryAssembler {

    /**
     * 持久化对象转为查询出参。
     *
     * @param po    持久化对象
     * @param level 机构层级
     * @return 查询出参
     */
    @Mapping(target = "id", source = "po.id", qualifiedByName = "longToString")
    @Mapping(target = "name", source = "po.name")
    @Mapping(target = "orgType", source = "po.orgType")
    @Mapping(target = "orgTypeLabel", source = "po.orgType", qualifiedByName = "orgTypeLabel")
    @Mapping(target = "superiorId", source = "po.superiorId", qualifiedByName = "longToString")
    @Mapping(target = "level", source = "level", qualifiedByName = "intToString")
    OrganizationDto toDto(OrganizationPo po, int level);

    /**
     * 类型枚举名转为中文含义。
     *
     * @param orgType 枚举名
     * @return 中文含义
     */
    @Named("orgTypeLabel")
    default String orgTypeLabel(String orgType) {
        return orgType == null ? null : OrganizationType.valueOf(orgType).getLabel();
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
     * int 转字符串。
     *
     * @param value 原始值
     * @return 字符串值
     */
    @Named("intToString")
    default String intToString(int value) {
        return String.valueOf(value);
    }
}
