package com.fuint.module.backendApi.controller.merchant;

import com.fuint.common.dto.common.ParamDto;
import com.fuint.common.dto.merchant.StaffDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StaffCategoryEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.StaffPage;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.PhoneFormatCheckUtils;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStaff;
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
 * 店铺员工管理
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-店铺员工相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/staff")
public class BackendStaffController extends BaseController {

    /**
     * 员工接口
     */
    private StaffService staffService;

    /**
     * 设置接口
     */
    private SettingService settingService;

    /**
     * 获取员工列表
     */
    @ApiOperation(value = "获取员工列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject list(@ModelAttribute StaffPage staffPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            staffPage.setStoreId(accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            staffPage.setMerchantId(accountInfo.getMerchantId());
        }
        PaginationResponse<StaffDto> paginationResponse = staffService.queryStaffListByPagination(staffPage);

        // 员工类别列表
        List<ParamDto> categoryList = StaffCategoryEnum.getStaffCategoryList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("categoryList", categoryList);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 更新员工状态
     */
    @ApiOperation(value = "更新员工状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        staffService.updateAuditedStatus(id, status, accountInfo.getAccountName());
        return getSuccessResult(true);
    }

    /**
     * 保存员工信息
     */
    @ApiOperation(value = "保存员工信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject saveHandler(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String id = params.get("id") == null ? "0" : params.get("id").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();
        String category = params.get("category") == null ? "0" : params.get("category").toString();
        String mobile = params.get("mobile") == null ? "" : CommonUtil.replaceXSS(params.get("mobile").toString());
        String realName = params.get("realName") == null ? "" : CommonUtil.replaceXSS(params.get("realName").toString());
        String avatar = params.get("avatar") == null ? "" : CommonUtil.replaceXSS(params.get("avatar").toString());
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        String status = params.get("auditedStatus") == null ? StatusEnum.FORBIDDEN.getKey() : CommonUtil.replaceXSS(params.get("auditedStatus").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }

        MtStaff mtStaff = new MtStaff();
        if (StringUtil.isNotEmpty(id)) {
            mtStaff = staffService.queryStaffById(Integer.parseInt(id));
        }

        if (mtStaff == null && StringUtil.isNotEmpty(id)) {
            return getFailureResult(201, "员工信息不存在");
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            mtStaff.setMerchantId(accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(storeId)) {
            mtStaff.setStoreId(Integer.parseInt(storeId));
        }
        mtStaff.setRealName(realName);
        mtStaff.setAvatar(avatar);
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
            mtStaff.setMobile(mobile);
        }
        mtStaff.setAuditedStatus(status);
        mtStaff.setDescription(description);
        mtStaff.setCategory(Integer.parseInt(category));

        if (StringUtil.isEmpty(mtStaff.getMobile())) {
            return getFailureResult(201, "手机号码不能为空");
        } else {
            MtStaff tempUser = staffService.queryStaffByMobile(mtStaff.getMobile());
            if (tempUser != null && !tempUser.getId().equals(mtStaff.getId())) {
                return getFailureResult(201, "该手机号码已经存在");
            }
        }
        staffService.saveStaff(mtStaff, accountInfo.getAccountName());
        return getSuccessResult(true);
    }

    /**
     * 查询员工详情
     */
    @ApiOperation(value = "查询员工详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject getStaffInfo(@PathVariable("id") Integer id) {
        MtStaff staffInfo = staffService.queryStaffById(id);
        if (staffInfo != null) {
            staffInfo.setMobile(CommonUtil.hidePhone(staffInfo.getMobile()));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("staffInfo", staffInfo);

        return getSuccessResult(result);
    }

    /**
     * 店铺员工列表
     */
    @ApiOperation(value = "店铺员工列表")
    @RequestMapping(value = "/storeStaffList/{storeId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject storeStaffList(@PathVariable("storeId") Integer storeId) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("MERCHANT_ID", accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }
        params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
        if (storeId != null && storeId > 0) {
            params.put("STORE_ID", storeId);
        }
        List<MtStaff> staffList = staffService.queryStaffByParams(params);

        Map<String, Object> result = new HashMap<>();
        result.put("staffList", staffList);

        return getSuccessResult(result);
    }

    /**
     * 删除员工
     */
    @ApiOperation(value = "删除员工")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject deleteStaff(@PathVariable("id") Integer id) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        staffService.updateAuditedStatus(id, StatusEnum.DISABLE.getKey(), accountInfo.getAccountName());
        return getSuccessResult(true);
    }
}
