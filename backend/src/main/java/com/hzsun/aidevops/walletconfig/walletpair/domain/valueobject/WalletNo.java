package com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject;

/**
 * 钱包编号值对象。
 *
 * <p>钱包以数字编号标识，由学校管理员手工填写，取值 1~8。
 * 编号即钱包的身份，取值范围由领域规约校验，本类型不承载校验逻辑。</p>
 *
 * @param value 编号数值
 */
public record WalletNo(int value) {

    /**
     * 由数值构造钱包编号。
     *
     * @param value 编号数值
     * @return 钱包编号
     */
    public static WalletNo of(int value) {
        return new WalletNo(value);
    }
}
