package com.hzsun.aidevops.common.tenant;

import com.hzsun.aidevops.common.exception.BusinessException;
import com.hzsun.aidevops.common.exception.ErrorCodes;

/**
 * 租户 ID 合法性校验。
 *
 * <p>应用服务入口第一步必须调用 {@link #requireValid(Long)} 完成校验，
 * 后续向领域模型、仓储、Mapper 传递的租户必须使用已校验值。</p>
 */
public final class TenantIds {

    /**
     * 校验租户 ID 是否合法，不合法时抛出业务异常。
     *
     * @param tenantId 待校验租户 ID
     * @return 校验通过的租户 ID
     */
    public static Long requireValid(Long tenantId) {
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException(ErrorCodes.COMMON_FAILURE, "租户上下文不合法");
        }
        return tenantId;
    }

    private TenantIds() {
    }
}
