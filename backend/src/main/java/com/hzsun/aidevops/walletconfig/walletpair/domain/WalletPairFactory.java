package com.hzsun.aidevops.walletconfig.walletpair.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;

/**
 * 钱包配对聚合根工厂。
 *
 * <p>以静态方法提供业务创建入口，不实例化；不承担持久化还原、数据库读写与领域事件发布。</p>
 */
public final class WalletPairFactory {

    private WalletPairFactory() {
    }

    /**
     * 创建钱包配对聚合根。
     *
     * @param idGenerator   内部 ID 生成器
     * @param tenantId      归属租户 ID
     * @param configuration 目标配置
     * @return 新建的钱包配对聚合根
     */
    public static WalletPair create(IdGenerator idGenerator, Long tenantId, WalletPairConfiguration configuration) {
        return WalletPair.create(idGenerator.nextId(), tenantId, configuration);
    }
}
