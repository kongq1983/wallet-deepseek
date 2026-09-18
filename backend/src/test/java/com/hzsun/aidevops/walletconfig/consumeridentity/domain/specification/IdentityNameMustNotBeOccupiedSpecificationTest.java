package com.hzsun.aidevops.walletconfig.consumeridentity.domain.specification;

import com.hzsun.aidevops.common.specification.ValidationResult;
import com.hzsun.aidevops.walletconfig.consumeridentity.domain.valueobject.IdentityRegistration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 身份名称唯一性规约单元测试。
 */
class IdentityNameMustNotBeOccupiedSpecificationTest {

    @Test
    @DisplayName("租户内不存在同名身份时规约通过")
    void passesWhenNameFree() {
        IdentityRegistration registration = new IdentityRegistration("学生", Set.of("老师"));

        ValidationResult result = new IdentityNameMustNotBeOccupiedSpecification().validate(registration);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("租户内已存在同名身份时规约失败")
    void failsWhenNameOccupied() {
        IdentityRegistration registration = new IdentityRegistration("学生", Set.of("学生", "老师"));

        ValidationResult result = new IdentityNameMustNotBeOccupiedSpecification().validate(registration);

        assertFalse(result.isValid());
        assertEquals("身份名称已存在", result.getError());
    }
}
