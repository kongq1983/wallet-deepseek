package com.hzsun.aidevops.walletconfig.organization.cmdservice;

import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.organization.cmdservice.cmd.OrganizationAddCmd;
import com.hzsun.aidevops.walletconfig.organization.cmdservice.cmd.OrganizationRenameCmd;
import com.hzsun.aidevops.walletconfig.organization.domain.Organization;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationFactory;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationRepository;
import com.hzsun.aidevops.walletconfig.organization.domain.specification.OrganizationDepthMustNotExceedLimitSpecification;
import com.hzsun.aidevops.walletconfig.organization.domain.specification.OrganizationNameMustNotBeOccupiedSpecification;
import com.hzsun.aidevops.walletconfig.organization.domain.specification.OrganizationStallMustNotHaveChildSpecification;
import com.hzsun.aidevops.walletconfig.organization.domain.valueobject.OrganizationRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 机构命令服务。
 *
 * <p>负责机构写流程编排与事务边界；机构树深度由上级机构层级推导，
 * 业务规则（层级上限、档口叶子约束、同层名称唯一）由领域规约校验。</p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationCmdService {

    /** 顶层机构的层级。 */
    private static final int ROOT_LEVEL = 1;

    private final OrganizationRepository organizationRepository;

    private final IdGenerator idGenerator;

    /**
     * 新增机构。
     *
     * @param tenantId 租户 ID
     * @param cmd      新增命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Long tenantId, OrganizationAddCmd cmd) {
        TenantIds.requireValid(tenantId);
        List<Organization> organizations = organizationRepository.findAll(tenantId);
        Long superiorId = resolveSuperiorId(cmd.getSuperiorId());
        Organization superior = findSuperior(organizations, superiorId);

        int targetLevel = superior == null ? ROOT_LEVEL : levelOf(organizations, superior.getId()) + 1;
        OrganizationRegistration registration = new OrganizationRegistration(
                cmd.getName(),
                cmd.getOrgType(),
                superiorId,
                superior == null ? null : superior.getOrgType(),
                targetLevel,
                namesUnderSuperior(organizations, superiorId, null));

        validateCreation(registration);
        organizationRepository.save(OrganizationFactory.create(idGenerator, tenantId, registration));
    }

    /**
     * 更改机构名称。
     *
     * @param tenantId 租户 ID
     * @param cmd      改名命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void rename(Long tenantId, OrganizationRenameCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long organizationId = IdCodec.toLong(cmd.getId());
        List<Organization> organizations = organizationRepository.findAll(tenantId);
        Organization organization = organizations.stream()
                .filter(item -> item.getId().equals(organizationId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORGANIZATION_NOT_FOUND, "机构不存在"));

        OrganizationRegistration registration = new OrganizationRegistration(
                cmd.getName(),
                organization.getOrgType(),
                organization.getSuperiorId(),
                null,
                levelOf(organizations, organizationId),
                namesUnderSuperior(organizations, organization.getSuperiorId(), organizationId));

        ValidationResult nameResult = new OrganizationNameMustNotBeOccupiedSpecification().validate(registration);
        if (!nameResult.isValid()) {
            throw new BusinessException(ErrorCodes.ORGANIZATION_NAME_DUPLICATED, nameResult.getError());
        }
        organization.rename(cmd.getName());
        organizationRepository.update(organization);
    }

    /**
     * 执行新增机构的领域规则校验，并按规则映射明确的业务错误码。
     *
     * @param registration 机构登记信息
     */
    private void validateCreation(OrganizationRegistration registration) {
        ValidationResult depthResult = new OrganizationDepthMustNotExceedLimitSpecification().validate(registration);
        if (!depthResult.isValid()) {
            throw new BusinessException(ErrorCodes.ORGANIZATION_DEPTH_EXCEEDED, depthResult.getError());
        }
        ValidationResult stallResult = new OrganizationStallMustNotHaveChildSpecification().validate(registration);
        if (!stallResult.isValid()) {
            throw new BusinessException(ErrorCodes.ORGANIZATION_STALL_CANNOT_HAVE_CHILD, stallResult.getError());
        }
        ValidationResult nameResult = new OrganizationNameMustNotBeOccupiedSpecification().validate(registration);
        if (!nameResult.isValid()) {
            throw new BusinessException(ErrorCodes.ORGANIZATION_NAME_DUPLICATED, nameResult.getError());
        }
    }

    private Long resolveSuperiorId(String superiorId) {
        return superiorId == null || superiorId.isBlank() ? null : IdCodec.toLong(superiorId);
    }

    private Organization findSuperior(List<Organization> organizations, Long superiorId) {
        if (superiorId == null) {
            return null;
        }
        return organizations.stream()
                .filter(item -> item.getId().equals(superiorId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORGANIZATION_NOT_FOUND, "上级机构不存在"));
    }

    /**
     * 计算指定机构在机构树中的层级，顶层机构为 1。
     *
     * @param organizations 租户内全部机构
     * @param organizationId 机构 ID
     * @return 层级
     */
    private int levelOf(List<Organization> organizations, Long organizationId) {
        Map<Long, Organization> index = organizations.stream()
                .collect(Collectors.toMap(Organization::getId, Function.identity(), (left, right) -> left));
        int level = ROOT_LEVEL;
        Organization current = index.get(organizationId);
        while (current != null && current.getSuperiorId() != null) {
            level++;
            current = index.get(current.getSuperiorId());
        }
        return level;
    }

    private Set<String> namesUnderSuperior(List<Organization> organizations, Long superiorId, Long excludedId) {
        return organizations.stream()
                .filter(item -> Objects.equals(item.getSuperiorId(), superiorId))
                .filter(item -> excludedId == null || !excludedId.equals(item.getId()))
                .map(Organization::getName)
                .collect(Collectors.toSet());
    }
}
