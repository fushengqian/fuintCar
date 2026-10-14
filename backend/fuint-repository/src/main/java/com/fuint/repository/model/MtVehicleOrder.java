package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("mt_vehicle_order")
@Schema(name="MtVehicleOrder对象", description="汽车服务单")
public class MtVehicleOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="服务单号")
    private String orderSn;

    @Schema(description="车牌号")
    private String vehiclePlateNo;

    @Schema(description="会员车辆ID")
    private Integer vehicleId;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="关联订单ID列表，逗号分隔")
    private String orderIds;

    @Schema(description="状态：A：已提交；B：服务中；C：已完成; D：已删除；")
    private String status;

    @Schema(description="扫码编码")
    private String scanCode;

    @Schema(description="备注信息")
    private String remark;

    @Schema(description="操作人")
    private String operator;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;
}
