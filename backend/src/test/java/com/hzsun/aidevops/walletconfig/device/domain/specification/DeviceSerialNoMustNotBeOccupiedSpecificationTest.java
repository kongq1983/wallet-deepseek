package com.hzsun.aidevops.walletconfig.device.domain.specification;

import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceType;
import com.hzsun.aidevops.walletconfig.device.domain.valueobject.DeviceRegistration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 设备序列号唯一性规约单元测试。
 */
class DeviceSerialNoMustNotBeOccupiedSpecificationTest {

    @Test
    @DisplayName("序列号未被占用时规约通过")
    void passesWhenSerialNoFree() {
        DeviceRegistration registration = new DeviceRegistration("POS-0001", DeviceType.POS_MACHINE, 10L, false);

        ValidationResult result = new DeviceSerialNoMustNotBeOccupiedSpecification().validate(registration);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("序列号已被占用时规约失败")
    void failsWhenSerialNoOccupied() {
        DeviceRegistration registration = new DeviceRegistration("POS-0001", DeviceType.POS_MACHINE, 10L, true);

        ValidationResult result = new DeviceSerialNoMustNotBeOccupiedSpecification().validate(registration);

        assertFalse(result.isValid());
        assertEquals("设备序列号已存在", result.getError());
    }
}
