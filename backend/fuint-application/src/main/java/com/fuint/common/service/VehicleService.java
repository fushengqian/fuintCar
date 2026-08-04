package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.dto.order.VehicleDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtVehicle;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface VehicleService extends IService<MtVehicle> {

    /**
     * 更新会员车辆
     *
     * @param mtVehicle 会员车辆
     * @param accountInfo 操作员
     * @return
     * */
    MtVehicle updateVehicle(MtVehicle mtVehicle, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 保存会员车辆
     *
     * @param mtVehicle 会员车辆
     * @throws BusinessCheckException
     * @return
     * */
    MtVehicle saveVehicle(MtVehicle mtVehicle) throws BusinessCheckException;

    /**
     * 分页查询会员车辆
     *
     * @param request 会员车辆
     * @return
     * */
    PaginationResponse<VehicleDto> getUserVehicleListByPagination(HttpServletRequest request);

    /**
     * 根据ID查询会员车辆
     *
     * @param id 会员车辆ID
     * @return
     * */
    VehicleDto getVehicleById(Integer id);

    /**
     * 根据ID查询会员车辆
     *
     * @param id 会员车辆ID
     * @return
     * */
    MtVehicle queryVehicleById(Integer id);

    /**
     * 删除会员车辆
     *
     * @param id 会员车辆ID
     * @param  accountInfo 操作员
     * @throws BusinessCheckException
     * @return
     * */
    void deleteVehicle(Integer id, AccountInfo  accountInfo) throws BusinessCheckException;

    /**
     * 根据用户ID查询会员车辆
     *
     * @param userId 会员ID
     * @param isDefault 是否默认车辆
     * @return
     * */
    List<VehicleDto> getVehicleByUserId(Integer userId, boolean isDefault);

    /**
     * 查询会员车辆
     *
     * @param paramMap 查询条件
     * @return
     * */
    List<MtVehicle> queryVehicleList(Map<String, Object> paramMap);

    /**
     * 更新车辆行驶公里数
     *
     * @param vehicleId 车辆ID
     * @param mileage 行驶公里数
     * @param accountInfo 操作员
     * @throws BusinessCheckException
     * */
    void updateVehicleMileage(Integer vehicleId, Integer mileage, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据关键字搜索车辆及会员信息（用于服务开单选择车辆）
     * 支持按车牌号、会员手机号搜索
     *
     * @param merchantId 商户ID
     * @param keyword 关键字（车牌号或会员手机号）
     * @return 车辆列表（含会员信息）
     * */
    List<MtVehicle> searchVehiclesByKeyword(Integer merchantId, String keyword);

}
