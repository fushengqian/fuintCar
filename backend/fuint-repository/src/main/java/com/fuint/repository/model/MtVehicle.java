package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("mt_vehicle")
@Schema(name="MtVehicle对象", description="会员车辆")
public class MtVehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="车辆型号")
    private String vehicleModel;

    @Schema(description="车辆类型")
    private String vehicleType;

    @Schema(description="车牌号")
    private String vehiclePlateNo;

    @Schema(description="车辆颜色")
    private String vehicleColor;

    @Schema(description="车辆品牌")
    private String vehicleBrand;

    @Schema(description="车辆行驶公里数")
    private Integer vehicleMileage;

    @Schema(description="行驶公里数记录时间")
    private Date mileageRecordTime;

    @Schema(description="车架号")
    private String vin;

    @Schema(description="状态：A：未使用；B：已使用；C：已过期; D：已删除；E：未领取")
    private String status;

    @Schema(description="是否默认")
    private String isDefault;

    @Schema(description="操作人")
    private String operator;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;
}
