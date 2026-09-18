package com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject;

/**
 * 钱包配对配置。
 *
 * <p>表达一次新增或修改钱包配对时的目标配置：一个工作钱包编号与一个追扣钱包编号的成对组合。</p>
 *
 * @param workWalletNo   工作钱包编号
 * @param deductWalletNo 追扣钱包编号
 */
public record WalletPairConfiguration(WalletNo workWalletNo, WalletNo deductWalletNo) {

    /**
     * 两个编号是否指向同一个钱包。
     *
     * @return true 表示工作钱包与追扣钱包编号相同
     */
    public boolean usesSameWalletNo() {
        return workWalletNo.value() == deductWalletNo.value();
    }
}
