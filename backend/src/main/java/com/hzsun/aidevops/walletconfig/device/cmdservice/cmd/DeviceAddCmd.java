package com.hzsun.aidevops.walletconfig.device.cmdservice.cmd;

import com.hzsun.aidevops.walletconfig.device.domain.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增设备入参命令。
 */
@Getter
@Setter
public class DeviceAddCmd {

    /** 设备序列号。 */
    @NotBlank(message = "请输入设备序列号")
    @Size(max = 64, message = "设备序列号不能超过64个字符")
    private String serialNo;

    /** 设备类型。 */
    @NotNull(message = "请选择设备类型")
    private DeviceType deviceType;

    /** 归属机构 ID。 */
    @NotBlank(message = "请选择归属机构")
    private String organizationId;
}
