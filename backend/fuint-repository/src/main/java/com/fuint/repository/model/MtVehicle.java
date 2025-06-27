package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("mt_vehicle")
@ApiModel(value = "MtVehicle对象", description = "")
public class MtVehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("车辆型号")
    private String vehicleModel;

    @ApiModelProperty("车辆类型")
    private String vehicleType;

    @ApiModelProperty("车牌号")
    private String vehiclePlateNo;

    @ApiModelProperty("车辆颜色")
    private String vehicleColor;

    @ApiModelProperty("车辆品牌")
    private String vehicleBrand;

    @ApiModelProperty("车架号")
    private String vin;

    @ApiModelProperty("状态：A：未使用；B：已使用；C：已过期; D：已删除；E：未领取")
    private String status;

    @ApiModelProperty("是否默认")
    private String isDefault;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
