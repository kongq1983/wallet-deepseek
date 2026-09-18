package com.hzsun.aidevops.walletconfig.walletpair.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzsun.aidevops.walletconfig.walletpair.infrastructure.po.WalletPairPo;

/**
 * 租户钱包配对 Mapper。
 *
 * <p>仅承载 {@code wallet_pair} 表的持久化访问，禁止跨聚合读写；自定义 SQL 必须写在 Mapper XML 中。</p>
 */
public interface WalletPairMapper extends BaseMapper<WalletPairPo> {
}
