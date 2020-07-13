package com.yimao.cloud.out.utils;


import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceWorkOrderMaterielDTO;
import com.yimao.cloud.pojo.vo.out.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 *
 *
 * @auther: liu yi
 * @date: 2019/3/18
 */
public class StockUtil {
    public static synchronized Map<String, MyInventoryCountVO> myInventory(Map<String, Object> baideResultMap, String detailParamName) {
        String baideDataStr = baideResultMap.get("data").toString();
        JSONObject baideJsonObject = JSONObject.fromObject(baideDataStr);
        JSONArray myInventoryCountArray;
        List<MyInventoryCountVO> myInventoryEntitys;
        Map result = new HashMap();
        Iterator iterator = baideJsonObject.keys();
        String key;
        String valueStr;
        while(iterator.hasNext()) {
            key = iterator.next().toString();
            valueStr = baideJsonObject.getString(key);
            myInventoryCountArray = JSONArray.fromObject(valueStr);
            myInventoryEntitys = new ArrayList();

            for(int i = 0; i < myInventoryCountArray.size(); ++i) {
                String detailStr = myInventoryCountArray.getString(i);
                MyInventoryCountVO stockVo = (MyInventoryCountVO)JsonUtil.toBean(detailStr, MyInventoryCountVO.class, detailParamName, MyInventoryVO.class);
                myInventoryEntitys.add(stockVo);
            }
            result.put(key, myInventoryEntitys);
        }

        return result;
    }

    public static synchronized List<StockInventoryCountVO> stockInventory(Map<String, Object> baideResultMap, String detailParamName) {
        String baideDataStr = baideResultMap.get("data").toString();
        JSONArray baideJsonArray = JSONArray.fromObject(baideDataStr);
        List<StockInventoryCountVO> resultList = new ArrayList();

        for(int i = 0; i < baideJsonArray.size(); ++i) {
            String detailStr = baideJsonArray.getString(i);
            StockInventoryCountVO stockVO = (StockInventoryCountVO)JsonUtil.toBean(detailStr, StockInventoryCountVO.class, detailParamName, StockInventoryVO.class);
            resultList.add(stockVO);
        }

        return resultList;
    }

    public static synchronized List<StockOtherEngineerInventoryVO> stockOtherEngineerInventory(Map<String, Object> baideResultMap, String detailParamName, String childDetailParamName) {
        String baideDataStr = baideResultMap.get("data").toString();
        JSONArray baideJsonArray = JSONArray.fromObject(baideDataStr);
        List<StockOtherEngineerInventoryVO> resultList = new ArrayList();
        List<MyInventoryCountVO> childStockList;
        MyInventoryCountVO childVo;

        for(int i = 0; i < baideJsonArray.size(); ++i) {
            String detailStr = baideJsonArray.getString(i);
            JSONObject parentObject = JSONObject.fromObject(detailStr);
            StockOtherEngineerInventoryVO stockVO = new StockOtherEngineerInventoryVO();
            stockVO.setEngineerId(parentObject.getString("engineerId"));
            stockVO.setEngineerName(parentObject.getString("engineerName"));
            stockVO.setEngineerPhone(parentObject.getString("engineerPhone"));
            String engineerStockStr = parentObject.getString("engineerStock");
            JSONArray childArray = JSONArray.fromObject(engineerStockStr);
            childStockList = new ArrayList();

            for(int j = 0; j < childArray.size(); ++j) {
                String childDetailStr = childArray.get(j).toString();
                childVo = (MyInventoryCountVO)JsonUtil.toBean(childDetailStr, MyInventoryCountVO.class, childDetailParamName, MyInventoryVO.class);
                childStockList.add(childVo);
            }

            stockVO.setEngineerStock(childStockList);
            resultList.add(stockVO);
            System.out.println();
        }

        return resultList;
    }

