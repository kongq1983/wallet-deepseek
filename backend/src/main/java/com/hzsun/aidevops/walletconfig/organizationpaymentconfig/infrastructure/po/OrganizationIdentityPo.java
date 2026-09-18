package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.infrastructure.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 机构可消费身份持久化对象。
 *
 * <p>对应表 {@code organization_identity}，一条记录表示机构允许消费的一个身份。</p>
 */
@Getter
@Setter
@TableName("organization_identity")
public class OrganizationIdentityPo {

    /** 系统内部主键。 */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 归属租户 ID。 */
    private Long tenantId;

    /** 机构 ID。 */
    private Long organizationId;

    /** 身份 ID。 */
    private Long identityId;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
