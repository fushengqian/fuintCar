package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.VehicleDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtVehicle;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface VehicleService extends IService<MtVehicle> {

    MtVehicle updateVehicle(MtVehicle mtVehicle);

    MtVehicle saveVehicle(MtVehicle mtVehicle) throws BusinessCheckException;

    PaginationResponse<VehicleDto> getUserVehicleListByPagination(HttpServletRequest request) throws BusinessCheckException;

    VehicleDto getVehicleById(Integer id) throws BusinessCheckException;

    MtVehicle queryVehicleById(Integer id);

    void deleteVehicle(Integer id, String operator) throws BusinessCheckException;

    List<VehicleDto> getVehicleByUserId(Integer userId, boolean isDefault);

    List<MtVehicle> queryVehicleList(Map<String, Object> paramMap);

}
