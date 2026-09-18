package com.hzsun.aidevops.api.admin.organization;

import com.hzsun.aidevops.common.response.ApiResponse;
import com.hzsun.aidevops.common.tenant.CurrentTenantProvider;
import com.hzsun.aidevops.walletconfig.organization.cmdservice.OrganizationCmdService;
import com.hzsun.aidevops.walletconfig.organization.cmdservice.cmd.OrganizationAddCmd;
import com.hzsun.aidevops.walletconfig.organization.cmdservice.cmd.OrganizationRenameCmd;
import com.hzsun.aidevops.walletconfig.organization.qryservice.OrganizationQryService;
import com.hzsun.aidevops.walletconfig.organization.qryservice.dto.OrganizationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 机构管理端接口。
 */
@RestController
@RequestMapping("/api/admin/organization")
@RequiredArgsConstructor
public class OrganizationAdminController {

    private final OrganizationCmdService organizationCmdService;

    private final OrganizationQryService organizationQryService;

    private final CurrentTenantProvider currentTenantProvider;

    /**
     * 新增机构。
     *
     * @param cmd 新增命令
     * @return 空数据成功响应
     */
    @PostMapping("/add")
    public ApiResponse<Void> add(@Valid @RequestBody OrganizationAddCmd cmd) {
        organizationCmdService.add(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 更改机构名称。
     *
     * @param cmd 改名命令
     * @return 空数据成功响应
     */
    @PostMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody OrganizationRenameCmd cmd) {
        organizationCmdService.rename(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 查询机构列表（含层级，供前端还原机构树）。
     *
     * @return 机构列表
     */
    @PostMapping("/list")
    public ApiResponse<List<OrganizationDto>> list() {
        return ApiResponse.success(organizationQryService.list(currentTenantProvider.currentTenantId()));
    }
}
