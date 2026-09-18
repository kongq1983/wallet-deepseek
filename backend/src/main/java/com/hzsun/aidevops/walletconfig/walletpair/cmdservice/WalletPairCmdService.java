package com.hzsun.aidevops.walletconfig.walletpair.cmdservice;

import com.hzsun.aidevops.common.codec.IdCodec;
import com.hzsun.aidevops.common.domainshared.IdGenerator;
import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;
import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.common.tenant.TenantIds;
import com.hzsun.aidevops.walletconfig.deviceidentitywallet.cmdservice.DeviceIdentityWalletCmdService;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfig;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.OrganizationPaymentConfigRepository;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd.WalletPairAddCmd;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd.WalletPairDeleteCmd;
import com.hzsun.aidevops.walletconfig.walletpair.cmdservice.cmd.WalletPairUpdateCmd;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPair;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairFactory;
import com.hzsun.aidevops.walletconfig.walletpair.domain.WalletPairRepository;
import com.hzsun.aidevops.walletconfig.walletpair.domain.specification.WalletPairMustNotUseSameWalletNoSpecification;
import com.hzsun.aidevops.walletconfig.walletpair.domain.specification.WalletPairNoMustBeInRangeSpecification;
import com.hzsun.aidevops.walletconfig.walletpair.domain.specification.WalletPairNoMustNotBeOccupiedSpecification;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairOccupiedNos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 租户钱包配对命令服务。
 *
 * <p>负责写流程编排与事务边界：进入领域行为前用领域规约完成业务前置校验，
 * 校验失败在此处转换为明确的业务错误码。任何配对变动都会重算受影响机构的下发表数据。</p>
 */
@Service
@RequiredArgsConstructor
public class WalletPairCmdService {

    private final WalletPairRepository walletPairRepository;

    private final OrganizationPaymentConfigRepository organizationPaymentConfigRepository;

    private final DeviceIdentityWalletCmdService deviceIdentityWalletCmdService;

    private final IdGenerator idGenerator;

    /**
     * 新增钱包配对。
     *
     * @param tenantId 租户 ID
     * @param cmd      新增命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Long tenantId, WalletPairAddCmd cmd) {
        TenantIds.requireValid(tenantId);
        WalletPairConfiguration configuration = toConfiguration(cmd.getWorkWalletNo(), cmd.getDeductWalletNo());
        validateConfiguration(tenantId, null, configuration);
        walletPairRepository.save(WalletPairFactory.create(idGenerator, tenantId, configuration));
        refreshDispatch(tenantId, Set.of(configuration.workWalletNo().value()));
    }

    /**
     * 修改钱包配对。
     *
     * @param tenantId 租户 ID
     * @param cmd      修改命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long tenantId, WalletPairUpdateCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long walletPairId = IdCodec.toLong(cmd.getId());
        WalletPair walletPair = walletPairRepository.findById(tenantId, walletPairId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.WALLET_PAIR_NOT_FOUND, "钱包配对不存在"));
        WalletPairConfiguration configuration = toConfiguration(cmd.getWorkWalletNo(), cmd.getDeductWalletNo());
        validateConfiguration(tenantId, walletPairId, configuration);

        int previousWorkWalletNo = walletPair.getWorkWalletNo().value();
        walletPair.updateConfiguration(configuration);
        walletPairRepository.update(walletPair);
        // 修改前后可能仍是同一个工作钱包编号，用 Set.copyOf 去重，避免 Set.of 因重复元素抛异常
        refreshDispatch(tenantId, Set.copyOf(List.of(previousWorkWalletNo, configuration.workWalletNo().value())));
    }

    /**
     * 删除钱包配对。
     *
     * <p>删除后编号被释放；引用该配对工作钱包的机构支付参数保持不变，其追扣能力随下发表刷新而失效。</p>
     *
     * @param tenantId 租户 ID
     * @param cmd      删除命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long tenantId, WalletPairDeleteCmd cmd) {
        TenantIds.requireValid(tenantId);
        Long walletPairId = IdCodec.toLong(cmd.getId());
        WalletPair walletPair = walletPairRepository.findById(tenantId, walletPairId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.WALLET_PAIR_NOT_FOUND, "钱包配对不存在"));
        int workWalletNo = walletPair.getWorkWalletNo().value();
        walletPairRepository.delete(tenantId, walletPairId);
        refreshDispatch(tenantId, Set.of(workWalletNo));
    }

    /**
     * 重算引用指定工作钱包的机构下设备的下发表数据。
     *
     * @param tenantId      租户 ID
     * @param workWalletNos 受影响的工作钱包编号集合
     */
    private void refreshDispatch(Long tenantId, Set<Integer> workWalletNos) {
        Set<Long> affectedOrganizationIds = organizationPaymentConfigRepository.findAll(tenantId).stream()
                .filter(config -> config.getWorkWalletNo() != null)
                .filter(config -> workWalletNos.contains(config.getWorkWalletNo().value()))
                .map(OrganizationPaymentConfig::getOrganizationId)
                .collect(Collectors.toSet());
        if (!affectedOrganizationIds.isEmpty()) {
            deviceIdentityWalletCmdService.refreshByOrganizations(tenantId, affectedOrganizationIds);
        }
    }

    private WalletPairConfiguration toConfiguration(Integer workWalletNo, Integer deductWalletNo) {
        return new WalletPairConfiguration(WalletNo.of(workWalletNo), WalletNo.of(deductWalletNo));
    }

    /**
     * 执行钱包配对配置的领域规则校验，并按规则映射明确的业务错误码。
     *
     * @param tenantId      租户 ID
     * @param excludedId    修改场景需排除的本条记录 ID，新增场景传 null
     * @param configuration 目标配置
     */
    private void validateConfiguration(Long tenantId, Long excludedId, WalletPairConfiguration configuration) {
        ValidationResult rangeResult = new WalletPairNoMustBeInRangeSpecification().validate(configuration);
        if (!rangeResult.isValid()) {
            throw new BusinessException(ErrorCodes.WALLET_NO_OUT_OF_RANGE, rangeResult.getError());
        }
        ValidationResult sameWalletNoResult = new WalletPairMustNotUseSameWalletNoSpecification().validate(configuration);
        if (!sameWalletNoResult.isValid()) {
            throw new BusinessException(ErrorCodes.WALLET_PAIR_SAME_WALLET_NO, sameWalletNoResult.getError());
        }
        ValidationResult occupiedResult = new WalletPairNoMustNotBeOccupiedSpecification(occupiedNos(tenantId, excludedId))
                .validate(configuration);
        if (!occupiedResult.isValid()) {
            throw new BusinessException(ErrorCodes.WALLET_NO_ALREADY_USED, occupiedResult.getError());
        }
    }

    /**
     * 汇总租户内已被占用的钱包编号。
     *
     * @param tenantId   租户 ID
     * @param excludedId 需排除的记录 ID，可为 null
     * @return 已被占用的编号集合
     */
    private WalletPairOccupiedNos occupiedNos(Long tenantId, Long excludedId) {
        Set<Integer> values = walletPairRepository.findAll(tenantId).stream()
                .filter(pair -> excludedId == null || !excludedId.equals(pair.getId()))
                .flatMap(pair -> pair.occupiedWalletNos().stream())
                .collect(Collectors.toSet());
        return WalletPairOccupiedNos.of(values);
    }
}
