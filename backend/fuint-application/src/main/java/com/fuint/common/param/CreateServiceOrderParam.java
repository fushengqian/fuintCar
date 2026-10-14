package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateServiceOrderParam {

    @Schema(description="会员车辆ID")
    private Integer vehicleId;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="卡券ID（可选）")
    private Integer couponId;

    @Schema(description="备注信息")
    private String remark;

    @Schema(description="关联的已有订单ID列表（客户提前在小程序下单的订单）")
    private List<Integer> existingOrderIds;

    @Schema(description="服务项目列表")
    private List<ServiceItem> serviceItems;

    @Data
    public static class ServiceItem {

        @Schema(description="商品ID")
        private Integer goodsId;

        @Schema(description="商品数量")
        private Integer buyNum;

        @Schema(description="单价")
        private BigDecimal price;
    }
}
