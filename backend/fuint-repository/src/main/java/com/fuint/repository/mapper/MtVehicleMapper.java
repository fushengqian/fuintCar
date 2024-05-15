package com.fuint.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuint.repository.model.MtVehicle;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MtVehicleMapper extends BaseMapper<MtVehicle> {
    List<MtVehicle> getVehicleByUserId(@Param("userId") Integer userId, @Param("isDefault") Boolean isDefault);
}
