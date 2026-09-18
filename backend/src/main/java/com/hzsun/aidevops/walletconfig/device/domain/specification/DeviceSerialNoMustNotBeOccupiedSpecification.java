package com.hzsun.aidevops.walletconfig.device.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.device.domain.valueobject.DeviceRegistration;

/**
 * 规约：设备序列号必须全局唯一，不得被其他设备占用。
 */
public class DeviceSerialNoMustNotBeOccupiedSpecification extends CompositeSpecification<DeviceRegistration> {

    @Override
    public ValidationResult validate(DeviceRegistration registration) {
        if (registration.serialNoOccupied()) {
            return ValidationResult.invalid("设备序列号已存在");
        }
        return ValidationResult.valid();
    }
}
