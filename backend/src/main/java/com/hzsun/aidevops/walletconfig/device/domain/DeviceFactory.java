package com.hzsun.aidevops.walletconfig.device.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.device.domain.valueobject.DeviceRegistration;

/**
 * 设备聚合根工厂。
 *
 * <p>以静态方法提供业务创建入口，不实例化。</p>
 */
public final class DeviceFactory {

    private DeviceFactory() {
    }

    /**
     * 创建设备聚合根。
     *
     * @param idGenerator  内部 ID 生成器
     * @param tenantId     归属租户 ID
     * @param registration 设备登记信息
     * @return 新建的设备聚合根
     */
    public static Device create(IdGenerator idGenerator, Long tenantId, DeviceRegistration registration) {
        return Device.create(idGenerator.nextId(), tenantId, registration);
    }
}
