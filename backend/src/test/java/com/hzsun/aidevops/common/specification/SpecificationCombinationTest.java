package com.hzsun.aidevops.common.specification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 规约组合语义单元测试。
 *
 * <p>覆盖 and 的失败短路与全错误收集、or 的任一通过、not 的结果反转。</p>
 */
class SpecificationCombinationTest {

    @Test
    @DisplayName("and 组合在 FAIL_FAST 下返回第一条失败规则")
    void andCombinationReturnsFirstFailureInFailFast() {
        Specification<String> specification = new AlwaysInvalidSpecification("第一条失败")
                .and(new AlwaysInvalidSpecification("第二条失败"));

        ValidationResult result = specification.validate("任意值");

        assertFalse(result.isValid());
        assertEquals("第一条失败", result.getError());
    }

    @Test
    @DisplayName("and 组合在 ALL_ERRORS 下收集全部失败规则")
    void andCombinationCollectsAllErrorsInAllErrorsStrategy() {
        Specification<String> specification = new AlwaysInvalidSpecification("第一条失败")
                .and(new AlwaysInvalidSpecification("第二条失败"))
                .withStrategy(ValidationStrategy.ALL_ERRORS);

        ValidationResult result = specification.validate("任意值");

        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertEquals("第一条失败", result.getErrors().get(0));
        assertEquals("第二条失败", result.getErrors().get(1));
    }

    @Test
    @DisplayName("or 组合任一通过即通过")
    void orCombinationPassesWhenEitherValid() {
        Specification<String> specification = new AlwaysInvalidSpecification("失败")
                .or(new AlwaysValidSpecification());

        assertTrue(specification.validate("任意值").isValid());
    }

    @Test
    @DisplayName("or 组合全部失败时返回失败")
    void orCombinationFailsWhenBothInvalid() {
        Specification<String> specification = new AlwaysInvalidSpecification("第一处失败")
                .or(new AlwaysInvalidSpecification("第二处失败"));

        ValidationResult result = specification.validate("任意值");

        assertFalse(result.isValid());
        assertEquals("第二处失败", result.getError());
    }

    @Test
    @DisplayName("not 组合反转校验结果")
    void notCombinationReversesResult() {
        Specification<String> validSpecification = new AlwaysValidSpecification().not();
        Specification<String> invalidSpecification = new AlwaysInvalidSpecification("失败").not();

        assertFalse(validSpecification.validate("任意值").isValid());
        assertTrue(invalidSpecification.validate("任意值").isValid());
    }

    /**
     * 始终通过的测试规约。
     */
    private static final class AlwaysValidSpecification extends CompositeSpecification<String> {

        @Override
        public ValidationResult validate(String value) {
            return ValidationResult.valid();
        }
    }

    /**
     * 始终失败的测试规约。
     */
    private static final class AlwaysInvalidSpecification extends CompositeSpecification<String> {

        private final String error;

        private AlwaysInvalidSpecification(String error) {
            this.error = error;
        }

        @Override
        public ValidationResult validate(String value) {
            return ValidationResult.invalid(error);
        }
    }
}
