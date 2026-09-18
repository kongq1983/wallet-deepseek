package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.valueobject;

import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;

import java.util.Set;

/**
 * 机构支付配置。
 *
 * <p>表达一次保存机构支付参数时的目标配置及校验所需事实：
 * 工作钱包编号、身份限制开关、可消费身份集合，以及当前租户钱包配对中可用的工作钱包编号集合。</p>
 *
 * @param workWalletNo            工作钱包编号
 * @param identityRestricted      身份限制开关
 * @param allowedIdentityIds      可消费身份 ID 集合
 * @param availableWorkWalletNos  租户钱包配对中已配置的工作钱包编号集合
 */
public record PaymentConfiguration(
        WalletNo workWalletNo,
        boolean identityRestricted,
        Set<Long> allowedIdentityIds,
        Set<Integer> availableWorkWalletNos) {

    /**
     * 工作钱包是否在可选范围内。
     *
     * @return true 表示该工作钱包已由租户钱包配对配置
     */
    public boolean workWalletAvailable() {
        return workWalletNo != null && availableWorkWalletNos.contains(workWalletNo.value());
    }

    /**
     * 是否缺少身份限制开关开启时必需的可消费身份。
     *
     * @return true 表示开启身份限制但未选择任何可消费身份
     */
    public boolean allowedIdentityMissing() {
        return identityRestricted && (allowedIdentityIds == null || allowedIdentityIds.isEmpty());
    }
}
