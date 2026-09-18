package com.hzsun.aidevops.walletconfig.device.cmdservice.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除设备入参命令。
 */
@Getter
@Setter
public class DeviceDeleteCmd {

    /** 设备 ID。 */
    @NotBlank(message = "设备不存在")
    private String id;
}
