package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 下发表重算领域服务。
 *
 * <p>纯内存差异比较：把“由配置推导出的目标行”与“现有行”做逐维度比对，产出变更计划。
 * 只负责判断哪些行该失效、哪些行该新增；批次版本号由命令服务统一分配，本服务不参与版本号计算。</p>
 */
public final class WalletDispatchDomainService {

    private WalletDispatchDomainService() {
    }

    /**
     * 计算下发表变更计划。
     *
     * @param existingRows 现有行（含历史失效行）
     * @param targets      目标行集合
     * @return 变更计划
     */
    public static WalletDispatchPlan plan(List<DeviceIdentityWallet> existingRows,
                                          List<WalletDispatchTarget> targets) {
        Map<WalletDimension, DeviceIdentityWallet> validRows = new HashMap<>();
        for (DeviceIdentityWallet row : existingRows) {
            if (row.isValid()) {
                validRows.put(row.getDimension(), row);
            }
        }

        List<DeviceIdentityWallet> rowsToInvalidate = new ArrayList<>();
        List<WalletDispatchTarget> targetsToInsert = new ArrayList<>();
        Set<WalletDimension> targetDimensions = new HashSet<>();

        for (WalletDispatchTarget target : targets) {
            WalletDimension dimension = target.dimension();
            // 同一维度重复出现时只保留一条：数据库按维度保证仅一行有效，重复新增会撞唯一索引
            if (!targetDimensions.add(dimension)) {
                continue;
            }
            DeviceIdentityWallet current = validRows.get(dimension);
            if (current != null && current.matches(target)) {
                continue;
            }
            if (current != null) {
                current.invalidate();
                rowsToInvalidate.add(current);
            }
            targetsToInsert.add(target);
        }

        // 配置消失：现有有效行不在本次目标集合内，置为无效并保留原批次号
        for (Map.Entry<WalletDimension, DeviceIdentityWallet> entry : validRows.entrySet()) {
            if (targetDimensions.contains(entry.getKey())) {
                continue;
            }
            entry.getValue().invalidate();
            rowsToInvalidate.add(entry.getValue());
        }
        return new WalletDispatchPlan(rowsToInvalidate, targetsToInsert);
    }
}
