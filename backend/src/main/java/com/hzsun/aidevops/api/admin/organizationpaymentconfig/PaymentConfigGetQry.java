package com.hzsun.aidevops.api.admin.organizationpaymentconfig;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 机构支付参数查询入参。
 */
@Getter
@Setter
public class PaymentConfigGetQry {

    /** 机构 ID。 */
    @NotBlank(message = "请选择机构")
    private String organizationId;
}
