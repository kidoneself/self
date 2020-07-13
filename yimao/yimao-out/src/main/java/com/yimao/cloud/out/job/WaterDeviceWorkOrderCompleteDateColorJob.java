package com.yimao.cloud.out.job;


import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 功能描述: 工单颜色调度
 *
 * @auther: liu yi
 * @date: 2019/3/29 10:41
 */
@Slf4j
@Component
@EnableScheduling
public class WaterDeviceWorkOrderCompleteDateColorJob {
    private static Map<String, Map<String, Object>> colorConfigMap = new HashMap<>();
    private static Map<String, Integer> completeDateConfigMap = new HashMap<>();
    private static boolean isUpdateInstall = true;
    private static boolean isUpdateMaintenance = true;
    private static List<String> afureTypeIndexList = new ArrayList<>();
    private static List<String> bfureTypeIndexList = new ArrayList<>();
    //true:读取配置失败
    private static boolean loadConfigResult = false;
    //最后执行时间
    private static long lastExecuteTime = 0L;
    private static Map<String, Map<String, Object>> fureTypeInfo = new HashMap<>();

    @Resource
    private OrderFeign orderFeign;

    //每隔2小时
    @Scheduled(cron = "0 0 0/2 * * ? ")
    public void executer() {
        log.info("更新一波百得工单的最晚完成时间&颜色配置", new Object[0]);
        initBaideColorConfig();
        if (loadConfigResult) {
            if (isUpdateInstall) {
                try {
                    orderFeign.updateLasteFinishTimeInstall((Integer) completeDateConfigMap.get(WorkOrderTypeEnum.ORDER_TYPE_INSTALL.getColorConfigWorkOrderTypeIndex()));
                    log.info("执行更新安装工单最晚完成的时间成功", new Object[0]);
                } catch (Exception var14) {
                    log.info("更新安装工单最晚完成时间失败了....", new Object[0]);
                } finally {
                    isUpdateInstall = false;
                }
            }

            if (isUpdateMaintenance) {
                try {
                    orderFeign.updateLasteFinishTime((Integer) completeDateConfigMap.get(WorkOrderTypeEnum.ORDER_TYPE_INSTALL.getColorConfigWorkOrderTypeIndex()));
                    log.info("执行更新维护工单最晚完成的时间成功", new Object[0]);
                } catch (Exception var12) {
                    log.info("更新维护工单最晚完成时间失败了....", new Object[0]);
                } finally {
                    isUpdateMaintenance = false;
                }
            }
        }
    }

    private static synchronized void initBaideColorConfig() {
        Map map;
        try {
            map = BaideApiUtil.workOrderDecisionRule();
            if (ObjectUtil.isNull(map) || map.size() <= 0) {
                log.info("百得工单颜色配置读取失败,获取的数据：{}", map.toString());
                return;
            }
        } catch (Exception e) {
            log.info("读取百得工单颜色配置失败了,异常信息：{}", e.getMessage());
            return;
        }

        if (map.containsKey("code") && !"00000000".equals(map.get("code").toString())) {
            log.info("百得工单颜色配置读取失败：{}", map.toString());
        } else {
            if (map.containsKey("data")) {
                String colorConfigStr = map.get("data").toString();
                log.info("百得颜色配置信息：{}", map.toString());
                startBaideColorConfig(colorConfigStr);
                loadConfigResult = true;
                lastExecuteTime = System.currentTimeMillis();
                log.info("百得工单颜色配置完成");
            }
        }
    }

