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
public class VehicleOrderRequest implements Serializable {

    @ApiModelProperty(value="车辆ID", name="vehicleId")
    private Integer vehicleId;

    @ApiModelProperty(value="车牌号", name="couponId")
    private Integer couponId;

    @ApiModelProperty(value="备注信息", name="remark")
    private String remark;

    @ApiModelProperty(value="扫码编码", name="scanCode")
    private String scanCode;

}
