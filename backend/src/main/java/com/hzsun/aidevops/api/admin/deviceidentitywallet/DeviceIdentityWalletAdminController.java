package com.hzsun.aidevops.api.admin.deviceidentitywallet;

import com.hzsun.aidevops.common.response.ApiResponse;
import com.hzsun.aidevops.common.tenant.CurrentTenantProvider;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.DeviceIdentityWalletQryService;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.dto.DeviceIdentityWalletDto;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.qry.DeviceIdentityWalletListQry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 下发表管理端接口。
 *
 * <p>下发表只由系统根据配置自动生成与刷新，不提供人工录入与修改入口，仅开放查询。</p>
 */
@RestController
@RequestMapping("/api/admin/device-identity-wallet")
@RequiredArgsConstructor
public class DeviceIdentityWalletAdminController {

    private final DeviceIdentityWalletQryService deviceIdentityWalletQryService;

    private final CurrentTenantProvider currentTenantProvider;

    /**
     * 查询下发表数据。
     *
     * @param qry 查询条件
     * @return 下发表列表
     */
    @PostMapping("/list")
    public ApiResponse<List<DeviceIdentityWalletDto>> list(@RequestBody(required = false) DeviceIdentityWalletListQry qry) {
        return ApiResponse.success(deviceIdentityWalletQryService.list(
                currentTenantProvider.currentTenantId(), qry));
    }
}
