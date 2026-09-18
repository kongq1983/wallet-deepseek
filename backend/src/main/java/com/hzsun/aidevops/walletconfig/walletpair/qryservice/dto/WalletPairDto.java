package com.hzsun.aidevops.walletconfig.walletpair.qryservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户钱包配对查询出参。
 *
 * <p>按 API 规范，编号与 ID 统一以字符串表达。</p>
 */
@Getter
@Setter
public class WalletPairDto {

    /** 钱包配对 ID。 */
    private String id;

    /** 工作钱包编号。 */
    private String workWalletNo;

    /** 追扣钱包编号。 */
    private String deductWalletNo;
}
