package com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.assembler;

import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletType;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.convertor.DeviceIdentityWalletConvertor;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.po.DeviceIdentityWalletPo;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.dto.DeviceIdentityWalletDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 下发表查询结果转换器。
 *
 * <p>设备序列号与交易身份名称由查询服务补充。</p>
 */
@Mapper(componentModel = "spring")
public interface DeviceIdentityWalletQryAssembler {

    /**
     * 持久化对象转为查询出参。
     *
     * @param po 持久化对象
     * @return 查询出参
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "deviceId", source = "deviceId", qualifiedByName = "longToString")
    @Mapping(target = "identityId", source = "identityId", qualifiedByName = "longToString")
    @Mapping(target = "walletType", source = "walletType")
    @Mapping(target = "walletTypeLabel", source = "walletType", qualifiedByName = "walletTypeLabel")
    @Mapping(target = "walletNo", source = "walletNo", qualifiedByName = "intToString")
    @Mapping(target = "deductAllowed", source = "deductAllowed", qualifiedByName = "toBoolean")
    @Mapping(target = "offlineAllowed", source = "offlineAllowed", qualifiedByName = "toBoolean")
    @Mapping(target = "validFlag", source = "validFlag", qualifiedByName = "intToString")
    @Mapping(target = "validFlagLabel", source = "validFlag", qualifiedByName = "validFlagLabel")
    @Mapping(target = "version", source = "version", qualifiedByName = "intToString")
    @Mapping(target = "deviceSerialNo", ignore = true)
    @Mapping(target = "identityName", ignore = true)
    DeviceIdentityWalletDto toDto(DeviceIdentityWalletPo po);

    /**
     * 钱包类型枚举名转为中文含义。
     *
     * @param walletType 枚举名
     * @return 中文含义
     */
    @Named("walletTypeLabel")
    default String walletTypeLabel(String walletType) {
        return walletType == null ? null : WalletType.valueOf(walletType).getLabel();
    }

    /**
     * 有效标记转为中文含义。
     *
     * @param validFlag 有效标记
     * @return 中文含义
     */
    @Named("validFlagLabel")
    default String validFlagLabel(Integer validFlag) {
        if (validFlag == null) {
            return null;
        }
        return validFlag == DeviceIdentityWalletConvertor.VALID_FLAG_VALID ? "有效" : "无效";
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
