package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<ParamDto> getVehicleOrderStatusList() {
        return Arrays.stream(VehicleOrderStatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
