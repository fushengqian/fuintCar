package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 预约订单实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_book_item")
@ApiModel(value = "MtBookItem对象", description = "MtBookItem表对象")
public class MtBookItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("预约ID")
    private Integer bookId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("预约联系人")
    private String contact;

    @ApiModelProperty("预约手机号")
    private String mobile;

    @ApiModelProperty("预约日期")
    private Date serviceDate;

    @ApiModelProperty("预约时间段")
    private Date serviceTime;

    @ApiModelProperty("预约备注")
    private String remark;

    @ApiModelProperty("预约备注")
    private Integer serviceStaffId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("A：正常；D：删除")
    private String status;

}
