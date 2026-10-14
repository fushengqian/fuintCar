package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="所属商户 ID")
    private Integer merchantId;

    @Schema(description="所属店铺 ID")
    private Integer storeId;

    @Schema(description="卡券名称")
    private String name;

    @Schema(description="卡券分组 ID")
    private Integer groupId;

    @Schema(description="卡券 ID")
    private Integer couponId;

    @Schema(description="卡券类型")
    private String type;

    @Schema(description="状态，A 正常；D 作废")
    private String status;

}
