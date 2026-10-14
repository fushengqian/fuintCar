package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 店铺分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StorePage extends PageParam implements Serializable {

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="店铺名称")
    private String name;

    @Schema(description="状态，A 正常；D 作废")
    private String status;

}
