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
 * 机构支付参数持久化对象。
 *
 * <p>对应表 {@code organization_payment_config}。</p>
 */
@Getter
@Setter
@TableName("organization_payment_config")
public class OrganizationPaymentConfigPo {

    /** 系统内部主键。 */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 归属租户 ID。 */
    private Long tenantId;

    /** 归属机构 ID。 */
    private Long organizationId;

    /** 是否可追扣。 */
    private Boolean deductAllowed;

    /** 工作钱包编号。 */
    private Integer workWalletNo;

    /** 身份限制开关。 */
    private Boolean identityRestricted;

    /** 是否允许脱机消费。 */
    private Boolean offlineAllowed;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
