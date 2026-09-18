package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.cmdservice;

import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityRepository;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.cmdservice.DeviceIdentityWalletCmdService;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationRepository;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.cmdservice.cmd.PaymentConfigSaveCmd;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfig;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigFactory;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigRepository;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.specification.IdentityRestrictionMustSelectAllowedIdentitySpecification;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.specification.PaymentWorkWalletMustBeAvailableSpecification;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.valueobject.PaymentConfiguration;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairRepository;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 机构支付参数命令服务。
 *
 * <p>机构支付参数按机构维度独立配置：不存在时新建，存在时覆盖。
 * 工作钱包必须来自租户钱包配对，身份限制开启时必须选择可消费身份。</p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationPaymentConfigCmdService {

    private final OrganizationPaymentConfigRepository organizationPaymentConfigRepository;

    private final OrganizationRepository organizationRepository;

    private final WalletPairRepository walletPairRepository;

    private final ConsumerIdentityRepository consumerIdentityRepository;

    private final DeviceIdentityWalletCmdService deviceIdentityWalletCmdService;

    private final IdGenerator idGenerator;

    /**
     * 保存机构支付参数。
     *
     * @param tenantId 租户 ID
     * @param cmd      保存命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(Long tenantId, PaymentConfigSaveCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long organizationId = IdCodec.toLong(cmd.getOrganizationId());
        organizationRepository.findById(tenantId, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORGANIZATION_NOT_FOUND, "机构不存在"));

        PaymentConfiguration configuration = new PaymentConfiguration(
                WalletNo.of(cmd.getWorkWalletNo()),
                Boolean.TRUE.equals(cmd.getIdentityRestricted()),
                resolveAllowedIdentityIds(tenantId, cmd.getAllowedIdentityIds()),
                availableWorkWalletNos(tenantId));

        validateConfiguration(configuration);

        Optional<OrganizationPaymentConfig> existing =
                organizationPaymentConfigRepository.findByOrganizationId(tenantId, organizationId);
        OrganizationPaymentConfig config = existing.orElseGet(
                () -> OrganizationPaymentConfigFactory.create(idGenerator, tenantId, organizationId));
        config.reconfigure(
                Boolean.TRUE.equals(cmd.getDeductAllowed()),
                configuration.workWalletNo(),
                configuration.identityRestricted(),
                Boolean.TRUE.equals(cmd.getOfflineAllowed()),
                configuration.allowedIdentityIds());

        if (existing.isPresent()) {
            organizationPaymentConfigRepository.update(config);
        } else {
            organizationPaymentConfigRepository.save(config);
        }
        deviceIdentityWalletCmdService.refreshByOrganization(tenantId, organizationId);
    }

    /**
     * 执行机构支付参数的领域规则校验，并按规则映射明确的业务错误码。
     *
     * @param configuration 目标配置
     */
    private void validateConfiguration(PaymentConfiguration configuration) {
        ValidationResult workWalletResult = new PaymentWorkWalletMustBeAvailableSpecification().validate(configuration);
        if (!workWalletResult.isValid()) {
            throw new BusinessException(ErrorCodes.ORGANIZATION_WORK_WALLET_INVALID, workWalletResult.getError());
        }
        ValidationResult identityResult = new IdentityRestrictionMustSelectAllowedIdentitySpecification()
                .validate(configuration);
        if (!identityResult.isValid()) {
            throw new BusinessException(ErrorCodes.ORGANIZATION_ALLOWED_IDENTITY_REQUIRED, identityResult.getError());
        }
    }

    /**
     * 解析并校验可消费身份，身份必须存在于身份字典。
     *
     * @param tenantId          租户 ID
     * @param allowedIdentityIds 请求中的身份 ID 字符串集合
     * @return 身份 ID 集合
     */
    private Set<Long> resolveAllowedIdentityIds(Long tenantId, List<String> allowedIdentityIds) {
        if (allowedIdentityIds == null || allowedIdentityIds.isEmpty()) {
            return Set.of();
        }
        Set<Long> requested = allowedIdentityIds.stream()
                .map(IdCodec::toLong)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Long> existing = consumerIdentityRepository.findByIds(tenantId, List.copyOf(requested)).stream()
                .map(identity -> identity.getId())
                .collect(Collectors.toSet());
        if (!existing.containsAll(requested)) {
            throw new BusinessException(ErrorCodes.IDENTITY_NOT_FOUND, "可消费身份不存在");
        }
        return requested;
    }

    private Set<Integer> availableWorkWalletNos(Long tenantId) {
        return walletPairRepository.findAll(tenantId).stream()
                .map(pair -> pair.getWorkWalletNo().value())
                .collect(Collectors.toSet());
    }
}
