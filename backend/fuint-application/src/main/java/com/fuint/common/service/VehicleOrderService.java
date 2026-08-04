package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.order.OrderDto;
import com.fuint.common.dto.order.VehicleOrderDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.CreateServiceOrderParam;
import com.fuint.common.param.VehicleOrderPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtVehicleOrder;

import java.util.List;
import java.util.Map;

public interface VehicleOrderService extends IService<MtVehicleOrder> {

    MtVehicleOrder updateVehicleOrder(MtVehicleOrder mtVehicleOrder);

    MtVehicleOrder submitVehicleOrder(MtVehicleOrder mtVehicleOrder) throws BusinessCheckException;

    MtVehicleOrder createServiceOrder(CreateServiceOrderParam param, AccountInfo accountInfo) throws BusinessCheckException;

    PaginationResponse<VehicleOrderDto> getVehicleOrderListByPagination(VehicleOrderPage vehicleOrderPage);

    MtVehicleOrder getVehicleOrderById(Integer id);

    List<MtVehicleOrder> queryVehicleOrderList(Map<String, Object> paramMap);

    void deleteVehicleOrder(Integer id, String operator) throws BusinessCheckException;
}
