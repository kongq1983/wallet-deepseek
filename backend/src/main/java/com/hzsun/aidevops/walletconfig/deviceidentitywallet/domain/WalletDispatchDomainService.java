package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;

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
 * 版本号在同一维度上单调递增：生成、失效、换号三类事件都推进版本号；
 * 未受影响的维度版本号与有效标记均不变。</p>
 */
public final class WalletDispatchDomainService {

    private WalletDispatchDomainService() {
    }

    /**
     * 计算下发表变更计划。
     *
     * @param idGenerator  内部 ID 生成器
     * @param tenantId     归属租户 ID
     * @param existingRows 现有行（含历史失效行，用于推导维度上的最大版本号）
     * @param targets      目标行集合
     * @return 变更计划
     */
    public static WalletDispatchPlan plan(IdGenerator idGenerator, Long tenantId,
                                          List<DeviceIdentityWallet> existingRows,
                                          List<WalletDispatchTarget> targets) {
        Map<WalletDimension, DeviceIdentityWallet> validRows = new HashMap<>();
        Map<WalletDimension, Integer> maxVersions = new HashMap<>();
        for (DeviceIdentityWallet row : existingRows) {
            if (row.isValid()) {
                validRows.put(row.getDimension(), row);
            }
            maxVersions.merge(row.getDimension(), row.getVersion(), Math::max);
        }

        List<DeviceIdentityWallet> rowsToInvalidate = new ArrayList<>();
        List<DeviceIdentityWallet> rowsToInsert = new ArrayList<>();
        Set<WalletDimension> targetDimensions = new HashSet<>();

        for (WalletDispatchTarget target : targets) {
            WalletDimension dimension = target.dimension();
            targetDimensions.add(dimension);

            DeviceIdentityWallet current = validRows.get(dimension);
            if (current != null && current.matches(target)) {
                continue;
            }
            int nextVersion = maxVersions.getOrDefault(dimension, 0) + 1;
            if (current != null) {
                current.invalidate(nextVersion);
                rowsToInvalidate.add(current);
                nextVersion++;
            }
            rowsToInsert.add(DeviceIdentityWalletFactory.create(idGenerator, tenantId, target, nextVersion));
            maxVersions.put(dimension, nextVersion);
        }

        for (Map.Entry<WalletDimension, DeviceIdentityWallet> entry : validRows.entrySet()) {
            if (targetDimensions.contains(entry.getKey())) {
                continue;
            }
            DeviceIdentityWallet row = entry.getValue();
            row.invalidate(maxVersions.getOrDefault(entry.getKey(), row.getVersion()) + 1);
            rowsToInvalidate.add(row);
        }
        return new WalletDispatchPlan(rowsToInvalidate, rowsToInsert);
    }
}
