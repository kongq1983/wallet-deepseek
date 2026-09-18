package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

/**
 * 下发表中的钱包类型。
 */
public enum WalletType {

    /** 工作钱包：日常消费优先扣减的钱包。 */
    WORK("工作钱包"),

    /** 追扣钱包：工作钱包不足且机构允许追扣时继续扣减的钱包。 */
    DEDUCT("追扣钱包");

    private final String label;

    WalletType(String label) {
        this.label = label;
    }

    /**
     * 获取用于展示的中文含义。
     *
     * @return 中文标签
     */
    public String getLabel() {
        return label;
    }
}
