package com.hzsun.aidevops.walletconfig.organization.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.walletconfig.organization.domain.Organization;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationRepository;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.convertor.OrganizationConvertor;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.mapper.OrganizationMapper;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.po.OrganizationPo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 机构仓储实现。
 *
 * <p>所有查改删语句显式限定 {@code tenant_id}。</p>
 */
@Repository
@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private final OrganizationMapper organizationMapper;

    private final OrganizationConvertor organizationConvertor;

    @Override
    public void save(Organization organization) {
        organizationMapper.insert(organizationConvertor.toPo(organization));
    }

    @Override
    public void update(Organization organization) {
        organizationMapper.updateById(organizationConvertor.toPo(organization));
    }

    @Override
    public Optional<Organization> findById(Long tenantId, Long id) {
        OrganizationPo po = organizationMapper.selectOne(new LambdaQueryWrapper<OrganizationPo>()
                .eq(OrganizationPo::getTenantId, tenantId)
                .eq(OrganizationPo::getId, id));
        return Optional.ofNullable(po).map(organizationConvertor::toDomain);
    }

    @Override
    public List<Organization> findAll(Long tenantId) {
        return organizationMapper.selectList(new LambdaQueryWrapper<OrganizationPo>()
                        .eq(OrganizationPo::getTenantId, tenantId)
                        .orderByAsc(OrganizationPo::getId))
                .stream()
                .map(organizationConvertor::toDomain)
                .toList();
    }

    @Override
    public List<Organization> findBySuperiorId(Long tenantId, Long superiorId) {
        LambdaQueryWrapper<OrganizationPo> wrapper = new LambdaQueryWrapper<OrganizationPo>()
                .eq(OrganizationPo::getTenantId, tenantId);
        if (superiorId == null) {
            wrapper.isNull(OrganizationPo::getSuperiorId);
        } else {
            wrapper.eq(OrganizationPo::getSuperiorId, superiorId);
        }
        return organizationMapper.selectList(wrapper).stream()
                .map(organizationConvertor::toDomain)
                .toList();
    }
}
