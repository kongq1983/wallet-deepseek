package com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentity;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityRepository;
import com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.convertor.ConsumerIdentityConvertor;
import com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.mapper.ConsumerIdentityMapper;
import com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.po.ConsumerIdentityPo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消费身份仓储实现。
 *
 * <p>所有查改删语句显式限定 {@code tenant_id}。</p>
 */
@Repository
@RequiredArgsConstructor
public class ConsumerIdentityRepositoryImpl implements ConsumerIdentityRepository {

    private final ConsumerIdentityMapper consumerIdentityMapper;

    private final ConsumerIdentityConvertor consumerIdentityConvertor;

    @Override
    public void save(ConsumerIdentity identity) {
        consumerIdentityMapper.insert(consumerIdentityConvertor.toPo(identity));
    }

    @Override
    public void update(ConsumerIdentity identity) {
        consumerIdentityMapper.updateById(consumerIdentityConvertor.toPo(identity));
    }

    @Override
    public void delete(Long tenantId, Long id) {
        consumerIdentityMapper.delete(new LambdaQueryWrapper<ConsumerIdentityPo>()
                .eq(ConsumerIdentityPo::getTenantId, tenantId)
                .eq(ConsumerIdentityPo::getId, id));
    }

    @Override
    public Optional<ConsumerIdentity> findById(Long tenantId, Long id) {
        ConsumerIdentityPo po = consumerIdentityMapper.selectOne(new LambdaQueryWrapper<ConsumerIdentityPo>()
                .eq(ConsumerIdentityPo::getTenantId, tenantId)
                .eq(ConsumerIdentityPo::getId, id));
        return Optional.ofNullable(po).map(consumerIdentityConvertor::toDomain);
    }

    @Override
    public List<ConsumerIdentity> findAll(Long tenantId) {
        return consumerIdentityMapper.selectList(new LambdaQueryWrapper<ConsumerIdentityPo>()
                        .eq(ConsumerIdentityPo::getTenantId, tenantId)
                        .orderByAsc(ConsumerIdentityPo::getId))
                .stream()
                .map(consumerIdentityConvertor::toDomain)
                .toList();
    }

    @Override
    public List<ConsumerIdentity> findByIds(Long tenantId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return consumerIdentityMapper.selectList(new LambdaQueryWrapper<ConsumerIdentityPo>()
                        .eq(ConsumerIdentityPo::getTenantId, tenantId)
                        .in(ConsumerIdentityPo::getId, ids))
                .stream()
                .map(consumerIdentityConvertor::toDomain)
                .toList();
    }
}
