package com.hzsun.aidevops.walletconfig.organization.domain.valueobject;

import com.hzsun.aidevops.walletconfig.organization.domain.OrganizationType;

import java.util.Set;

/**
 * 机构登记信息。
 *
 * <p>表达一次新增机构操作所需的全部业务事实：目标名称与类型、上级机构及其类型、
 * 目标层级（由上级机构层级推导）以及上级机构下已存在的机构名称。
 * 这些事实用于机构树深度、档口叶子约束与同层名称唯一性规则的校验。</p>
 *
 * @param name                      目标机构名称
 * @param orgType                   目标机构类型
 * @param superiorId                上级机构 ID，为空表示创建顶层机构
 * @param superiorType              上级机构类型，顶层机构场景为 null
 * @param targetLevel               目标机构层级，顶层机构为 1
 * @param existingNamesUnderSuperior 上级机构下已存在的机构名称
 */
public record OrganizationRegistration(
        String name,
        OrganizationType orgType,
        Long superiorId,
        OrganizationType superiorType,
        int targetLevel,
        Set<String> existingNamesUnderSuperior) {

    /**
     * 上级机构是否为档口。
     *
     * @return true 表示上级机构是档口
     */
    public boolean superiorIsStall() {
        return superiorType == OrganizationType.STALL;
    }

    /**
     * 名称是否已被同层机构占用。
     *
     * @param candidateName 待校验名称
     * @return true 表示已被占用
     */
    public boolean nameOccupiedBy(String candidateName) {
        return existingNamesUnderSuperior.contains(candidateName);
    }
}
