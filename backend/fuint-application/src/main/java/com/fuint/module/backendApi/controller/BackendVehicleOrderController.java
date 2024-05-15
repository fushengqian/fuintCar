package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.VehicleOrderDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.VehicleOrderStatusEnum;
import com.fuint.common.service.AccountService;
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
import com.fuint.repository.model.TAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags="后台管理-车辆服务单相关接口")
@RestController
@RequestMapping(value = "/backendApi/vehicleOrder")
public class BackendVehicleOrderController extends BaseController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private VehicleOrderService vehicleOrderService;

    @Autowired
    private StoreService storeService;

    @ApiOperation(value="车辆服务单列表", notes="查询车辆服务单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
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

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        if (account == null) {
            return getFailureResult(1002, "账号不存在");
        }
        PaginationRequest paginationRequest = new PaginationRequest();
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

        paginationRequest.setSearchParams(params);
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);
        PaginationResponse<VehicleOrderDto> paginationResponse = vehicleOrderService.getVehicleOrderListByPagination(paginationRequest);

        // 店铺列表
        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();
        if (storeId != null && storeId > 0) {
            paramsStore.put("storeId", storeId.toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        // 服务单状态列表
        VehicleOrderStatusEnum[] statusListEnum = VehicleOrderStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (VehicleOrderStatusEnum enumItem : statusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            statusList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("storeList", storeList);
        result.put("statusList", statusList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    @ApiOperation(value="查询车辆服务单", notes="查询车辆服务单")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
            String token = request.getHeader("Access-Token");

            AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
            if (accountInfo == null) {
                return getFailureResult(1001, "请先登录");
            }
            TAccount account = accountService.getAccountInfoById(accountInfo.getId());
            if (account == null) {
                return getFailureResult(1002, "账号不存在");
            }

            MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(id);
            return getSuccessResult(mtVehicleOrder);
    }

    @ApiOperation(value="更新服务单信息", notes="更新车辆服务单信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(HttpServletRequest request, @RequestBody Map<String, String> param) {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        if (account == null) {
            return getFailureResult(1002, "账号不存在");
        }

        String orderId = param.get("orderId");
        String remark = param.get("remark");
        String status  = param.get("status");

        if(StringUtils.isEmpty(orderId)) {
            return getSuccessResult(false);
        }

        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(Integer.parseInt(orderId));
        if(mtVehicleOrder == null) {
            return getSuccessResult(false);
        }

        mtVehicleOrder.setRemark(remark);
        mtVehicleOrder.setStatus(status);
        vehicleOrderService.updateVehicleOrder(mtVehicleOrder);

        return getSuccessResult(true);
    }
    @ApiOperation(value = "更新车辆服务单状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, String> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer vehicleId = param.get("vehicleId") == null ? 0 : Integer.parseInt(param.get("vehicleId"));
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(vehicleId);
        if (mtVehicleOrder == null) {
            return getFailureResult(201, "会员车辆不存在");
        }
        mtVehicleOrder.setOperator(accountInfo.getAccountName());

        mtVehicleOrder.setStatus(status);
        vehicleOrderService.updateVehicleOrder(mtVehicleOrder);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "删除车辆服务单", notes = "删除车辆服务单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        vehicleOrderService.deleteVehicleOrder(id, operator);
        return getSuccessResult(true);
    }
}
