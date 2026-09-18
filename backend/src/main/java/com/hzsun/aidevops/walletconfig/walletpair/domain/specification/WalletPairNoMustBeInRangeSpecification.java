package com.hzsun.aidevops.walletconfig.walletpair.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;

/**
 * 规约：钱包配对中的工作钱包编号与追扣钱包编号都必须在 1~8 之间。
 */
public class WalletPairNoMustBeInRangeSpecification extends CompositeSpecification<WalletPairConfiguration> {

    /** 钱包编号允许的最小值。 */
    private static final int MIN_WALLET_NO = 1;

    /** 钱包编号允许的最大值。 */
    private static final int MAX_WALLET_NO = 8;

    @Override
    public ValidationResult validate(WalletPairConfiguration configuration) {
        if (isOutOfRange(configuration.workWalletNo().value())) {
            return ValidationResult.invalid("工作钱包编号须在1~8之间");
        }
        if (isOutOfRange(configuration.deductWalletNo().value())) {
            return ValidationResult.invalid("追扣钱包编号须在1~8之间");
        }
        return ValidationResult.valid();
    }

    private boolean isOutOfRange(int walletNo) {
        return walletNo < MIN_WALLET_NO || walletNo > MAX_WALLET_NO;
    }
}
