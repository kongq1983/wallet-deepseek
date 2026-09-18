package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;

/**
 * 下发表聚合根工厂。
 *
 * <p>以静态方法提供业务创建入口，不实例化。</p>
 */
public final class DeviceIdentityWalletFactory {

    private DeviceIdentityWalletFactory() {
    }

    /**
     * 创建下发表行并写入目标内容。
     *
     * @param idGenerator 内部 ID 生成器
     * @param tenantId    归属租户 ID
     * @param target      目标行
     * @param version     版本号
     * @return 新建的下发表行
     */
    public static DeviceIdentityWallet create(IdGenerator idGenerator, Long tenantId, WalletDispatchTarget target,
                                              int version) {
        DeviceIdentityWallet row = DeviceIdentityWallet.create(idGenerator.nextId(), tenantId, target.dimension());
        row.apply(target, version);
        return row;
    }
}
