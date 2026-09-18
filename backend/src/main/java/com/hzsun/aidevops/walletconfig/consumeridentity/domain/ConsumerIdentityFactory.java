package com.hzsun.aidevops.walletconfig.consumeridentity.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.valueobject.IdentityRegistration;

/**
 * 消费身份聚合根工厂。
 *
 * <p>以静态方法提供业务创建入口，不实例化。</p>
 */
public final class ConsumerIdentityFactory {

    private ConsumerIdentityFactory() {
    }

    /**
     * 创建消费身份聚合根。
     *
     * @param idGenerator    内部 ID 生成器
     * @param tenantId       归属租户 ID
     * @param registration   身份登记信息
     * @param offlineAllowed 是否允许脱机消费
     * @return 新建的身份聚合根
     */
    public static ConsumerIdentity create(IdGenerator idGenerator, Long tenantId, IdentityRegistration registration,
                                          boolean offlineAllowed) {
        return ConsumerIdentity.create(idGenerator.nextId(), tenantId, registration, offlineAllowed);
    }
}
