package com.hzsun.aidevops.walletconfig.organization.qryservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 机构查询出参。
 *
 * <p>按 API 规范，ID 与数值统一以字符串表达，枚举同时返回编码与中文含义。
 * 层级（{@code level}）用于前端还原机构树，顶层机构为 1。</p>
 */
@Getter
@Setter
public class OrganizationDto {

    /** 机构 ID。 */
    private String id;

    /** 机构名称。 */
    private String name;

    /** 机构类型编码。 */
    private String orgType;

    /** 机构类型中文含义。 */
    private String orgTypeLabel;

    /** 上级机构 ID，顶层机构为空。 */
    private String superiorId;

    /** 机构层级。 */
    private String level;
}
