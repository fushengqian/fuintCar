package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.order.VehicleOrderDto;
import com.fuint.common.param.VehicleOrderPage;
import com.fuint.common.service.VehicleOrderService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtVehicleOrder;
import com.fuint.utils.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 服务单类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-服务单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/vehicleOrder")
public class ClientVehicleOrderController extends BaseController {

    /**
     * 服务单服务接口
     * */
    private VehicleOrderService vehicleOrderService;

    /**
     * 获取我的订单列表
     */
    @Operation(summary="获取我的服务单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody VehicleOrderPage param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        param.setUserId(userInfo.getId());
        PaginationResponse<VehicleOrderDto> paginationResponse = vehicleOrderService.getVehicleOrderListByPagination(param);
        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取服务单详情
     */
    @Operation(summary="获取服务单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(201, "服务单ID不能为空");
        }
        MtVehicleOrder orderInfo = vehicleOrderService.getVehicleOrderById(Integer.parseInt(orderId));
        return getSuccessResult(orderInfo);
    }
}
