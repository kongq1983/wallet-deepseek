package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

/**
 * 下发表维度。
 *
 * <p>由设备、交易身份与钱包类型三元组构成，是下发表行的业务唯一标识，
 * 也是版本号单调递增的边界：同一维度上任意一次生成、失效或换号都会推进版本号。</p>
 *
 * @param deviceId   设备 ID
 * @param identityId 交易身份 ID
 * @param walletType 钱包类型
 */
public record WalletDimension(Long deviceId, Long identityId, WalletType walletType) {
}
