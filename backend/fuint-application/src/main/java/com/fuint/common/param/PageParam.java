package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PageParam implements Serializable {

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("当前页数")
    private Integer page = 1;

    @ApiModelProperty("分页大小")
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    @ApiModelProperty(value="排序字段",name="orderBy")
    private String orderBy;

    /**
     * 排序方式
     */
    @ApiModelProperty(value="排序方式",name="order")
    private String order;

}

