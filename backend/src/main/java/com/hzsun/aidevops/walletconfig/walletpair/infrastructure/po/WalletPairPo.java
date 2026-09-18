package com.hzsun.aidevops.walletconfig.walletpair.infrastructure.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 租户钱包配对持久化对象。
 *
 * <p>对应表 {@code wallet_pair}，系统内部主键由领域层雪花算法生成，不使用数据库自增。</p>
 */
@Getter
@Setter
@TableName("wallet_pair")
public class WalletPairPo {

    /** 系统内部主键。 */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 归属租户 ID。 */
    private Long tenantId;

    /** 工作钱包编号。 */
    private Integer workWalletNo;

    /** 追扣钱包编号。 */
    private Integer deductWalletNo;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
