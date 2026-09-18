package com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject;

import java.util.Set;

/**
 * 租户内钱包编号占用情况。
 *
 * <p>聚合当前已被占用的钱包编号集合：工作钱包列与追扣钱包列合计，同一编号最多出现一次。
 * 该值对象由应用层从仓储读取后构造，用于承载“编号未被占用”规则的校验事实。</p>
 *
 * @param values 已被占用的钱包编号集合
 */
public record WalletPairOccupiedNos(Set<Integer> values) {

    /**
     * 由编号集合构造占用情况。
     *
     * @param values 已被占用的钱包编号集合
     * @return 占用情况
     */
    public static WalletPairOccupiedNos of(Set<Integer> values) {
        return new WalletPairOccupiedNos(Set.copyOf(values));
    }

    /**
     * 判断编号是否已被占用。
     *
     * @param walletNo 待判断编号
     * @return true 表示已被占用
     */
    public boolean contains(WalletNo walletNo) {
        return values.contains(walletNo.value());
    }
}
