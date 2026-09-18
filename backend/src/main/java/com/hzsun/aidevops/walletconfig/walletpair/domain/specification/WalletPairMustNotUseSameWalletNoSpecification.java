package com.hzsun.aidevops.walletconfig.walletpair.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;

/**
 * 规约：禁止在一条钱包配对记录中将工作钱包编号与追扣钱包编号设置为同一个编号。
 */
public class WalletPairMustNotUseSameWalletNoSpecification extends CompositeSpecification<WalletPairConfiguration> {

    @Override
    public ValidationResult validate(WalletPairConfiguration configuration) {
        if (configuration.usesSameWalletNo()) {
            return ValidationResult.invalid("追扣钱包编号不能与工作钱包编号相同");
        }
        return ValidationResult.valid();
    }
}
