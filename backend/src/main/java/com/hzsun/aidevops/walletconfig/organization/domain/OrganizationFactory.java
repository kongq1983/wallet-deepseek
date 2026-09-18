package com.hzsun.aidevops.walletconfig.organization.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.organization.domain.valueobject.OrganizationRegistration;

/**
 * 机构聚合根工厂。
 *
 * <p>以静态方法提供业务创建入口，不实例化；不承担持久化还原、数据库读写与领域事件发布。</p>
 */
public final class OrganizationFactory {

    private OrganizationFactory() {
    }

    /**
     * 创建机构聚合根。
     *
     * @param idGenerator  内部 ID 生成器
     * @param tenantId     归属租户 ID
     * @param registration 机构登记信息
     * @return 新建的机构聚合根
     */
    public static Organization create(IdGenerator idGenerator, Long tenantId, OrganizationRegistration registration) {
        return Organization.create(idGenerator.nextId(), tenantId, registration);
    }
}
