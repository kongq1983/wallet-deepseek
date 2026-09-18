package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentity;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityRepository;
import com.hzsun.aidevops.walletconfig.organization.domain.Organization;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationRepository;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.mapper.OrganizationIdentityMapper;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.mapper.OrganizationPaymentConfigMapper;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po.OrganizationIdentityPo;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po.OrganizationPaymentConfigPo;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice.assembler.OrganizationPaymentConfigQryAssembler;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice.dto.OrganizationPaymentConfigDto;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPair;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 机构支付参数查询服务。
 *
 * <p>读链路直接查询持久化对象，并补充机构名称、可消费身份名称与隐式推导的追扣钱包编号。</p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationPaymentConfigQryService {

    private final OrganizationPaymentConfigMapper organizationPaymentConfigMapper;

    private final OrganizationIdentityMapper organizationIdentityMapper;

    private final OrganizationPaymentConfigQryAssembler organizationPaymentConfigQryAssembler;

    private final OrganizationRepository organizationRepository;

    private final ConsumerIdentityRepository consumerIdentityRepository;

    private final WalletPairRepository walletPairRepository;

    /**
     * 查询指定机构的支付参数。
     *
     * @param tenantId          租户 ID
     * @param organizationIdStr 机构 ID
     * @return 机构支付参数出参，未配置时返回 configured=false
     */
    public OrganizationPaymentConfigDto get(Long tenantId, String organizationIdStr) {
        TenantIds.requireValid(tenantId);
        Long organizationId = IdCodec.toLong(organizationIdStr);
        Organization organization = organizationRepository.findById(tenantId, organizationId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORGANIZATION_NOT_FOUND, "机构不存在"));

        OrganizationPaymentConfigPo po = organizationPaymentConfigMapper.selectOne(
                new LambdaQueryWrapper<OrganizationPaymentConfigPo>()
                        .eq(OrganizationPaymentConfigPo::getTenantId, tenantId)
                        .eq(OrganizationPaymentConfigPo::getOrganizationId, organizationId));
        if (po == null) {
            return organizationPaymentConfigQryAssembler.notConfigured(organizationId, organization.getName());
        }

        OrganizationPaymentConfigDto dto = organizationPaymentConfigQryAssembler.toDto(po);
        dto.setOrganizationName(organization.getName());
        dto.setDeductWalletNo(deductWalletNo(tenantId, po.getWorkWalletNo()));

        List<Long> allowedIdentityIds = organizationIdentityMapper.selectList(
                        new LambdaQueryWrapper<OrganizationIdentityPo>()
                                .eq(OrganizationIdentityPo::getTenantId, tenantId)
                                .eq(OrganizationIdentityPo::getOrganizationId, organizationId))
                .stream()
                .map(OrganizationIdentityPo::getIdentityId)
                .toList();
        dto.setAllowedIdentityIds(allowedIdentityIds.stream().map(IdCodec::toString).toList());

        Map<Long, ConsumerIdentity> identityIndex = consumerIdentityRepository
                .findByIds(tenantId, allowedIdentityIds).stream()
                .collect(Collectors.toMap(ConsumerIdentity::getId, Function.identity(), (left, right) -> left));
        dto.setAllowedIdentityNames(allowedIdentityIds.stream()
                .map(identityId -> Optional.ofNullable(identityIndex.get(identityId))
                        .map(ConsumerIdentity::getName)
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList());
        return dto;
    }

    /**
     * 按机构工作钱包推导追扣钱包编号。
     *
     * @param tenantId     租户 ID
     * @param workWalletNo 工作钱包编号
     * @return 追扣钱包编号字符串，配对不存在时返回 null
     */
    private String deductWalletNo(Long tenantId, Integer workWalletNo) {
        if (workWalletNo == null) {
            return null;
        }
        return walletPairRepository.findAll(tenantId).stream()
                .filter(pair -> pair.getWorkWalletNo().value() == workWalletNo)
                .findFirst()
                .map(WalletPair::getDeductWalletNo)
                .map(walletNo -> String.valueOf(walletNo.value()))
                .orElse(null);
    }
}
