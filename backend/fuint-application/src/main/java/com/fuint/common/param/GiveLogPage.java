package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 转赠记录分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GiveLogPage extends PageParam implements Serializable {

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="卡券ID")
    private Integer couponId;

}
