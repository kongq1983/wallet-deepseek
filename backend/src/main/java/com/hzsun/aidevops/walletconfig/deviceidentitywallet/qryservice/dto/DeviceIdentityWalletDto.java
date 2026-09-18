package com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 下发表查询出参。
 */
@Getter
@Setter
public class DeviceIdentityWalletDto {

    /** 下发行 ID。 */
    private String id;

    /** 设备 ID。 */
    private String deviceId;

    /** 设备序列号。 */
    private String deviceSerialNo;

    /** 交易身份 ID。 */
    private String identityId;

    /** 交易身份名称。 */
    private String identityName;

    /** 钱包类型编码。 */
    private String walletType;

    /** 钱包类型中文含义。 */
    private String walletTypeLabel;

    /** 钱包编号。 */
    private String walletNo;

    /** 是否允许追扣。 */
    private Boolean deductAllowed;

    /** 是否允许脱机消费。 */
    private Boolean offlineAllowed;

    /** 有效标记编码：1 有效、0 无效。 */
    private String validFlag;

    /** 有效标记中文含义。 */
    private String validFlagLabel;

    /** 版本号。 */
    private String version;
}
