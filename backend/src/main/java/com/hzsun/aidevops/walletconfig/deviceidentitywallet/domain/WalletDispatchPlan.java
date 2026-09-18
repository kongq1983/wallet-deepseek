package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import java.util.List;

/**
 * 下发表变更计划。
 *
 * <p>差异比较的结果：需要置为无效的现有行，以及需要插入的新有效行。
 * 失效行永久保留，不进行物理删除（设备删除导致的连带删除除外）。</p>
 *
 * @param rowsToInvalidate 需要置为无效的行
 * @param rowsToInsert     需要插入的新有效行
 */
public record WalletDispatchPlan(List<DeviceIdentityWallet> rowsToInvalidate,
                                 List<DeviceIdentityWallet> rowsToInsert) {

    /**
     * 是否为空计划（无任何变更）。
     *
     * @return true 表示无需落库
     */
    public boolean isEmpty() {
        return rowsToInvalidate.isEmpty() && rowsToInsert.isEmpty();
    }
}
