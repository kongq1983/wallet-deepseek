package com.hzsun.aidevops.walletconfig.organization.domain;

import com.hzsun.aidevops.walletconfig.organization.domain.valueobject.OrganizationRegistration;
import lombok.Getter;

/**
 * 机构聚合根。
 *
 * <p>表达机构树中的一个节点：具备名称、类型与上级机构。机构创建后只允许改名，
 * 不允许删除、停用、改类型或变更上级机构。</p>
 */
@Getter
public class Organization {

    /** 系统内部 ID。 */
    private final Long id;

    /** 归属租户 ID。 */
    private final Long tenantId;

    /** 机构类型。 */
    private final OrganizationType orgType;

    /** 上级机构 ID，为空表示顶层机构。 */
    private final Long superiorId;

    /** 机构名称。 */
    private String name;

    private Organization(Long id, Long tenantId, String name, OrganizationType orgType, Long superiorId) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.orgType = orgType;
        this.superiorId = superiorId;
    }

    /**
     * 业务创建：仅供 {@link OrganizationFactory} 调用。
     *
     * @param id           系统内部 ID
     * @param tenantId     归属租户 ID
     * @param registration 机构登记信息
     * @return 机构聚合根
     */
    static Organization create(Long id, Long tenantId, OrganizationRegistration registration) {
        return new Organization(id, tenantId, registration.name(), registration.orgType(), registration.superiorId());
    }

    /**
     * 持久化还原：仅供基础设施层仓储实现调用，禁止在业务链路中直接使用。
     *
     * @param id         系统内部 ID
     * @param tenantId   归属租户 ID
     * @param name       机构名称
     * @param orgType    机构类型
     * @param superiorId 上级机构 ID
     * @return 机构聚合根
     */
    public static Organization reconstitute(Long id, Long tenantId, String name, OrganizationType orgType, Long superiorId) {
        return new Organization(id, tenantId, name, orgType, superiorId);
    }

    /**
     * 变更机构名称。
     *
     * @param newName 新名称
     */
    public void rename(String newName) {
        this.name = newName;
    }

    /**
     * 是否为档口类型。
     *
     * @return true 表示档口
     */
    public boolean isStall() {
        return orgType == OrganizationType.STALL;
    }
}
