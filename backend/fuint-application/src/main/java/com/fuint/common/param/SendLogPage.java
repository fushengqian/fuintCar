package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="状态")
    private String status;

    @Schema(description="会员 ID")
    private Integer userId;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="发放批次")
    private String uuid;

    @Schema(description="卡券 ID")
    private Integer couponId;

    @Schema(description="所属商户 ID")
    private Integer merchantId;

    @Schema(description="所属店铺 ID")
    private Integer storeId;

}
