package com.hzsun.aidevops.walletconfig.organization.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.organization.domain.valueobject.OrganizationRegistration;

/**
 * 规约：机构树的深度不得超过 3 层（含顶层机构）。
 */
public class OrganizationDepthMustNotExceedLimitSpecification extends CompositeSpecification<OrganizationRegistration> {

    /** 机构树允许的最大层级。 */
    private static final int MAX_LEVEL = 3;

    @Override
    public ValidationResult validate(OrganizationRegistration registration) {
        if (registration.targetLevel() > MAX_LEVEL) {
            return ValidationResult.invalid("机构层级不能超过3层");
        }
        return ValidationResult.valid();
    }
}
