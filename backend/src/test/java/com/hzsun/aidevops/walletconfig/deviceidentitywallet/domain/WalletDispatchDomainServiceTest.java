package com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain;

import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 下发表重算领域服务单元测试。
 *
 * <p>覆盖行级差异比较的四类场景：首次生成、内容未变化、内容变化（追扣钱包换号）、配置消失（失效），
 * 并验证失效只翻转有效标记、不改写批次版本号，以及失效行不被物理删除。</p>
 */
class WalletDispatchDomainServiceTest {

    private static final Long TENANT_ID = 1L;

    private static final Long DEVICE_ID = 100L;

    private static final Long IDENTITY_ID = 200L;

    private final IdGenerator idGenerator = new SequenceIdGenerator();

    @Test
    @DisplayName("首次生成时产出待新增目标行，无需失效任何行")
    void plansInitialInsertWhenNoExistingRow() {
        WalletDispatchPlan plan = WalletDispatchDomainService.plan(List.of(), List.of(workTarget(1, false, false)));

        assertTrue(plan.rowsToInvalidate().isEmpty());
        assertEquals(1, plan.targetsToInsert().size());
        assertEquals(1, plan.targetsToInsert().get(0).walletNo().value());
        assertFalse(plan.isEmpty());
    }

    @Test
    @DisplayName("配置未变化时不产生任何变更")
    void plansNothingWhenTargetMatchesExistingRow() {
        DeviceIdentityWallet existing =
                DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(1, true, true), 7);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                List.of(existing), List.of(workTarget(1, true, true)));

        assertTrue(plan.isEmpty());
        assertEquals(7, existing.getVersion());
    }

    @Test
    @DisplayName("追扣钱包换号时旧行失效且批次号保持原值，同时产出新目标行")
    void plansInvalidateAndInsertWhenWalletNoChanged() {
        DeviceIdentityWallet existing =
                DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(2, false, false), 7);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                List.of(existing), List.of(workTarget(5, false, false)));

        assertEquals(1, plan.rowsToInvalidate().size());
        assertEquals(1, plan.targetsToInsert().size());

        DeviceIdentityWallet invalidated = plan.rowsToInvalidate().get(0);
        assertFalse(invalidated.isValid());
        assertEquals(7, invalidated.getVersion());
        assertEquals(5, plan.targetsToInsert().get(0).walletNo().value());
    }

    @Test
    @DisplayName("配置消失时原行置为无效并保留批次号，且不产出新行")
    void plansInvalidateOnlyWhenTargetMissing() {
        DeviceIdentityWallet existing =
                DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(1, false, false), 13);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(List.of(existing), List.of());

        assertEquals(1, plan.rowsToInvalidate().size());
        assertTrue(plan.targetsToInsert().isEmpty());
        assertFalse(plan.rowsToInvalidate().get(0).isValid());
        assertEquals(13, plan.rowsToInvalidate().get(0).getVersion());
    }

    @Test
    @DisplayName("同一维度重复出现的目标行只保留一条，避免撞有效行唯一索引")
    void keepsSingleTargetPerDimension() {
        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                List.of(), List.of(workTarget(1, false, false), workTarget(1, false, false)));

        assertEquals(1, plan.targetsToInsert().size());
    }

    @Test
    @DisplayName("工作钱包与追扣钱包是两个独立维度，互不影响")
    void treatsWorkAndDeductAsSeparateDimensions() {
        DeviceIdentityWallet workRow =
                DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(1, false, false), 1);
        WalletDispatchTarget deductTarget = new WalletDispatchTarget(
                workDimension(WalletType.DEDUCT), WalletNo.of(2), false, false);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                List.of(workRow), List.of(workTarget(1, false, false), deductTarget));

        assertTrue(plan.rowsToInvalidate().isEmpty());
        assertEquals(1, plan.targetsToInsert().size());
        assertEquals(WalletType.DEDUCT, plan.targetsToInsert().get(0).dimension().walletType());
    }

    private WalletDispatchTarget workTarget(int walletNo, boolean deductAllowed, boolean offlineAllowed) {
        return new WalletDispatchTarget(workDimension(WalletType.WORK), WalletNo.of(walletNo), deductAllowed, offlineAllowed);
    }

    private WalletDimension workDimension(WalletType walletType) {
        return new WalletDimension(DEVICE_ID, IDENTITY_ID, walletType);
    }

    /**
     * 递增序列 ID 生成器，仅用于测试替身。
     */
    private static final class SequenceIdGenerator implements IdGenerator {

        private final AtomicLong sequence = new AtomicLong(1000L);

        @Override
        public Long nextId() {
            return sequence.incrementAndGet();
        }
    }
}
