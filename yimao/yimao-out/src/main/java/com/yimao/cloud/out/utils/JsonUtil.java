package com.yimao.cloud.out.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;

public class JsonUtil {
    public JsonUtil() {
    }

    public static <T> String toJSONString(List<T> list) {
        JSONArray jsonArray = JSONArray.fromObject(list);
        return jsonArray.toString();
    }

    public static String toJSONString(Object object) {
        JSONArray jsonArray = JSONArray.fromObject(object);
        return jsonArray.toString();
    }

    public static String toJSONString(JSONArray jsonArray) {
        return jsonArray.toString();
    }

    public static String toJSONString(JSONObject jsonObject) {
        return jsonObject.toString();
    }

    public static List toArrayList(Object object) {
        List arrayList = new ArrayList();
        JSONArray jsonArray = JSONArray.fromObject(object);
        Iterator it = jsonArray.iterator();

        while(it.hasNext()) {
            JSONObject jsonObject = (JSONObject)it.next();
            Iterator keys = jsonObject.keys();

            while(keys.hasNext()) {
                Object key = keys.next();
                Object value = jsonObject.get(key);
                arrayList.add(value);
            }
        }

        return arrayList;
    }

    public static Collection toCollection(Object object) {
        JSONArray jsonArray = JSONArray.fromObject(object);
        return JSONArray.toCollection(jsonArray);
    }

    public static JSONArray toJSONArray(Object object) {
        return JSONArray.fromObject(object);
    }

    public static JSONObject toJSONObject(Object object) {
        return JSONObject.fromObject(object);
    }

    public static HashMap toHashMap(Object object) {
        HashMap<String, Object> data = new HashMap();
        JSONObject jsonObject = toJSONObject(object);
        Iterator it = jsonObject.keys();

        while(it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = jsonObject.get(key);
            data.put(key, value);
        }

        return data;
    }

    public static List<Map<String, Object>> toList(Object object) {;
        List<Map<String, Object>> list = new ArrayList();
        JSONArray jsonArray = JSONArray.fromObject(object);
        Iterator var3 = jsonArray.iterator();

        while(var3.hasNext()) {
            Object obj = var3.next();
            JSONObject jsonObject = (JSONObject)obj;
            Map<String, Object> map = new HashMap();
            Iterator it = jsonObject.keys();

            while(it.hasNext()) {
                String key = (String)it.next();
                Object value = jsonObject.get(key);
                map.put(key, value);
            }

            list.add(map);
        }

        return list;
    }

    public static <T> List<T> toList(JSONArray jsonArray, Class<T> objectClass) {
        return JSONArray.toList(jsonArray, objectClass);
    }

    public static <T> List<T> toList(Object object, Class<T> objectClass) {
        JSONArray jsonArray = JSONArray.fromObject(object);
        return JSONArray.toList(jsonArray, objectClass);
    }

    public static <T> Object toBean(JSONObject jsonObject, Class<T> beanClass) {
        return JSONObject.toBean(jsonObject, beanClass);
    }

    public static <T> Object toBean(Object object, Class<T> beanClass) {
        JSONObject jsonObject = JSONObject.fromObject(object);
        return JSONObject.toBean(jsonObject, beanClass);
    }

    public static <T, D> Object toBean(String jsonString, Class<T> mainClass, String detailName, Class<D> detailClass) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        if (jsonObject.size() <= 0) {
            return null;
        } else {
            JSONArray jsonArray = (JSONArray)jsonObject.get(detailName);
            Object mainEntity = toBean(jsonObject, mainClass);
            List detailList = toList(jsonArray, detailClass);

            try {
                BeanUtils.setProperty(mainEntity, detailName, detailList);
                return mainEntity;
            } catch (Exception var9) {
                throw new RuntimeException("主从关系JSON反序列化实体失败！");
            }
        }
    }

    public static <T, D1, D2> Object toBean(String jsonString, Class<T> mainClass, String detailName1, Class<D1> detailClass1, String detailName2, Class<D2> detailClass2) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        JSONArray jsonArray1 = (JSONArray)jsonObject.get(detailName1);
        JSONArray jsonArray2 = (JSONArray)jsonObject.get(detailName2);
        Object mainEntity = toBean(jsonObject, mainClass);
        List<D1> detailList1 = toList(jsonArray1, detailClass1);
        List detailList2 = toList(jsonArray2, detailClass2);

        try {
            BeanUtils.setProperty(mainEntity, detailName1, detailList1);
            BeanUtils.setProperty(mainEntity, detailName2, detailList2);
            return mainEntity;
        } catch (Exception var13) {
            throw new RuntimeException("主从关系JSON反序列化实体失败！");
        }
    }

    public static <T, D1, D2, D3> Object toBean(String jsonString, Class<T> mainClass, String detailName1, Class<D1> detailClass1, String detailName2, Class<D2> detailClass2, String detailName3, Class<D3> detailClass3) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        JSONArray jsonArray1 = (JSONArray)jsonObject.get(detailName1);
        JSONArray jsonArray2 = (JSONArray)jsonObject.get(detailName2);
        JSONArray jsonArray3 = (JSONArray)jsonObject.get(detailName3);
        Object mainEntity = toBean(jsonObject, mainClass);
        List<D1> detailList1 = toList(jsonArray1, detailClass1);
        List<D2> detailList2 = toList(jsonArray2, detailClass2);
        List detailList3 = toList(jsonArray3, detailClass3);

        try {
            BeanUtils.setProperty(mainEntity, detailName1, detailList1);
            BeanUtils.setProperty(mainEntity, detailName2, detailList2);
            BeanUtils.setProperty(mainEntity, detailName3, detailList3);
            return mainEntity;
        } catch (Exception var17) {
            throw new RuntimeException("主从关系JSON反序列化实体失败！");
        }
    }

    public static <T> Object toBean(String jsonString, Class<T> mainClass, HashMap<String, Class> detailClass) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        Object mainEntity = toBean(jsonObject, mainClass);
        Iterator var5 = detailClass.keySet().iterator();

        while(var5.hasNext()) {
            Object key = var5.next();

            try {
                Class value = (Class)detailClass.get(key);
                BeanUtils.setProperty(mainEntity, key.toString(), value);
            } catch (Exception var8) {
                throw new RuntimeException("主从关系JSON反序列化实体失败！");
            }
        }

        return mainEntity;
    }
}
