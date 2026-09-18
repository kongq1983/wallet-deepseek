package com.hzsun.aidevops.walletconfig.organization.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzsun.aidevops.walletconfig.organization.infrastructure.po.OrganizationPo;

/**
 * 机构 Mapper。
 *
 * <p>仅承载 {@code organization} 表的持久化访问，禁止跨聚合读写。</p>
 */
public interface OrganizationMapper extends BaseMapper<OrganizationPo> {
}
