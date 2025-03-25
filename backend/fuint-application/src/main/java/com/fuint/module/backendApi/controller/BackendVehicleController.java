package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.VehicleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.UserVehicleParam;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.VehicleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtVehicle;
import com.fuint.repository.model.TAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags="后台管理-车辆相关接口")
@RestController
@RequestMapping(value = "/backendApi/vehicle")
public class BackendVehicleController extends BaseController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private VehicleService vehicleService;

    @ApiOperation(value="查询车辆列表", notes="查询车辆列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        if (account == null) {
            return getFailureResult(1002, "账号不存在");
        }

        PaginationResponse<VehicleDto> paginationResponse = vehicleService.getUserVehicleListByPagination(request);

        return getSuccessResult(paginationResponse);
    }

    @ApiOperation(value="查询车辆信息", notes="查询车辆信息")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        if (account == null) {
            return getFailureResult(1002, "账号不存在");
        }

        VehicleDto vehicleDto = vehicleService.getVehicleById(id);

        return getSuccessResult(vehicleDto);
    }

    @ApiOperation(value="更新车辆信息", notes="更新会员车辆信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(HttpServletRequest request, @RequestBody UserVehicleParam param) {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        if (account == null) {
            return getFailureResult(1002, "账号不存在");
        }

        String vehiclePlateNo = param.getVehiclePlateNo();
        String vehicleType  = param.getVehicleType();
        String vehicleColor = param.getVehicleColor();
        String vehicleBrand = param.getVehicleBrand();
        String vehicleModel = param.getVehicleModel();

        MtVehicle mtVehicle = vehicleService.queryVehicleById(param.getId());
        if(mtVehicle == null) {
            return getSuccessResult(false);
        }

        mtVehicle.setVehiclePlateNo(vehiclePlateNo);
        mtVehicle.setVehicleType(vehicleType);
        mtVehicle.setVehicleColor(vehicleColor);
        mtVehicle.setVehicleBrand(vehicleBrand);
        mtVehicle.setVehicleModel(vehicleModel);

        vehicleService.updateVehicle(mtVehicle);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "更新车辆状态", notes="更新车辆状态信息")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, String> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer vehicleId = param.get("vehicleId") == null ? 0 : Integer.parseInt(param.get("vehicleId"));
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        MtVehicle mtVehicle = vehicleService.queryVehicleById(vehicleId);
        if (mtVehicle == null) {
            return getFailureResult(201, "会员车辆不存在");
        }
        mtVehicle.setOperator(accountInfo.getAccountName());

        mtVehicle.setStatus(status);
        vehicleService.updateVehicle(mtVehicle);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "删除车辆", notes = "根据ID删除会员车辆")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        String operator = accountInfo.getAccountName();
        vehicleService.deleteVehicle(id, operator);

        return getSuccessResult(true);
    }
}
