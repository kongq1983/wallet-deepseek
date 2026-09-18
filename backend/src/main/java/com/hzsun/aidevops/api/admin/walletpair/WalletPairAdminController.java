package com.hzsun.aidevops.api.admin.walletpair;

import com.hzsun.aidevops.common.response.ApiResponse;
import com.hzsun.aidevops.common.tenant.CurrentTenantProvider;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.WalletPairCmdService;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd.WalletPairAddCmd;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd.WalletPairDeleteCmd;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd.WalletPairUpdateCmd;
import com.hzsun.aidevops.walletconfig.walletpair.qryservice.WalletPairQryService;
import com.hzsun.aidevops.walletconfig.walletpair.qryservice.dto.WalletPairDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 租户钱包配对管理端接口。
 *
 * <p>只做协议转换、参数校验与租户上下文读取，业务编排委派给命令服务与查询服务。</p>
 */
@RestController
@RequestMapping("/api/admin/wallet-pair")
@RequiredArgsConstructor
public class WalletPairAdminController {

    private final WalletPairCmdService walletPairCmdService;

    private final WalletPairQryService walletPairQryService;

    private final CurrentTenantProvider currentTenantProvider;

    /**
     * 新增钱包配对。
     *
     * @param cmd 新增命令
     * @return 空数据成功响应
     */
    @PostMapping("/add")
    public ApiResponse<Void> add(@Valid @RequestBody WalletPairAddCmd cmd) {
        walletPairCmdService.add(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 修改钱包配对。
     *
     * @param cmd 修改命令
     * @return 空数据成功响应
     */
    @PostMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody WalletPairUpdateCmd cmd) {
        walletPairCmdService.update(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 删除钱包配对。
     *
     * @param cmd 删除命令
     * @return 空数据成功响应
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody WalletPairDeleteCmd cmd) {
        walletPairCmdService.delete(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 查询钱包配对列表。
     *
     * @return 钱包配对列表
     */
    @PostMapping("/list")
    public ApiResponse<List<WalletPairDto>> list() {
        return ApiResponse.success(walletPairQryService.list(currentTenantProvider.currentTenantId()));
    }
}
