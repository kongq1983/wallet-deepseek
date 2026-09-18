package com.hzsun.aidevops.walletconfig.organization.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.organization.domain.valueobject.OrganizationRegistration;

/**
 * 规约：禁止为类型为档口的机构创建子机构。
 */
public class OrganizationStallMustNotHaveChildSpecification extends CompositeSpecification<OrganizationRegistration> {

    @Override
    public ValidationResult validate(OrganizationRegistration registration) {
        if (registration.superiorIsStall()) {
            return ValidationResult.invalid("档口下不能创建子机构");
        }
        return ValidationResult.valid();
    }
}
