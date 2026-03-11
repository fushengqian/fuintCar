package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponPage extends PageParam implements Serializable {

    @ApiModelProperty("所属商户 ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺 ID")
    private Integer storeId;

    @ApiModelProperty("卡券名称")
    private String name;

    @ApiModelProperty("卡券分组 ID")
    private Integer groupId;

    @ApiModelProperty("卡券 ID")
    private Integer couponId;

    @ApiModelProperty("卡券类型")
    private String type;

    @ApiModelProperty("状态，A 正常；D 作废")
    private String status;

}
