package com.hzsun.aidevops.walletconfig.consumeridentity.infrastructure.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 消费身份持久化对象。
 *
 * <p>对应表 {@code consumer_identity}。</p>
 */
@Getter
@Setter
@TableName("consumer_identity")
public class ConsumerIdentityPo {

    /** 系统内部主键。 */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 归属租户 ID。 */
    private Long tenantId;

    /** 身份名称。 */
    private String name;

    /** 是否允许脱机消费。 */
    private Boolean offlineAllowed;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
