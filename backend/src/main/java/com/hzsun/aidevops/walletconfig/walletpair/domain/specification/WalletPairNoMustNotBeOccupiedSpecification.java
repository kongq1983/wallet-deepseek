package com.hzsun.aidevops.walletconfig.walletpair.domain.specification;

import com.hzsun.aidevops.common.specification.CompositeSpecification;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairOccupiedNos;

/**
 * 规约：钱包配对中的工作钱包编号与追扣钱包编号都不得被其他钱包配对占用。
 *
 * <p>钱包编号在租户钱包配对表内全局唯一：工作钱包列与追扣钱包列合计，任一编号最多只能出现一次。</p>
 */
public class WalletPairNoMustNotBeOccupiedSpecification extends CompositeSpecification<WalletPairConfiguration> {

    private final WalletPairOccupiedNos occupiedNos;

    /**
     * 构造规约。
     *
     * @param occupiedNos 租户内已被占用的钱包编号（修改场景需排除本条记录自身）
     */
    public WalletPairNoMustNotBeOccupiedSpecification(WalletPairOccupiedNos occupiedNos) {
        this.occupiedNos = occupiedNos;
    }

    @Override
    public ValidationResult validate(WalletPairConfiguration configuration) {
        if (occupiedNos.contains(configuration.workWalletNo())) {
            return ValidationResult.invalid("工作钱包编号已存在");
        }
        if (occupiedNos.contains(configuration.deductWalletNo())) {
            return ValidationResult.invalid("追扣钱包编号已存在");
        }
        return ValidationResult.valid();
    }
}
