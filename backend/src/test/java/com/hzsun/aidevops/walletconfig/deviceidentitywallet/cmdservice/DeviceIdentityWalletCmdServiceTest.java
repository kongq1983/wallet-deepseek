package com.hzsun.aidevops.walletconfig.deviceidentitywallet.cmdservice;

import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentity;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.ConsumerIdentityRepository;
import com.hzsun.aidevops.walletconfig.device.domain.Device;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceRepository;
import com.hzsun.aidevops.walletconfig.device.domain.DeviceType;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWallet;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWalletFactory;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.DeviceIdentityWalletRepository;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletDimension;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletDispatchTarget;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.domain.WalletType;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfig;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigRepository;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPair;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairFactory;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairRepository;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 下发表命令服务单元测试。
 *
 * <p>聚焦批次版本号的分配规则：租户内首次下发从 1 开始、后续取「历史最大号 + 1」、
 * 无变更时不占用批次号、失效行保留其被下发时的批次号。</p>
 */
@ExtendWith(MockitoExtension.class)
class DeviceIdentityWalletCmdServiceTest {

    private static final Long TENANT_ID = 1L;

    private static final Long DEVICE_ID = 100L;

    private static final Long ORGANIZATION_ID = 300L;

    private static final Long IDENTITY_ID = 200L;

    @Mock
    private DeviceIdentityWalletRepository deviceIdentityWalletRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private OrganizationPaymentConfigRepository organizationPaymentConfigRepository;

    @Mock
    private WalletPairRepository walletPairRepository;

    @Mock
    private ConsumerIdentityRepository consumerIdentityRepository;

    @Mock
    private IdGenerator idGenerator;

    private DeviceIdentityWalletCmdService cmdService;

    @BeforeEach
    void setUp() {
        cmdService = new DeviceIdentityWalletCmdService(deviceIdentityWalletRepository, deviceRepository,
                organizationPaymentConfigRepository, walletPairRepository, consumerIdentityRepository, idGenerator);
    }

    @Test
    @DisplayName("租户内首次下发时批次版本号从 1 开始")
    void allocatesFirstBatchVersionWhenNoHistory() {
        givenDeviceWithPaymentConfig();
        when(deviceIdentityWalletRepository.findByDeviceIds(TENANT_ID, List.of(DEVICE_ID))).thenReturn(List.of());
        when(deviceIdentityWalletRepository.findMaxVersion(TENANT_ID)).thenReturn(0);

        cmdService.refreshByDevice(TENANT_ID, DEVICE_ID);

        List<DeviceIdentityWallet> inserted = captureInserted();
        assertEquals(1, inserted.size());
        assertTrue(inserted.get(0).isValid());
        assertEquals(1, inserted.get(0).getVersion());
        verify(deviceIdentityWalletRepository, never()).updateAll(anyList());
    }

    @Test
    @DisplayName("已有历史行时批次版本号取历史最大号 + 1")
    void allocatesNextBatchVersionAfterHistory() {
        givenDeviceWithPaymentConfig();
        when(deviceIdentityWalletRepository.findByDeviceIds(TENANT_ID, List.of(DEVICE_ID))).thenReturn(List.of());
        when(deviceIdentityWalletRepository.findMaxVersion(TENANT_ID)).thenReturn(13);

        cmdService.refreshByDevice(TENANT_ID, DEVICE_ID);

        assertEquals(14, captureInserted().get(0).getVersion());
    }

    @Test
    @DisplayName("同一次重算内新增行共用同一个批次版本号")
    void sharesBatchVersionWithinOneRefresh() {
        givenDeviceWithPaymentConfig();
        // 先构造好配对再打桩：构造过程会调用 idGenerator（也是 mock），不能落在 thenReturn 参数里
        WalletPair walletPair = walletPairOfWorkWallet(1, 2);
        when(walletPairRepository.findAll(TENANT_ID)).thenReturn(List.of(walletPair));
        when(deviceIdentityWalletRepository.findByDeviceIds(TENANT_ID, List.of(DEVICE_ID))).thenReturn(List.of());
        when(deviceIdentityWalletRepository.findMaxVersion(TENANT_ID)).thenReturn(4);

        cmdService.refreshByDevice(TENANT_ID, DEVICE_ID);

        List<DeviceIdentityWallet> inserted = captureInserted();
        assertEquals(2, inserted.size());
        assertEquals(5, inserted.get(0).getVersion());
        assertEquals(5, inserted.get(1).getVersion());
    }

