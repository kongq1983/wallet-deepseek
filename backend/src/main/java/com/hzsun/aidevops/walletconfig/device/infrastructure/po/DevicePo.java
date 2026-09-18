package com.hzsun.aidevops.walletconfig.device.infrastructure.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 设备持久化对象。
 *
 * <p>对应表 {@code device}；序列号在数据库层有全局唯一约束。</p>
 */
@Getter
@Setter
@TableName("device")
public class DevicePo {

    /** 系统内部主键。 */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 归属租户 ID。 */
    private Long tenantId;

    /** 设备序列号。 */
    private String serialNo;

    /** 设备类型枚举名。 */
    private String deviceType;

    /** 归属机构 ID。 */
    private Long organizationId;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
