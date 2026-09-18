package com.hzsun.aidevops.api.admin.device;

import com.hzsun.aidevops.common.response.ApiResponse;
import com.hzsun.aidevops.common.tenant.CurrentTenantProvider;
import com.hzsun.aidevops.walletconfig.device.cmdservice.DeviceCmdService;
import com.hzsun.aidevops.walletconfig.device.cmdservice.cmd.DeviceAddCmd;
import com.hzsun.aidevops.walletconfig.device.cmdservice.cmd.DeviceDeleteCmd;
import com.hzsun.aidevops.walletconfig.device.qryservice.DeviceQryService;
import com.hzsun.aidevops.walletconfig.device.qryservice.dto.DeviceDto;
import com.hzsun.aidevops.walletconfig.device.qryservice.qry.DeviceListQry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 设备管理端接口。
 */
@RestController
@RequestMapping("/api/admin/device")
@RequiredArgsConstructor
public class DeviceAdminController {

    private final DeviceCmdService deviceCmdService;

    private final DeviceQryService deviceQryService;

    private final CurrentTenantProvider currentTenantProvider;

    /**
     * 新增设备。
     *
     * @param cmd 新增命令
     * @return 空数据成功响应
     */
    @PostMapping("/add")
    public ApiResponse<Void> add(@Valid @RequestBody DeviceAddCmd cmd) {
        deviceCmdService.add(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 删除设备。
     *
     * @param cmd 删除命令
     * @return 空数据成功响应
     */
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody DeviceDeleteCmd cmd) {
        deviceCmdService.delete(currentTenantProvider.currentTenantId(), cmd);
        return ApiResponse.success(null);
    }

    /**
     * 查询设备列表。
     *
     * @param qry 查询条件
     * @return 设备列表
     */
    @PostMapping("/list")
    public ApiResponse<List<DeviceDto>> list(@RequestBody(required = false) DeviceListQry qry) {
        String serialNo = qry == null ? null : qry.getSerialNo();
        return ApiResponse.success(deviceQryService.list(currentTenantProvider.currentTenantId(), serialNo));
    }
}
