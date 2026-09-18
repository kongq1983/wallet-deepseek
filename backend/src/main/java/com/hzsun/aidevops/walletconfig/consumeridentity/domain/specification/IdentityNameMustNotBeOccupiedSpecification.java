package com.hzsun.aidevops.walletconfig.consumeridentity.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.valueobject.IdentityRegistration;

/**
 * 规约：身份名称不得在租户内被其他身份占用。
 */
public class IdentityNameMustNotBeOccupiedSpecification extends CompositeSpecification<IdentityRegistration> {

    @Override
    public ValidationResult validate(IdentityRegistration registration) {
        if (registration.nameOccupiedBy(registration.name())) {
            return ValidationResult.invalid("身份名称已存在");
        }
        return ValidationResult.valid();
    }
}
