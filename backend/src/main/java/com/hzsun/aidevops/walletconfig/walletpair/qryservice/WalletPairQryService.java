package com.hzsun.aidevops.walletconfig.walletpair.qryservice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.mapper.WalletPairMapper;
import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.po.WalletPairPo;
import com.hzsun.aidevops.walletconfig.walletpair.qryservice.assembler.WalletPairQryAssembler;
import com.hzsun.aidevops.walletconfig.walletpair.qryservice.dto.WalletPairDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 租户钱包配对查询服务。
 *
 * <p>读链路直接调用 Mapper 查询，不绕行领域层执行业务编排。</p>
 */
@Service
@RequiredArgsConstructor
public class WalletPairQryService {

    private final WalletPairMapper walletPairMapper;

    private final WalletPairQryAssembler walletPairQryAssembler;

    /**
     * 查询租户下的钱包配对列表，工作钱包编号升序。
     *
     * @param tenantId 租户 ID
     * @return 钱包配对列表，无数据时返回空列表
     */
    public List<WalletPairDto> list(Long tenantId) {
        TenantIds.requireValid(tenantId);
        return walletPairMapper.selectList(new LambdaQueryWrapper<WalletPairPo>()
                        .eq(WalletPairPo::getTenantId, tenantId)
                        .orderByAsc(WalletPairPo::getWorkWalletNo))
                .stream()
                .map(walletPairQryAssembler::toDto)
                .toList();
    }
}
