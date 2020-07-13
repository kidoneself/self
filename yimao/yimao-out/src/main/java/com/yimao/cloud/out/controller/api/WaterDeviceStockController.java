package com.yimao.cloud.out.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.utils.ObjectUtil;
import com.yimao.cloud.out.utils.StockUtil;
import com.yimao.cloud.pojo.vo.out.StockPutVo;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.vo.out.MaterielStockVO;
import com.yimao.cloud.pojo.vo.out.ServiceApplyMaterielVO;
import com.yimao.cloud.pojo.vo.out.StockStateQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 功能描述:库存管理
 *
 * @auther: liu yi
 * @date: 2019/4/19 13:38
 */
@Controller
@Slf4j
@Api("WaterDeviceStockController")
@RequestMapping({"/web/waterDevice/stock/"})
public class WaterDeviceStockController {
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;

    private boolean checkBaideResult(Map<String, Object> baideMap) {
        String code;
        try {
            code = baideMap.get("code").toString();
        } catch (NullPointerException var4) {
            return false;
        }

        return "00000000".equals(code);
    }

    @RequestMapping(value = {"index"}, method = {RequestMethod.GET})
    @ApiOperation("打开库存首页")
    public String index(HttpServletRequest request, @RequestParam(name = "entrance", defaultValue = "", required = false) String entrance, ModelMap modelMap) {
        modelMap.addAttribute("entrance", "");
        String token = request.getParameter("token");
        modelMap.addAttribute("token", token);
        if ("index".equals(entrance)) {
            modelMap.addAttribute("entrance", "index");
        }
        return "waterDevice/stock/index";
    }

