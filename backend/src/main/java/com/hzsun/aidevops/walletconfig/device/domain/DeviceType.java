package com.hzsun.aidevops.walletconfig.device.domain;

/**
 * 设备类型。
 */
public enum DeviceType {

    /** POS 机。 */
    POS_MACHINE("POS机"),

    /** 多媒体人脸机。 */
    FACE_RECOGNITION_MACHINE("多媒体人脸机"),

    /** 其他类型，作为扩展兜底值。 */
    OTHER("其他");

    private final String label;

    DeviceType(String label) {
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