    public static Map getDateInfo(String workOrderType, Date workOrderTime, boolean isBespeak, String faultTypeIds) {
        log.info("获取工单最晚完成时限,颜色", new Object[]{"workOrderType:" + workOrderType + " ,workorderTime:" + workOrderTime + ", isBespeak:" + isBespeak + " ,faulttypeIds:" + faultTypeIds});
        Map<String, Object> resultMap = new HashMap();
        if (!loadConfigResult) {
            if (lastExecuteTime != 0L && System.currentTimeMillis() - lastExecuteTime < 1200000L) {
                return resultMap;
            }

            initBaideColorConfig();
            if (!loadConfigResult) {
                return resultMap;
            }
        }

        Calendar startTimeCa = Calendar.getInstance();
        Date startTime = new Date();
        Date endTime = null;
        WorkOrderTypeEnum workOrderTypeEnum = WorkOrderTypeEnum.getByType(workOrderType);
        if (!isBespeak) {
            endTime = getBaideCompleteDateConfig(workOrderTypeEnum, workOrderTime, faultTypeIds);
        } else {
            endTime = workOrderTime;
        }

        try {
            Long seconds = DateUtil.getSeconds(startTime, endTime);
            resultMap.put("dateStr", DateUtil.getDateStr(startTime, endTime));
            resultMap.put("timeStamp", seconds);
            resultMap.put("lastCompleteTime", endTime.getTime());
            resultMap.put("color", getBaideColorConfig(workOrderTypeEnum, startTime, endTime));
        } catch (NullPointerException var11) {
            log.info("工单最晚时限配置出错啦", new Object[]{"startTime:" + startTime + " ,endTime:" + endTime});
            var11.printStackTrace();
        }

        return resultMap;
    }

    public static void startBaideColorConfig(String colorConfigStr) {
        if (StringUtil.isEmpty(colorConfigStr)) {
            log.info("颜色配置为空,不进行操作", new Object[0]);
        } else {
            try {
                JSONArray colorConfigList = JSONArray.fromObject(colorConfigStr);

                for (int i = 0; i < colorConfigList.size(); ++i) {
                    JSONObject jsonObject = (JSONObject) colorConfigList.get(i);
                    String id = jsonObject.get("ID").toString();
                    colorConfigMap.put(id, jsonObject);
                    int completeHour = Integer.parseInt(jsonObject.getString("workorderTimeThreshold"));
                    checkUpdateWorkOrder(id, completeHour);
                    completeDateConfigMap.put(id, completeHour);
                    if (!StringUtils.isEmpty(id) && "2".equals(id)) {
                        setFureTypeInfo(jsonObject);
                    }
                }
            } catch (Exception var6) {
                log.info("解析颜色配置失败", new Object[]{colorConfigStr});
            }
        }
    }

    public static void checkUpdateWorkOrder(String index, int completeHour) {
        WorkOrderTypeEnum workOrderTypeEnum = WorkOrderTypeEnum.getByType(index);
        Integer prevHour = null;
        if (!ObjectUtil.isNull(workOrderTypeEnum)) {
            switch (workOrderTypeEnum) {
                case ORDER_TYPE_INSTALL:
                    prevHour = (Integer) completeDateConfigMap.get(index);
                    if (!ObjectUtil.isNull(prevHour) && prevHour != completeHour) {
                        isUpdateInstall = true;
                    }
                    break;
                case ORDER_TYPE_MAINTENANCE:
                    prevHour = (Integer) completeDateConfigMap.get(index);
                    if (!ObjectUtil.isNull(prevHour) && prevHour != completeHour) {
                        isUpdateMaintenance = true;
                    }
            }
        }

    }

    private static void setFureTypeInfo(JSONObject jsonObject) {
        String repairTypeStr = jsonObject.getString("children");
        JSONArray repairTypeArray = JSONArray.fromObject(repairTypeStr);
        String childID = null;
        String fureTypeIndex = null;

        for (int j = 0; j < repairTypeArray.size(); ++j) {
            JSONObject repairTypeMap = (JSONObject) repairTypeArray.get(j);
            childID = repairTypeMap.getString("ID");
            fureTypeIndex = repairTypeMap.getString("faultType");
            fureTypeInfo.put(fureTypeIndex, repairTypeMap);
            if ("7".equals(childID)) {
                afureTypeIndexList.add(fureTypeIndex);
            } else {
                bfureTypeIndexList.add(fureTypeIndex);
            }
        }

    }

