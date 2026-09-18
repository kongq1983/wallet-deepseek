package com.hzsun.aidevops.walletconfig.device.domain;

import java.util.List;
import java.util.Optional;

/**
 * 设备仓储接口。
 */
public interface DeviceRepository {

    /**
     * 保存新的设备。
     *
     * @param device 设备聚合根
     */
    void save(Device device);

    /**
     * 删除设备。
     *
     * @param tenantId 租户 ID
     * @param id       设备 ID
     */
    void delete(Long tenantId, Long id);

    /**
     * 按 ID 查询设备。
     *
     * @param tenantId 租户 ID
     * @param id       设备 ID
     * @return 设备聚合根
     */
    Optional<Device> findById(Long tenantId, Long id);

    /**
     * 按序列号查询设备，用于校验序列号全局唯一。
     *
     * @param serialNo 设备序列号
     * @return 设备聚合根
     */
    Optional<Device> findBySerialNo(String serialNo);

    /**
     * 查询租户下全部设备。
     *
     * @param tenantId 租户 ID
     * @return 设备列表
     */
    List<Device> findAll(Long tenantId);

    /**
     * 查询指定机构下的全部设备。
     *
     * @param tenantId       租户 ID
     * @param organizationId 归属机构 ID
     * @return 设备列表
     */
    List<Device> findByOrganizationId(Long tenantId, Long organizationId);
}
