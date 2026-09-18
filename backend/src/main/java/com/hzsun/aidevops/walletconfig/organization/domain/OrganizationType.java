package com.hzsun.aidevops.walletconfig.organization.domain;

/**
 * 机构类型。
 *
 * <p>类型与层级不绑定，管理员可自由组合；唯一硬约束是“档口”不能拥有子机构。</p>
 */
public enum OrganizationType {

    /** 餐厅。 */
    RESTAURANT("餐厅"),

    /** 档口。 */
    STALL("档口"),

    /** 超市。 */
    SUPERMARKET("超市");

    private final String label;

    OrganizationType(String label) {
        this.label = label;
    }

    /**
     * 获取用于展示的中文含义。
     *
     * @return 中文标签
     */
    public String getLabel() {
        return label;
    }
}
