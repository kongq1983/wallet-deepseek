package com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除钱包配对入参命令。
 */
@Getter
@Setter
public class WalletPairDeleteCmd {

    /** 钱包配对 ID，前端以字符串承载以避免大整数精度丢失。 */
    @NotBlank(message = "钱包配对不存在")
    private String id;
}
