package com.hzsun.aidevops.walletconfig.deviceidentitywallet.qryservice.qry;

import lombok.Getter;
import lombok.Setter;

/**
 * 下发表列表查询入参。
 */
@Getter
@Setter
public class DeviceIdentityWalletListQry {

    /** 设备 ID，为空表示不按设备筛选。 */
    private String deviceId;

    /** 交易身份 ID，为空表示不按身份筛选。 */
    private String identityId;

    /** 有效标记：1 有效、0 无效，为空表示不筛选。 */
    private String validFlag;
}
