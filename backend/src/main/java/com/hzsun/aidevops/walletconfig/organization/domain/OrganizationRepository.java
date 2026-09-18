package com.hzsun.aidevops.walletconfig.organization.domain;

import java.util.List;
import java.util.Optional;

/**
 * 机构仓储接口。
 *
 * <p>聚合根的还原必须通过本接口获取；调用方禁止绕过仓储自行构造或还原聚合根。</p>
 */
public interface OrganizationRepository {

    /**
     * 保存新的机构。
     *
     * @param organization 机构聚合根
     */
    void save(Organization organization);

    /**
     * 更新已存在的机构。
     *
     * @param organization 机构聚合根
     */
    void update(Organization organization);

    /**
     * 按 ID 查询机构。
     *
     * @param tenantId 租户 ID
     * @param id       机构 ID
     * @return 机构聚合根
     */
    Optional<Organization> findById(Long tenantId, Long id);

    /**
     * 查询租户下全部机构。
     *
     * @param tenantId 租户 ID
     * @return 机构列表
     */
    List<Organization> findAll(Long tenantId);

    /**
     * 查询指定上级机构下的直属机构。
     *
     * @param tenantId   租户 ID
     * @param superiorId 上级机构 ID，为 null 时查询顶层机构
     * @return 直属机构列表
     */
    List<Organization> findBySuperiorId(Long tenantId, Long superiorId);
}
