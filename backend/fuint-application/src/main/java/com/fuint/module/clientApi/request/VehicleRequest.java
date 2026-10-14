package com.fuint.module.clientApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="车辆ID")
    private Integer vehicleId;

    @Schema(description="车牌号")
    private String vehiclePlateNo;

    @Schema(description="车辆品牌")
    private String vehicleBrand;

    @Schema(description="车辆型号")
    private String vehicleModel;

    @Schema(description="车辆类型")
    private String vehicleType;

    @Schema(description="状态")
    private String status;

    @Schema(description="是否默认")
    private String isDefault;

}
