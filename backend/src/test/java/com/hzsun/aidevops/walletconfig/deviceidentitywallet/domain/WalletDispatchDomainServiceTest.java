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
 * 并验证版本号在同一维度上单调递增以及失效行不被物理删除。</p>
 */
class WalletDispatchDomainServiceTest {

    private static final Long TENANT_ID = 1L;

    private static final Long DEVICE_ID = 100L;

    private static final Long IDENTITY_ID = 200L;

    private final IdGenerator idGenerator = new SequenceIdGenerator();

    @Test
    @DisplayName("首次生成时插入有效行且版本号为 1")
    void plansInitialInsertWhenNoExistingRow() {
        WalletDispatchTarget target = workTarget(1, false, false);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                idGenerator, TENANT_ID, List.of(), List.of(target));

        assertTrue(plan.rowsToInvalidate().isEmpty());
        assertEquals(1, plan.rowsToInsert().size());
        DeviceIdentityWallet inserted = plan.rowsToInsert().get(0);
        assertTrue(inserted.isValid());
        assertEquals(1, inserted.getVersion());
    }

    @Test
    @DisplayName("配置未变化时不产生任何变更")
    void plansNothingWhenTargetMatchesExistingRow() {
        DeviceIdentityWallet existing = DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(1, true, true), 1);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                idGenerator, TENANT_ID, List.of(existing), List.of(workTarget(1, true, true)));

        assertTrue(plan.isEmpty());
    }

    @Test
    @DisplayName("追扣钱包换号时旧行失效并插入新行，两条行版本号依次推进")
    void plansInvalidateAndInsertWhenWalletNoChanged() {
        DeviceIdentityWallet existing = DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(2, false, false), 1);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                idGenerator, TENANT_ID, List.of(existing), List.of(workTarget(5, false, false)));

        assertEquals(1, plan.rowsToInvalidate().size());
        assertEquals(1, plan.rowsToInsert().size());

        DeviceIdentityWallet invalidated = plan.rowsToInvalidate().get(0);
        assertFalse(invalidated.isValid());
        assertEquals(2, invalidated.getVersion());

        DeviceIdentityWallet inserted = plan.rowsToInsert().get(0);
        assertTrue(inserted.isValid());
        assertEquals(3, inserted.getVersion());
        assertEquals(5, inserted.getWalletNo().value());
    }

    @Test
    @DisplayName("配置消失时原行置为无效且版本号推进，不产生新行")
    void plansInvalidateOnlyWhenTargetMissing() {
        DeviceIdentityWallet existing = DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(1, false, false), 1);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                idGenerator, TENANT_ID, List.of(existing), List.of());

        assertEquals(1, plan.rowsToInvalidate().size());
        assertTrue(plan.rowsToInsert().isEmpty());
        assertEquals(2, plan.rowsToInvalidate().get(0).getVersion());
        assertFalse(plan.rowsToInvalidate().get(0).isValid());
    }

    @Test
    @DisplayName("失效行参与版本号推导，重新出现时版本号继续递增")
    void keepsVersionMonotonicAcrossHistoricalRows() {
        DeviceIdentityWallet historical = DeviceIdentityWallet.reconstitute(
                idGenerator.nextId(), TENANT_ID, workDimension(WalletType.WORK), WalletNo.of(1), false, false, false, 7);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                idGenerator, TENANT_ID, List.of(historical), List.of(workTarget(1, false, false)));

        assertEquals(1, plan.rowsToInsert().size());
        assertEquals(8, plan.rowsToInsert().get(0).getVersion());
    }

    @Test
    @DisplayName("工作钱包与追扣钱包是两个独立维度，互不影响")
    void treatsWorkAndDeductAsSeparateDimensions() {
        DeviceIdentityWallet workRow = DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, workTarget(1, false, false), 1);
        WalletDispatchTarget deductTarget = new WalletDispatchTarget(
                workDimension(WalletType.DEDUCT), WalletNo.of(2), false, false);

        WalletDispatchPlan plan = WalletDispatchDomainService.plan(
                idGenerator, TENANT_ID, List.of(workRow), List.of(workTarget(1, false, false), deductTarget));

        assertTrue(plan.rowsToInvalidate().isEmpty());
        assertEquals(1, plan.rowsToInsert().size());
        assertEquals(WalletType.DEDUCT, plan.rowsToInsert().get(0).getDimension().walletType());
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
