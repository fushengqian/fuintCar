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
@TableName("mt_vehicle_order")
@ApiModel(value = "MtVehicleOrder对象", description = "")
public class MtVehicleOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("服务单号")
    private String orderSn;

    @ApiModelProperty("车牌号")
    private String vehiclePlateNo;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("状态：A：已提交；B：服务中；C：已完成; D：已删除；")
    private String status;

    @ApiModelProperty("扫码编码")
    private String scanCode;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
