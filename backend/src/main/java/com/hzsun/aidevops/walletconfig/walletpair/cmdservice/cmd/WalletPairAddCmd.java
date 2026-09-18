package com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增钱包配对入参命令。
 *
 * <p>编号的取值范围与唯一性属于领域业务规则，由领域规约校验；此处仅做必填边界校验。</p>
 */
@Getter
@Setter
public class WalletPairAddCmd {

    /** 工作钱包编号。 */
    @NotNull(message = "请输入工作钱包编号")
    private Integer workWalletNo;

    /** 追扣钱包编号。 */
    @NotNull(message = "请输入追扣钱包编号")
    private Integer deductWalletNo;
}
