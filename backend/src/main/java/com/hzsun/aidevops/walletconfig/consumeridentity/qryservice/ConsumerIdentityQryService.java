package com.hzsun.aidevops.walletconfig.consumeridentity.qryservice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.mapper.ConsumerIdentityMapper;
import com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.po.ConsumerIdentityPo;
import com.hzsun.aidevops.walletconfig.consumeridentity.qryservice.assembler.ConsumerIdentityQryAssembler;
import com.hzsun.aidevops.walletconfig.consumeridentity.qryservice.dto.ConsumerIdentityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消费身份查询服务。
 */
@Service
@RequiredArgsConstructor
public class ConsumerIdentityQryService {

    private final ConsumerIdentityMapper consumerIdentityMapper;

    private final ConsumerIdentityQryAssembler consumerIdentityQryAssembler;

    /**
     * 查询租户下的身份列表。
     *
     * @param tenantId 租户 ID
     * @return 身份列表，无数据时返回空列表
     */
    public List<ConsumerIdentityDto> list(Long tenantId) {
        TenantIds.requireValid(tenantId);
        return consumerIdentityMapper.selectList(new LambdaQueryWrapper<ConsumerIdentityPo>()
                        .eq(ConsumerIdentityPo::getTenantId, tenantId)
                        .orderByAsc(ConsumerIdentityPo::getId))
                .stream()
                .map(consumerIdentityQryAssembler::toDto)
                .toList();
    }
}
