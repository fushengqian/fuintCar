package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.order.OrderDto;
import com.fuint.common.dto.order.VehicleDto;
import com.fuint.common.dto.order.VehicleOrderDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.CreateServiceOrderParam;
import com.fuint.common.param.VehicleOrderPage;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtVehicleOrderMapper;
import com.fuint.repository.model.*;
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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class VehicleOrderServiceImpl extends ServiceImpl<MtVehicleOrderMapper, MtVehicleOrder> implements VehicleOrderService {

    @Resource
    private MtVehicleOrderMapper mtVehicleOrderMapper;

    private UserCouponService userCouponService;

    private CouponService couponService;

    private MemberService memberService;

    private StoreService storeService;

    private OrderService orderService;

    private GoodsService goodsService;

    private VehicleService vehicleService;

    @Override
    @OperationServiceLog(description = "查询车辆服务单列表")
    public PaginationResponse<VehicleOrderDto> getVehicleOrderListByPagination(VehicleOrderPage vehicleOrderPage) {
        String userNo = vehicleOrderPage.getUserNo();
        String mobile = vehicleOrderPage.getMobile();
        Integer userId = vehicleOrderPage.getUserId();
        if (StringUtils.isNotEmpty(userNo)){
            MtUser userInfo = memberService.queryMemberByUserNo(0, userNo);
            if (userInfo != null) {
                userId = userInfo.getId();
            } else {
                userId = 0;
            }
        } else if (StringUtils.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(0, mobile);
            if (userInfo != null) {
                userId = userInfo.getId();
            } else {
                userId = 0;
            }
        }
        LambdaQueryWrapper<MtVehicleOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtVehicleOrder::getStatus, StatusEnum.DISABLE.getKey());
        String orderSn = vehicleOrderPage.getOrderSn();
        if (StringUtils.isNotEmpty(orderSn)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getOrderSn, orderSn);
        }
        String storeIds = vehicleOrderPage.getStoreIds();
        if (StringUtils.isNotEmpty(storeIds)) {
            List<String> idList = Arrays.asList(storeIds.split(","));
            lambdaQueryWrapper.in(MtVehicleOrder::getStoreId, idList);
        }
        String status = vehicleOrderPage.getStatus();
        if (StringUtils.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getStatus, status);
        }
        if (userId != null) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getUserId, userId);
        }
        Integer merchantId = vehicleOrderPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getMerchantId, merchantId);
        }
        Integer storeId = vehicleOrderPage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getStoreId, storeId);
        }
        String vehiclePlateNo = vehicleOrderPage.getVehiclePlateNo();
        if (StringUtils.isNotEmpty(vehiclePlateNo)){
            lambdaQueryWrapper.eq(MtVehicleOrder::getVehiclePlateNo, vehiclePlateNo);
        }
        String startTime = vehicleOrderPage.getStartTime();
        if (StringUtil.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(MtVehicleOrder::getCreateTime, startTime);
        }
        String endTime = vehicleOrderPage.getEndTime();
        if (StringUtil.isNotEmpty(endTime)) {
            lambdaQueryWrapper.le(MtVehicleOrder::getCreateTime, endTime);
        }
        Page<MtVehicleOrder> pageHelper = PageHelper.startPage(vehicleOrderPage.getPage(), vehicleOrderPage.getPageSize());
        lambdaQueryWrapper.orderByDesc(MtVehicleOrder::getId);
        List<MtVehicleOrder> dataList = mtVehicleOrderMapper.selectList(lambdaQueryWrapper);
        List<VehicleOrderDto> vehicleOrderList = new ArrayList<>();
        if (dataList != null && dataList.size() > 0) {
            for (MtVehicleOrder mtVehicleOrder : dataList) {
                 VehicleOrderDto vehicleOrderDto = new VehicleOrderDto();
                 BeanUtils.copyProperties(mtVehicleOrder, vehicleOrderDto);
                 MtUser mtUser = memberService.queryMemberById(mtVehicleOrder.getUserId());
                 if (mtUser != null) {
                     vehicleOrderDto.setName(mtUser.getName());
                     vehicleOrderDto.setMobile(mtUser.getMobile());
                     vehicleOrderDto.setUserNo(mtUser.getUserNo());
                 }
                 MtStore mtStore = storeService.queryStoreById(mtVehicleOrder.getStoreId());
                 if (mtStore != null) {
                     vehicleOrderDto.setStoreInfo(mtStore);
                 }
                 vehicleOrderList.add(vehicleOrderDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(vehicleOrderPage.getPage(), vehicleOrderPage.getPageSize());
        PageImpl<VehicleDto> pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<VehicleOrderDto> paginationResponse = new PaginationResponse(pageImpl, VehicleOrderDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(vehicleOrderList);

        return paginationResponse;
    }

    @Override
    public MtVehicleOrder getVehicleOrderById(Integer id) {
        return mtVehicleOrderMapper.selectById(id);
    }

    @Override
    @OperationServiceLog(description = "更新车辆服务单")
    @Transactional(rollbackFor = Exception.class)
    public MtVehicleOrder updateVehicleOrder(MtVehicleOrder mtVehicleOrder) {
        mtVehicleOrder.setUpdateTime(new Date());

        Boolean result = updateById(mtVehicleOrder);
        log.info("更新车辆服务单：{}", result);

        return mtVehicleOrder;
    }

    @Override
    @OperationServiceLog(description = "提交车辆服务单")
    @Transactional(rollbackFor = Exception.class)
    public MtVehicleOrder submitVehicleOrder(MtVehicleOrder mtVehicleOrder) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", mtVehicleOrder.getUserId());
        params.put("vehiclePlateNo", mtVehicleOrder.getVehiclePlateNo());

        MtUserCoupon mtUserCoupon = userCouponService.getUserCouponDetail(mtVehicleOrder.getCouponId());
        if (mtUserCoupon == null || !mtUserCoupon.getUserId().equals(mtVehicleOrder.getUserId())) {
            throw new BusinessCheckException("该次卡不存在");
        }

        params.put("couponId", mtVehicleOrder.getCouponId());
        params.put("status", StatusEnum.ENABLED.getKey());
        List<MtVehicleOrder> dataList = queryVehicleOrderList(params);
        if (dataList != null && dataList.size() > 0) {
            throw new BusinessCheckException("服务单已存在");
        }

        couponService.useCoupon(mtVehicleOrder.getCouponId(), mtVehicleOrder.getUserId(), mtVehicleOrder.getStoreId(), 0, new BigDecimal("0"), "车辆服务单核销");

        mtVehicleOrder.setCreateTime(new Date());
        mtVehicleOrder.setUpdateTime(new Date());
        mtVehicleOrder.setOrderSn(CommonUtil.createOrderSN(mtVehicleOrder.getUserId().toString()));
        mtVehicleOrder.setStatus(StatusEnum.ENABLED.getKey());
        this.save(mtVehicleOrder);
        return mtVehicleOrder;
    }

    @Override
    public List<MtVehicleOrder> queryVehicleOrderList(Map<String, Object> paramMap) {
        String userId = paramMap.get("userId") == null ? "" : paramMap.get("userId").toString();
        String vehiclePlateNo = paramMap.get("vehiclePlateNo") == null ? "" : paramMap.get("vehiclePlateNo").toString();
        String status = paramMap.get("status") == null ? "" : paramMap.get("status").toString();
        LambdaQueryWrapper<MtVehicleOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtVehicleOrder::getStatus, StatusEnum.DISABLE.getKey());
        if (StringUtil.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getUserId, userId);
        }
        if (StringUtil.isNotEmpty(vehiclePlateNo)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getVehiclePlateNo, vehiclePlateNo);
        }
        if (StringUtil.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getStatus, status);
        }
        return mtVehicleOrderMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    @OperationServiceLog(description = "后台服务开单")
    @Transactional(rollbackFor = Exception.class)
    public MtVehicleOrder createServiceOrder(CreateServiceOrderParam param, AccountInfo accountInfo) throws BusinessCheckException {
        if (param.getServiceItems() == null || param.getServiceItems().isEmpty()) {
            throw new BusinessCheckException("服务项目不能为空");
        }

        // 查询车辆信息
        MtVehicle mtVehicle = vehicleService.queryVehicleById(param.getVehicleId());
        if (mtVehicle == null) {
            throw new BusinessCheckException("车辆不存在");
        }
        Integer userId = param.getUserId() != null ? param.getUserId() : mtVehicle.getUserId();
        if (userId == null || userId == 0) {
            throw new BusinessCheckException("未找到关联会员信息");
        }
        MtUser mtUser = memberService.queryMemberById(userId);
        if (mtUser == null) {
            throw new BusinessCheckException("会员不存在");
        }

        // 创建服务单
        MtVehicleOrder mtVehicleOrder = new MtVehicleOrder();
        mtVehicleOrder.setUserId(userId);
        mtVehicleOrder.setVehiclePlateNo(mtVehicle.getVehiclePlateNo());
        mtVehicleOrder.setVehicleId(mtVehicle.getId());
        mtVehicleOrder.setMerchantId(accountInfo.getMerchantId());
        mtVehicleOrder.setStoreId(param.getStoreId() != null ? param.getStoreId() : accountInfo.getStoreId());
        mtVehicleOrder.setOrderSn(CommonUtil.createOrderSN(userId.toString()));
        mtVehicleOrder.setCouponId(param.getCouponId() != null ? param.getCouponId() : 0);
        mtVehicleOrder.setRemark(param.getRemark());
        mtVehicleOrder.setOperator(accountInfo.getAccountName());
        mtVehicleOrder.setStatus(StatusEnum.ENABLED.getKey());
        mtVehicleOrder.setCreateTime(new Date());
        mtVehicleOrder.setUpdateTime(new Date());
        this.save(mtVehicleOrder);

        // 为每个服务项目创建订单
        List<Integer> orderIdList = new ArrayList<>();
        for (CreateServiceOrderParam.ServiceItem item : param.getServiceItems()) {
            MtGoods mtGoods = goodsService.queryGoodsById(item.getGoodsId());
            if (mtGoods == null) {
                throw new BusinessCheckException("商品不存在，ID：" + item.getGoodsId());
            }

            OrderDto orderDto = new OrderDto();
            orderDto.setType("service");
            orderDto.setGoodsId(item.getGoodsId());
            orderDto.setBuyNum(item.getBuyNum() != null ? item.getBuyNum() : 1);
            orderDto.setAmount(item.getPrice() != null ? item.getPrice() : mtGoods.getPrice());
            orderDto.setUserId(userId);
            orderDto.setStoreId(mtVehicleOrder.getStoreId());
            orderDto.setOrderMode("oneself");
            orderDto.setParam("vehicleOrderId:" + mtVehicleOrder.getId());
            orderDto.setRemark("服务开单-" + mtVehicleOrder.getOrderSn());

            // 如果有卡券，绑定到第一笔订单
            if (param.getCouponId() != null && param.getCouponId() > 0 && orderIdList.isEmpty()) {
                orderDto.setCouponId(param.getCouponId());
            }

            MtOrder mtOrder = orderService.saveOrder(orderDto);
            orderIdList.add(mtOrder.getId());
        }

        // 更新服务单的关联订单ID
        String orderIds = StringUtils.join(orderIdList, ",");
        mtVehicleOrder.setOrderIds(orderIds);
        mtVehicleOrder.setUpdateTime(new Date());
        updateById(mtVehicleOrder);

        log.info("服务开单成功，服务单ID：{}，订单ID列表：{}，操作人：{}", mtVehicleOrder.getId(), orderIds, accountInfo.getAccountName());
        return mtVehicleOrder;
    }

    @Override
    public void deleteVehicleOrder(Integer id, String operator) throws BusinessCheckException {
        MtVehicleOrder mtVehicleOrder = mtVehicleOrderMapper.selectById(id);
        if(mtVehicleOrder == null) {
            throw new BusinessCheckException("车辆服务单不存在");
        }
        mtVehicleOrder.setStatus(StatusEnum.DISABLE.getKey());
        mtVehicleOrder.setUpdateTime(new Date());
        updateById(mtVehicleOrder);
    }
}
