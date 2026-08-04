package com.fuint.module.backendApi.controller.order;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.dto.common.ParamDto;
import com.fuint.common.dto.order.VehicleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.VehicleTypeEnum;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // 车辆类型列表
        List<ParamDto> vehicleTypeList = VehicleTypeEnum.getVehicleTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleTypeList", vehicleTypeList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
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
        Integer userId = null;
        // 根据会员号或会员手机号查询/新增会员信息
        if (StringUtil.isNotEmpty(param.getUserNo()) || StringUtil.isNotEmpty(param.getMobile())) {
            MtUser mtUser = null;
            // 优先根据会员号查询
            if (StringUtil.isNotEmpty(param.getUserNo())) {
                mtUser = memberService.queryMemberByUserNo(accountInfo.getMerchantId(), param.getUserNo());
            }
            // 会员号查不到则根据手机号查询
            if (mtUser == null && StringUtil.isNotEmpty(param.getMobile())) {
                mtUser = memberService.queryMemberByMobile(accountInfo.getMerchantId(), param.getMobile());
            }
            if (mtUser == null) {
                // 没查到会员信息，新增一个会员
                mtUser = new MtUser();
                mtUser.setName(param.getName());
                mtUser.setMobile(param.getMobile());
                mtUser.setUserNo(param.getUserNo());
                mtUser.setMerchantId(accountInfo.getMerchantId());
                mtUser.setStoreId(accountInfo.getStoreId());
                mtUser.setStatus(StatusEnum.ENABLED.getKey());
                mtUser = memberService.addMember(mtUser, null);
            } else {
                // 查到已有会员，更新会员信息
                if (StringUtil.isNotEmpty(param.getName())) {
                    mtUser.setName(param.getName());
                }
                if (StringUtil.isNotEmpty(param.getMobile())) {
                    mtUser.setMobile(param.getMobile());
                }
                if (StringUtil.isNotEmpty(param.getUserNo())) {
                    mtUser.setUserNo(param.getUserNo());
                }
                memberService.updateMember(mtUser, false);
            }
            userId = mtUser.getId();
        } else {
            // 未传入会员号或手机号，保持原有关联会员
            userId = mtVehicle.getUserId();
            if (userId != null) {
                MtUser mtUser = memberService.queryMemberById(userId);
                if (mtUser != null) {
                    if (StringUtil.isNotEmpty(param.getName())) {
                        mtUser.setName(param.getName());
                    }
                    if (StringUtil.isNotEmpty(param.getMobile())) {
                        mtUser.setMobile(param.getMobile());
                    }
                    if (StringUtil.isNotEmpty(param.getUserNo())) {
                        mtUser.setUserNo(param.getUserNo());
                    }
                    memberService.updateMember(mtUser, false);
                }
            }
        }

        mtVehicle.setVehiclePlateNo(vehiclePlateNo);
        mtVehicle.setVehicleType(vehicleType);
        mtVehicle.setVehicleColor(vehicleColor);
        mtVehicle.setVehicleBrand(vehicleBrand);
        mtVehicle.setVehicleModel(vehicleModel);
        mtVehicle.setMerchantId(accountInfo.getMerchantId());
        mtVehicle.setUserId(userId);
        mtVehicle.setVin(vin);

        vehicleService.saveVehicle(mtVehicle);
        return getSuccessResult(true);
    }

    @ApiOperation(value = "更新车辆状态", notes="更新车辆状态信息")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody Map<String, String> param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer vehicleId = param.get("vehicleId") == null ? 0 : Integer.parseInt(param.get("vehicleId"));
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status");

        MtVehicle mtVehicle = vehicleService.queryVehicleById(vehicleId);
        if (mtVehicle == null) {
            return getFailureResult(201, "会员车辆不存在");
        }
        mtVehicle.setOperator(accountInfo.getAccountName());

        mtVehicle.setStatus(status);
        vehicleService.updateVehicle(mtVehicle, accountInfo);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "更新车辆行驶公里数", notes="更新车辆行驶公里数并记录时间")
    @RequestMapping(value = "/updateMileage", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateMileage(@RequestBody Map<String, String> param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer vehicleId = param.get("vehicleId") == null ? 0 : Integer.parseInt(param.get("vehicleId"));
        Integer mileage = param.get("mileage") == null ? null : Integer.parseInt(param.get("mileage"));

        if (vehicleId <= 0) {
            return getFailureResult(201, "车辆ID不能为空");
        }
        if (mileage == null || mileage < 0) {
            return getFailureResult(202, "行驶公里数不能为空或负数");
        }

        vehicleService.updateVehicleMileage(vehicleId, mileage, accountInfo);
        return getSuccessResult(true);
    }

    @ApiOperation(value = "搜索车辆（服务开单选择车辆）", notes = "按车牌号或会员手机号搜索车辆及会员信息")
    @RequestMapping(value = "/searchVehicles", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject searchVehicles(@RequestParam String keyword) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (StringUtil.isEmpty(keyword)) {
            return getFailureResult(201, "关键字不能为空");
        }
        List<MtVehicle> vehicleList = vehicleService.searchVehiclesByKeyword(accountInfo.getMerchantId(), keyword);
        // 附加会员信息
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (vehicleList != null) {
            for (MtVehicle v : vehicleList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", v.getId());
                map.put("vehiclePlateNo", v.getVehiclePlateNo());
                map.put("vehicleType", v.getVehicleType());
                map.put("vehicleBrand", v.getVehicleBrand());
                map.put("vehicleModel", v.getVehicleModel());
                map.put("vehicleColor", v.getVehicleColor());
                map.put("vehicleMileage", v.getVehicleMileage());
                map.put("mileageRecordTime", v.getMileageRecordTime());
                map.put("vin", v.getVin());
                map.put("userId", v.getUserId());
                map.put("merchantId", v.getMerchantId());
                if (v.getUserId() != null) {
                    MtUser mtUser = memberService.queryMemberById(v.getUserId());
                    if (mtUser != null) {
                        map.put("name", mtUser.getName());
                        map.put("mobile", mtUser.getMobile());
                        map.put("userNo", mtUser.getUserNo());
                    }
                }
                resultList.add(map);
            }
        }
        return getSuccessResult(resultList);
    }

    @ApiOperation(value = "删除车辆", notes = "根据ID删除会员车辆")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        vehicleService.deleteVehicle(id, accountInfo);
        return getSuccessResult(true);
    }
}
