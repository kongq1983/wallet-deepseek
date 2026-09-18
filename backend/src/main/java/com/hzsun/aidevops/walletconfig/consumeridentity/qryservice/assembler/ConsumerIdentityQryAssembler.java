package com.hzsun.aidevops.walletconfig.consumeridentity.qryservice.assembler;

import com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.po.ConsumerIdentityPo;
import com.hzsun.aidevops.walletconfig.consumeridentity.qryservice.dto.ConsumerIdentityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 消费身份查询结果转换器。
 */
@Mapper(componentModel = "spring")
public interface ConsumerIdentityQryAssembler {

    /**
     * 持久化对象转为查询出参。
     *
     * @param po 持久化对象
     * @return 查询出参
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "offlineAllowed", source = "offlineAllowed", qualifiedByName = "toBoolean")
    ConsumerIdentityDto toDto(ConsumerIdentityPo po);

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
