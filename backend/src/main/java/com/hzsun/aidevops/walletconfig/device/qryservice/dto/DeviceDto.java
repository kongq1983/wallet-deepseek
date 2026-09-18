package com.hzsun.aidevops.walletconfig.device.qryservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 设备查询出参。
 */
@Getter
@Setter
public class DeviceDto {

    /** 设备 ID。 */
    private String id;

    /** 设备序列号。 */
    private String serialNo;

    /** 设备类型编码。 */
    private String deviceType;

    /** 设备类型中文含义。 */
    private String deviceTypeLabel;

    /** 归属机构 ID。 */
    private String organizationId;
}
