package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 机构支付参数查询出参。
 *
 * <p>{@code deductWalletNo} 由机构工作钱包经租户钱包配对隐式推导，用于前端核对；
 * {@code configured} 表示该机构是否已配置支付参数。</p>
 */
@Getter
@Setter
public class OrganizationPaymentConfigDto {

    /** 机构 ID。 */
    private String organizationId;

    /** 机构名称。 */
    private String organizationName;

    /** 是否已配置支付参数。 */
    private Boolean configured;

    /** 是否可追扣。 */
    private Boolean deductAllowed;

    /** 工作钱包编号。 */
    private String workWalletNo;

    /** 追扣钱包编号（由配对关系推导）。 */
    private String deductWalletNo;

    /** 身份限制开关。 */
    private Boolean identityRestricted;

    /** 可消费身份 ID 集合。 */
    private List<String> allowedIdentityIds;

    /** 可消费身份名称集合。 */
    private List<String> allowedIdentityNames;

    /** 是否允许脱机消费。 */
    private Boolean offlineAllowed;
}
