package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.VehicleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.UserVehicleParam;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.VehicleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtVehicle;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags="后台管理-车辆相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/vehicle")
public class BackendVehicleController extends BaseController {

    /**
     * 会员车辆服务接口
     * */
    private VehicleService vehicleService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    @ApiOperation(value="查询车辆列表", notes="查询车辆列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        PaginationResponse<VehicleDto> paginationResponse = vehicleService.getUserVehicleListByPagination(request);
        return getSuccessResult(paginationResponse);
    }

    @ApiOperation(value="查询车辆信息", notes="查询会员车辆信息")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        VehicleDto vehicleDto = vehicleService.getVehicleById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(vehicleDto.getMerchantId())) {
                return getFailureResult(1004);
            }
        }
        return getSuccessResult(vehicleDto);
    }

    @ApiOperation(value="保存车辆信息", notes="保存会员车辆信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandle(@RequestBody UserVehicleParam param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        String vehiclePlateNo = param.getVehiclePlateNo();
        String vehicleType  = param.getVehicleType();
        String vehicleColor = param.getVehicleColor();
        String vehicleBrand = param.getVehicleBrand();
        String vehicleModel = param.getVehicleModel();
        String vin = param.getVin();

        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }

        MtVehicle mtVehicle = vehicleService.queryVehicleById(param.getId());
        if (mtVehicle == null) {
            mtVehicle = new MtVehicle();
        }

        MtUser mtUser = new MtUser();
        if (StringUtil.isNotEmpty(param.getUserNo())) {
            mtUser = memberService.queryMemberByUserNo(accountInfo.getMerchantId(), param.getUserNo());
            if (mtUser == null) {
                mtUser = new MtUser();
                mtUser.setUserNo(param.getUserNo());
                mtUser.setName(param.getName());
                mtUser.setMobile(param.getMobile());
                mtUser.setMerchantId(accountInfo.getMerchantId());
                mtUser.setStoreId(accountInfo.getStoreId());
                mtUser.setStatus(StatusEnum.ENABLED.getKey());
                mtUser = memberService.addMember(mtUser, null);
            }
        } else if (StringUtil.isNotEmpty(param.getMobile())) {
            mtUser = memberService.queryMemberByMobile(accountInfo.getMerchantId(), param.getMobile());
            if (mtUser == null) {
                mtUser = new MtUser();
                mtUser.setName(param.getName());
                mtUser.setMobile(param.getMobile());
                mtUser.setUserNo(param.getUserNo());
                mtUser.setMerchantId(accountInfo.getMerchantId());
                mtUser.setStoreId(accountInfo.getStoreId());
                mtUser.setStatus(StatusEnum.ENABLED.getKey());
                mtUser = memberService.addMember(mtUser, null);
            }
        }

        mtVehicle.setVehiclePlateNo(vehiclePlateNo);
        mtVehicle.setVehicleType(vehicleType);
        mtVehicle.setVehicleColor(vehicleColor);
        mtVehicle.setVehicleBrand(vehicleBrand);
        mtVehicle.setVehicleModel(vehicleModel);
        mtVehicle.setMerchantId(accountInfo.getMerchantId());
        mtVehicle.setUserId(mtUser.getId());
        mtVehicle.setVin(vin);

        vehicleService.saveVehicle(mtVehicle);
        return getSuccessResult(true);
    }

    @ApiOperation(value = "更新车辆状态", notes="更新车辆状态信息")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody Map<String, String> param) throws BusinessCheckException {
        Integer vehicleId = param.get("vehicleId") == null ? 0 : Integer.parseInt(param.get("vehicleId"));
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();

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
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        vehicleService.deleteVehicle(id, accountInfo.getAccountName());
        return getSuccessResult(true);
    }
}
