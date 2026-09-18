package com.hzsun.aidevops.walletconfig.device.cmdservice;

import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.device.cmdservice.cmd.DeviceAddCmd;
import com.hzsun.aidevops.walletconfig.device.cmdservice.cmd.DeviceDeleteCmd;
import com.hzsun.aidevops.walletconfig.device.domain.Device;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceFactory;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceRepository;
import com.hzsun.aidevops.walletconfig.device.domain.specification.DeviceSerialNoMustNotBeOccupiedSpecification;
import com.hzsun.aidevops.walletconfig.device.domain.valueobject.DeviceRegistration;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.cmdservice.DeviceIdentityWalletCmdService;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备命令服务。
 *
 * <p>负责设备写流程编排与事务边界；归属机构必须已存在，序列号全局唯一由领域规约校验。</p>
 */
@Service
@RequiredArgsConstructor
public class DeviceCmdService {

    private final DeviceRepository deviceRepository;

    private final OrganizationRepository organizationRepository;

    private final DeviceIdentityWalletCmdService deviceIdentityWalletCmdService;

    private final IdGenerator idGenerator;

    /**
     * 新增设备。
     *
     * @param tenantId 租户 ID
     * @param cmd      新增命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Long tenantId, DeviceAddCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long organizationId = IdCodec.toLong(cmd.getOrganizationId());
        organizationRepository.findById(tenantId, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORGANIZATION_NOT_FOUND, "归属机构不存在"));

        DeviceRegistration registration = new DeviceRegistration(
                cmd.getSerialNo(),
                cmd.getDeviceType(),
                organizationId,
                deviceRepository.findBySerialNo(cmd.getSerialNo()).isPresent());

        ValidationResult serialNoResult = new DeviceSerialNoMustNotBeOccupiedSpecification().validate(registration);
        if (!serialNoResult.isValid()) {
            throw new BusinessException(ErrorCodes.DEVICE_SERIAL_NO_DUPLICATED, serialNoResult.getError());
        }
        Device device = DeviceFactory.create(idGenerator, tenantId, registration);
        deviceRepository.save(device);
        deviceIdentityWalletCmdService.refreshByDevice(tenantId, device.getId());
    }

    /**
     * 删除设备，其下发表数据一并清除。
     *
     * @param tenantId 租户 ID
     * @param cmd      删除命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long tenantId, DeviceDeleteCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long deviceId = IdCodec.toLong(cmd.getId());
        deviceRepository.findById(tenantId, deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.DEVICE_NOT_FOUND, "设备不存在"));
        deviceIdentityWalletCmdService.removeByDevice(tenantId, deviceId);
        deviceRepository.delete(tenantId, deviceId);
    }
}
