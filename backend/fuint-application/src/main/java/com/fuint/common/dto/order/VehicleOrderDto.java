package com.fuint.common.dto.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fuint.repository.model.MtStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class VehicleOrderDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="服务单好")
    private String orderSn;

    @Schema(description="车牌号")
    private String vehiclePlateNo;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="用户电话")
    private String mobile;

    @Schema(description="会员号")
    private String userNo;

    @Schema(description="会员名称")
    private String name;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="所属店铺")
    private MtStore storeInfo;

    @Schema(description="扫码编码")
    private String scanCode;

    @Schema(description="备注信息")
    private String remark;

    @Schema(description="关联订单ID列表，逗号分隔")
    private String orderIds;

    @Schema(description="状态：A：已提交；B：服务中；C：已完成; D：已删除；")
    private String status;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

}

