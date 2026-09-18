package com.hzsun.aidevops.walletconfig.consumeridentity.domain.valueobject;

import java.util.Set;

/**
 * 身份登记信息。
 *
 * <p>表达一次身份创建或改名操作所需的业务事实：目标名称与租户内已存在的身份名称集合，
 * 用于名称租户内唯一性规则的校验。</p>
 *
 * @param name          目标身份名称
 * @param existingNames 租户内已存在的身份名称（改名场景需排除本条记录自身）
 */
public record IdentityRegistration(String name, Set<String> existingNames) {

    /**
     * 名称是否已被其他身份占用。
     *
     * @param candidateName 待校验名称
     * @return true 表示已被占用
     */
    public boolean nameOccupiedBy(String candidateName) {
        return existingNames.contains(candidateName);
    }
}
