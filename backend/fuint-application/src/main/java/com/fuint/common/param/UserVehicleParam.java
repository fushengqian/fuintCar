package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 会员车辆请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserVehicleParam implements Serializable {

    @ApiModelProperty(value="车辆ID", name="id")
    private Integer id;

    @ApiModelProperty(value="会员名称", name="name")
    private String name;

    @ApiModelProperty(value="会员会员号", name="userNo")
    private String userNo;

    @ApiModelProperty(value="会员手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="车牌号", name="vehiclePlateNo")
    private String vehiclePlateNo;

    @ApiModelProperty(value="车辆类型", name="vehicleType")
    private String vehicleType;

    @ApiModelProperty(value="车辆颜色", name="vehicleColor")
    private String vehicleColor;

    @ApiModelProperty(value="车辆品牌", name="vehicleBrand")
    private String vehicleBrand;

    @ApiModelProperty(value="车辆型号", name="vehicleModel")
    private String vehicleModel;

    @ApiModelProperty(value="行驶公里数", name="mileage")
    private Integer mileage;

    @ApiModelProperty(value="下个保养日期", name="maintenanceDate")
    private Date maintenanceDate;

    @ApiModelProperty(value="所在地址", name="address")
    private String address;

    @ApiModelProperty(value="车架号", name="vin")
    private String vin;

}