    @Test
    @DisplayName("配置未变化时不占用批次版本号，也不写库")
    void skipsBatchAllocationWhenNothingChanged() {
        givenDeviceWithPaymentConfig();
        DeviceIdentityWallet unchanged = workWalletRow(1, 5);
        when(deviceIdentityWalletRepository.findByDeviceIds(TENANT_ID, List.of(DEVICE_ID)))
                .thenReturn(List.of(unchanged));

        cmdService.refreshByDevice(TENANT_ID, DEVICE_ID);

        verify(deviceIdentityWalletRepository, never()).findMaxVersion(TENANT_ID);
        verify(deviceIdentityWalletRepository, never()).saveAll(anyList());
        verify(deviceIdentityWalletRepository, never()).updateAll(anyList());
    }

    @Test
    @DisplayName("失效行保留原批次号，新增行取新批次号")
    void keepsOriginalBatchVersionOnInvalidatedRow() {
        givenDeviceWithPaymentConfig();
        DeviceIdentityWallet stale = workWalletRow(5, 7);
        when(deviceIdentityWalletRepository.findByDeviceIds(TENANT_ID, List.of(DEVICE_ID))).thenReturn(List.of(stale));
        when(deviceIdentityWalletRepository.findMaxVersion(TENANT_ID)).thenReturn(9);

        cmdService.refreshByDevice(TENANT_ID, DEVICE_ID);

        ArgumentCaptor<List<DeviceIdentityWallet>> invalidatedCaptor = ArgumentCaptor.forClass(List.class);
        verify(deviceIdentityWalletRepository).updateAll(invalidatedCaptor.capture());
        DeviceIdentityWallet invalidated = invalidatedCaptor.getValue().get(0);
        assertFalse(invalidated.isValid());
        assertEquals(7, invalidated.getVersion());

        List<DeviceIdentityWallet> inserted = captureInserted();
        assertEquals(10, inserted.get(0).getVersion());
        assertTrue(inserted.get(0).isValid());
    }

    @SuppressWarnings("unchecked")
    private List<DeviceIdentityWallet> captureInserted() {
        ArgumentCaptor<List<DeviceIdentityWallet>> captor = ArgumentCaptor.forClass(List.class);
        verify(deviceIdentityWalletRepository).saveAll(captor.capture());
        return captor.getValue();
    }

    private void givenDeviceWithPaymentConfig() {
        when(deviceRepository.findById(TENANT_ID, DEVICE_ID)).thenReturn(Optional.of(
                Device.reconstitute(DEVICE_ID, TENANT_ID, "POS-0001", DeviceType.POS_MACHINE, ORGANIZATION_ID)));
        when(organizationPaymentConfigRepository.findAll(TENANT_ID)).thenReturn(List.of(
                OrganizationPaymentConfig.reconstitute(1L, TENANT_ID, ORGANIZATION_ID, false, WalletNo.of(1),
                        false, false, Set.of())));
        when(consumerIdentityRepository.findAll(TENANT_ID)).thenReturn(List.of(
                ConsumerIdentity.reconstitute(IDENTITY_ID, TENANT_ID, "学生", false)));
        when(walletPairRepository.findAll(TENANT_ID)).thenReturn(List.of());
    }

    private DeviceIdentityWallet workWalletRow(int walletNo, int version) {
        return DeviceIdentityWalletFactory.create(idGenerator, TENANT_ID, new WalletDispatchTarget(
                new WalletDimension(DEVICE_ID, IDENTITY_ID, WalletType.WORK), WalletNo.of(walletNo), false, false),
                version);
    }

    private WalletPair walletPairOfWorkWallet(int workWalletNo, int deductWalletNo) {
        return WalletPairFactory.create(idGenerator, TENANT_ID,
                new WalletPairConfiguration(WalletNo.of(workWalletNo), WalletNo.of(deductWalletNo)));
    }
}
