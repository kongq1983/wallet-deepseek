package com.hzsun.aidevops.walletconfig.consumeridentity.qryservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 消费身份查询出参。
 */
@Getter
@Setter
public class ConsumerIdentityDto {

    /** 身份 ID。 */
    private String id;

    /** 身份名称。 */
    private String name;

    /** 是否允许脱机消费。 */
    private Boolean offlineAllowed;
}
