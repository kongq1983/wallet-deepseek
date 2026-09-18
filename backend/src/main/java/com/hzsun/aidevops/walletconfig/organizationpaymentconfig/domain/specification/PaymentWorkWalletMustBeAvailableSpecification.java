package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.valueobject.PaymentConfiguration;

/**
 * 规约：机构支付参数的工作钱包必须取自租户钱包配对中已配置的工作钱包编号。
 */
public class PaymentWorkWalletMustBeAvailableSpecification extends CompositeSpecification<PaymentConfiguration> {

    @Override
    public ValidationResult validate(PaymentConfiguration configuration) {
        if (!configuration.workWalletAvailable()) {
            return ValidationResult.invalid("请选择工作钱包");
        }
        return ValidationResult.valid();
    }
}
