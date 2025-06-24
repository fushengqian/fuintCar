package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.VehicleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.VehicleService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtVehicleMapper;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtVehicle;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class VehicleServiceImpl extends ServiceImpl<MtVehicleMapper, MtVehicle> implements VehicleService {

    private MtVehicleMapper mtVehicleMapper;

    private MemberService memberService;

    @Override
    @OperationServiceLog(description = "查询用户车辆列表")
    public PaginationResponse<VehicleDto> getUserVehicleListByPagination(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        String userNo = request.getParameter("userNo");
        String mobile = request.getParameter("mobile");
        String plate = request.getParameter("plate");
        String status = request.getParameter("status");
        String type = request.getParameter("vtype");
        Integer merchantId = accountInfo.getMerchantId();

        Integer page =  request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        String userId = null;
        if (StringUtils.isNotEmpty(userNo)) {
            MtUser userInfo = memberService.queryMemberByUserNo(0, userNo);
            if (userInfo != null) {
                userId = userInfo.getId() + "";
            } else {
                userId = "0";
            }
        } else if (StringUtils.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(0, mobile);
            if (userInfo != null) {
                userId = userInfo.getId() + "";
            } else {
                userId = "0";
            }
        }

        Page<MtVehicle> pageHelper = PageHelper.startPage(page, pageSize);
        LambdaQueryWrapper<MtVehicle> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if (StringUtils.isEmpty(status)) {
            lambdaQueryWrapper.eq(MtVehicle::getStatus, StatusEnum.ENABLED.getKey());
        } else {
            lambdaQueryWrapper.eq(MtVehicle::getStatus, status);
        }
        if (StringUtils.isNotEmpty(type)) {
            lambdaQueryWrapper.eq(MtVehicle::getVehicleType, type);
        }
        if (StringUtils.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(MtVehicle::getUserId, userId);
        }
        if (StringUtils.isNotEmpty(plate)) {
            lambdaQueryWrapper.eq(MtVehicle::getVehiclePlateNo, plate);
        }
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtVehicle::getMerchantId, merchantId);
        }
        lambdaQueryWrapper.orderByDesc(MtVehicle::getId);
        List<MtVehicle> dataList = mtVehicleMapper.selectList(lambdaQueryWrapper);
        List<VehicleDto> vehicleDtoList = new ArrayList<>();
        for (MtVehicle mtVehicle : dataList) {
             VehicleDto vehicleDto = new VehicleDto();
             BeanUtils.copyProperties(mtVehicle, vehicleDto);
             MtUser mtUser = memberService.queryMemberById(mtVehicle.getUserId());
             if (mtUser != null) {
                vehicleDto.setUserNo(mtUser.getUserNo());
                vehicleDto.setMobile(mtUser.getMobile());
                vehicleDto.setName(mtUser.getName());
                if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    continue;
                }
             }
             vehicleDtoList.add(vehicleDto);
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        PageImpl<VehicleDto> pageImpl = new PageImpl(vehicleDtoList, pageRequest, pageHelper.getTotal());
        PaginationResponse<VehicleDto> paginationResponse = new PaginationResponse(pageImpl, MtVehicle.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());

        paginationResponse.setContent(vehicleDtoList);
        return paginationResponse;
    }

    @Override
    public VehicleDto getVehicleById(Integer id) throws BusinessCheckException {
        MtVehicle mtVehicle = mtVehicleMapper.selectById(id);
        VehicleDto vehicleDto = new VehicleDto();
        BeanUtils.copyProperties(mtVehicle, vehicleDto);

        MtUser mtUser = memberService.queryMemberById(mtVehicle.getUserId());
        if (mtUser != null) {
            vehicleDto.setUserNo(mtUser.getUserNo());
            vehicleDto.setMobile(mtUser.getMobile());
            vehicleDto.setName(mtUser.getName());
        }

        return vehicleDto;
    }

    @Override
    @OperationServiceLog(description = "更新车辆信息")
    @Transactional(rollbackFor = Exception.class)
    public MtVehicle updateVehicle(MtVehicle mtVehicle) {
        mtVehicle.setUpdateTime(new Date());

        Boolean result = updateById(mtVehicle);
        log.info("更新车辆信息结果：{}", result);

        return mtVehicle;
    }

    @Override
    @OperationServiceLog(description = "增加车辆")
    @Transactional(rollbackFor = Exception.class)
    public MtVehicle saveVehicle(MtVehicle mtVehicle) throws BusinessCheckException {
        // 只能有一个默认车牌
        if (mtVehicle.getIsDefault() != null && mtVehicle.getIsDefault().equals(YesOrNoEnum.YES.getKey())) {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", mtVehicle.getUserId());
            params.put("isDefault", YesOrNoEnum.YES.getKey());
            List<MtVehicle> dataList = queryVehicleList(params);
            if (dataList != null && dataList.size() > 0) {
                for (MtVehicle item : dataList) {
                     if (!mtVehicle.getId().equals(item.getId())) {
                         item.setIsDefault(YesOrNoEnum.NO.getKey());
                         mtVehicleMapper.updateById(item);
                     }
                }
            }
        }

        if (mtVehicle.getId() != null && mtVehicle.getId() > 0) {
            MtVehicle vehicle = mtVehicleMapper.selectById(mtVehicle.getId());
            if(StringUtils.isNotEmpty(mtVehicle.getVehiclePlateNo())) {
                vehicle.setVehiclePlateNo(mtVehicle.getVehiclePlateNo());
            }
            if(StringUtils.isNotEmpty(mtVehicle.getVehicleColor())) {
                vehicle.setVehicleColor(mtVehicle.getVehicleColor());
            }
            if (StringUtils.isNotEmpty(mtVehicle.getVehicleBrand())) {
                vehicle.setVehicleBrand(mtVehicle.getVehicleBrand());
            }
            if (StringUtils.isNotEmpty(mtVehicle.getVehicleModel())) {
                vehicle.setVehicleModel(mtVehicle.getVehicleModel());
            }
            if (StringUtils.isNotEmpty(mtVehicle.getVehicleType())) {
                vehicle.setVehicleType(mtVehicle.getVehicleType());
            }
            if (StringUtils.isNotEmpty(mtVehicle.getIsDefault())) {
                vehicle.setIsDefault(mtVehicle.getIsDefault());
            }
            if (StringUtils.isNotEmpty(mtVehicle.getStatus())) {
                vehicle.setStatus(mtVehicle.getStatus());
            }
            vehicle.setUpdateTime(new Date());
            mtVehicleMapper.updateById(vehicle);
        } else {
            List<VehicleDto> myVehicles = getVehicleByUserId(mtVehicle.getUserId(), false);
            if (myVehicles == null || myVehicles.size() < 1) {
                mtVehicle.setIsDefault(YesOrNoEnum.YES.getKey());
            }

            Map<String, Object> params = new HashMap<>();
            params.put("userId", mtVehicle.getUserId());
            params.put("vehiclePlateNo", mtVehicle.getVehiclePlateNo());
            params.put("status", StatusEnum.ENABLED.getKey());
            List<MtVehicle> dataList = queryVehicleList(params);
            if (dataList != null && dataList.size() > 0) {
                throw new BusinessCheckException("该车牌号已存在");
            }

            mtVehicle.setCreateTime(new Date());
            mtVehicle.setUpdateTime(new Date());
            this.save(mtVehicle);
        }
        return mtVehicle;
    }

    @Override
    public MtVehicle queryVehicleById(Integer id) {
        return mtVehicleMapper.selectById(id);
    }

    @Override
    public void deleteVehicle(Integer id, String operator) throws BusinessCheckException {
        MtVehicle mtVehicle = mtVehicleMapper.selectById(id);
        if(mtVehicle == null) {
            throw new BusinessCheckException("车辆不存在");
        }
        mtVehicle.setStatus(StatusEnum.DISABLE.getKey());
        mtVehicle.setUpdateTime(new Date());
        updateById(mtVehicle);
    }

    @Override
    public List<VehicleDto> getVehicleByUserId(Integer userId, boolean isDefault) {
        List<MtVehicle> vehicleList = mtVehicleMapper.getVehicleByUserId(userId, isDefault);
        List<VehicleDto> vehicleDtos = new ArrayList<>();
        if (vehicleList != null && vehicleList.size() > 0) {
            for (MtVehicle mtVehicle : vehicleList) {
                 VehicleDto vehicleDto = new VehicleDto();
                 BeanUtils.copyProperties(mtVehicle, vehicleDto);
                 vehicleDtos.add(vehicleDto);
            }
        }
        return vehicleDtos;
    }

    @Override
    public List<MtVehicle> queryVehicleList(Map<String, Object> paramMap) {
        String userId = paramMap.get("userId") == null ? "" : paramMap.get("userId").toString();
        String isDefault = paramMap.get("isDefault") == null ? "" : paramMap.get("isDefault").toString();
        String vehiclePlateNo = paramMap.get("vehiclePlateNo") == null ? "" : paramMap.get("vehiclePlateNo").toString();
        LambdaQueryWrapper<MtVehicle> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtVehicle::getStatus, StatusEnum.DISABLE.getKey());
        if (StringUtil.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(MtVehicle::getUserId, userId);
        }
        if (StringUtil.isNotEmpty(isDefault)) {
            lambdaQueryWrapper.eq(MtVehicle::getIsDefault, isDefault);
        }
        if (StringUtil.isNotEmpty(vehiclePlateNo)) {
            lambdaQueryWrapper.eq(MtVehicle::getVehiclePlateNo, vehiclePlateNo);
        }
        List<MtVehicle> vehicleList = mtVehicleMapper.selectList(lambdaQueryWrapper);
        return vehicleList;
    }
}
