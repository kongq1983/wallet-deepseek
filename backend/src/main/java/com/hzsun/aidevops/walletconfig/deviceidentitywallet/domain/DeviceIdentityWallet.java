package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import lombok.Getter;

/**
 * 设备身份钱包下发表聚合根（一行）。
 *
 * <p>下发表是配置态数据快照，一行代表「设备 × 交易身份 × 钱包类型」维度上的一次配置。
 * 变更采用行级 diff：旧行原地置为无效、新行插入；版本号是<b>租户级批次号</b>，
 * 失效行保留它被下发时使用的批次号、不做改写。</p>
 */
@Getter
public class DeviceIdentityWallet {

    /** 系统内部 ID。 */
    private final Long id;

    /** 归属租户 ID。 */
    private final Long tenantId;

    /** 下发表维度。 */
    private final WalletDimension dimension;

    /** 钱包编号。 */
    private WalletNo walletNo;

    /** 是否允许追扣，取自设备所属机构的机构支付参数。 */
    private boolean deductAllowed;

    /** 是否允许脱机消费，机构允许与身份允许的逻辑与。 */
    private boolean offlineAllowed;

    /** 有效标记。 */
    private boolean valid;

    /** 下发批次版本号：同一租户内一次重算共用一个号，从 1 开始递增。 */
    private int version;

    private DeviceIdentityWallet(Long id, Long tenantId, WalletDimension dimension) {
        this.id = id;
        this.tenantId = tenantId;
        this.dimension = dimension;
    }

    /**
     * 业务创建：仅供 {@link DeviceIdentityWalletFactory} 调用。
     *
     * @param id        系统内部 ID
     * @param tenantId  归属租户 ID
     * @param dimension 下发表维度
     * @return 下发表行
     */
    static DeviceIdentityWallet create(Long id, Long tenantId, WalletDimension dimension) {
        return new DeviceIdentityWallet(id, tenantId, dimension);
    }

    /**
     * 持久化还原：仅供基础设施层仓储实现调用。
     *
     * @param id             系统内部 ID
     * @param tenantId       归属租户 ID
     * @param dimension      下发表维度
     * @param walletNo       钱包编号
     * @param deductAllowed  是否允许追扣
     * @param offlineAllowed 是否允许脱机消费
     * @param valid          有效标记
     * @param version        行级版本号
     * @return 下发表行
     */
    public static DeviceIdentityWallet reconstitute(Long id, Long tenantId, WalletDimension dimension, WalletNo walletNo,
                                                    boolean deductAllowed, boolean offlineAllowed, boolean valid,
                                                    int version) {
        DeviceIdentityWallet row = new DeviceIdentityWallet(id, tenantId, dimension);
        row.walletNo = walletNo;
        row.deductAllowed = deductAllowed;
        row.offlineAllowed = offlineAllowed;
        row.valid = valid;
        row.version = version;
        return row;
    }

    /**
     * 以目标行填充内容并置为有效。
     *
     * @param target       目标行
     * @param batchVersion 本次下发批次版本号
     */
    public void apply(WalletDispatchTarget target, int batchVersion) {
        this.walletNo = target.walletNo();
        this.deductAllowed = target.deductAllowed();
        this.offlineAllowed = target.offlineAllowed();
        this.valid = true;
        this.version = batchVersion;
    }

    /**
     * 置为无效。
     *
     * <p>版本号保留该行被下发时使用的批次号：版本号表达「这批数据是哪一批下发的」，
     * 失效只翻转有效标记，不改写批次号。</p>
     */
    public void invalidate() {
        this.valid = false;
    }

    /**
     * 内容是否与目标行一致。
     *
     * @param target 目标行
     * @return true 表示钱包编号与两个开关完全一致
     */
    public boolean matches(WalletDispatchTarget target) {
        return walletNo.value() == target.walletNo().value()
                && deductAllowed == target.deductAllowed()
                && offlineAllowed == target.offlineAllowed();
    }
}
