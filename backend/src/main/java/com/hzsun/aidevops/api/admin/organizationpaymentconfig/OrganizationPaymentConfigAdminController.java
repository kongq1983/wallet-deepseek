package com.hzsun.aidevops.api.admin.organizationpaymentconfig;

import com.hzsun.aidevops.common.response.ApiResponse;
import com.hzsun.aidevops.common.tenant.CurrentTenantProvider;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.cmdservice.OrganizationPaymentConfigCmdService;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.cmdservice.cmd.PaymentConfigSaveCmd;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice.OrganizationPaymentConfigQryService;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.qryservice.dto.OrganizationPaymentConfigDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 机构支付参数管理端接口。
 */
@RestController
@RequestMapping("/api/admin/organization-payment-config")
@RequiredArgsConstructor
public class OrganizationPaymentConfigAdminController {

    private final OrganizationPaymentConfigCmdService organizationPaymentConfigCmdService;

    private final OrganizationPaymentConfigQryService organizationPaymentConfigQryService;

    private final CurrentTenantProvider currentTenantProvider;

    /**
     * 保存机构支付参数。
     *
     * @param cmd 保存命令
     * @return 空数据成功响应
     */
    @PostMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody PaymentConfigSaveCmd cmd) {
        organizationPaymentConfigCmdService.save(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 查询机构支付参数。
     *
     * @param qry 查询条件
     * @return 机构支付参数
     */
    @PostMapping("/get")
    public ApiResponse<OrganizationPaymentConfigDto> get(@Valid @RequestBody PaymentConfigGetQry qry) {
        return ApiResponse.success(organizationPaymentConfigQryService.get(
                currentTenantProvider.currentTenantId(), qry.getOrganizationId()));
    }
}
