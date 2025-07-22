package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.dto.VehicleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.BannerService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.VehicleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBanner;
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
 * 页面接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-页面相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/page")
public class ClientPageController extends BaseController {

    /**
     * Banner服务接口
     * */
    private BannerService bannerService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 车辆服务接口
     * */
    private VehicleService vehicleService;

    /**
     * 获取页面数据
     */
    @ApiOperation(value = "获取首页页面数据")
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String token = request.getHeader("Access-Token");

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (storeId > 0) {
            params.put("storeId", storeId);
        }
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId > 0) {
            params.put("merchantId", merchantId);
        }

        List<MtBanner> bannerData = bannerService.queryBannerListByParams(params);
        VehicleDto vehicle = null;
        if (mtUser != null) {
            List<VehicleDto> vehicles = vehicleService.getVehicleByUserId(mtUser.getId(), true);
            if (vehicles != null && vehicles.size() > 0) {
                vehicle = vehicles.get(0);
            }
        }

        Map<String, Object> outParams = new HashMap();
        outParams.put("banner", bannerData);
        outParams.put("vehicle", vehicle);
        return getSuccessResult(outParams);
    }
}
