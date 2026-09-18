package com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.infrastructure.po.DeviceIdentityWalletPo;
import org.apache.ibatis.annotations.Param;

/**
 * 设备身份钱包下发表 Mapper。
 */
public interface DeviceIdentityWalletMapper extends BaseMapper<DeviceIdentityWalletPo> {

    /**
     * 查询租户下已使用过的最大批次版本号（SQL 见 Mapper XML）。
     *
     * @param tenantId 租户 ID
     * @return 最大批次版本号；租户内暂无数据时返回 null
     */
    Integer selectMaxVersion(@Param("tenantId") Long tenantId);
}
