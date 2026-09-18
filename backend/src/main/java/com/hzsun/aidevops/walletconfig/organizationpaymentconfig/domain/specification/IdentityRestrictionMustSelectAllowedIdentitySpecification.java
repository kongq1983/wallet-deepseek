package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.valueobject.PaymentConfiguration;

/**
 * 规约：必须满足身份限制开启时可消费身份已选择才允许保存机构支付参数。
 */
public class IdentityRestrictionMustSelectAllowedIdentitySpecification extends CompositeSpecification<PaymentConfiguration> {

    @Override
    public ValidationResult validate(PaymentConfiguration configuration) {
        if (configuration.allowedIdentityMissing()) {
            return ValidationResult.invalid("请选择可消费身份");
        }
        return ValidationResult.valid();
    }
}
