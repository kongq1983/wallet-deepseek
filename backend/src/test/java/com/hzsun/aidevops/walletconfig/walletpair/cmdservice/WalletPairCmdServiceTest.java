package com.hzsun.aidevops.walletconfig.walletpair.cmdservice;

import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.cmdservice.DeviceIdentityWalletCmdService;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfig;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigRepository;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd.WalletPairUpdateCmd;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPair;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairRepository;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 租户钱包配对命令服务单元测试。
 *
 * <p>聚焦「修改配对导致工作钱包编号变化」时的机构参数级联迁移：
 * 旧编号从配对表消失后，引用它的机构工作钱包必须跟随新编号，否则配置悬空；
 * 工作钱包编号未变化时不得改动机构参数。</p>
 */
@ExtendWith(MockitoExtension.class)
class WalletPairCmdServiceTest {

    private static final Long TENANT_ID = 1L;

    private static final Long PAIR_ID = 9001L;

    private static final Long ORGANIZATION_ID = 300L;

    @Mock
    private WalletPairRepository walletPairRepository;

    @Mock
    private OrganizationPaymentConfigRepository organizationPaymentConfigRepository;

    @Mock
    private DeviceIdentityWalletCmdService deviceIdentityWalletCmdService;

    @Mock
    private IdGenerator idGenerator;

    private WalletPairCmdService cmdService;

    @BeforeEach
    void setUp() {
        cmdService = new WalletPairCmdService(walletPairRepository, organizationPaymentConfigRepository,
                deviceIdentityWalletCmdService, idGenerator);
        // 编号唯一性校验需要读取整张配对表
        when(walletPairRepository.findAll(TENANT_ID)).thenReturn(List.of(pairOf(1, 2)));
    }

    @Test
    @DisplayName("工作钱包编号变化时，引用旧编号的机构工作钱包级联迁移到新编号并保留其余参数")
    void migratesOrganizationWorkWalletWhenWorkWalletNoChanged() {
        when(walletPairRepository.findById(TENANT_ID, PAIR_ID)).thenReturn(Optional.of(pairOf(1, 2)));
        when(organizationPaymentConfigRepository.findAll(TENANT_ID)).thenReturn(List.of(configOfWorkWallet(1)));

        cmdService.update(TENANT_ID, updateCmd(3, 4));

        ArgumentCaptor<OrganizationPaymentConfig> captor = ArgumentCaptor.forClass(OrganizationPaymentConfig.class);
        verify(organizationPaymentConfigRepository).update(captor.capture());
        OrganizationPaymentConfig migrated = captor.getValue();
        assertEquals(3, migrated.getWorkWalletNo().value());
        assertTrue(migrated.isDeductAllowed());
        assertFalse(migrated.isIdentityRestricted());
        assertTrue(migrated.isOfflineAllowed());
        // 迁移后该机构的工作钱包变为 3，落在受影响编号集合内，因此会被重算
        verify(deviceIdentityWalletCmdService).refreshByOrganizations(TENANT_ID, Set.of(ORGANIZATION_ID));
    }

    @Test
    @DisplayName("仅追扣钱包变化时不动机构参数，只重算该机构的下发表")
    void keepsOrganizationWorkWalletWhenOnlyDeductWalletNoChanged() {
        when(walletPairRepository.findById(TENANT_ID, PAIR_ID)).thenReturn(Optional.of(pairOf(1, 2)));
        when(organizationPaymentConfigRepository.findAll(TENANT_ID)).thenReturn(List.of(configOfWorkWallet(1)));

        cmdService.update(TENANT_ID, updateCmd(1, 3));

        verify(organizationPaymentConfigRepository, never()).update(any());
        verify(deviceIdentityWalletCmdService).refreshByOrganizations(TENANT_ID, Set.of(ORGANIZATION_ID));
    }

    @Test
    @DisplayName("没有机构引用旧工作钱包时不写入机构参数，也不触发下发表重算")
    void skipsMigrationWhenNoOrganizationReferencesOldWorkWallet() {
        when(walletPairRepository.findById(TENANT_ID, PAIR_ID)).thenReturn(Optional.of(pairOf(1, 2)));
        when(organizationPaymentConfigRepository.findAll(TENANT_ID)).thenReturn(List.of(configOfWorkWallet(5)));

        cmdService.update(TENANT_ID, updateCmd(3, 4));

        verify(organizationPaymentConfigRepository, never()).update(any());
        verify(deviceIdentityWalletCmdService, never()).refreshByOrganizations(any(), any());
    }

    private WalletPairUpdateCmd updateCmd(int workWalletNo, int deductWalletNo) {
        WalletPairUpdateCmd cmd = new WalletPairUpdateCmd();
        cmd.setId(IdCodec.toString(PAIR_ID));
        cmd.setWorkWalletNo(workWalletNo);
        cmd.setDeductWalletNo(deductWalletNo);
        return cmd;
    }

    private WalletPair pairOf(int workWalletNo, int deductWalletNo) {
        return WalletPair.reconstitute(PAIR_ID, TENANT_ID, WalletNo.of(workWalletNo), WalletNo.of(deductWalletNo));
    }

    private OrganizationPaymentConfig configOfWorkWallet(int workWalletNo) {
        return OrganizationPaymentConfig.reconstitute(1L, TENANT_ID, ORGANIZATION_ID, true, WalletNo.of(workWalletNo),
                false, true, Set.of());
    }
}
