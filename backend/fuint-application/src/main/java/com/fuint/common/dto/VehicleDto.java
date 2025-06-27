package com.fuint.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class VehicleDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("用户电话")
    private String mobile;

    @ApiModelProperty("会员号")
    private String userNo;

    @ApiModelProperty("会员名称")
    private String name;

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

    @ApiModelProperty("是否默认")
    private String isDefault;

    @ApiModelProperty("状态：A：未使用；D：已删除")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
  }

