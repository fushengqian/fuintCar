package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class VehicleOrderPage extends PageParam implements Serializable {

    @Schema(description="ID")
    private String id;

    @Schema(description="车牌号")
    private String vehiclePlateNo;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="店铺ID，逗号分隔")
    private String storeIds;

    @Schema(description="开始时间")
    private String startTime;

    @Schema(description="结束时间")
    private String endTime;

    @Schema(description="状态")
    private String status;

}
