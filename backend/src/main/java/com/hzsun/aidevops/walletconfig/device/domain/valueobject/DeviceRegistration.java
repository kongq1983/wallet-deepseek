package com.hzsun.aidevops.walletconfig.device.domain.valueobject;

import com.hzsun.aidevops.walletconfig.device.domain.DeviceType;

/**
 * 设备登记信息。
 *
 * <p>表达一次设备新增操作所需的业务事实：设备序列号、设备类型、归属机构，
 * 以及该序列号是否已被其他设备占用。</p>
 *
 * @param serialNo          设备序列号
 * @param deviceType        设备类型
 * @param organizationId    归属机构 ID
 * @param serialNoOccupied  序列号是否已被占用
 */
public record DeviceRegistration(String serialNo, DeviceType deviceType, Long organizationId, boolean serialNoOccupied) {
}
