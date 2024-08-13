package com.fuint.module.backendApi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.GoodsTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtGoodsSkuMapper;
import com.fuint.repository.mapper.MtGoodsSpecMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-商品相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/goods/goods")
public class BackendGoodsController extends BaseController {

    private MtGoodsSpecMapper mtGoodsSpecMapper;

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 商品服务接口
     */
    private GoodsService goodsService;

    /**
     * 商品分类服务接口
     */
    private CateService cateService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 后台账户服务接口
     */
    private AccountService accountService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 分页查询商品列表
     *
     * @param request
     * @throws BusinessCheckException
     * @return
     */
    @ApiOperation(value = "分页查询商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");
        String goodsNo = request.getParameter("goodsNo");
        String isSingleSpec = request.getParameter("isSingleSpec");
        String type = request.getParameter("type");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");
        String stock = request.getParameter("stock");
        String cateId = request.getParameter("cateId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();
        Integer merchantId = account.getMerchantId() == null ? 0 : account.getMerchantId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        if (merchantId > 0) {
            params.put("merchantId", merchantId);
        }
        if (storeId > 0) {
            params.put("storeId", storeId);
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(type)) {
            params.put("type", type);
        }
        if (StringUtil.isNotEmpty(cateId)) {
            params.put("cateId", cateId);
        }
        if (StringUtil.isNotEmpty(goodsNo)) {
            params.put("goodsNo", goodsNo);
        }
        if (StringUtil.isNotEmpty(isSingleSpec)) {
            params.put("isSingleSpec", isSingleSpec);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(stock)) {
            params.put("stock", stock);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        // 商品类型列表
        GoodsTypeEnum[] typeListEnum = GoodsTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (GoodsTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (storeId != null && storeId > 0) {
            paramsStore.put("storeId", storeId.toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        Map<String, Object> cateParam = new HashMap<>();
        cateParam.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            cateParam.put("merchantId", accountInfo.getMerchantId());
        }
        if (storeId != null && storeId > 0) {
            cateParam.put("storeId", storeId.toString());
        }
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(cateParam);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("typeList", typeList);
        result.put("storeList", storeList);
        result.put("cateList", cateList);

        return getSuccessResult(result);
    }

    /**
     * 删除商品
     *
     * @param request
     * @param goodsId 商品ID
     * @return
     */
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:edit')")
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer goodsId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        String operator = accountInfo.getAccountName();
        goodsService.deleteGoods(goodsId, operator);
        return getSuccessResult(true);
    }

    /**
     * 更新商品状态
     *
     * @return
     */
    @ApiOperation(value = "更新商品状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:edit')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtGoods mtGoods = goodsService.queryGoodsById(id);
        if (mtGoods == null) {
            return getFailureResult(201, "该商品不存在");
        }

        String operator = accountInfo.getAccountName();

        MtGoods goodsInfo = new MtGoods();
        goodsInfo.setOperator(operator);
        goodsInfo.setId(id);
        goodsInfo.setStatus(status);
        goodsService.saveGoods(goodsInfo);

        return getSuccessResult(true);
    }

    /**
     * 获取商品详情
     *
     * @param request
     * @param goodsId
     * @return
     */
    @ApiOperation(value = "获取商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer goodsId) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Integer storeId = accountInfo.getStoreId();
        GoodsDto goods = goodsService.getGoodsDetail(goodsId, false);

        Map<String, Object> result = new HashMap<>();
        result.put("goods", goods);

        List<String> images = new ArrayList<>();
        if (goods != null) {
            images = JSONArray.parseArray(goods.getImages(), String.class);
        }
        result.put("images", images);

        // 商品规格列表
        List<String> specNameArr = new ArrayList<>();
        List<Integer> specIdArr = new ArrayList<>();
        List<GoodsSpecItemDto> specArr = new ArrayList<>();

        // sku列表
        List<GoodsSkuDto> skuArr = new ArrayList<>();
        if (goods != null) {
            // 处理规格列表
            for (MtGoodsSpec mtGoodsSpec : goods.getSpecList()) {
                if (!specNameArr.contains(mtGoodsSpec.getName())) {
                    specNameArr.add(mtGoodsSpec.getName());
                    specIdArr.add(mtGoodsSpec.getId());
                }
            }

            for (int i = 0; i < specNameArr.size(); i++) {
                GoodsSpecItemDto item = new GoodsSpecItemDto();
                List<GoodsSpecChildDto> child = new ArrayList<>();
                Integer specId = specIdArr.get(i) == null ? (i+1) : specIdArr.get(i);
                String name = specNameArr.get(i);
                for (MtGoodsSpec mtGoodsSpec : goods.getSpecList()) {
                    if (mtGoodsSpec.getName().equals(name)) {
                        GoodsSpecChildDto goodsSpecChildDto = new GoodsSpecChildDto();
                        goodsSpecChildDto.setId(mtGoodsSpec.getId());
                        goodsSpecChildDto.setName(mtGoodsSpec.getValue());
                        goodsSpecChildDto.setChecked(true);
                        child.add(goodsSpecChildDto);
                    }
                }
                item.setId(specId);
                item.setName(name);
                item.setChild(child);
                specArr.add(item);
            }

            // 处理sku列表
            for (MtGoodsSku mtGoodsSku : goods.getSkuList()) {
                 GoodsSkuDto skuDto = new GoodsSkuDto();
                 BeanUtils.copyProperties(mtGoodsSku, skuDto);
                 List<MtGoodsSpec> specList = new ArrayList<>();
                 String[] specIds = skuDto.getSpecIds().split("-");
                 for (String specId : specIds) {
                      MtGoodsSpec spec = goodsService.getSpecDetail(Integer.parseInt(specId));
                      if (spec != null) {
                          specList.add(spec);
                      }
                 }
                 skuDto.setSpecList(specList);
                 skuArr.add(skuDto);
            }
        }

        result.put("specData", specArr);
        result.put("skuData", skuArr);

        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            param.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);
        result.put("cateList", cateList);

        String imagePath = settingService.getUploadBasePath();
        result.put("imagePath", imagePath);

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (storeId != null && storeId > 0) {
            paramsStore.put("storeId", storeId.toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        // 商品类型列表
        GoodsTypeEnum[] typeListEnum = GoodsTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (GoodsTypeEnum enumItem : typeListEnum) {
             ParamDto paramDto = new ParamDto();
             paramDto.setKey(enumItem.getKey());
             paramDto.setName(enumItem.getValue());
             paramDto.setValue(enumItem.getKey());
             typeList.add(paramDto);
        }

        result.put("typeList", typeList);
        result.put("storeId", storeId);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 保存商品信息
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存商品信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:add')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String goodsId = param.get("goodsId") == null ? "0" : param.get("goodsId").toString();
        if (StringUtil.isEmpty(goodsId)) {
            goodsId = "0";
        }

        String name = param.get("name") == null ? null : CommonUtil.replaceXSS(param.get("name").toString());
        String description = param.get("description") == null ? "" : param.get("description").toString();
        List<String> images = param.get("images") == null ? new ArrayList<>() : (List) param.get("images");
        String sort = param.get("sort") == null ? "" : param.get("sort").toString();
        String stock = param.get("stock") == null ? "" : param.get("stock").toString();
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String goodsNo = param.get("goodsNo") == null ? "" : param.get("goodsNo").toString();
        String price = param.get("price") == null ? "" : param.get("price").toString();
        String linePrice = param.get("linePrice") == null ? "" : param.get("linePrice").toString();
        String weight = param.get("weight") == null ? "" : param.get("weight").toString();
        Integer initSale = param.get("initSale") == null ? 0 : Integer.parseInt(param.get("initSale").toString());
        String salePoint = param.get("salePoint") == null ? "" : param.get("salePoint").toString();
        String canUsePoint = param.get("canUsePoint") == null ? "" : param.get("canUsePoint").toString();
        String isMemberDiscount = param.get("isMemberDiscount") == null ? "" : param.get("isMemberDiscount").toString();
        String isSingleSpec = param.get("isSingleSpec") == null ? "" : param.get("isSingleSpec").toString();
        Integer cateId = (param.get("cateId") == null || StringUtil.isEmpty(param.get("cateId").toString())) ? 0 : Integer.parseInt(param.get("cateId").toString());
        Integer storeId = (param.get("storeId") == null || StringUtil.isEmpty(param.get("storeId").toString())) ? 0 : Integer.parseInt(param.get("storeId").toString());
        String type = param.get("type") == null ? "" : param.get("type").toString();
        String couponIds = param.get("couponIds") == null ? "" : param.get("couponIds").toString();
        String serviceTime = param.get("serviceTime") == null ? "0" : param.get("serviceTime").toString();
        List<LinkedHashMap> skuList = param.get("skuData") == null ? new ArrayList<>() : (List) param.get("skuData");
        List<LinkedHashMap> specList = param.get("specData") == null ? new ArrayList<>() : (List) param.get("specData");

        // 保存规格名称
        if (specList.size() > 0) {
            for (LinkedHashMap specDto : specList) {
                 String specId = specDto.get("id") == null ? "" : specDto.get("id").toString();
                 String specName = specDto.get("name") == null ? "" : specDto.get("name").toString();
                 if (StringUtil.isNotEmpty(specId) && StringUtil.isNotEmpty(specName)) {
                     MtGoodsSpec mtGoodsSpec = mtGoodsSpecMapper.selectById(Integer.parseInt(specId));
                     String oldName = mtGoodsSpec.getName();
                     Map<String, Object> paramSearch = new HashMap<>();
                     paramSearch.put("goods_id", goodsId);
                     paramSearch.put("name", oldName);
                     List<MtGoodsSpec> dataList = mtGoodsSpecMapper.selectByMap(paramSearch);
                     if (dataList.size() > 0 && !specName.equals(oldName)) {
                         for (MtGoodsSpec mtSpec : dataList) {
                              mtSpec.setName(specName);
                              mtGoodsSpecMapper.updateById(mtSpec);
                         }
                     }
                 }
            }
        }

        // 全部规格
        List<String> specIdList = new ArrayList<>();
        if (skuList.size() > 0) {
            for (LinkedHashMap skuDto : skuList) {
                 specIdList.add(skuDto.get("specIds").toString());
            }
        }

        // 保存新规格或或单规格商品，要先删除旧的sku数据
        if (skuList.size() > 0 || isSingleSpec.equals(YesOrNoEnum.YES.getKey())) {
            Map<String, Object> param0 = new HashMap<>();
            param0.put("goods_id", goodsId);
            List<MtGoodsSku> goodsSkuList = mtGoodsSkuMapper.selectByMap(param0);
            if (goodsSkuList.size() > 0) {
                for (MtGoodsSku mtGoodsSku : goodsSkuList) {
                     if (!specIdList.contains(mtGoodsSku.getSpecIds())) {
                         mtGoodsSkuMapper.deleteById(mtGoodsSku.getId());
                     }
                }
            }
        }

        for (LinkedHashMap skuDto : skuList) {
            Map<String, Object> params = new HashMap<>();
            params.put("goods_id", goodsId);
            params.put("spec_ids", skuDto.get("specIds"));
            // 是否已存在
            List<MtGoodsSku> goodsSkuList = mtGoodsSkuMapper.selectByMap(params);
            MtGoodsSku sku = new MtGoodsSku();
            if (goodsSkuList.size() > 0) {
                sku = goodsSkuList.get(0);
            }
            sku.setSkuNo(skuDto.get("skuNo").toString());
            sku.setLogo(skuDto.get("logo").toString());
            sku.setGoodsId(Integer.parseInt(goodsId));
            sku.setSpecIds(skuDto.get("specIds").toString());
            String skuStock = skuDto.get("stock").toString();
            if (StringUtil.isEmpty(skuStock)) {
                skuStock = "0";
            }
            sku.setStock(Integer.parseInt(skuStock));

            BigDecimal skuPrice = new BigDecimal("0");
            if (skuDto.get("price") != null && StringUtil.isNotEmpty(skuDto.get("price").toString())) {
                skuPrice = new BigDecimal(skuDto.get("price").toString());
            }
            sku.setPrice(skuPrice);

            BigDecimal skuLinePrice = new BigDecimal("0");
            if (skuDto.get("linePrice") != null && StringUtil.isNotEmpty(skuDto.get("linePrice").toString())) {
                skuLinePrice = new BigDecimal(skuDto.get("linePrice").toString());
            }
            sku.setLinePrice(skuLinePrice);

            BigDecimal skuWeight = new BigDecimal("0");
            if (skuDto.get("weight") != null && StringUtil.isNotEmpty(skuDto.get("weight").toString())) {
                skuWeight = new BigDecimal(skuDto.get("weight").toString());
            }
            sku.setWeight(skuWeight);
            sku.setStatus(StatusEnum.ENABLED.getKey());
            if (sku.getId() != null && sku.getId() > 0) {
                mtGoodsSkuMapper.updateById(sku);
            } else {
                mtGoodsSkuMapper.insert(sku);
            }
        }

        // 多规格商品，价格默认取第一个sku
        if (skuList.size() > 0 && isSingleSpec.equals(YesOrNoEnum.NO.getKey())) {
            price = skuList.get(0).get("price").toString();
            linePrice = skuList.get(0).get("linePrice").toString();
            weight = skuList.get(0).get("weight").toString();
            // 库存等于所有sku库存相加
            Integer allStock = 0;
            for (LinkedHashMap item : skuList) {
                 allStock = allStock + Integer.parseInt(item.get("stock").toString());
            }
            stock = allStock.toString();
        }

        Integer myStoreId = accountInfo.getStoreId();
        if (myStoreId > 0) {
            storeId = myStoreId;
        }

        MtGoods mtGoods = new MtGoods();
        mtGoods.setId(Integer.parseInt(goodsId));
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            mtGoods.setMerchantId(accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(type)) {
            mtGoods.setType(type);
        }
        mtGoods.setCateId(cateId);
        mtGoods.setName(name);
        mtGoods.setGoodsNo(goodsNo);
        if (StringUtil.isNotEmpty(serviceTime)) {
            mtGoods.setServiceTime(Integer.parseInt(serviceTime));
        }
        if (StringUtil.isNotEmpty(couponIds)) {
            mtGoods.setCouponIds(couponIds);
        }
        mtGoods.setIsSingleSpec(isSingleSpec);
        if (StringUtil.isNotEmpty(stock)) {
            mtGoods.setStock(Integer.parseInt(stock));
        }
        if (StringUtil.isNotEmpty(description)) {
            mtGoods.setDescription(description);
        }
        if (storeId != null && param.get("storeId") != null) {
            mtGoods.setStoreId(storeId);
        }
        if (images.size() > 0) {
            mtGoods.setLogo(images.get(0));
        }
        if (StringUtil.isNotEmpty(sort)) {
            mtGoods.setSort(Integer.parseInt(sort));
        }
        if (StringUtil.isNotEmpty(status)) {
            mtGoods.setStatus(status);
        }
        if (StringUtil.isNotEmpty(price)) {
            mtGoods.setPrice(new BigDecimal(price));
        }
        if (StringUtil.isNotEmpty(linePrice)) {
            mtGoods.setLinePrice(new BigDecimal(linePrice));
        }
        if (StringUtil.isNotEmpty(weight)) {
            mtGoods.setWeight(new BigDecimal(weight));
        }
        if (initSale > 0) {
            mtGoods.setInitSale(initSale);
        }
        if (StringUtil.isNotEmpty(salePoint)) {
            mtGoods.setSalePoint(salePoint);
        }
        if (StringUtil.isNotEmpty(canUsePoint)) {
            mtGoods.setCanUsePoint(canUsePoint);
        }
        if (StringUtil.isNotEmpty(isMemberDiscount)) {
            mtGoods.setIsMemberDiscount(isMemberDiscount);
        }
        if (images.size() > 0) {
            String imagesJson = JSONObject.toJSONString(images);
            mtGoods.setImages(imagesJson);
        }
        mtGoods.setOperator(accountInfo.getAccountName());

        MtGoods goodsInfo = goodsService.saveGoods(mtGoods);

        Map<String, Object> result = new HashMap();
        result.put("goodsInfo", goodsInfo);

        return getSuccessResult(result);
    }

    /**
     * 保存商品规格
     *
     * @param request HttpServletRequest对象
     */
    @ApiOperation(value = "保存商品规格")
    @RequestMapping(value = "/saveSpecName", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:add')")
    public ResponseObject saveSpecName(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        String goodsId = param.get("goodsId") == null ? "0" : param.get("goodsId").toString();
        String name = param.get("name") == null ? "" : param.get("name").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (StringUtil.isEmpty(goodsId)) {
            return getFailureResult(201, "请先保存商品基础信息");
        }

        Map<String, Object> paramSearch = new HashMap<>();
        paramSearch.put("goods_id", goodsId);
        paramSearch.put("name", name);
        List<MtGoodsSpec> dataList = mtGoodsSpecMapper.selectByMap(paramSearch);

        Integer targetId;
        MtGoodsSpec mtGoodsSpec;
        if (dataList.size() < 1) {
            mtGoodsSpec = new MtGoodsSpec();
            mtGoodsSpec.setGoodsId(Integer.parseInt(goodsId));
            mtGoodsSpec.setName(name);
            mtGoodsSpec.setValue("");
            mtGoodsSpec.setStatus(StatusEnum.ENABLED.getKey());
            mtGoodsSpecMapper.insert(mtGoodsSpec);
        } else {
            mtGoodsSpec = dataList.get(0);
            if (!mtGoodsSpec.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                mtGoodsSpec.setStatus(StatusEnum.ENABLED.getKey());
                mtGoodsSpec.setValue("");
                mtGoodsSpecMapper.updateById(mtGoodsSpec);
            }
        }
        targetId = mtGoodsSpec.getId();

        Map<String, Object> outParams = new HashMap();
        outParams.put("id", targetId);

        return getSuccessResult(outParams);
    }

    /**
     * 保存商品规格值
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存商品规格值")
    @RequestMapping(value = "/saveSpecValue", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:add')")
    public ResponseObject saveSpecValue(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        String specName = param.get("specName") == null ? "" : param.get("specName").toString();
        String goodsId = param.get("goodsId") == null ? "" : param.get("goodsId").toString();
        String value = param.get("value") == null ? "" : param.get("value").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (StringUtil.isEmpty(goodsId)) {
            return getFailureResult(201, "请先保存商品基础信息");
        }

        if (StringUtil.isEmpty(specName)) {
            return getFailureResult(201, "规格名称不能为空");
        }

        if ( StringUtil.isEmpty(value)) {
            return getFailureResult(201, "规格值不能为空");
        }

        Map<String, Object> paramSearch = new HashMap<>();
        paramSearch.put("goods_id", goodsId);
        paramSearch.put("name", specName);
        paramSearch.put("status", StatusEnum.ENABLED.getKey());
        List<MtGoodsSpec> dataList = mtGoodsSpecMapper.selectByMap(paramSearch);

        // 1.先修改空值
        Integer id = 0;
        if (dataList.size() > 0) {
            for (MtGoodsSpec mtGoodsSpec : dataList) {
                if (StringUtil.isEmpty(mtGoodsSpec.getValue())) {
                    mtGoodsSpec.setValue(value);
                    id = mtGoodsSpec.getId();
                    mtGoodsSpecMapper.updateById(mtGoodsSpec);
                    break;
                }
            }
        }

        // 2.没有空值再新增
        if (id < 1) {
            MtGoodsSpec mtGoodsSpec = new MtGoodsSpec();
            mtGoodsSpec.setGoodsId(Integer.parseInt(goodsId));
            mtGoodsSpec.setName(specName);
            mtGoodsSpec.setValue(value);
            mtGoodsSpec.setStatus(StatusEnum.ENABLED.getKey());
            mtGoodsSpecMapper.insert(mtGoodsSpec);
            id = mtGoodsSpec.getId();
        }

        // 3.更新已存在的sku的规格ID
        Map<String, Object> skuParams = new HashMap<>();
        skuParams.put("goods_id", goodsId);
        skuParams.put("status", StatusEnum.ENABLED.getKey());
        List<MtGoodsSku> goodsSkuList = mtGoodsSkuMapper.selectByMap(skuParams);
        List<MtGoodsSpec> dataCountList = mtGoodsSpecMapper.getGoodsSpecCountList(Integer.parseInt(goodsId));
        if (goodsSkuList.size() > 0) {
            for (MtGoodsSku mtGoodsSku : goodsSkuList) {
                 String specIds = mtGoodsSku.getSpecIds();
                 String[] specIdArr = specIds.split("-");
                 if (specIdArr.length < dataCountList.size()) {
                     specIds = specIds + "-" + id;
                     mtGoodsSku.setSpecIds(specIds);
                     mtGoodsSkuMapper.updateById(mtGoodsSku);
                 }
            }
        }

        // 4.返回新增的ID
        Map<String, Object> result = new HashMap();
        result.put("id", id);

        return getSuccessResult(result);
    }

    /**
     * 删除商品规格
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "删除商品规格")
    @RequestMapping(value = "/deleteSpec", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:add')")
    public ResponseObject deleteSpec(HttpServletRequest request) {
        String specName = request.getParameter("specName") == null ? "" : request.getParameter("specName");
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");

        if (StringUtil.isEmpty(specName) || StringUtil.isEmpty(goodsId)) {
            return getFailureResult(201, "请求参数错误");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("goods_id", goodsId);
        param.put("name", specName);
        List<MtGoodsSpec> dataList = mtGoodsSpecMapper.selectByMap(param);
        if (dataList.size() > 0) {
            for (MtGoodsSpec mtGoodsSpec : dataList) {
                 mtGoodsSpec.setStatus(StatusEnum.DISABLE.getKey());
                 mtGoodsSpecMapper.updateById(mtGoodsSpec);
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 删除商品规格值
     *
     * @param request HttpServletRequest对象
     */
    @ApiOperation(value = "删除商品规格值")
    @RequestMapping(value = "/deleteSpecValue", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:goods:add')")
    public ResponseObject deleteSpecValue(HttpServletRequest request) {
        Integer specId = request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));

        if (specId < 1) {
            return getFailureResult(201, "请求参数错误");
        }

        MtGoodsSpec mtGoodsSpec = mtGoodsSpecMapper.selectById(specId);
        if (mtGoodsSpec == null) {
            return getFailureResult(201, "该规格值不存在");
        }

        mtGoodsSpec.setStatus(StatusEnum.DISABLE.getKey());
        mtGoodsSpecMapper.updateById(mtGoodsSpec);

        // 把对应的sku删掉
        Map<String, Object> param = new HashMap<>();
        param.put("goods_id", mtGoodsSpec.getGoodsId().toString());
        List<MtGoodsSku> goodsSkuList = mtGoodsSkuMapper.selectByMap(param);
        for(MtGoodsSku mtGoodsSku : goodsSkuList) {
            String[] ss = mtGoodsSku.getSpecIds().split("-");
            for (int i = 0; i < ss.length; i++) {
                 if (ss[i].equals(specId+"")) {
                     mtGoodsSku.setStatus(StatusEnum.DISABLE.getKey());
                     mtGoodsSkuMapper.updateById(mtGoodsSku);
                 }
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 获取选择商品列表
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "获取选择商品列表")
    @RequestMapping(value = "/selectGoods", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject selectGoods(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.put("storeId", accountInfo.getStoreId());
        }
        PaginationResponse<GoodsDto> paginationResponse = goodsService.selectGoodsList(params);
        String imagePath = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap();
        result.put("paginationResponse", paginationResponse);
        result.put("imagePath", imagePath);

        return getSuccessResult(result);
    }
}
