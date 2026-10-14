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
public class VehicleOrderRequest implements Serializable {

    @Schema(description="车辆ID")
    private Integer vehicleId;

    @Schema(description="车牌号")
    private Integer couponId;

    @Schema(description="备注信息")
    private String remark;

    @Schema(description="扫码编码")
    private String scanCode;

}
