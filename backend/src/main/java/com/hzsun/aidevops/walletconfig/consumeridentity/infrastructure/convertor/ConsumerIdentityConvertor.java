package com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.convertor;

import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentity;
import com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.po.ConsumerIdentityPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 消费身份持久化对象与领域对象转换器。
 */
@Mapper(componentModel = "spring")
public interface ConsumerIdentityConvertor {

    /**
     * 聚合根转为持久化对象。
     *
     * @param identity 身份聚合根
     * @return 持久化对象
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ConsumerIdentityPo toPo(ConsumerIdentity identity);

    /**
     * 持久化对象还原为聚合根。
     *
     * @param po 持久化对象
     * @return 身份聚合根
     */
    default ConsumerIdentity toDomain(ConsumerIdentityPo po) {
        return ConsumerIdentity.reconstitute(
                po.getId(),
                po.getTenantId(),
                po.getName(),
                Boolean.TRUE.equals(po.getOfflineAllowed()));
    }
}
