package com.hzsun.aidevops.walletconfig.walletpair.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPair;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairRepository;
import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.convertor.WalletPairConvertor;
import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.mapper.WalletPairMapper;
import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.po.WalletPairPo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 租户钱包配对仓储实现。
 *
 * <p>通过 MyBatis-Plus Mapper 与 PO 完成数据库读写，所有查改删语句显式限定 {@code tenant_id}。</p>
 */
@Repository
@RequiredArgsConstructor
public class WalletPairRepositoryImpl implements WalletPairRepository {

    private final WalletPairMapper walletPairMapper;

    private final WalletPairConvertor walletPairConvertor;

    @Override
    public void save(WalletPair walletPair) {
        walletPairMapper.insert(walletPairConvertor.toPo(walletPair));
    }

    @Override
    public void update(WalletPair walletPair) {
        walletPairMapper.updateById(walletPairConvertor.toPo(walletPair));
    }

    @Override
    public void delete(Long tenantId, Long id) {
        walletPairMapper.delete(new LambdaQueryWrapper<WalletPairPo>()
                .eq(WalletPairPo::getTenantId, tenantId)
                .eq(WalletPairPo::getId, id));
    }

    @Override
    public Optional<WalletPair> findById(Long tenantId, Long id) {
        WalletPairPo po = walletPairMapper.selectOne(new LambdaQueryWrapper<WalletPairPo>()
                .eq(WalletPairPo::getTenantId, tenantId)
                .eq(WalletPairPo::getId, id));
        return Optional.ofNullable(po).map(walletPairConvertor::toDomain);
    }

    @Override
    public List<WalletPair> findAll(Long tenantId) {
        return walletPairMapper.selectList(new LambdaQueryWrapper<WalletPairPo>()
                        .eq(WalletPairPo::getTenantId, tenantId)
                        .orderByAsc(WalletPairPo::getWorkWalletNo))
                .stream()
                .map(walletPairConvertor::toDomain)
                .toList();
    }
}
