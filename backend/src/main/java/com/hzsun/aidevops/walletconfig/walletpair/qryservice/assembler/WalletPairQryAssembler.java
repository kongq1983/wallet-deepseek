package com.hzsun.aidevops.walletconfig.walletpair.qryservice.assembler;

import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.po.WalletPairPo;
import com.hzsun.aidevops.walletconfig.walletpair.qryservice.dto.WalletPairDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 租户钱包配对查询结果转换器。
 *
 * <p>负责把持久化对象投影为查询出参，所有内部 ID 与数值均转换为字符串表达。</p>
 */
@Mapper(componentModel = "spring")
public interface WalletPairQryAssembler {

    /**
     * 持久化对象转为查询出参。
     *
     * @param po 持久化对象
     * @return 查询出参
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "workWalletNo", source = "workWalletNo", qualifiedByName = "intToString")
    @Mapping(target = "deductWalletNo", source = "deductWalletNo", qualifiedByName = "intToString")
    WalletPairDto toDto(WalletPairPo po);

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
}
