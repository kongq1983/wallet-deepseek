package com.hzsun.aidevops.walletconfig.organization.cmdservice.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 更改机构名称入参命令。
 */
@Getter
@Setter
public class OrganizationRenameCmd {

    /** 机构 ID。 */
    @NotBlank(message = "机构不存在")
    private String id;

    /** 新的机构名称。 */
    @NotBlank(message = "请输入机构名称")
    @Size(max = 50, message = "机构名称不能超过50个字符")
    private String name;
}
