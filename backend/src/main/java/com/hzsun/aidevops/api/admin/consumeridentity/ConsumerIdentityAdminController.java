package com.hzsun.aidevops.api.admin.consumeridentity;

import com.hzsun.aidevops.common.response.ApiResponse;
import com.hzsun.aidevops.common.tenant.CurrentTenantProvider;
import com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.ConsumerIdentityCmdService;
import com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd.IdentityCreateCmd;
import com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd.IdentityDeleteCmd;
import com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd.IdentityUpdateCmd;
import com.hzsun.aidevops.walletconfig.consumeridentity.qryservice.ConsumerIdentityQryService;
import com.hzsun.aidevops.walletconfig.consumeridentity.qryservice.dto.ConsumerIdentityDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消费身份管理端接口。
 */
@RestController
@RequestMapping("/api/admin/consumer-identity")
@RequiredArgsConstructor
public class ConsumerIdentityAdminController {

    private final ConsumerIdentityCmdService consumerIdentityCmdService;

    private final ConsumerIdentityQryService consumerIdentityQryService;

    private final CurrentTenantProvider currentTenantProvider;

    /**
     * 创建身份。
     *
     * @param cmd 创建命令
     * @return 空数据成功响应
     */
    @PostMapping("/add")
    public ApiResponse<Void> add(@Valid @RequestBody IdentityCreateCmd cmd) {
        consumerIdentityCmdService.create(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 更改身份名称与脱机消费参数。
     *
     * @param cmd 更改命令
     * @return 空数据成功响应
     */
    @PostMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody IdentityUpdateCmd cmd) {
        consumerIdentityCmdService.update(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 删除身份。
     *
     * @param cmd 删除命令
     * @return 空数据成功响应
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody IdentityDeleteCmd cmd) {
        consumerIdentityCmdService.delete(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 查询身份列表。
     *
     * @return 身份列表
     */
    @PostMapping("/list")
    public ApiResponse<List<ConsumerIdentityDto>> list() {
        return ApiResponse.success(consumerIdentityQryService.list(currentTenantProvider.currentTenantId()));
    }
}
