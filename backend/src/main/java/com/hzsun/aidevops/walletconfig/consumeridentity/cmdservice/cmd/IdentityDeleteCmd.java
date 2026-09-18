package com.hzsun.aidevops.walletconfig.consumeridentity.cmdservice.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除身份入参命令。
 */
@Getter
@Setter
public class IdentityDeleteCmd {

    /** 身份 ID。 */
    @NotBlank(message = "身份不存在")
    private String id;
}