    @RequestMapping(value = {"getGoodStockUsableNum"},method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation("读取工程师可用良品库存")
    public Map getGoodStockUsableNum(String materielCode, HttpSession httpSession) {
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("success", true);
        resultMap.put("data", 2147483647);
        return resultMap;
    }

    @GetMapping(value = {"inventory"})
    @ApiOperation("读取工程师库存")
    public String getMyInventory(HttpServletRequest request, ModelMap modelMap, HttpSession session) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        Map<String, Object> baideRroductResult;
        Map baideMaterielResult;
        try {
            baideRroductResult = BaideApiUtil.engineerStockMateriel(engineer.getOldId(), 0);
            if (this.checkBaideResult(baideRroductResult)) {
                modelMap.addAttribute("productResult", StockUtil.myInventory(baideRroductResult, "productList"));
            }

            baideMaterielResult = BaideApiUtil.engineerStockMateriel(engineer.getOldId(), 1);
            if (this.checkBaideResult(baideMaterielResult)) {
                modelMap.addAttribute("materielResult", StockUtil.myInventory(baideMaterielResult, "materielList"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.addAttribute("errorMsg", e.getMessage());
        }

        return "waterDevice/stock/stock";
    }

    @GetMapping(value = {"stockQuery"})
    @ApiOperation("读取服务站库存/服务站其他工程师库存")
    public String stockQuery(HttpServletRequest request, @RequestParam(name = "entrance", defaultValue = "", required = false) String entrance, ModelMap modelMap, HttpSession session) {
        modelMap.addAttribute("entrance", "");
        if ("index".equals(entrance)) {
            modelMap.addAttribute("entrance", "index");
        }

        Map<String, Object> serviceProductResult;
        Map<String, Object> serviceMaterielResult;
        Map<String, Object> serviceEngineerProductResult;
        Map<String, Object> serviceEngineerMaterielResult;
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        String serviceSiteId = engineer.getOldSiteId();
        try {
            serviceProductResult = BaideApiUtil.serviceSiteProductStock(serviceSiteId);
            if (this.checkBaideResult(serviceProductResult)) {
                modelMap.addAttribute("serviceProduct", StockUtil.stockInventory(serviceProductResult, "productList"));
            }

            serviceMaterielResult = BaideApiUtil.serviceSiteMaterielStock(serviceSiteId);
            if (this.checkBaideResult(serviceMaterielResult)) {
                modelMap.addAttribute("serviceMateriel", StockUtil.stockInventory(serviceMaterielResult, "materielList"));
            }

            serviceEngineerProductResult = BaideApiUtil.serviceSiteEngineerProductStock(serviceSiteId, engineer.getOldId());
            if (this.checkBaideResult(serviceEngineerProductResult)) {
                modelMap.addAttribute("serviceEngineerProduct", StockUtil.stockOtherEngineerInventory(serviceEngineerProductResult, "engineerStock", "productList"));
            }

            serviceEngineerMaterielResult = BaideApiUtil.serviceEngineerConsumableForstation(serviceSiteId, engineer.getOldId());
            if (this.checkBaideResult(serviceEngineerMaterielResult)) {
                modelMap.addAttribute("serviceEngineerMateriel", StockUtil.stockOtherEngineerInventory(serviceEngineerMaterielResult, "engineerStock", "materielList"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("读取库存失败！");
        }

        return "waterDevice/stock/stockQuery";
    }

    @GetMapping(value = {"applyMaterielToService"})
    @ResponseBody
    @ApiOperation("向服务站申请设备")
    public Map<String, Object> applyMaterielToService(HttpServletRequest request, String materielApplyList) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        String serviceId = engineer.getOldSiteId();
        Map resultMap;

        try {
            resultMap = BaideApiUtil.materielApply(serviceId, engineer.getOldId(), materielApplyList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("向服务站申请设备失败！");
        }

        return resultMap;
    }

    @GetMapping(value = {"applyMaterielToServiceEngineer"})
    @ApiOperation("向服务站其他工程师申请设备")
    public String applyMaterielToServiceEngineer(HttpServletRequest request, String allotFromId, String allotList) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        Map resultMap;
        try {
            resultMap = BaideApiUtil.materielAllot(allotFromId, engineer.getOldId(), allotList);
            log.info("申请结果:" + resultMap, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("申请失败！");
        }

        return "redirect:myApply";
    }

    @PostMapping(value = {"scanInstock"})
    @ApiOperation("打开工程师借调扫码入库inventory页面")
    public String scanInstock(ModelMap modelMap, HttpServletRequest request, String serielNo, String inputFromId, String count) {
        if (StringUtil.isNotBlank(serielNo) && StringUtil.isNotBlank(inputFromId) && StringUtil.isNotBlank(count)) {
            modelMap.addAttribute("serielNo", serielNo);
            modelMap.addAttribute("inputFromId", inputFromId);
            modelMap.addAttribute("count", count);
            return "waterDevice/stock/scanInstock";
        }

        return "redirect:myApply";
    }

    @GetMapping(value = {"myApply"})
    @ApiOperation("我的申请")
    public String myApply(ModelMap modelMap, HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        Map<String, Object> applyCheckIngResult;
        Map<String, Object> applyCheckedResult;
        List result;

        try {
            applyCheckIngResult = BaideApiUtil.engineerApplyList(engineer.getOldId(), "0", "0");
            applyCheckedResult = BaideApiUtil.engineerApplyList(engineer.getOldId(), "1", "0");
            if (this.checkBaideResult(applyCheckIngResult)) {
                result = StockUtil.applyStockInventory(applyCheckIngResult, "stockList");
                modelMap.addAttribute("applyInList", result);
            }

            if (this.checkBaideResult(applyCheckedResult)) {
                result = StockUtil.applyStockInventory(applyCheckedResult, "stockList");
                modelMap.addAttribute("applyEdList", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("我的申请读取失败！");
        }

        return "waterDevice/stock/myApply";
    }

    @GetMapping(value = {"myCheck"})
    @ApiOperation("我的审核")
    public String myCheck(ModelMap modelMap, HttpServletRequest request) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        Map<String, Object> checkIngResult;
        Map<String, Object> checkedResult;
        List result;

        try {
            checkIngResult = BaideApiUtil.engineerApplyList(engineer.getOldId(), "0", "1");
            checkedResult = BaideApiUtil.engineerApplyList(engineer.getOldId(), "1", "1");
            if (this.checkBaideResult(checkIngResult)) {
                result = StockUtil.applyStockInventory(checkIngResult, "stockList");
                modelMap.addAttribute("applyInList", result);
            }

            if (this.checkBaideResult(checkedResult)) {
                result = StockUtil.applyStockInventory(checkedResult, "stockList");
                modelMap.addAttribute("applyEdList", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("我的审核读取失败！");
        }

        return "waterDevice/stock/myCheck";
    }

    private List<String> myCheckDataDispose(Map<String, List> applyMap, List<ServiceApplyMaterielVO> dataList) {
        List<ServiceApplyMaterielVO> applyList = null;
        List<String> indexList = new ArrayList();
        Iterator var5 = dataList.iterator();

        while (var5.hasNext()) {
            ServiceApplyMaterielVO materielVO = (ServiceApplyMaterielVO) var5.next();
            applyList = (List) applyMap.get(materielVO.getId());
            if (applyList == null) {
                applyList = new ArrayList();
                applyList.add(materielVO);
                applyMap.put(materielVO.getId(), applyList);
                indexList.add(materielVO.getId());
            } else {
                applyList.add(materielVO);
            }
        }

        Collections.reverse(indexList);
        return indexList;
    }

    @RequestMapping(value = {"engineerApplyOperate"}, method = {RequestMethod.POST})
    @ResponseBody
    @ApiOperation("工程师同意/拒绝借调")
    public Map<String, Object> engineerApplyOperate(@RequestParam(name = "serialNo") String serialNo, @RequestParam(name = "Status", defaultValue = "2", required = false) Integer Status) {
        Map resultMap;
        try {
            resultMap = BaideApiUtil.engineerApplyOperate(serialNo, Status);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap = new HashMap();
            resultMap.put("msg", "借调失败");
        }
        return resultMap;
    }

    @PostMapping(value = {"engineerPutStock"})
    @ResponseBody
    @ApiOperation("产品/物料扫描入库")
    public Map<String, Object> engineerPutStock(Integer type, @RequestParam(name = "inputFromId", required = false) String inputFromId, String epsJson, String allotId, String materielType, HttpSession session) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        String stationId = engineer.getOldSiteId();
        Map<String, Object> resultMap = null;
        List dataList;

        try {
            JSONArray jsonArray = JSONArray.fromObject(epsJson);
            dataList = (List) JSONArray.toCollection(jsonArray, StockPutVo.class);
            if (type == 0) {
                resultMap = BaideApiUtil.engineerPutStock(type, engineer.getOldId(), inputFromId, dataList, allotId, "");
            } else if (type == 1) {
                resultMap = BaideApiUtil.engineerPutStock(type, engineer.getOldId(), stationId, dataList, allotId, materielType);
            }
        } catch (Exception e) {
            throw new YimaoException("产品/物料扫描入库失败！");
        }

        return resultMap;
    }

    @GetMapping(value = {"stateQuery"})
    @ApiOperation("打开设备耗材查询页面")
    public String stateQuery() {
        return "waterDevice/stock/stateQuery";
    }

    @PostMapping(value = {"getMaterielStatus"})
    @ApiOperation("扫码查看设备和滤芯状态")
    public String getMaterielStatus(HttpServletRequest request, String batchCodes, ModelMap modelMap) {
        Map resultMap;

        try {
            resultMap = BaideApiUtil.materielStatus(batchCodes);
            if ("00000000".equals(resultMap.get("code"))) {
                JSONArray jsonObject = JSONArray.fromObject(resultMap.get("data").toString());
                List<StockStateQueryVO> result = (List) JSONArray.toCollection(jsonObject, StockStateQueryVO.class);
                if (result != null && result.size() > 0) {
                    modelMap.addAttribute("stockInfo", result.get(0));
                }
            }

            return "waterDevice/stock/stateQueryInfo";
        } catch (Exception e) {
            e.printStackTrace();
            return "waterDevice/stock/stateQueryInfo";
        }
    }

    @PostMapping(value = {"getMaterielStockInfo"})
    @ApiOperation("我的库存-打开设备/物料库存详情页面")
    public String getMaterielStock(String materielId, String stockOwnFlag, String stockOwnId, String status, String isFrozen, String pageIndex, @RequestParam(name = "fromPageIndex", defaultValue = "1", required = false) String fromPageIndex, ModelMap modelMap) {
        if (StringUtil.isNotEmpty(materielId) && StringUtil.isNotEmpty(stockOwnFlag) && StringUtil.isNotEmpty(stockOwnId) && StringUtil.isNotEmpty(status) && StringUtil.isNotEmpty(isFrozen) && StringUtil.isNotEmpty(pageIndex)) {
            modelMap.addAttribute("materielId", materielId);
            modelMap.addAttribute("stockOwnFlag", stockOwnFlag);
            modelMap.addAttribute("stockOwnId", stockOwnId);
            modelMap.addAttribute("status", status);
            modelMap.addAttribute("isFrozen", isFrozen);
            modelMap.addAttribute("pageIndex", pageIndex);
            modelMap.addAttribute("fromPageIndex", fromPageIndex);
            return "waterDevice/stock/stockInventoryDesc";
        }

        return "redirect:inventory";
    }

    @PostMapping(value = {"getMaterielStockInfoDetail"})
    @ApiOperation("读取设备/物料库存详情")
    public String getMaterielStockList(HttpServletRequest request, ModelMap modelMap, String materielId, String stockOwnFlag, String stockOwnId, String status, String isFrozen, String pageIndex, String page, @RequestParam(name = "fromPageIndex", defaultValue = "1", required = false) String fromPageIndex) {
        Map<String, Object> resultMap;
        String jsonList;
        List<MaterielStockVO> resultList;

        Integer engineerId = userCache.getCurrentEngineerId();
        if (engineerId == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new YimaoException("用户信息过期或不存在！");
        }

        if (engineerId == null) {
            throw new BadRequestException("TOKEN 错误或已失效");
        }

        if ("1".equals(fromPageIndex) && "0".equals(stockOwnId)) {
            stockOwnId = engineer.getOldId();
        } else if ("2".equals(fromPageIndex) && "1".equals(stockOwnFlag)) {
            stockOwnId = engineer.getOldSiteId();
        }

        try {
            resultMap = BaideApiUtil.materielStockList(materielId, stockOwnFlag, stockOwnId, status, isFrozen, page);
            if ("00000000".equals(resultMap.get("code"))) {
                String dataString = (String) resultMap.get("data");
                resultMap = (Map) JSONObject.parse(dataString);
                com.alibaba.fastjson.JSONArray jsonArray = (com.alibaba.fastjson.JSONArray) resultMap.get("rows");
                jsonList = jsonArray.toJSONString();
                JSONArray jsonArray1 = JSONArray.fromObject(jsonList);
                resultList = (List) JSONArray.toCollection(jsonArray1, MaterielStockVO.class);
                modelMap.addAttribute("isFrozen", isFrozen);
                modelMap.addAttribute("resultList", resultList);
                modelMap.addAttribute("status", status);
                modelMap.addAttribute("fromPageIndex", fromPageIndex);
            }
        } catch (Exception e) {
            throw new YimaoException("读取设备/物料库存详情失败！");
        }

        return "0".equals(pageIndex) ? "waterDevice/stock/stockList" : "waterDevice/stock/materielList";
    }

    @GetMapping(value = {"deviceScan"})
    @ApiOperation("打开服务站申请设备扫描")
    public String getEnigneerStockHistoryInfo(String page, String limit, HttpServletRequest request, ModelMap modelMap) {
       /* PageVO<ProductDTO> pageDTO = null;
        List<ProductModalVO> list = new ArrayList<>();
        if (pageDTO != null && pageDTO.getTotal() > 0) {
            ProductModalVO vo;
            StringBuffer sb;
            for (ProductDTO product : pageDTO.getResult()) {
                vo = new ProductModalVO();
               *//* vo.setId(product.getId().toString());
                if(!product.getCostIds().isEmpty()){
                    sb=new StringBuffer("");
                    for(Integer costId:product.getCostIds()){
                        sb.append(costId+",");
                    }
                    vo.setCosts(sb.toString());
                }

                vo.setCreateTime(product.getCreateTime());
                vo.setCreateUser(product.getCreator());
                //vo.setDeleteTime();
                //vo.setDelStatus();
                vo.setGoodsSystemId("waterdevice");
                //vo.setIdStatus();
                vo.setImg(product.getImg());
                //vo.setKindId(product.getCategoryId());//前台二级分类id
                vo.setKindName(product.getCategoryName());//前台二级分类名称
                vo.setModalDescription("");*//*
                vo.setModalName(product.getCategoryName());//三级分类名称
                // vo.setPayAccountId(product.getCompanyId());//支付套餐id
                //vo.setPayAccountName();//支付套餐名称
               *//* vo.setProductId(product.getId().toString());//产品id
                vo.setProductName(product.getName());
                vo.setProductPrice(product.getPrice());*//*
                //vo.setRenews();
                //vo.setYimaoOldSystemId();

                list.add(vo);
            }

            modelMap.addAttribute("productModel", list);
        }*/

        return "waterDevice/stock/deviceScan";
    }

    @RequestMapping(value = {"engineerMaterielStatusChange"}, method = {RequestMethod.POST})
    @ApiOperation("良品转不良品/不良品转良品")
    @ResponseBody
    public Map<String, Object> engineerMaterielStatusChange(String batchCode, String status, HttpServletRequest request) {
        Map resultMap;
        try {
            Integer engineerId = userCache.getCurrentEngineerId();
            if (engineerId == null) {
                throw new YimaoException("用户信息过期或不存在！");
            }
            EngineerDTO engineer = userFeign.getEngineerById(engineerId);
            if (engineer == null) {
                throw new YimaoException("用户信息过期或不存在！");
            }
            resultMap = BaideApiUtil.engineerMaterielStatusChange(engineer.getOldId(), batchCode, status);
        } catch (YimaoException e) {
            e.printStackTrace();
            throw new YimaoException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("转换失败！");
        }

        return resultMap;
    }

    @GetMapping(value = {"getMaterielInfoByBatchCode"})
    @ResponseBody
    @ApiOperation("通过batchCode读取设备基本信息")
    public Map getMaterielInfoByBatchCode(String batchCode) {
        Map<String, Object> resultMap;
        List resultList;

        try {
            resultMap = BaideApiUtil.materielStatus(batchCode);
            if (resultMap != null) {
                if (!"00000000".equals(resultMap.get("code"))) {
                    resultMap.put("data", new MaterielStockVO());
                    resultMap.put("success", 0);

                    return resultMap;
                }
                String data = (String) resultMap.get("data");
                JSONArray jsonArray = JSONArray.fromObject(data);
                resultList = (List) JSONArray.toCollection(jsonArray, MaterielStockVO.class);
                if (!ObjectUtil.isNull(resultList) && resultList.size() > 0) {
                    resultMap.put("data", resultList.get(0));
                } else {
                    resultMap.put("data", new MaterielStockVO());
                }
                resultMap.put("success", 1);

                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("读取设备基本信息失败！");
        }
        return null;
    }
}
