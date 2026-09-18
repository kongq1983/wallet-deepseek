package com.hzsun.aidevops.walletconfig.consumeridentity.domain;

import com.hzsun.aidevops.walletconfig.consumeridentity.domain.valueobject.IdentityRegistration;
import lombok.Getter;

/**
 * 消费身份聚合根。
 *
 * <p>身份表达消费主体类别（如学生、老师），在平台内创建维护、扁平一层。
 * 身份可改名与删除，不支持停用；删除后其下发表数据置为无效。</p>
 */
@Getter
public class ConsumerIdentity {

    /** 系统内部 ID。 */
    private final Long id;

    /** 归属租户 ID。 */
    private final Long tenantId;

    /** 身份名称。 */
    private String name;

    /** 是否允许脱机消费。 */
    private boolean offlineAllowed;

    private ConsumerIdentity(Long id, Long tenantId, String name, boolean offlineAllowed) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.offlineAllowed = offlineAllowed;
    }

    /**
     * 业务创建：仅供 {@link ConsumerIdentityFactory} 调用。
     *
     * @param id           系统内部 ID
     * @param tenantId     归属租户 ID
     * @param registration 身份登记信息
     * @param offlineAllowed 是否允许脱机消费
     * @return 身份聚合根
     */
    static ConsumerIdentity create(Long id, Long tenantId, IdentityRegistration registration, boolean offlineAllowed) {
        return new ConsumerIdentity(id, tenantId, registration.name(), offlineAllowed);
    }

    /**
     * 持久化还原：仅供基础设施层仓储实现调用，禁止在业务链路中直接使用。
     *
     * @param id             系统内部 ID
     * @param tenantId       归属租户 ID
     * @param name           身份名称
     * @param offlineAllowed 是否允许脱机消费
     * @return 身份聚合根
     */
    public static ConsumerIdentity reconstitute(Long id, Long tenantId, String name, boolean offlineAllowed) {
        return new ConsumerIdentity(id, tenantId, name, offlineAllowed);
    }

    /**
     * 变更身份名称与脱机消费参数。
     *
     * @param newName        新名称
     * @param offlineAllowed 是否允许脱机消费
     */
    public void changeProfile(String newName, boolean offlineAllowed) {
        this.name = newName;
        this.offlineAllowed = offlineAllowed;
    }
}
