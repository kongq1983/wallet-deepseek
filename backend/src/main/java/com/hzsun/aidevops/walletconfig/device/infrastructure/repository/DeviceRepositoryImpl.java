package com.hzsun.aidevops.walletconfig.device.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.walletconfig.device.domain.Device;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceRepository;
import com.hzsun.aidevops.walletconfig.device.infrastructure.convertor.DeviceConvertor;
import com.hzsun.aidevops.walletconfig.device.infrastructure.mapper.DeviceMapper;
import com.hzsun.aidevops.walletconfig.device.infrastructure.po.DevicePo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 设备仓储实现。
 *
 * <p>tenant 归属型查询显式限定 {@code tenant_id}；序列号查询按全局唯一语义不限定租户。</p>
 */
@Repository
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepository {

    private final DeviceMapper deviceMapper;

    private final DeviceConvertor deviceConvertor;

    @Override
    public void save(Device device) {
        deviceMapper.insert(deviceConvertor.toPo(device));
    }

    @Override
    public void delete(Long tenantId, Long id) {
        deviceMapper.delete(new LambdaQueryWrapper<DevicePo>()
                .eq(DevicePo::getTenantId, tenantId)
                .eq(DevicePo::getId, id));
    }

    @Override
    public Optional<Device> findById(Long tenantId, Long id) {
        DevicePo po = deviceMapper.selectOne(new LambdaQueryWrapper<DevicePo>()
                .eq(DevicePo::getTenantId, tenantId)
                .eq(DevicePo::getId, id));
        return Optional.ofNullable(po).map(deviceConvertor::toDomain);
    }

    @Override
    public Optional<Device> findBySerialNo(String serialNo) {
        DevicePo po = deviceMapper.selectOne(new LambdaQueryWrapper<DevicePo>()
                .eq(DevicePo::getSerialNo, serialNo));
        return Optional.ofNullable(po).map(deviceConvertor::toDomain);
    }

    @Override
    public List<Device> findAll(Long tenantId) {
        return deviceMapper.selectList(new LambdaQueryWrapper<DevicePo>()
                        .eq(DevicePo::getTenantId, tenantId)
                        .orderByDesc(DevicePo::getCreatedAt))
                .stream()
                .map(deviceConvertor::toDomain)
                .toList();
    }

    @Override
    public List<Device> findByOrganizationId(Long tenantId, Long organizationId) {
        return deviceMapper.selectList(new LambdaQueryWrapper<DevicePo>()
                        .eq(DevicePo::getTenantId, tenantId)
                        .eq(DevicePo::getOrganizationId, organizationId))
                .stream()
                .map(deviceConvertor::toDomain)
                .toList();
    }
}
