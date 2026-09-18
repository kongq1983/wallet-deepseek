package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;

/**
 * 下发表目标行。
 *
 * <p>由配置推导出的“应当存在”的下发表数据，用于与现有有效行做差异比较。</p>
 *
 * @param dimension      下发表维度
 * @param walletNo       钱包编号
 * @param deductAllowed  是否允许追扣
 * @param offlineAllowed 是否允许脱机消费
 */
public record WalletDispatchTarget(WalletDimension dimension, WalletNo walletNo, boolean deductAllowed,
                                   boolean offlineAllowed) {
}
