package com.hzsun.aidevops.walletconfig.device.qryservice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.device.infrastructure.mapper.DeviceMapper;
import com.hzsun.aidevops.walletconfig.device.infrastructure.po.DevicePo;
import com.hzsun.aidevops.walletconfig.device.qryservice.assembler.DeviceQryAssembler;
import com.hzsun.aidevops.walletconfig.device.qryservice.dto.DeviceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备查询服务。
 *
 * <p>默认按创建时间倒序展示，支持按设备序列号搜索。</p>
 */
@Service
@RequiredArgsConstructor
public class DeviceQryService {

    private final DeviceMapper deviceMapper;

    private final DeviceQryAssembler deviceQryAssembler;

    /**
     * 查询租户下的设备列表。
     *
     * @param tenantId 租户 ID
     * @param serialNo 设备序列号搜索关键字，为空表示不筛选
     * @return 设备列表，无数据时返回空列表
     */
    public List<DeviceDto> list(Long tenantId, String serialNo) {
        TenantIds.requireValid(tenantId);
        LambdaQueryWrapper<DevicePo> wrapper = new LambdaQueryWrapper<DevicePo>()
                .eq(DevicePo::getTenantId, tenantId)
                .orderByDesc(DevicePo::getCreatedAt);
        if (serialNo != null && !serialNo.isBlank()) {
            wrapper.like(DevicePo::getSerialNo, serialNo.trim());
        }
        return deviceMapper.selectList(wrapper).stream()
                .map(deviceQryAssembler::toDto)
                .toList();
    }
}
