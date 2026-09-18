package com.hzsun.aidevops.walletconfig.device.qryservice.qry;

import lombok.Getter;
import lombok.Setter;

/**
 * 设备列表查询入参。
 */
@Getter
@Setter
public class DeviceListQry {

    /** 设备序列号搜索关键字，为空表示不筛选。 */
    private String serialNo;
}
