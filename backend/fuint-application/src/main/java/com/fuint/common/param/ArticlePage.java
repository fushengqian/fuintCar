package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 文章分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ArticlePage extends PageParam implements Serializable {

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="标题")
    private String title;

    @Schema(description="商户编码")
    private String merchantNo;

    @Schema(description="状态，A正常；D作废")
    private String status;

}
