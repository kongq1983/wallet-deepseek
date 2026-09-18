package com.hzsun.aidevops.common.exception;

/**
 * 全局错误码常量。
 *
 * <p>规范约定：code=200 为唯一成功码，code=500 为通用失败码（无特殊前端处理需求时统一使用），
 * code>=501 为特殊错误码且只能递增、不得复用。此处集中登记 walletconfig 上下文的业务错误码。</p>
 */
public final class ErrorCodes {

    /** 通用失败：无特殊前端处理需求时使用。 */
    public static final int COMMON_FAILURE = 500;

    /** 登录状态已失效。 */
    public static final int UNAUTHORIZED = 501;

    // ------- 租户钱包设置（502~506） -------

    /** 钱包编号超出 1~8 的取值范围。 */
    public static final int WALLET_NO_OUT_OF_RANGE = 502;

    /** 钱包编号在租户钱包配对中已被占用。 */
    public static final int WALLET_NO_ALREADY_USED = 503;

    /** 同一条钱包配对记录中工作钱包编号与追扣钱包编号相同。 */
    public static final int WALLET_PAIR_SAME_WALLET_NO = 504;

    /** 待操作的钱包配对不存在。 */
    public static final int WALLET_PAIR_NOT_FOUND = 505;

    // ------- 机构管理（506~509） -------

    /** 机构名称在同一上级机构下重复。 */
    public static final int ORGANIZATION_NAME_DUPLICATED = 506;

    /** 机构树深度超过 3 层。 */
    public static final int ORGANIZATION_DEPTH_EXCEEDED = 507;

    /** 在档口下创建子机构。 */
    public static final int ORGANIZATION_STALL_CANNOT_HAVE_CHILD = 508;

    /** 待操作的机构不存在。 */
    public static final int ORGANIZATION_NOT_FOUND = 509;

    // ------- 机构支付参数（510~513） -------

    /** 机构支付参数的工作钱包未在租户钱包配对中配置。 */
    public static final int ORGANIZATION_WORK_WALLET_INVALID = 510;

    /** 启用身份限制时未选择可消费身份。 */
    public static final int ORGANIZATION_ALLOWED_IDENTITY_REQUIRED = 511;

    /** 机构支付参数不存在。 */
    public static final int ORGANIZATION_PAYMENT_CONFIG_NOT_FOUND = 512;

    // ------- 身份管理（513~514） -------

    /** 身份名称在租户内重复。 */
    public static final int IDENTITY_NAME_DUPLICATED = 513;

    /** 待操作的身份不存在。 */
    public static final int IDENTITY_NOT_FOUND = 514;

    // ------- 设备管理（515~516） -------

    /** 设备序列号已存在。 */
    public static final int DEVICE_SERIAL_NO_DUPLICATED = 515;

    /** 待操作的设备不存在。 */
    public static final int DEVICE_NOT_FOUND = 516;

    private ErrorCodes() {
    }
}
