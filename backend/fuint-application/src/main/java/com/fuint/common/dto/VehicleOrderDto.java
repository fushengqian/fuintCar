package com.fuint.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class VehicleOrderDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("服务单好")
    private String orderSn;

    @ApiModelProperty("车牌号")
    private String vehiclePlateNo;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("用户电话")
    private String mobile;

    @ApiModelProperty("会员号")
    private String userNo;

    @ApiModelProperty("会员名称")
    private String name;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("所属店铺")
    private MtStore storeInfo;

    @ApiModelProperty("扫码编码")
    private String scanCode;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("状态：A：未使用；D：已删除")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
  }