    public static synchronized List<MyApplyStockCountVO> applyStockInventory(Map<String, Object> baideResultMap, String detailParamName) {
        String baideDataStr = baideResultMap.get("data").toString();
        JSONArray baideJsonArray = JSONArray.fromObject(baideDataStr);
        List<MyApplyStockCountVO> resultList = new ArrayList();
        String detailStr;
        MyApplyStockCountVO applyStockEntity;

        for(int i = 0; i < baideJsonArray.size(); ++i) {
            detailStr = baideJsonArray.getString(i);
            applyStockEntity = (MyApplyStockCountVO)JsonUtil.toBean(detailStr, MyApplyStockCountVO.class, detailParamName, MyApplyStockVO.class);
            int totalCount=0;
            for(MyApplyStockVO myApplyStock:applyStockEntity.getStockList()){
                if(StringUtils.isNotBlank(myApplyStock.getRemainingNum())){
                    totalCount=totalCount+Integer.valueOf(myApplyStock.getRemainingNum());
                }
            }
            applyStockEntity.setRemainingTotal(totalCount);
            resultList.add(applyStockEntity);
        }

        return resultList;
    }

    public static WaterDeviceWorkOrderMaterielDTO getWaterDeviceFilter(String workcode, String batchCode, WorkOrderTypeEnum workOrderTypeEnum) {
        MaterielStockVO result = getBaideMaterielInfoByBatchCode(batchCode);

        return ObjectUtil.isNull(result) ? null : copyToWaterDeviceWorkOrderFilter(workcode, result, workOrderTypeEnum);
    }

    public static WaterDeviceWorkOrderMaterielDTO getWaterDeviceElectric(String workcode, JSONObject jsonObject, WorkOrderTypeEnum workOrderTypeEnum) {
        WaterDeviceWorkOrderMaterielDTO materielEntity = new WaterDeviceWorkOrderMaterielDTO();
        //materielEntity.setId(IdGenerator.localSafeId());
        materielEntity.setMaterielId(jsonObject.get("materielType").toString());
        materielEntity.setMaterielName(jsonObject.get("materielName").toString());
        materielEntity.setMaterielIndex("2");
        materielEntity.setWorkCode(workcode);
        materielEntity.setWorkOrderIndex(workOrderTypeEnum.getType());
        materielEntity.setMaterielTypeId(jsonObject.get("materielType").toString());
        materielEntity.setMaterielTypeName(jsonObject.get("materielName").toString());

        return materielEntity;
    }

    private static WaterDeviceWorkOrderMaterielDTO copyToWaterDeviceWorkOrderFilter(String workcode, MaterielStockVO MaterielStockVO, WorkOrderTypeEnum workOrderTypeEnum) {
        if (ObjectUtil.isNull(MaterielStockVO)) {
            return null;
        }

        WaterDeviceWorkOrderMaterielDTO filterEntity = new WaterDeviceWorkOrderMaterielDTO();
        //filterEntity.setId(IdGenerator.localSafeId());
        filterEntity.setMaterielId(MaterielStockVO.getId());
        filterEntity.setMaterielBatchCode(MaterielStockVO.getBatchCode());
        filterEntity.setMaterielName(MaterielStockVO.getMaterielName());
        filterEntity.setWorkOrderIndex(workOrderTypeEnum.getType());
        filterEntity.setMaterielTypeName(MaterielStockVO.getMaterielTypeName());
        filterEntity.setMaterielIndex("1");
        filterEntity.setWorkCode(workcode);

        return filterEntity;

    }

    private static MaterielStockVO getBaideMaterielInfoByBatchCode(String batchCode) {
        try {
            Map<String, Object> resultMap = BaideApiUtil.materielStatus(batchCode);
            if (resultMap != null && "00000000".equals(resultMap.get("code"))) {
                String data = (String)resultMap.get("data");
                JSONArray jsonArray = JSONArray.fromObject(data);
                List<MaterielStockVO> materielList = (List) JSONArray.toCollection(jsonArray, MaterielStockVO.class);
                if (!ObjectUtil.isNull(materielList) && materielList.size() > 0) {
                    return (MaterielStockVO)materielList.get(0);
                }

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
