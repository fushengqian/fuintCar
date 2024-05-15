package com.fuint.common.enums;

/**
 * 车辆服务单状态枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum VehicleOrderStatusEnum {
    CREATED("A", "已提交"),
    SERVICE("B", "服务中"),
    COMPLETE("C", "已完成");

    private String key;

    private String value;

    VehicleOrderStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
