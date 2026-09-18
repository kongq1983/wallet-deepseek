package com.hzsun.aidevops.walletconfig.deviceidentitywallet.cmdservice;

import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentity;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityRepository;
import com.hzsun.aidevops.walletconfig.device.domain.Device;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceRepository;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWallet;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWalletRepository;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletDimension;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletDispatchDomainService;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletDispatchPlan;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletDispatchTarget;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletType;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfig;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigRepository;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairRepository;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 下发表命令服务。
 *
 * <p>下发表只由系统生成，无人工录入入口。任何关联配置变动都通过本服务重算受影响设备的下发表数据，
 * 由领域服务完成行级差异比较与版本号推进。</p>
 */
@Service
@RequiredArgsConstructor
public class DeviceIdentityWalletCmdService {

    private final DeviceIdentityWalletRepository deviceIdentityWalletRepository;

    private final DeviceRepository deviceRepository;

    private final OrganizationPaymentConfigRepository organizationPaymentConfigRepository;

    private final WalletPairRepository walletPairRepository;

    private final ConsumerIdentityRepository consumerIdentityRepository;

    private final IdGenerator idGenerator;

    /**
     * 按设备重算。
     *
     * @param tenantId 租户 ID
     * @param deviceId 设备 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshByDevice(Long tenantId, Long deviceId) {
        TenantIds.requireValid(tenantId);
        deviceRepository.findById(tenantId, deviceId)
                .ifPresent(device -> refreshDevices(tenantId, List.of(device)));
    }

    /**
     * 按机构重算该机构下全部设备。
     *
     * @param tenantId       租户 ID
     * @param organizationId 机构 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshByOrganization(Long tenantId, Long organizationId) {
        TenantIds.requireValid(tenantId);
        refreshDevices(tenantId, deviceRepository.findByOrganizationId(tenantId, organizationId));
    }

    /**
     * 按机构集合重算。
     *
     * @param tenantId        租户 ID
     * @param organizationIds 机构 ID 集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshByOrganizations(Long tenantId, Collection<Long> organizationIds) {
        TenantIds.requireValid(tenantId);
        for (Long organizationId : organizationIds) {
            refreshDevices(tenantId, deviceRepository.findByOrganizationId(tenantId, organizationId));
        }
    }

    /**
     * 按身份重算该身份涉及的全部设备。
     *
     * @param tenantId   租户 ID
     * @param identityId 身份 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshByIdentity(Long tenantId, Long identityId) {
        TenantIds.requireValid(tenantId);
        Set<Long> deviceIds = deviceIdentityWalletRepository.findByIdentityIds(tenantId, List.of(identityId)).stream()
                .map(row -> row.getDimension().deviceId())
                .collect(Collectors.toSet());
        List<Device> devices = deviceIds.stream()
                .map(deviceId -> deviceRepository.findById(tenantId, deviceId))
                .flatMap(Optional::stream)
                .toList();
        refreshDevices(tenantId, devices);
    }

    /**
     * 全量重算租户下全部设备的下发表。
     *
     * @param tenantId 租户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshAll(Long tenantId) {
        TenantIds.requireValid(tenantId);
        refreshDevices(tenantId, deviceRepository.findAll(tenantId));
    }

    /**
     * 设备删除时连带物理删除其下发表数据。
     *
     * @param tenantId 租户 ID
     * @param deviceId 设备 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByDevice(Long tenantId, Long deviceId) {
        TenantIds.requireValid(tenantId);
        deviceIdentityWalletRepository.deleteByDeviceIds(tenantId, List.of(deviceId));
    }

    private void refreshDevices(Long tenantId, List<Device> devices) {
        if (devices.isEmpty()) {
            return;
        }
        List<Long> deviceIds = devices.stream().map(Device::getId).toList();
        List<DeviceIdentityWallet> existingRows = deviceIdentityWalletRepository.findByDeviceIds(tenantId, deviceIds);
        List<WalletDispatchTarget> targets = buildTargets(tenantId, devices);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(idGenerator, tenantId, existingRows, targets);
        if (plan.isEmpty()) {
            return;
        }
        if (!plan.rowsToInvalidate().isEmpty()) {
            deviceIdentityWalletRepository.updateAll(plan.rowsToInvalidate());
        }
        if (!plan.rowsToInsert().isEmpty()) {
            deviceIdentityWalletRepository.saveAll(plan.rowsToInsert());
        }
    }

    /**
     * 依据机构支付参数、租户钱包配对与身份字典推导目标下发行。
     *
     * @param tenantId 租户 ID
     * @param devices  设备集合
     * @return 目标行集合
     */
    private List<WalletDispatchTarget> buildTargets(Long tenantId, List<Device> devices) {
        Map<Long, OrganizationPaymentConfig> configs = organizationPaymentConfigRepository.findAll(tenantId).stream()
                .collect(Collectors.toMap(OrganizationPaymentConfig::getOrganizationId, Function.identity(),
                        (left, right) -> left));
        List<ConsumerIdentity> identities = consumerIdentityRepository.findAll(tenantId);
        Map<Integer, Integer> walletPairMappings = walletPairRepository.findAll(tenantId).stream()
                .collect(Collectors.toMap(
                        pair -> pair.getWorkWalletNo().value(),
                        pair -> pair.getDeductWalletNo().value(),
                        (left, right) -> left));

        List<WalletDispatchTarget> targets = new ArrayList<>();
        for (Device device : devices) {
            OrganizationPaymentConfig config = configs.get(device.getOrganizationId());
            if (config == null) {
                continue;
            }
            List<ConsumerIdentity> consumableIdentities = config.isIdentityRestricted()
                    ? identities.stream().filter(identity -> config.getAllowedIdentityIds().contains(identity.getId())).toList()
                    : identities;
            int workWalletNo = config.getWorkWalletNo().value();
            Integer deductWalletNo = walletPairMappings.get(workWalletNo);
            for (ConsumerIdentity identity : consumableIdentities) {
                boolean offlineAllowed = config.isOfflineAllowed() && identity.isOfflineAllowed();
                targets.add(new WalletDispatchTarget(
                        new WalletDimension(device.getId(), identity.getId(), WalletType.WORK),
                        WalletNo.of(workWalletNo), config.isDeductAllowed(), offlineAllowed));
                if (deductWalletNo != null) {
                    targets.add(new WalletDispatchTarget(
                            new WalletDimension(device.getId(), identity.getId(), WalletType.DEDUCT),
                            WalletNo.of(deductWalletNo), config.isDeductAllowed(), offlineAllowed));
                }
            }
        }
        return targets;
    }
}
