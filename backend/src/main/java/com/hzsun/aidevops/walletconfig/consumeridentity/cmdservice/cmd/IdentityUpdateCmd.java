package com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 更改身份入参命令，同时承载名称与脱机消费参数的变更。
 */
@Getter
@Setter
public class IdentityUpdateCmd {

    /** 身份 ID。 */
    @NotBlank(message = "身份不存在")
    private String id;

    /** 身份名称。 */
    @NotBlank(message = "请输入身份名称")
    @Size(max = 50, message = "身份名称不能超过50个字符")
    private String name;

    /** 是否允许脱机消费，为空视为不允许。 */
    private Boolean offlineAllowed;
}
