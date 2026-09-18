package com.hzsun.aidevops.walletconfig.organization.qryservice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.mapper.OrganizationMapper;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.po.OrganizationPo;
import com.hzsun.aidevops.walletconfig.organization.qryservice.assembler.OrganizationQryAssembler;
import com.hzsun.aidevops.walletconfig.organization.qryservice.dto.OrganizationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机构查询服务。
 *
 * <p>读链路直接调用 Mapper 查询，层级由上级链在内存中推导，供前端还原机构树。</p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationQryService {

    private final OrganizationMapper organizationMapper;

    private final OrganizationQryAssembler organizationQryAssembler;

    /**
     * 查询租户下的机构列表，按层级与层级内顺序排列。
     *
     * @param tenantId 租户 ID
     * @return 机构列表，无数据时返回空列表
     */
    public List<OrganizationDto> list(Long tenantId) {
        TenantIds.requireValid(tenantId);
        List<OrganizationPo> all = organizationMapper.selectList(new LambdaQueryWrapper<OrganizationPo>()
                .eq(OrganizationPo::getTenantId, tenantId)
                .orderByAsc(OrganizationPo::getId));
        Map<Long, OrganizationPo> index = new HashMap<>();
        all.forEach(po -> index.put(po.getId(), po));

        List<OrganizationDto> result = new ArrayList<>(all.size());
        for (OrganizationPo po : all) {
            result.add(organizationQryAssembler.toDto(po, levelOf(index, po.getId())));
        }
        result.sort(Comparator.comparing(OrganizationDto::getLevel));
        return result;
    }

    private int levelOf(Map<Long, OrganizationPo> index, Long organizationId) {
        int level = 1;
        OrganizationPo current = index.get(organizationId);
        while (current != null && current.getSuperiorId() != null) {
            level++;
            current = index.get(current.getSuperiorId());
        }
        return level;
    }
}
