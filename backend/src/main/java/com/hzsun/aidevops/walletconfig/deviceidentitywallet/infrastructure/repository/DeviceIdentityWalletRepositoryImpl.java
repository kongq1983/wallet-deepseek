package com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWallet;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWalletRepository;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.convertor.DeviceIdentityWalletConvertor;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.mapper.DeviceIdentityWalletMapper;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.po.DeviceIdentityWalletPo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 下发表仓储实现。
 *
 * <p>所有语句显式限定 {@code tenant_id}；失效行永久保留，仅设备删除场景做物理连带删除。</p>
 */
@Repository
@RequiredArgsConstructor
public class DeviceIdentityWalletRepositoryImpl implements DeviceIdentityWalletRepository {

    private final DeviceIdentityWalletMapper deviceIdentityWalletMapper;

    private final DeviceIdentityWalletConvertor deviceIdentityWalletConvertor;

    @Override
    public void saveAll(List<DeviceIdentityWallet> rows) {
        for (DeviceIdentityWallet row : rows) {
            deviceIdentityWalletMapper.insert(deviceIdentityWalletConvertor.toPo(row));
        }
    }

    @Override
    public void updateAll(List<DeviceIdentityWallet> rows) {
        for (DeviceIdentityWallet row : rows) {
            deviceIdentityWalletMapper.updateById(deviceIdentityWalletConvertor.toPo(row));
        }
    }

    @Override
    public int findMaxVersion(Long tenantId) {
        Integer maxVersion = deviceIdentityWalletMapper.selectMaxVersion(tenantId);
        return maxVersion == null ? 0 : maxVersion;
    }

    @Override
    public List<DeviceIdentityWallet> findByDeviceIds(Long tenantId, List<Long> deviceIds) {
        if (deviceIds.isEmpty()) {
            return List.of();
        }
        return selectAll(new LambdaQueryWrapper<DeviceIdentityWalletPo>()
                .eq(DeviceIdentityWalletPo::getTenantId, tenantId)
                .in(DeviceIdentityWalletPo::getDeviceId, deviceIds));
    }

    @Override
    public List<DeviceIdentityWallet> findByIdentityIds(Long tenantId, List<Long> identityIds) {
        if (identityIds.isEmpty()) {
            return List.of();
        }
        return selectAll(new LambdaQueryWrapper<DeviceIdentityWalletPo>()
                .eq(DeviceIdentityWalletPo::getTenantId, tenantId)
                .in(DeviceIdentityWalletPo::getIdentityId, identityIds));
    }

    @Override
    public List<DeviceIdentityWallet> findAll(Long tenantId) {
        return selectAll(new LambdaQueryWrapper<DeviceIdentityWalletPo>()
                .eq(DeviceIdentityWalletPo::getTenantId, tenantId));
    }

    @Override
    public void deleteByDeviceIds(Long tenantId, List<Long> deviceIds) {
        if (deviceIds.isEmpty()) {
            return;
        }
        deviceIdentityWalletMapper.delete(new LambdaQueryWrapper<DeviceIdentityWalletPo>()
                .eq(DeviceIdentityWalletPo::getTenantId, tenantId)
                .in(DeviceIdentityWalletPo::getDeviceId, deviceIds));
    }

    private List<DeviceIdentityWallet> selectAll(LambdaQueryWrapper<DeviceIdentityWalletPo> wrapper) {
        return deviceIdentityWalletMapper.selectList(wrapper).stream()
                .map(deviceIdentityWalletConvertor::toDomain)
                .toList();
    }
}
