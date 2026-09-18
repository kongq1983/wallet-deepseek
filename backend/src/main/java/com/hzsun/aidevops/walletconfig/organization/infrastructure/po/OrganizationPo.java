package com.hzsun.aidevops.walletconfig.organization.infrastructure.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 机构持久化对象。
 *
 * <p>对应表 {@code organization}；{@code superior_id} 为空表示顶层机构。</p>
 */
@Getter
@Setter
@TableName("organization")
public class OrganizationPo {

    /** 系统内部主键。 */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 归属租户 ID。 */
    private Long tenantId;

    /** 机构名称。 */
    private String name;

    /** 机构类型枚举名。 */
    private String orgType;

    /** 上级机构 ID。 */
    private Long superiorId;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
