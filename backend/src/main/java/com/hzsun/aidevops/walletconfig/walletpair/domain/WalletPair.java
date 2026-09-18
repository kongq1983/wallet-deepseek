package com.hzsun.aidevops.walletconfig.walletpair.domain;

import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;
import lombok.Getter;

import java.util.List;
import java.util.Set;

/**
 * 钱包配对聚合根。
 *
 * <p>表达租户级的一条「工作钱包 ↔ 追扣钱包」配对关系。机构支付参数只需选定工作钱包，
 * 对应的追扣钱包由本聚合的配对关系隐式推导。</p>
 */
@Getter
public class WalletPair {

    /** 系统内部 ID。 */
    private final Long id;

    /** 归属租户 ID。 */
    private final Long tenantId;

    /** 工作钱包编号。 */
    private WalletNo workWalletNo;

    /** 追扣钱包编号。 */
    private WalletNo deductWalletNo;

    private WalletPair(Long id, Long tenantId, WalletNo workWalletNo, WalletNo deductWalletNo) {
        this.id = id;
        this.tenantId = tenantId;
        this.workWalletNo = workWalletNo;
        this.deductWalletNo = deductWalletNo;
    }

    /**
     * 业务创建：仅供 {@link WalletPairFactory} 调用。
     *
     * @param id            系统内部 ID
     * @param tenantId      归属租户 ID
     * @param configuration 目标配置
     * @return 钱包配对聚合根
     */
    static WalletPair create(Long id, Long tenantId, WalletPairConfiguration configuration) {
        return new WalletPair(id, tenantId, configuration.workWalletNo(), configuration.deductWalletNo());
    }

    /**
     * 持久化还原：仅供基础设施层仓储实现调用，禁止在业务链路中直接使用。
     *
     * @param id             系统内部 ID
     * @param tenantId       归属租户 ID
     * @param workWalletNo   工作钱包编号
     * @param deductWalletNo 追扣钱包编号
     * @return 钱包配对聚合根
     */
    public static WalletPair reconstitute(Long id, Long tenantId, WalletNo workWalletNo, WalletNo deductWalletNo) {
        return new WalletPair(id, tenantId, workWalletNo, deductWalletNo);
    }

    /**
     * 变更配对配置。
     *
     * @param configuration 目标配置
     */
    public void updateConfiguration(WalletPairConfiguration configuration) {
        this.workWalletNo = configuration.workWalletNo();
        this.deductWalletNo = configuration.deductWalletNo();
    }

    /**
     * 本条配对占用的钱包编号集合。
     *
     * @return 编号集合
     */
    public Set<Integer> occupiedWalletNos() {
        // 两个编号由规约保证不同，此处仍用去重写法避免异常路径
        return Set.copyOf(List.of(workWalletNo.value(), deductWalletNo.value()));
    }
}
