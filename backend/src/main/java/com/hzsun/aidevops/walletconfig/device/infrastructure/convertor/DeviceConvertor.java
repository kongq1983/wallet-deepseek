package com.hzsun.aidevops.walletconfig.device.infrastructure.convertor;

import com.hzsun.aidevops.walletconfig.device.domain.Device;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceType;
import com.hzsun.aidevops.walletconfig.device.infrastructure.po.DevicePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 设备持久化对象与领域对象转换器。
 */
@Mapper(componentModel = "spring")
public interface DeviceConvertor {

    /**
     * 聚合根转为持久化对象。
     *
     * @param device 设备聚合根
     * @return 持久化对象
     */
    @Mapping(target = "deviceType", expression = "java(device.getDeviceType().name())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DevicePo toPo(Device device);

    /**
     * 持久化对象还原为聚合根。
     *
     * @param po 持久化对象
     * @return 设备聚合根
     */
    default Device toDomain(DevicePo po) {
        return Device.reconstitute(
                po.getId(),
                po.getTenantId(),
                po.getSerialNo(),
                DeviceType.valueOf(po.getDeviceType()),
                po.getOrganizationId());
    }
}
