package com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改钱包配对入参命令。
 */
@Getter
@Setter
public class WalletPairUpdateCmd {

    /** 钱包配对 ID，前端以字符串承载以避免大整数精度丢失。 */
    @NotBlank(message = "钱包配对不存在")
    private String id;

    /** 工作钱包编号。 */
    @NotNull(message = "请输入工作钱包编号")
    private Integer workWalletNo;

    /** 追扣钱包编号。 */
    @NotNull(message = "请输入追扣钱包编号")
    private Integer deductWalletNo;
}
