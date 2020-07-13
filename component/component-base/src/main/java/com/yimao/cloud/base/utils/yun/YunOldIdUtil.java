package com.yimao.cloud.base.utils.yun;

import com.yimao.cloud.base.enums.BaideConsumableTypeIdEnum;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：云平台系统老的产品型号相关ID映射关系
 *
 * @Author Zhang Bo
 * @Date 2019/10/20
 */
@Slf4j
public class YunOldIdUtil {

    private static final String MODEL_1601T = "1601T";
    private static final String MODEL_1602T = "1602T";
    private static final String MODEL_1603T = "1603T";
    private static final String MODEL_1601L = "1601L";

    public static final List<String> DEVICE_MODEL = new ArrayList<>();

    static {
        DEVICE_MODEL.add(MODEL_1601T);
        DEVICE_MODEL.add(MODEL_1602T);
        DEVICE_MODEL.add(MODEL_1603T);
        DEVICE_MODEL.add(MODEL_1601L);
    }

    /**
     * 获取原云平台products表的ID
     */
    public static String getProductTypeId() {
        return "58a2d5900cf253250cfbbc68";
    }

    /**
     * 获取原云平台products表的NAME
     */
    public static String getProductTypeName() {
        return "净水设备";
    }

    /**
     * 根据产品型号获取原云平台waterdevice表的ID
     *
     * @param deviceModel 水机型号
     */
    public static String getProductId(String deviceModel) {
        if (MODEL_1601T.equalsIgnoreCase(deviceModel)) {
            return "56f4fa3922d91e04568b58b1";
        } else if (MODEL_1602T.equalsIgnoreCase(deviceModel)) {
            return "58c004266c8c1f2c148786f1";
        } else if (MODEL_1603T.equalsIgnoreCase(deviceModel)) {
            return "58c004566c8c1f2c148786f3";
        } else if (MODEL_1601L.equalsIgnoreCase(deviceModel)) {
            return "5715caeb22d91e5b4b336dd3";
        } else {
            return null;
        }
    }

    /**
     * 根据产品型号获取原云平台waterdevice表的NAME
     *
     * @param deviceModel 水机型号
     */
    public static String getProductName(String deviceModel) {
        if (MODEL_1601T.equalsIgnoreCase(deviceModel)) {
            return "家用型1型";
        } else if (MODEL_1602T.equalsIgnoreCase(deviceModel)) {
            return "家用型2型";
        } else if (MODEL_1603T.equalsIgnoreCase(deviceModel)) {
            return "家用型3型";
        } else if (MODEL_1601L.equalsIgnoreCase(deviceModel)) {
            return "商用型1型";
        } else {
            return null;
        }
    }

    /**
     * 根据产品型号获取原云平台productsmodel表的ID
     *
     * @param deviceModel 水机型号
     */
    public static String getProductModelId(String deviceModel) {
        if (MODEL_1601T.equalsIgnoreCase(deviceModel)) {
            return "58a2d5d10cf253250cfbbc69";
        } else if (MODEL_1602T.equalsIgnoreCase(deviceModel)) {
            return "58a2d5e10cf253250cfbbc6a";
        } else if (MODEL_1603T.equalsIgnoreCase(deviceModel)) {
            return "58a2d5f30cf253250cfbbc6b";
        } else if (MODEL_1601L.equalsIgnoreCase(deviceModel)) {
            return "58a2d6050cf253250cfbbc6c";
        } else {
            return null;
        }
    }

    /**
     * 根据产品型号获取原云平台对应的产品范围
     *
     * @param deviceModel 水机型号
     */
    public static String getProductScope(String deviceModel) {
        if (MODEL_1601T.equalsIgnoreCase(deviceModel) || MODEL_1602T.equalsIgnoreCase(deviceModel) || MODEL_1603T.equalsIgnoreCase(deviceModel)) {
            return "家用";
        } else if (MODEL_1601L.equalsIgnoreCase(deviceModel)) {
            return "商用";
        } else {
            return null;
        }
    }

    /**
     * 根据产品型号获取原云平台对应的产品范围
     *
     * @param id ScopeId
     */
    public static String getProductScopeById(String id) {
        if ("58a2d7540cf253250cfbbc6d".equalsIgnoreCase(id)) {
            return "家用";
        } else if ("58a2d7590cf253250cfbbc6e".equalsIgnoreCase(id)) {
            return "商用";
        } else {
            return null;
        }
    }

    /**
     * 根据产品型号获取原云平台对应的productsscope表的ID
     *
     * @param deviceModel 水机型号
     */
    public static String getProductScopeId(String deviceModel) {
        if (MODEL_1601T.equalsIgnoreCase(deviceModel) || MODEL_1602T.equalsIgnoreCase(deviceModel) || MODEL_1603T.equalsIgnoreCase(deviceModel)) {
            return "58a2d7540cf253250cfbbc6d";
        } else if (MODEL_1601L.equalsIgnoreCase(deviceModel)) {
            return "58a2d7590cf253250cfbbc6e";
        } else {
            return null;
        }
    }

    /**
     * 更根据原云平台的产品范围id获取产品型号
     *
     * @param scopeId
     */
    public static List<String> getDeviceModel(String scopeId) {
        List<String> DeviceModelList = new ArrayList<>();
        if ("58a2d7540cf253250cfbbc6d".equals(scopeId)) {
            DeviceModelList.add(MODEL_1601T);
            DeviceModelList.add(MODEL_1602T);
            DeviceModelList.add(MODEL_1603T);
        } else if ("58a2d7590cf253250cfbbc6e".equals(scopeId)) {
            DeviceModelList.add(MODEL_1601L);
        }
        return DeviceModelList;

    }

    /***
     * 功能描述:对应上百得的id
     *
     * @param: [filterList]
     * @auther: liu yi
     * @date: 2019/5/31 9:52
     * @return: java.util.List<com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO>
     */
    public static Map<String,String> getBaideFilterMap(List<String> filterList) {
        Map<String,String> consumableTypeMap = new HashMap<>();
        String baideTypeId;
        for (String filterName : filterList) {
            if (BaideConsumableTypeIdEnum.FILTER_UDF.name.equalsIgnoreCase(filterName)) {
                baideTypeId = BaideConsumableTypeIdEnum.FILTER_UDF.value;
            } else if (BaideConsumableTypeIdEnum.FILTER_T33.name.equalsIgnoreCase(filterName)) {
                baideTypeId = BaideConsumableTypeIdEnum.FILTER_T33.value;
            } else if (BaideConsumableTypeIdEnum.FILTER_CTO.name.equalsIgnoreCase(filterName)) {
                baideTypeId = BaideConsumableTypeIdEnum.FILTER_CTO.value;
            } else if (BaideConsumableTypeIdEnum.FILTER_PP.name.equalsIgnoreCase(filterName)) {
                baideTypeId = BaideConsumableTypeIdEnum.FILTER_PP.value;
            } else if (BaideConsumableTypeIdEnum.FILTER_RO.name.equalsIgnoreCase(filterName)) {
                baideTypeId = BaideConsumableTypeIdEnum.FILTER_RO.value;
            } else {
                log.error("耗材百得无对应filterName:" + filterName);
                continue;
            }
            consumableTypeMap.put(baideTypeId,filterName);
        }

        return consumableTypeMap;
    }

}
