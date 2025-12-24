package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.dto.VehicleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.VehicleOrderService;
import com.fuint.common.service.VehicleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.clientApi.request.VehicleOrderRequest;
import com.fuint.module.clientApi.request.VehicleRequest;
import com.fuint.repository.model.MtVehicle;
import com.fuint.repository.model.MtVehicleOrder;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员车辆controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-会员车辆相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/vehicle")
public class ClientVehicleController extends BaseController {

    /**
     * 车辆服务接口
     * */
    private VehicleService vehicleService;

    /**
     * 车辆服务单接口
     * */
    private VehicleOrderService vehicleOrderService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 保存会员车辆信息
     */
    @ApiOperation(value="保存会员车辆信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody VehicleRequest requestParam) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        UserInfo mtUser = TokenUtil.getUserInfo();

        String vehiclePlateNo = requestParam.getVehiclePlateNo() == null ? "" : requestParam.getVehiclePlateNo();
        String vehicleBrand = requestParam.getVehicleBrand() == null ? "" : requestParam.getVehicleBrand();
        String vehicleModel = requestParam.getVehicleModel() == null ? "" : requestParam.getVehicleModel();
        String vehicleType = requestParam.getVehicleType() == null ? "" : requestParam.getVehicleType();
        String status = requestParam.getStatus() == null ? StatusEnum.ENABLED.getKey() : requestParam.getStatus();
        String isDefault = requestParam.getIsDefault() == null ? "" : requestParam.getIsDefault();
        Integer vehicleId = requestParam.getVehicleId() == null ? 0 : requestParam.getVehicleId();

        MtVehicle mtVehicle = new MtVehicle();
        mtVehicle.setId(vehicleId);
        mtVehicle.setVehiclePlateNo(vehiclePlateNo);
        mtVehicle.setStatus(status);
        mtVehicle.setUserId(mtUser.getId());
        if (StringUtil.isNotEmpty(isDefault)) {
            mtVehicle.setIsDefault(isDefault);
        }
        mtVehicle.setMerchantId(merchantId);
        mtVehicle.setVehicleBrand(vehicleBrand);
        mtVehicle.setVehicleModel(vehicleModel);
        mtVehicle.setVehicleType(vehicleType);

        vehicleService.saveVehicle(mtVehicle);
        return getSuccessResult(true);
    }

    /**
     * 获取会员车辆列表
     */
    @ApiOperation(value="获取会员车辆列表", notes="获取会员车辆列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String isDefault = request.getParameter("isDefault") == null ? "" : request.getParameter("isDefault");
        UserInfo mtUser = TokenUtil.getUserInfo();
        List<VehicleDto> dataList = vehicleService.getVehicleByUserId(mtUser.getId(), StringUtil.isNotEmpty(isDefault) ? true : false);
        return getSuccessResult(dataList);
    }

    /**
     * 获取会员车辆详情
     */
    @ApiOperation(value="获取会员车辆详情", notes="根据ID获取会员车辆详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody VehicleRequest requestParam) throws BusinessCheckException {
        Integer vehicleId = requestParam.getVehicleId() == null ? 0 : requestParam.getVehicleId();
        UserInfo mtUser = TokenUtil.getUserInfo();

        MtVehicle mtVehicle = null;
        if (vehicleId > 0) {
            mtVehicle = vehicleService.queryVehicleById(vehicleId);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", mtUser.getId());
            params.put("isDefault", YesOrNoEnum.YES.getKey());
            List<MtVehicle> vehicleList = vehicleService.queryVehicleList(params);
            if (vehicleList.size() > 0) {
                mtVehicle = vehicleList.get(0);
            }
        }

        return getSuccessResult(mtVehicle);
    }

    /**
     * 提交服务单
     */
    @ApiOperation(value="提交服务单")
    @RequestMapping(value = "/submitOrder", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submitOrder(HttpServletRequest request, @RequestBody VehicleOrderRequest requestParam) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        UserInfo mtUser = TokenUtil.getUserInfo();

        Integer vehicleId = requestParam.getVehicleId() == null ? 0 : requestParam.getVehicleId();
        Integer couponId = requestParam.getCouponId() == null ? 0 : requestParam.getCouponId();
        String remark = requestParam.getRemark() == null ? "" : requestParam.getRemark();
        String scanCode = requestParam.getScanCode();

        MtVehicle mtVehicle = vehicleService.queryVehicleById(vehicleId);
        if (mtVehicle == null) {
            return getFailureResult(201, "该车辆不存在");
        }

        MtVehicleOrder mtVehicleOrder = new MtVehicleOrder();
        mtVehicleOrder.setCouponId(couponId);
        mtVehicleOrder.setMerchantId(merchantId);
        mtVehicleOrder.setStoreId(storeId);
        mtVehicleOrder.setRemark(remark);
        mtVehicleOrder.setVehiclePlateNo(mtVehicle.getVehiclePlateNo());
        mtVehicleOrder.setUserId(mtUser.getId());
        mtVehicleOrder.setScanCode(scanCode);

        vehicleOrderService.submitVehicleOrder(mtVehicleOrder);

        return getSuccessResult(true);
    }
}
