package com.fuint.module.clientApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fuint.common.dto.content.NavigationDto;
import com.fuint.common.dto.decorate.PageDecorationDto;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.order.VehicleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.BannerService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.PageDecorateService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.VehicleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBanner;
import com.fuint.utils.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-页面相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/page")
public class ClientPageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientPageController.class);

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
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 页面装修服务接口
     */
    private PageDecorateService pageDecorateService;

    /**
     * 获取页面数据
     */
    @Operation(summary="获取首页页面数据")
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws JsonProcessingException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));

        UserInfo mtUser = TokenUtil.getUserInfo();

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (storeId > 0) {
            params.put("storeId", storeId);
        }
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId > 0) {
            params.put("merchantId", merchantId);
        }

        // 优先返回页面装修数据，无装修时返回默认组件数据（兼容旧版本）
        PageDecorationDto page = pageDecorateService.getDefaultPage(merchantId, storeId, "index");

        Map<String, Object> outParams = new HashMap();
        // 图片上传根路径，客户端渲染装修组件图片时需用它补全相对路径
        outParams.put("imagePath", settingService.getUploadBasePath());
        logger.info("client home: merchantId={}, storeId={}, pageType={}, pageHit={}, components={}",
                merchantId, storeId, "index",
                page != null,
                page != null && page.getComponents() != null ? page.getComponents().size() : 0);
        if (page != null && page.getComponents() != null && page.getComponents().size() > 0) {
            outParams.put("page", page);
        } else {
            List<MtBanner> bannerList = bannerService.queryBannerListByParams(params);
            VehicleDto vehicle = null;
            if (mtUser != null) {
                List<VehicleDto> vehicles = vehicleService.getVehicleByUserId(mtUser.getId(), true);
                if (vehicles != null && vehicles.size() > 0) {
                    vehicle = vehicles.get(0);
                }
            }
            List<NavigationDto> navigation = settingService.getNavigation(merchantId, storeId, StatusEnum.ENABLED.getKey());
            outParams.put("banner", bannerList);
            outParams.put("vehicle", vehicle);
            outParams.put("navigation", navigation);
        }
        return getSuccessResult(outParams);
    }
}
