package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.OpenGiftDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.OpenGiftPage;
import com.fuint.common.service.OpenGiftService;
import com.fuint.common.service.UserGradeService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOpenGift;
import com.fuint.repository.model.MtUserGrade;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开卡赠礼管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-开卡赠礼相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/openGift")
public class BackendOpenGiftController extends BaseController {

    /**
     * 开卡赠礼服务接口
     */
    private OpenGiftService openGiftService;

    /**
     * 会员等级服务接口
     **/
    private UserGradeService userGradeService;

    /**
     * 开卡赠礼列表查询
     */
    @ApiOperation(value = "开卡赠礼列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('openGift:index')")
    public ResponseObject list(@ModelAttribute OpenGiftPage openGiftPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            openGiftPage.setMerchantId(accountInfo.getMerchantId());
        }

        ResponseObject response = openGiftService.getOpenGiftList(openGiftPage);
        List<MtUserGrade> userGradeList = userGradeService.getMerchantGradeList(accountInfo.getMerchantId(), null);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", response.getData());
        result.put("userGradeList", userGradeList);

        return getSuccessResult(result);
    }

    /**
     * 开卡赠礼详情
     * */
    @ApiOperation(value = "开卡赠礼详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('openGift:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        List<MtUserGrade> userGradeMap = userGradeService.getMerchantGradeList(accountInfo.getMerchantId(), null);
        OpenGiftDto openGiftInfo = openGiftService.getOpenGiftDetail(id);

        Map<String, Object> result = new HashMap<>();
        result.put("openGiftInfo", openGiftInfo);
        result.put("userGradeMap", userGradeMap);

        return getSuccessResult(result);
    }

    /**
     * 提交开卡赠礼
     */
    @ApiOperation(value = "提交开卡赠礼")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('openGift:add')")
    public ResponseObject handleSave(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        String id = param.get("id").toString();
        String gradeId = param.get("gradeId").toString();
        String couponId = param.get("couponId").toString();
        String couponNum = param.get("couponNum").toString();
        String point = param.get("point").toString();
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        if (StringUtil.isEmpty(couponId) && StringUtil.isEmpty(couponNum) && StringUtil.isEmpty(point)) {
            return getFailureResult(201, "积分和卡券必须填写一项");
        }

        if (StringUtil.isEmpty(gradeId)) {
            return getFailureResult(201, "会员等级不能为空");
        }

        MtOpenGift reqDto = new MtOpenGift();
        if (StringUtil.isNotEmpty(couponId)) {
            reqDto.setCouponId(Integer.parseInt(couponId));
        } else {
            reqDto.setCouponId(0);
        }
        if (StringUtil.isNotEmpty(couponNum)) {
            reqDto.setCouponNum(Integer.parseInt(couponNum));
        } else {
            reqDto.setCouponNum(0);
        }
        if (StringUtil.isNotEmpty(gradeId)) {
            reqDto.setGradeId(Integer.parseInt(gradeId));
        } else {
            reqDto.setGradeId(0);
        }
        if (StringUtil.isNotEmpty(point)) {
            reqDto.setPoint(Integer.parseInt(point));
        } else {
            reqDto.setPoint(0);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            reqDto.setMerchantId(accountInfo.getMerchantId());
        }
        reqDto.setStoreId(0);
        reqDto.setStatus(status);
        reqDto.setOperator(accountInfo.getAccountName());

        if (StringUtil.isNotEmpty(id)) {
            reqDto.setId(Integer.parseInt(id));
            openGiftService.updateOpenGift(reqDto);
        } else {
            openGiftService.addOpenGift(reqDto);
        }

        return getSuccessResult(true);
    }

    /**
     * 更新开卡赠礼
     */
    @ApiOperation(value = "更新开卡赠礼")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('openGift:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer id = param.get("id") == null ? 0 : Integer.parseInt(param.get("id").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        OpenGiftDto info = openGiftService.getOpenGiftDetail(id);
        if (info == null) {
            return getFailureResult(201, "开卡赠礼不存在");
        }

        MtOpenGift mtOpenGift = new MtOpenGift();
        mtOpenGift.setId(id);
        mtOpenGift.setStatus(status);
        mtOpenGift.setOperator(accountInfo.getAccountName());
        openGiftService.updateOpenGift(mtOpenGift);
        return getSuccessResult(true);
    }

    /**
     * 删除开卡赠礼
     */
    @ApiOperation(value = "删除开卡赠礼")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('openGift:index')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        String operator = accountInfo.getAccountName();
        openGiftService.deleteOpenGift(id, operator);

        return getSuccessResult(true);
    }
}
