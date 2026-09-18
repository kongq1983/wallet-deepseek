package com.hzsun.aidevops.walletconfig.walletpair.domain.specification;

import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletNo;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairConfiguration;
import com.hzsun.aidevops.walletconfig.walletpair.domain.valueobject.WalletPairOccupiedNos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 租户钱包配对领域规约单元测试。
 *
 * <p>覆盖“编号范围”“编号未被占用”“同一条记录两个编号不相同”三条领域业务规则的通过与失败分支。</p>
 */
class WalletPairSpecificationsTest {

    @Test
    @DisplayName("编号在 1~8 之间时范围规约通过")
    void rangeSpecificationPassesWhenWalletNosInRange() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(1), WalletNo.of(8));

        ValidationResult result = new WalletPairNoMustBeInRangeSpecification().validate(configuration);

        assertTrue(result.isValid());
        assertNull(result.getError());
    }

    @Test
    @DisplayName("工作钱包编号小于 1 时范围规约失败并给出工作钱包提示")
    void rangeSpecificationFailsWhenWorkWalletNoTooSmall() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(0), WalletNo.of(2));

        ValidationResult result = new WalletPairNoMustBeInRangeSpecification().validate(configuration);

        assertFalse(result.isValid());
        assertEquals("工作钱包编号须在1~8之间", result.getError());
    }

    @Test
    @DisplayName("追扣钱包编号大于 8 时范围规约失败并给出追扣钱包提示")
    void rangeSpecificationFailsWhenDeductWalletNoTooLarge() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(1), WalletNo.of(9));

        ValidationResult result = new WalletPairNoMustBeInRangeSpecification().validate(configuration);

        assertFalse(result.isValid());
        assertEquals("追扣钱包编号须在1~8之间", result.getError());
    }

    @Test
    @DisplayName("编号未被占用时占用规约通过")
    void occupiedSpecificationPassesWhenWalletNosFree() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(3), WalletNo.of(4));
        WalletPairOccupiedNos occupiedNos = WalletPairOccupiedNos.of(Set.of(1, 2));

        ValidationResult result = new WalletPairNoMustNotBeOccupiedSpecification(occupiedNos).validate(configuration);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("工作钱包编号已被占用时占用规约失败")
    void occupiedSpecificationFailsWhenWorkWalletNoOccupied() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(1), WalletNo.of(4));
        WalletPairOccupiedNos occupiedNos = WalletPairOccupiedNos.of(Set.of(1, 2));

        ValidationResult result = new WalletPairNoMustNotBeOccupiedSpecification(occupiedNos).validate(configuration);

        assertFalse(result.isValid());
        assertEquals("工作钱包编号已存在", result.getError());
    }

    @Test
    @DisplayName("追扣钱包编号已被占用时占用规约失败")
    void occupiedSpecificationFailsWhenDeductWalletNoOccupied() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(3), WalletNo.of(2));
        WalletPairOccupiedNos occupiedNos = WalletPairOccupiedNos.of(Set.of(1, 2));

        ValidationResult result = new WalletPairNoMustNotBeOccupiedSpecification(occupiedNos).validate(configuration);

        assertFalse(result.isValid());
        assertEquals("追扣钱包编号已存在", result.getError());
    }

    @Test
    @DisplayName("两个编号不同时同一钱包规约通过")
    void sameWalletNoSpecificationPassesWhenDifferent() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(1), WalletNo.of(2));

        ValidationResult result = new WalletPairMustNotUseSameWalletNoSpecification().validate(configuration);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("两个编号相同时同一钱包规约失败")
    void sameWalletNoSpecificationFailsWhenIdentical() {
        WalletPairConfiguration configuration = new WalletPairConfiguration(WalletNo.of(3), WalletNo.of(3));

        ValidationResult result = new WalletPairMustNotUseSameWalletNoSpecification().validate(configuration);

        assertFalse(result.isValid());
        assertEquals("追扣钱包编号不能与工作钱包编号相同", result.getError());
    }
}
