package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfig;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigRepository;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.convertor.OrganizationPaymentConfigConvertor;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.mapper.OrganizationIdentityMapper;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.mapper.OrganizationPaymentConfigMapper;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po.OrganizationIdentityPo;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po.OrganizationPaymentConfigPo;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 机构支付参数仓储实现。
 *
 * <p>可消费身份在保存时按机构维度全量覆盖；所有语句显式限定 {@code tenant_id}。</p>
 */
@Repository
@RequiredArgsConstructor
public class OrganizationPaymentConfigRepositoryImpl implements OrganizationPaymentConfigRepository {

    private final OrganizationPaymentConfigMapper organizationPaymentConfigMapper;

    private final OrganizationIdentityMapper organizationIdentityMapper;

    private final OrganizationPaymentConfigConvertor organizationPaymentConfigConvertor;

    private final IdGenerator idGenerator;

    @Override
    public void save(OrganizationPaymentConfig config) {
        organizationPaymentConfigMapper.insert(organizationPaymentConfigConvertor.toPo(config));
        replaceAllowedIdentities(config);
    }

    @Override
    public void update(OrganizationPaymentConfig config) {
        organizationPaymentConfigMapper.updateById(organizationPaymentConfigConvertor.toPo(config));
        replaceAllowedIdentities(config);
    }

    @Override
    public Optional<OrganizationPaymentConfig> findByOrganizationId(Long tenantId, Long organizationId) {
        OrganizationPaymentConfigPo po = organizationPaymentConfigMapper.selectOne(
                new LambdaQueryWrapper<OrganizationPaymentConfigPo>()
                        .eq(OrganizationPaymentConfigPo::getTenantId, tenantId)
                        .eq(OrganizationPaymentConfigPo::getOrganizationId, organizationId));
        if (po == null) {
            return Optional.empty();
        }
        Map<Long, Set<Long>> allowedIdentityIds = allowedIdentityIds(tenantId, Set.of(organizationId));
        return Optional.of(toDomain(po, allowedIdentityIds.getOrDefault(organizationId, Set.of())));
    }

    @Override
    public List<OrganizationPaymentConfig> findAll(Long tenantId) {
        List<OrganizationPaymentConfigPo> configs = organizationPaymentConfigMapper.selectList(
                new LambdaQueryWrapper<OrganizationPaymentConfigPo>()
                        .eq(OrganizationPaymentConfigPo::getTenantId, tenantId));
        if (configs.isEmpty()) {
            return List.of();
        }
        Set<Long> organizationIds = configs.stream()
                .map(OrganizationPaymentConfigPo::getOrganizationId)
                .collect(Collectors.toSet());
        Map<Long, Set<Long>> allowedIdentityIds = allowedIdentityIds(tenantId, organizationIds);
        return configs.stream()
                .map(po -> toDomain(po, allowedIdentityIds.getOrDefault(po.getOrganizationId(), Set.of())))
                .toList();
    }

    @Override
    public void removeIdentityReference(Long tenantId, Long identityId) {
        organizationIdentityMapper.delete(new LambdaQueryWrapper<OrganizationIdentityPo>()
                .eq(OrganizationIdentityPo::getTenantId, tenantId)
                .eq(OrganizationIdentityPo::getIdentityId, identityId));
    }

    /**
     * 按机构维度全量覆盖可消费身份。
     *
     * @param config 机构支付参数聚合根
     */
    private void replaceAllowedIdentities(OrganizationPaymentConfig config) {
        organizationIdentityMapper.delete(new LambdaQueryWrapper<OrganizationIdentityPo>()
                .eq(OrganizationIdentityPo::getTenantId, config.getTenantId())
                .eq(OrganizationIdentityPo::getOrganizationId, config.getOrganizationId()));
        for (Long identityId : config.getAllowedIdentityIds()) {
            organizationIdentityMapper.insert(organizationPaymentConfigConvertor.toIdentityPo(
                    idGenerator.nextId(), config.getTenantId(), config.getOrganizationId(), identityId));
        }
    }

    private Map<Long, Set<Long>> allowedIdentityIds(Long tenantId, Set<Long> organizationIds) {
        if (organizationIds.isEmpty()) {
            return Map.of();
        }
        return organizationIdentityMapper.selectList(new LambdaQueryWrapper<OrganizationIdentityPo>()
                        .eq(OrganizationIdentityPo::getTenantId, tenantId)
                        .in(OrganizationIdentityPo::getOrganizationId, organizationIds))
                .stream()
                .collect(Collectors.groupingBy(OrganizationIdentityPo::getOrganizationId,
                        Collectors.mapping(OrganizationIdentityPo::getIdentityId, Collectors.toSet())));
    }

    private OrganizationPaymentConfig toDomain(OrganizationPaymentConfigPo po, Set<Long> allowedIdentityIds) {
        return OrganizationPaymentConfig.reconstitute(
                po.getId(),
                po.getTenantId(),
                po.getOrganizationId(),
                Boolean.TRUE.equals(po.getDeductAllowed()),
                WalletNo.of(po.getWorkWalletNo()),
                Boolean.TRUE.equals(po.getIdentityRestricted()),
                Boolean.TRUE.equals(po.getOfflineAllowed()),
                allowedIdentityIds);
    }
}
