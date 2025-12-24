package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.VehicleOrderDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.VehicleOrderStatusEnum;
import com.fuint.common.service.StoreService;
import com.fuint.common.service.VehicleOrderService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtVehicleOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags="后台管理-车辆服务单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/vehicleOrder")
public class BackendVehicleOrderController extends BaseController {

    /**
     * 服务订单接口
     * */
    private VehicleOrderService vehicleOrderService;

    /**
     * 店铺接口
     * */
    private StoreService storeService;

    @ApiOperation(value="车辆服务单列表", notes="查询车辆服务单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        String userId = request.getParameter("userId");
        String vehiclePlateNo = request.getParameter("vehiclePlateNo");
        String status = request.getParameter("status");
        String orderSn = request.getParameter("orderSn");
        String userNo = request.getParameter("userNo");
        String mobile = request.getParameter("mobile");
        String storeIds = request.getParameter("storeIds");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("vehiclePlateNo", vehiclePlateNo);
        params.put("orderSn", orderSn);
        params.put("userNo", userNo);
        params.put("status", status);
        params.put("mobile", mobile);
        params.put("storeIds", storeIds);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        PaginationResponse<VehicleOrderDto> paginationResponse = vehicleOrderService.getVehicleOrderListByPagination(new PaginationRequest(page, pageSize, params));

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        // 服务单状态列表
        List<ParamDto> statusList = VehicleOrderStatusEnum.getVehicleOrderStatusList();

        Map<String, Object> result = new HashMap<>();
        result.put("storeList", storeList);
        result.put("statusList", statusList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    @ApiOperation(value="查询车辆服务单", notes="查询车辆服务单")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(id);
        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        return getSuccessResult(mtVehicleOrder);
    }

    @ApiOperation(value="更新服务单信息", notes="更新车辆服务单信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(@RequestBody Map<String, String> param) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        String orderId = param.get("orderId");
        String remark = param.get("remark");
        String status  = param.get("status");

        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(Integer.parseInt(orderId));
        if (mtVehicleOrder == null) {
            return getFailureResult(201, "服务单不存在");
        }

        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        mtVehicleOrder.setRemark(remark);
        mtVehicleOrder.setStatus(status);
        vehicleOrderService.updateVehicleOrder(mtVehicleOrder);

        return getSuccessResult(true);
    }
    @ApiOperation(value = "更新车辆服务单状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody Map<String, String> param) throws BusinessCheckException {
        Integer vehicleId = param.get("vehicleId") == null ? 0 : Integer.parseInt(param.get("vehicleId"));
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(vehicleId);
        if (mtVehicleOrder == null) {
            return getFailureResult(201, "服务单不存在");
        }

        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        mtVehicleOrder.setOperator(accountInfo.getAccountName());
        mtVehicleOrder.setStatus(status);
        vehicleOrderService.updateVehicleOrder(mtVehicleOrder);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "删除车辆服务单", notes = "删除车辆服务单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(id);
        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        vehicleOrderService.deleteVehicleOrder(id, accountInfo.getAccountName());
        return getSuccessResult(true);
    }
}
