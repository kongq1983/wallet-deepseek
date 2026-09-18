package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.cmdservice.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 保存机构支付参数入参命令。
 */
@Getter
@Setter
public class PaymentConfigSaveCmd {

    /** 机构 ID。 */
    @NotBlank(message = "请选择机构")
    private String organizationId;

    /** 是否可追扣，为空视为不允许。 */
    private Boolean deductAllowed;

    /** 工作钱包编号。 */
    @NotNull(message = "请选择工作钱包")
    private Integer workWalletNo;

    /** 身份限制开关，为空视为关闭。 */
    private Boolean identityRestricted;

    /** 可消费身份 ID 集合。 */
    private List<String> allowedIdentityIds;

    /** 是否允许脱机消费，为空视为不允许。 */
    private Boolean offlineAllowed;
}
