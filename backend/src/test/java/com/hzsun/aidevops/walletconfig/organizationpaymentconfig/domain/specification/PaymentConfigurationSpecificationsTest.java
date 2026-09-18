package com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.specification;

import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.organizationpaymentconfig.domain.valueobject.PaymentConfiguration;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 机构支付参数领域规约单元测试。
 *
 * <p>覆盖“工作钱包必须来自租户钱包配对”与“身份限制开启时必须选择可消费身份”两条规则。</p>
 */
class PaymentConfigurationSpecificationsTest {

    @Test
    @DisplayName("工作钱包在可用范围内时规约通过")
    void workWalletSpecificationPassesWhenAvailable() {
        PaymentConfiguration configuration = configuration(WalletNo.of(1), false, Set.of(), Set.of(1, 3));

        ValidationResult result = new PaymentWorkWalletMustBeAvailableSpecification().validate(configuration);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("工作钱包未配置时规约失败")
    void workWalletSpecificationFailsWhenMissing() {
        PaymentConfiguration configuration = configuration(null, false, Set.of(), Set.of(1, 3));

        ValidationResult result = new PaymentWorkWalletMustBeAvailableSpecification().validate(configuration);

        assertFalse(result.isValid());
        assertEquals("请选择工作钱包", result.getError());
    }

    @Test
    @DisplayName("工作钱包不在租户钱包配对中时规约失败")
    void workWalletSpecificationFailsWhenNotConfiguredInWalletPair() {
        PaymentConfiguration configuration = configuration(WalletNo.of(5), false, Set.of(), Set.of(1, 3));

        ValidationResult result = new PaymentWorkWalletMustBeAvailableSpecification().validate(configuration);

        assertFalse(result.isValid());
        assertEquals("请选择工作钱包", result.getError());
    }

    @Test
    @DisplayName("身份限制关闭时不要求可消费身份")
    void identitySpecificationPassesWhenRestrictionDisabled() {
        PaymentConfiguration configuration = configuration(WalletNo.of(1), false, Set.of(), Set.of(1));

        ValidationResult result = new IdentityRestrictionMustSelectAllowedIdentitySpecification()
                .validate(configuration);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("身份限制开启且已选择可消费身份时规约通过")
    void identitySpecificationPassesWhenIdentitiesSelected() {
        PaymentConfiguration configuration = configuration(WalletNo.of(1), true, Set.of(200L, 201L), Set.of(1));

        ValidationResult result = new IdentityRestrictionMustSelectAllowedIdentitySpecification()
                .validate(configuration);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("身份限制开启但未选择可消费身份时规约失败")
    void identitySpecificationFailsWhenIdentitiesEmpty() {
        PaymentConfiguration configuration = configuration(WalletNo.of(1), true, Set.of(), Set.of(1));

        ValidationResult result = new IdentityRestrictionMustSelectAllowedIdentitySpecification()
                .validate(configuration);

        assertFalse(result.isValid());
        assertEquals("请选择可消费身份", result.getError());
    }

    private PaymentConfiguration configuration(WalletNo workWalletNo, boolean identityRestricted,
                                              Set<Long> allowedIdentityIds, Set<Integer> availableWorkWalletNos) {
        return new PaymentConfiguration(workWalletNo, identityRestricted, allowedIdentityIds, availableWorkWalletNos);
    }
}
