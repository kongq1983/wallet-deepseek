package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain;

import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import lombok.Getter;

import java.util.Set;

/**
 * 机构支付参数聚合根。
 *
 * <p>每个机构独立配置一份，不继承上级机构。工作钱包经租户钱包配对隐式推导出追扣钱包；
 * 可消费身份仅在身份限制开关开启时生效。</p>
 */
@Getter
public class OrganizationPaymentConfig {

    /** 系统内部 ID。 */
    private final Long id;

    /** 归属租户 ID。 */
    private final Long tenantId;

    /** 归属机构 ID。 */
    private final Long organizationId;

    /** 是否可追扣。 */
    private boolean deductAllowed;

    /** 工作钱包编号。 */
    private WalletNo workWalletNo;

    /** 身份限制开关。 */
    private boolean identityRestricted;

    /** 是否允许脱机消费。 */
    private boolean offlineAllowed;

    /** 可消费身份 ID 集合。 */
    private Set<Long> allowedIdentityIds;

    private OrganizationPaymentConfig(Long id, Long tenantId, Long organizationId) {
        this.id = id;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.allowedIdentityIds = Set.of();
    }

    /**
     * 业务创建：仅供 {@link OrganizationPaymentConfigFactory} 调用。
     *
     * @param id             系统内部 ID
     * @param tenantId       归属租户 ID
     * @param organizationId 归属机构 ID
     * @return 机构支付参数聚合根
     */
    static OrganizationPaymentConfig create(Long id, Long tenantId, Long organizationId) {
        return new OrganizationPaymentConfig(id, tenantId, organizationId);
    }

    /**
     * 持久化还原：仅供基础设施层仓储实现调用。
     *
     * @param id                 系统内部 ID
     * @param tenantId           归属租户 ID
     * @param organizationId     归属机构 ID
     * @param deductAllowed      是否可追扣
     * @param workWalletNo       工作钱包编号
     * @param identityRestricted 身份限制开关
     * @param offlineAllowed     是否允许脱机消费
     * @param allowedIdentityIds 可消费身份 ID 集合
     * @return 机构支付参数聚合根
     */
    public static OrganizationPaymentConfig reconstitute(Long id, Long tenantId, Long organizationId,
                                                         boolean deductAllowed, WalletNo workWalletNo,
                                                         boolean identityRestricted, boolean offlineAllowed,
                                                         Set<Long> allowedIdentityIds) {
        OrganizationPaymentConfig config = new OrganizationPaymentConfig(id, tenantId, organizationId);
        config.deductAllowed = deductAllowed;
        config.workWalletNo = workWalletNo;
        config.identityRestricted = identityRestricted;
        config.offlineAllowed = offlineAllowed;
        config.allowedIdentityIds = allowedIdentityIds == null ? Set.of() : Set.copyOf(allowedIdentityIds);
        return config;
    }

    /**
     * 重设机构支付参数。
     *
     * @param deductAllowed      是否可追扣
     * @param workWalletNo       工作钱包编号
     * @param identityRestricted 身份限制开关
     * @param offlineAllowed     是否允许脱机消费
     * @param allowedIdentityIds 可消费身份 ID 集合
     */
    public void reconfigure(boolean deductAllowed, WalletNo workWalletNo, boolean identityRestricted,
                            boolean offlineAllowed, Set<Long> allowedIdentityIds) {
        this.deductAllowed = deductAllowed;
        this.workWalletNo = workWalletNo;
        this.identityRestricted = identityRestricted;
        this.offlineAllowed = offlineAllowed;
        this.allowedIdentityIds = allowedIdentityIds == null ? Set.of() : Set.copyOf(allowedIdentityIds);
    }

    /**
     * 变更工作钱包编号。
     *
     * <p>用于钱包配对改号时的级联迁移：机构的追扣钱包由「工作钱包 + 配对表」推导，
     * 旧编号从配对表消失后机构参数必须跟随新编号，否则配置悬空、下发表会继续下发旧编号。</p>
     *
     * @param workWalletNo 新的工作钱包编号
     */
    public void changeWorkWallet(WalletNo workWalletNo) {
        this.workWalletNo = workWalletNo;
    }

    /**
     * 移除已删除身份在可消费身份中的引用。
     *
     * @param identityId 被删除的身份 ID
     */
    public void removeAllowedIdentity(Long identityId) {
        if (allowedIdentityIds.contains(identityId)) {
            allowedIdentityIds = allowedIdentityIds.stream()
                    .filter(item -> !item.equals(identityId))
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
        }
    }
}
