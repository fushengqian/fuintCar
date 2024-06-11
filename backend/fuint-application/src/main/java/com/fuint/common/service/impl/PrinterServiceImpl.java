package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtPrinter;
import com.fuint.common.service.PrinterService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.mapper.MtPrinterMapper;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 打印机服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class PrinterServiceImpl extends ServiceImpl<MtPrinterMapper, MtPrinter> implements PrinterService {

    private static final Logger logger = LoggerFactory.getLogger(PrinterServiceImpl.class);

    private MtPrinterMapper mtPrinterMapper;

    /**
     * 分页查询数据列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtPrinter> queryPrinterListByPagination(PaginationRequest paginationRequest) {
        Page<MtPrinter> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtPrinter> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtPrinter::getStatus, StatusEnum.DISABLE.getKey());

        lambdaQueryWrapper.orderByAsc(MtPrinter::getId);
        List<MtPrinter> dataList = mtPrinterMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtPrinter> paginationResponse = new PaginationResponse(pageImpl, MtPrinter.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加打印机
     *
     * @param mtPrinter 打印机信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增打印机")
    public MtPrinter addPrinter(MtPrinter mtPrinter) throws BusinessCheckException {
        mtPrinter.setStatus(StatusEnum.ENABLED.getKey());
        mtPrinter.setUpdateTime(new Date());
        mtPrinter.setCreateTime(new Date());
        Integer id = mtPrinterMapper.insert(mtPrinter);
        if (id > 0) {
            return mtPrinter;
        } else {
            throw new BusinessCheckException("新增打印机数据失败");
        }
    }

    /**
     * 根据ID获打印机取息
     *
     * @param id 打印机ID
     * @return
     */
    @Override
    public MtPrinter queryPrinterById(Integer id) {
        return mtPrinterMapper.selectById(id);
    }

    /**
     * 根据ID删除打印机
     *
     * @param id 打印机ID
     * @param operator 操作人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除打印机")
    public void deletePrinter(Integer id, String operator) {
        MtPrinter mtPrinter = queryPrinterById(id);
        if (null == mtPrinter) {
            return;
        }
        mtPrinter.setStatus(StatusEnum.DISABLE.getKey());
        mtPrinter.setUpdateTime(new Date());
        mtPrinterMapper.updateById(mtPrinter);
    }

    /**
     * 修改打印机数据
     *
     * @param mtPrinter
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新打印机")
    public MtPrinter updatePrinter(MtPrinter mtPrinter) throws BusinessCheckException {
        MtPrinter printer = queryPrinterById(mtPrinter.getId());
        BeanUtils.copyProperties(mtPrinter, printer);
        if (mtPrinter == null) {
            throw new BusinessCheckException("该打印机状态异常");
        }
        mtPrinter.setUpdateTime(new Date());
        mtPrinterMapper.updateById(printer);
        return printer;
    }

   /**
    * 根据条件搜索打印机
    *
    * @param params 查询参数
    * @throws BusinessCheckException
    * @return
    * */
    @Override
    public List<MtPrinter> queryPrinterListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String merchantId =  params.get("merchantId") == null ? "" : params.get("merchantId").toString();

        LambdaQueryWrapper<MtPrinter> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtPrinter::getStatus, status);
        }
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtPrinter::getMerchantId, merchantId);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtPrinter::getStoreId, 0)
                    .or()
                    .eq(MtPrinter::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtPrinter::getId);
        List<MtPrinter> dataList = mtPrinterMapper.selectList(lambdaQueryWrapper);
        return dataList;
    }
}
