package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="车辆ID")
    private Integer id;

    @Schema(description="会员名称")
    private String name;

    @Schema(description="会员会员号")
    private String userNo;

    @Schema(description="会员手机号")
    private String mobile;

    @Schema(description="车牌号")
    private String vehiclePlateNo;

    @Schema(description="车辆类型")
    private String vehicleType;

    @Schema(description="车辆颜色")
    private String vehicleColor;

    @Schema(description="车辆品牌")
    private String vehicleBrand;

    @Schema(description="车辆型号")
    private String vehicleModel;

    @Schema(description="行驶公里数")
    private Integer mileage;

    @Schema(description="下个保养日期")
    private Date maintenanceDate;

    @Schema(description="所在地址")
    private String address;

    @Schema(description="车架号")
    private String vin;

}
