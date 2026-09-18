package com.hzsun.aidevops.walletconfig.device.qryservice.assembler;

import com.hzsun.aidevops.walletconfig.device.domain.DeviceType;
import com.hzsun.aidevops.walletconfig.device.infrastructure.po.DevicePo;
import com.hzsun.aidevops.walletconfig.device.qryservice.dto.DeviceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 设备查询结果转换器。
 */
@Mapper(componentModel = "spring")
public interface DeviceQryAssembler {

    /**
     * 持久化对象转为查询出参。
     *
     * @param po 持久化对象
     * @return 查询出参
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "deviceType", source = "deviceType")
    @Mapping(target = "deviceTypeLabel", source = "deviceType", qualifiedByName = "deviceTypeLabel")
    @Mapping(target = "organizationId", source = "organizationId", qualifiedByName = "longToString")
    DeviceDto toDto(DevicePo po);

    /**
     * 类型枚举名转为中文含义。
     *
     * @param deviceType 枚举名
     * @return 中文含义
     */
    @Named("deviceTypeLabel")
    default String deviceTypeLabel(String deviceType) {
        return deviceType == null ? null : DeviceType.valueOf(deviceType).getLabel();
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
}