    private static String getBaideColorConfig(WorkOrderTypeEnum workOrderTypeEnum, Date startTime, Date endTime) {
        String workOrderTypeIndex = workOrderTypeEnum.getColorConfigWorkOrderTypeIndex();
        Map map = (Map) colorConfigMap.get(workOrderTypeIndex);
        return !ObjectUtil.isNull(map) && map.size() > 0 ? getBaideColorInfo(map, startTime, endTime) : "";
    }

    private static Date getBaideCompleteDateConfig(WorkOrderTypeEnum workOrderTypeEnum, Date createTime, String faultTypeIds) {
        int hours = 0;
        boolean isNotABFault = true;
        String baideIndex;
        if (WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType().equals(workOrderTypeEnum.getType()) && !StringUtil.isEmpty(faultTypeIds)) {
            baideIndex = checkAAndB(faultTypeIds);
            if (!ObjectUtil.isNull(baideIndex)) {
                isNotABFault = false;
                hours += Integer.parseInt(((Map) fureTypeInfo.get(baideIndex)).get("workorderTimeThreshold").toString());
            }
        }

        if (isNotABFault) {
            baideIndex = workOrderTypeEnum.getColorConfigWorkOrderTypeIndex();
            hours += (Integer) completeDateConfigMap.get(baideIndex);
        }

        Calendar ca = Calendar.getInstance();
        ca.setTime(createTime);
        ca.add(11, hours);
        return ca.getTime();
    }

    private static String checkAAndB(String faultTypeIds) {
        String[] faultTypeIdArr = faultTypeIds.split(",");
        String cacheFault = null;
        boolean isA = false;
        Iterator var4 = afureTypeIndexList.iterator();

        while (true) {
            String fureTypeIdIndex;
            int i;
            while (var4.hasNext()) {
                fureTypeIdIndex = (String) var4.next();

                for (i = 0; i < faultTypeIdArr.length; ++i) {
                    if (fureTypeIdIndex.contains(faultTypeIdArr[i])) {
                        cacheFault = fureTypeIdIndex;
                        isA = true;
                        break;
                    }
                }
            }

            if (!isA) {
                var4 = bfureTypeIndexList.iterator();

                while (true) {
                    while (var4.hasNext()) {
                        fureTypeIdIndex = (String) var4.next();

                        for (i = 0; i < faultTypeIdArr.length; ++i) {
                            if (fureTypeIdIndex.contains(faultTypeIdArr[i])) {
                                cacheFault = fureTypeIdIndex;
                                break;
                            }
                        }
                    }

                    return cacheFault;
                }
            }

            return cacheFault;
        }
    }

    private static String getBaideColorInfo(Map colorConfigMap, Date startTime, Date endTime) {
        long seconds = DateUtil.getSeconds(startTime, endTime);
        String value = getValue(colorConfigMap, seconds);
        String color = null;
        if (value.equals("middle")) {
            color = colorConfigMap.get("colorMiddle").toString();
        } else if (value.equals("max")) {
            color = colorConfigMap.get("colorMax").toString();
        } else if (value.equals("min")) {
            color = colorConfigMap.get("colorMin").toString();
        }

        return color;
    }

    private static String getValue(Map colorConfigMap, long seconds) {
        int colorMaxthresholdEnd = Integer.parseInt(colorConfigMap.get("colorMaxthresholdEnd").toString());
        int colorMaxthresholdStart = Integer.parseInt(colorConfigMap.get("colorMaxthresholdStart").toString());
        int colorMiddlethresholdEnd = Integer.parseInt(colorConfigMap.get("colorMiddlethresholdEnd").toString());
        int colorMiddlethresholdStart = Integer.parseInt(colorConfigMap.get("colorMiddlethresholdStart").toString());
        int colorMinthresholdEnd = Integer.parseInt(colorConfigMap.get("colorMinthresholdEnd").toString());
        int colorMinthresholdStart = Integer.parseInt(colorConfigMap.get("colorMinthresholdStart").toString());
        if (checkDate(colorMaxthresholdStart, seconds)) {
            return "max";
        } else {
            return checkDate(colorMiddlethresholdStart, seconds) ? "middle" : "min";
        }
    }

    private static boolean checkDate(int hours, long seconds) {
        return seconds > (long) (hours * 60 * 60);
    }
}
