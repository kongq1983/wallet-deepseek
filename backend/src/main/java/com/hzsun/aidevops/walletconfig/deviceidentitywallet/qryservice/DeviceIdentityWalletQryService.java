package com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentity;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityRepository;
import com.hzsun.aidevops.walletconfig.device.domain.Device;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceRepository;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.mapper.DeviceIdentityWalletMapper;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.po.DeviceIdentityWalletPo;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.assembler.DeviceIdentityWalletQryAssembler;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.dto.DeviceIdentityWalletDto;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.qry.DeviceIdentityWalletListQry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 下发表查询服务。
 *
 * <p>默认按版本号倒序展示；支持按设备、交易身份与有效标记筛选。
 * 设备序列号与交易身份名称由本服务补充，便于核对下发内容。</p>
 */
@Service
@RequiredArgsConstructor
public class DeviceIdentityWalletQryService {

    private final DeviceIdentityWalletMapper deviceIdentityWalletMapper;

    private final DeviceIdentityWalletQryAssembler deviceIdentityWalletQryAssembler;

    private final DeviceRepository deviceRepository;

    private final ConsumerIdentityRepository consumerIdentityRepository;

    /**
     * 查询下发表数据。
     *
     * @param tenantId 租户 ID
     * @param qry      查询条件
     * @return 下发表列表，无数据时返回空列表
     */
    public List<DeviceIdentityWalletDto> list(Long tenantId, DeviceIdentityWalletListQry qry) {
        TenantIds.requireValid(tenantId);
        LambdaQueryWrapper<DeviceIdentityWalletPo> wrapper = new LambdaQueryWrapper<DeviceIdentityWalletPo>()
                .eq(DeviceIdentityWalletPo::getTenantId, tenantId)
                .orderByDesc(DeviceIdentityWalletPo::getVersion);
        if (qry != null) {
            if (qry.getDeviceId() != null && !qry.getDeviceId().isBlank()) {
                wrapper.eq(DeviceIdentityWalletPo::getDeviceId, IdCodec.toLong(qry.getDeviceId()));
            }
            if (qry.getIdentityId() != null && !qry.getIdentityId().isBlank()) {
                wrapper.eq(DeviceIdentityWalletPo::getIdentityId, IdCodec.toLong(qry.getIdentityId()));
            }
            if (qry.getValidFlag() != null && !qry.getValidFlag().isBlank()) {
                wrapper.eq(DeviceIdentityWalletPo::getValidFlag, Integer.valueOf(qry.getValidFlag().trim()));
            }
        }

        List<DeviceIdentityWalletPo> rows = deviceIdentityWalletMapper.selectList(wrapper);
        if (rows.isEmpty()) {
            return List.of();
        }
        Map<Long, Device> deviceIndex = deviceRepository.findAll(tenantId).stream()
                .collect(Collectors.toMap(Device::getId, Function.identity(), (left, right) -> left));
        Map<Long, ConsumerIdentity> identityIndex = consumerIdentityRepository.findAll(tenantId).stream()
                .collect(Collectors.toMap(ConsumerIdentity::getId, Function.identity(), (left, right) -> left));

        return rows.stream()
                .map(po -> {
                    DeviceIdentityWalletDto dto = deviceIdentityWalletQryAssembler.toDto(po);
                    Device device = deviceIndex.get(po.getDeviceId());
                    dto.setDeviceSerialNo(device == null ? null : device.getSerialNo());
                    ConsumerIdentity identity = identityIndex.get(po.getIdentityId());
                    dto.setIdentityName(identity == null ? null : identity.getName());
                    return dto;
                })
                .toList();
    }
}
