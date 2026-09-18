package com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice;

import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd.IdentityCreateCmd;
import com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd.IdentityDeleteCmd;
import com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd.IdentityUpdateCmd;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentity;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityFactory;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityRepository;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.specification.IdentityNameMustNotBeOccupiedSpecification;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.valueobject.IdentityRegistration;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.cmdservice.DeviceIdentityWalletCmdService;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消费身份命令服务。
 *
 * <p>负责身份写流程编排与事务边界；名称唯一性由领域规约校验。</p>
 */
@Service
@RequiredArgsConstructor
public class ConsumerIdentityCmdService {

    private final ConsumerIdentityRepository consumerIdentityRepository;

    private final OrganizationPaymentConfigRepository organizationPaymentConfigRepository;

    private final DeviceIdentityWalletCmdService deviceIdentityWalletCmdService;

    private final IdGenerator idGenerator;

    /**
     * 创建身份。
     *
     * @param tenantId 租户 ID
     * @param cmd      创建命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(Long tenantId, IdentityCreateCmd cmd) {
        TenantIds.requireValid(tenantId);
        List<ConsumerIdentity> identities = consumerIdentityRepository.findAll(tenantId);
        IdentityRegistration registration = new IdentityRegistration(cmd.getName(), namesOf(identities, null));
        validateName(registration);
        consumerIdentityRepository.save(ConsumerIdentityFactory.create(
                idGenerator, tenantId, registration, Boolean.TRUE.equals(cmd.getOfflineAllowed())));
        deviceIdentityWalletCmdService.refreshAll(tenantId);
    }

    /**
     * 更改身份名称与脱机消费参数。
     *
     * @param tenantId 租户 ID
     * @param cmd      更改命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long tenantId, IdentityUpdateCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long identityId = IdCodec.toLong(cmd.getId());
        List<ConsumerIdentity> identities = consumerIdentityRepository.findAll(tenantId);
        ConsumerIdentity identity = identities.stream()
                .filter(item -> item.getId().equals(identityId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCodes.IDENTITY_NOT_FOUND, "身份不存在"));

        IdentityRegistration registration = new IdentityRegistration(cmd.getName(), namesOf(identities, identityId));
        validateName(registration);
        identity.changeProfile(cmd.getName(), Boolean.TRUE.equals(cmd.getOfflineAllowed()));
        consumerIdentityRepository.update(identity);
        deviceIdentityWalletCmdService.refreshByIdentity(tenantId, identityId);
    }

    /**
     * 删除身份。
     *
     * @param tenantId 租户 ID
     * @param cmd      删除命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long tenantId, IdentityDeleteCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long identityId = IdCodec.toLong(cmd.getId());
        consumerIdentityRepository.findById(tenantId, identityId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.IDENTITY_NOT_FOUND, "身份不存在"));
        consumerIdentityRepository.delete(tenantId, identityId);
        organizationPaymentConfigRepository.removeIdentityReference(tenantId, identityId);
        deviceIdentityWalletCmdService.refreshByIdentity(tenantId, identityId);
    }

    private void validateName(IdentityRegistration registration) {
        ValidationResult result = new IdentityNameMustNotBeOccupiedSpecification().validate(registration);
        if (!result.isValid()) {
            throw new BusinessException(ErrorCodes.IDENTITY_NAME_DUPLICATED, result.getError());
        }
    }

    private Set<String> namesOf(List<ConsumerIdentity> identities, Long excludedId) {
        return identities.stream()
                .filter(identity -> excludedId == null || !excludedId.equals(identity.getId()))
                .map(ConsumerIdentity::getName)
                .collect(Collectors.toSet());
    }
}
