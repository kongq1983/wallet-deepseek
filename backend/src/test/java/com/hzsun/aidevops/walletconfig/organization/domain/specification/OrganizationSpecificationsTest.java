package com.hzsun.aidevops.walletconfig.organization.domain.specification;

import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationType;
import com.hzsun.aidevops.walletconfig.organization.domain.valueobject.OrganizationRegistration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 机构领域规约单元测试。
 *
 * <p>覆盖层级上限、档口叶子约束与同层名称唯一三条领域业务规则。</p>
 */
class OrganizationSpecificationsTest {

    @Test
    @DisplayName("目标层级不超过 3 时层级规约通过")
    void depthSpecificationPassesWhenLevelWithinLimit() {
        ValidationResult result = new OrganizationDepthMustNotExceedLimitSpecification()
                .validate(registration("凉菜窗口", OrganizationType.STALL, 3, OrganizationType.STALL, Set.of()));

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("目标层级超过 3 时层级规约失败")
    void depthSpecificationFailsWhenLevelExceeded() {
        ValidationResult result = new OrganizationDepthMustNotExceedLimitSpecification()
                .validate(registration("内层窗口", OrganizationType.STALL, 4, OrganizationType.STALL, Set.of()));

        assertFalse(result.isValid());
        assertEquals("机构层级不能超过3层", result.getError());
    }

    @Test
    @DisplayName("上级机构不是档口时叶子约束规约通过")
    void stallSpecificationPassesWhenSuperiorIsNotStall() {
        ValidationResult result = new OrganizationStallMustNotHaveChildSpecification()
                .validate(registration("一号档口", OrganizationType.STALL, 2, OrganizationType.RESTAURANT, Set.of()));

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("上级机构是档口时叶子约束规约失败")
    void stallSpecificationFailsWhenSuperiorIsStall() {
        ValidationResult result = new OrganizationStallMustNotHaveChildSpecification()
                .validate(registration("内部窗口", OrganizationType.STALL, 3, OrganizationType.STALL, Set.of()));

        assertFalse(result.isValid());
        assertEquals("档口下不能创建子机构", result.getError());
    }

    @Test
    @DisplayName("同层名称未被占用时名称规约通过")
    void nameSpecificationPassesWhenNameFree() {
        ValidationResult result = new OrganizationNameMustNotBeOccupiedSpecification()
                .validate(registration("一号档口", OrganizationType.STALL, 2, OrganizationType.RESTAURANT, Set.of("二号档口")));

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("同层名称已存在时名称规约失败")
    void nameSpecificationFailsWhenNameOccupied() {
        ValidationResult result = new OrganizationNameMustNotBeOccupiedSpecification()
                .validate(registration("一号档口", OrganizationType.STALL, 2, OrganizationType.RESTAURANT, Set.of("一号档口")));

        assertFalse(result.isValid());
        assertEquals("机构名称已存在", result.getError());
    }

    private OrganizationRegistration registration(String name, OrganizationType orgType, int targetLevel,
                                                  OrganizationType superiorType, Set<String> existingNames) {
        return new OrganizationRegistration(name, orgType, 10L, superiorType, targetLevel, existingNames);
    }
}
