package com.hzsun.aidevops.walletconfig.device.domain;

import com.hzsun.aidevops.walletconfig.device.domain.valueobject.DeviceRegistration;
import lombok.Getter;

/**
 * 设备聚合根。
 *
 * <p>设备归属于某一个机构，归属关系创建后不可变更；设备可删除，删除时其下发表数据一并清除。</p>
 */
@Getter
public class Device {

    /** 系统内部 ID。 */
    private final Long id;

    /** 归属租户 ID。 */
    private final Long tenantId;

    /** 设备序列号，全局唯一。 */
    private final String serialNo;

    /** 设备类型。 */
    private final DeviceType deviceType;

    /** 归属机构 ID。 */
    private final Long organizationId;

    private Device(Long id, Long tenantId, String serialNo, DeviceType deviceType, Long organizationId) {
        this.id = id;
        this.tenantId = tenantId;
        this.serialNo = serialNo;
        this.deviceType = deviceType;
        this.organizationId = organizationId;
    }

    /**
     * 业务创建：仅供 {@link DeviceFactory} 调用。
     *
     * @param id           系统内部 ID
     * @param tenantId     归属租户 ID
     * @param registration 设备登记信息
     * @return 设备聚合根
     */
    static Device create(Long id, Long tenantId, DeviceRegistration registration) {
        return new Device(id, tenantId, registration.serialNo(), registration.deviceType(), registration.organizationId());
    }

    /**
     * 持久化还原：仅供基础设施层仓储实现调用。
     *
     * @param id             系统内部 ID
     * @param tenantId       归属租户 ID
     * @param serialNo       设备序列号
     * @param deviceType     设备类型
     * @param organizationId 归属机构 ID
     * @return 设备聚合根
     */
    public static Device reconstitute(Long id, Long tenantId, String serialNo, DeviceType deviceType, Long organizationId) {
        return new Device(id, tenantId, serialNo, deviceType, organizationId);
    }
}
