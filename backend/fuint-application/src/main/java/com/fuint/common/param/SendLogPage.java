package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券发放记录分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SendLogPage extends PageParam implements Serializable {

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("会员 ID")
    private Integer userId;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("发放批次")
    private String uuid;

    @ApiModelProperty("卡券 ID")
    private Integer couponId;

    @ApiModelProperty("所属商户 ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺 ID")
    private Integer storeId;

}
