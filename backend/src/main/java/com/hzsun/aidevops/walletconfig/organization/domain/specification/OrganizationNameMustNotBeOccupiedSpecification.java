package com.hzsun.aidevops.walletconfig.organization.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.organization.domain.valueobject.OrganizationRegistration;

/**
 * 规约：机构名称不得被同一上级机构下的其他机构占用。
 */
public class OrganizationNameMustNotBeOccupiedSpecification extends CompositeSpecification<OrganizationRegistration> {

    @Override
    public ValidationResult validate(OrganizationRegistration registration) {
        if (registration.nameOccupiedBy(registration.name())) {
            return ValidationResult.invalid("机构名称已存在");
        }
        return ValidationResult.valid();
    }
}
