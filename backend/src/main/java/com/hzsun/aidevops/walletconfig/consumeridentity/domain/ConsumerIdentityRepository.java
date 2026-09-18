package com.hzsun.aidevops.walletconfig.consumeridentity.domain;

import java.util.List;
import java.util.Optional;

/**
 * 消费身份仓储接口。
 */
public interface ConsumerIdentityRepository {

    /**
     * 保存新的身份。
     *
     * @param identity 身份聚合根
     */
    void save(ConsumerIdentity identity);

    /**
     * 更新已存在的身份。
     *
     * @param identity 身份聚合根
     */
    void update(ConsumerIdentity identity);

    /**
     * 删除身份。
     *
     * @param tenantId 租户 ID
     * @param id       身份 ID
     */
    void delete(Long tenantId, Long id);

    /**
     * 按 ID 查询身份。
     *
     * @param tenantId 租户 ID
     * @param id       身份 ID
     * @return 身份聚合根
     */
    Optional<ConsumerIdentity> findById(Long tenantId, Long id);

    /**
     * 查询租户下全部身份。
     *
     * @param tenantId 租户 ID
     * @return 身份列表
     */
    List<ConsumerIdentity> findAll(Long tenantId);

    /**
     * 按 ID 集合批量查询身份。
     *
     * @param tenantId 租户 ID
     * @param ids      身份 ID 集合
     * @return 身份列表
     */
    List<ConsumerIdentity> findByIds(Long tenantId, List<Long> ids);
}
