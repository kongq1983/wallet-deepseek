package com.hzsun.aidevops.walletconfig.walletpair.infrastructure.convertor;

import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPair;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.po.WalletPairPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 租户钱包配对持久化对象与领域对象转换器。
 *
 * <p>承担 PO 与聚合根之间的双向转换，领域层之外禁止散落字段拷贝逻辑。</p>
 */
@Mapper(componentModel = "spring")
public interface WalletPairConvertor {

    /**
     * 聚合根转为持久化对象。
     *
     * @param walletPair 钱包配对聚合根
     * @return 持久化对象
     */
    @Mapping(target = "workWalletNo", source = "workWalletNo.value")
    @Mapping(target = "deductWalletNo", source = "deductWalletNo.value")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WalletPairPo toPo(WalletPair walletPair);

    /**
     * 持久化对象还原为聚合根。
     *
     * @param po 持久化对象
     * @return 钱包配对聚合根
     */
    default WalletPair toDomain(WalletPairPo po) {
        return WalletPair.reconstitute(
                po.getId(),
                po.getTenantId(),
                WalletNo.of(po.getWorkWalletNo()),
                WalletNo.of(po.getDeductWalletNo()));
    }
}
