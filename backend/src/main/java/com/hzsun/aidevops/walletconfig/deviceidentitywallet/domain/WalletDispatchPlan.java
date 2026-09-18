package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import java.util.List;

/**
 * 下发表变更计划。
 *
 * <p>差异比较的结果：需要置为无效的现有行，以及需要新增的目标行。
 * 失效行永久保留，不进行物理删除（设备删除导致的连带删除除外）。</p>
 *
 * <p>计划里不携带版本号：新增行只有在确认本次重算确有变更后，才由命令服务分配一个租户级批次号。</p>
 *
 * @param rowsToInvalidate  需要置为无效的行（已翻转有效标记，批次号保持原值）
 * @param targetsToInsert   需要新增的有效行目标
 */
public record WalletDispatchPlan(List<DeviceIdentityWallet> rowsToInvalidate,
                                 List<WalletDispatchTarget> targetsToInsert) {

    /**
     * 是否为空计划（无任何变更）。
     *
     * @return true 表示无需落库
     */
    public boolean isEmpty() {
        return rowsToInvalidate.isEmpty() && targetsToInsert.isEmpty();
    }
}
