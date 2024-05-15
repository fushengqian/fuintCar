package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.VehicleOrderDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtVehicleOrder;
import java.util.List;
import java.util.Map;

public interface VehicleOrderService extends IService<MtVehicleOrder> {

    MtVehicleOrder updateVehicleOrder(MtVehicleOrder mtVehicleOrder);

    MtVehicleOrder submitVehicleOrder(MtVehicleOrder mtVehicleOrder) throws BusinessCheckException;

    PaginationResponse<VehicleOrderDto> getVehicleOrderListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    MtVehicleOrder getVehicleOrderById(Integer id);

    List<MtVehicleOrder> queryVehicleOrderList(Map<String, Object> paramMap);

    void deleteVehicleOrder(Integer id, String operator) throws BusinessCheckException;
}
