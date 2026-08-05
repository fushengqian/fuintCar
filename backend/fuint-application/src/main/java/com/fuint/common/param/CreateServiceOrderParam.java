package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateServiceOrderParam {

    @ApiModelProperty("会员车辆ID")
    private Integer vehicleId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("卡券ID（可选）")
    private Integer couponId;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("关联的已有订单ID列表（客户提前在小程序下单的订单）")
    private List<Integer> existingOrderIds;

    @ApiModelProperty("服务项目列表")
    private List<ServiceItem> serviceItems;

    @Data
    public static class ServiceItem {

        @ApiModelProperty("商品ID")
        private Integer goodsId;

        @ApiModelProperty("商品数量")
        private Integer buyNum;

        @ApiModelProperty("单价")
        private BigDecimal price;
    }
}
