package com.hzsun.aidevops.walletconfig.organization.cmdservice.cmd;

import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增机构入参命令。
 */
@Getter
@Setter
public class OrganizationAddCmd {

    /** 机构名称。 */
    @NotBlank(message = "请输入机构名称")
    @Size(max = 50, message = "机构名称不能超过50个字符")
    private String name;

    /** 机构类型。 */
    @NotNull(message = "请选择机构类型")
    private OrganizationType orgType;

    /** 上级机构 ID，为空表示创建顶层机构。 */
    private String superiorId;
}
