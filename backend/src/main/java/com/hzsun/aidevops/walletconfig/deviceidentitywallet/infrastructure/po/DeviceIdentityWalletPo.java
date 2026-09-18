package com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 设备身份钱包下发表持久化对象。
 *
 * <p>对应表 {@code device_identity_wallet}；有效行在数据库层由部分唯一索引保证维度唯一。</p>
 */
@Getter
@Setter
@TableName("device_identity_wallet")
public class DeviceIdentityWalletPo {

    /** 系统内部主键。 */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 归属租户 ID。 */
    private Long tenantId;

    /** 设备 ID。 */
    private Long deviceId;

    /** 交易身份 ID。 */
    private Long identityId;

    /** 钱包类型枚举名。 */
    private String walletType;

    /** 钱包编号。 */
    private Integer walletNo;

    /** 是否允许追扣。 */
    private Boolean deductAllowed;

    /** 是否允许脱机消费。 */
    private Boolean offlineAllowed;

    /** 有效标记：1 有效、0 无效。 */
    private Integer validFlag;

    /** 行级版本号。 */
    private Integer version;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
