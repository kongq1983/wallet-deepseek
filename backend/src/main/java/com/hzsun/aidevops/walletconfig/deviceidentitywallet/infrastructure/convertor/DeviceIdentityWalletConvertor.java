package com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.convertor;

import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWallet;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletDimension;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletType;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.po.DeviceIdentityWalletPo;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 下发表持久化对象与领域对象转换器。
 */
@Mapper(componentModel = "spring")
public interface DeviceIdentityWalletConvertor {

    /** 有效标记：有效。 */
    int VALID_FLAG_VALID = 1;

    /** 有效标记：无效。 */
    int VALID_FLAG_INVALID = 0;

    /**
     * 聚合根转为持久化对象。
     *
     * @param row 下发表行
     * @return 持久化对象
     */
    @Mapping(target = "deviceId", source = "dimension.deviceId")
    @Mapping(target = "identityId", source = "dimension.identityId")
    @Mapping(target = "walletType", expression = "java(row.getDimension().walletType().name())")
    @Mapping(target = "walletNo", expression = "java(row.getWalletNo().value())")
    @Mapping(target = "validFlag", expression = "java(row.isValid() ? VALID_FLAG_VALID : VALID_FLAG_INVALID)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DeviceIdentityWalletPo toPo(DeviceIdentityWallet row);

    /**
     * 持久化对象还原为聚合根。
     *
     * @param po 持久化对象
     * @return 下发表行
     */
    default DeviceIdentityWallet toDomain(DeviceIdentityWalletPo po) {
        WalletDimension dimension = new WalletDimension(
                po.getDeviceId(), po.getIdentityId(), WalletType.valueOf(po.getWalletType()));
        return DeviceIdentityWallet.reconstitute(
                po.getId(),
                po.getTenantId(),
                dimension,
                WalletNo.of(po.getWalletNo()),
                Boolean.TRUE.equals(po.getDeductAllowed()),
                Boolean.TRUE.equals(po.getOfflineAllowed()),
                po.getValidFlag() != null && po.getValidFlag() == VALID_FLAG_VALID,
                po.getVersion() == null ? 0 : po.getVersion());
    }
}
