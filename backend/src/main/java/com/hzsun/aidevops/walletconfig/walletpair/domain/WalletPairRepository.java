package com.hzsun.aidevops.walletconfig.walletpair.domain;

import java.util.List;
import java.util.Optional;

/**
 * 钱包配对仓储接口。
 *
 * <p>聚合根的还原必须通过本接口获取；调用方禁止绕过仓储自行构造或还原聚合根。</p>
 */
public interface WalletPairRepository {

    /**
     * 保存新的钱包配对。
     *
     * @param walletPair 钱包配对聚合根
     */
    void save(WalletPair walletPair);

    /**
     * 更新已存在的钱包配对。
     *
     * @param walletPair 钱包配对聚合根
     */
    void update(WalletPair walletPair);

    /**
     * 删除钱包配对。
     *
     * @param tenantId 租户 ID
     * @param id       钱包配对 ID
     */
    void delete(Long tenantId, Long id);

    /**
     * 按 ID 查询钱包配对。
     *
     * @param tenantId 租户 ID
     * @param id       钱包配对 ID
     * @return 钱包配对聚合根
     */
    Optional<WalletPair> findById(Long tenantId, Long id);

    /**
     * 查询租户下全部钱包配对。
     *
     * @param tenantId 租户 ID
     * @return 钱包配对列表
     */
    List<WalletPair> findAll(Long tenantId);
}
