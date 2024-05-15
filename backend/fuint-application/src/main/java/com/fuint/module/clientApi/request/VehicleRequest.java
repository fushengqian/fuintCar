package com.fuint.module.clientApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 请求车辆请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class VehicleRequest implements Serializable {

    @ApiModelProperty(value="车辆ID", name="vehicleId")
    private Integer vehicleId;

    @ApiModelProperty(value="车牌号", name="vehiclePlateNo")
    private String vehiclePlateNo;

    @ApiModelProperty(value="车辆品牌", name="vehicleBrand")
    private String vehicleBrand;

    @ApiModelProperty(value="车辆型号", name="vehicleModel")
    private String vehicleModel;

    @ApiModelProperty(value="车辆类型", name="vehicleType")
    private String vehicleType;

    @ApiModelProperty(value="状态", name="status")
    private String status;

    @ApiModelProperty(value="是否默认", name="isDefault")
    private String isDefault;

}
