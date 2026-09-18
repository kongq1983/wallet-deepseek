package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import java.util.List;

/**
 * 下发表仓储接口。
 *
 * <p>查询结果包含历史失效行，供差异比较与版本号推导使用。</p>
 */
public interface DeviceIdentityWalletRepository {

    /**
     * 批量插入新的有效行。
     *
     * @param rows 下发表行集合
     */
    void saveAll(List<DeviceIdentityWallet> rows);

    /**
     * 批量更新行（置为无效并推进版本号）。
     *
     * @param rows 下发表行集合
     */
    void updateAll(List<DeviceIdentityWallet> rows);

    /**
     * 按设备 ID 集合查询全部行（含历史失效行）。
     *
     * @param tenantId  租户 ID
     * @param deviceIds 设备 ID 集合
     * @return 下发表行集合
     */
    List<DeviceIdentityWallet> findByDeviceIds(Long tenantId, List<Long> deviceIds);

    /**
     * 按身份 ID 集合查询全部行（含历史失效行）。
     *
     * @param tenantId    租户 ID
     * @param identityIds 身份 ID 集合
     * @return 下发表行集合
     */
    List<DeviceIdentityWallet> findByIdentityIds(Long tenantId, List<Long> identityIds);

    /**
     * 查询租户下全部行（含历史失效行）。
     *
     * @param tenantId 租户 ID
     * @return 下发表行集合
     */
    List<DeviceIdentityWallet> findAll(Long tenantId);

    /**
     * 按设备 ID 集合物理删除下发表数据。
     *
     * <p>仅在设备被删除（连带删除）时使用；其余场景一律通过置为无效保留历史。</p>
     *
     * @param tenantId  租户 ID
     * @param deviceIds 设备 ID 集合
     */
    void deleteByDeviceIds(Long tenantId, List<Long> deviceIds);
}
