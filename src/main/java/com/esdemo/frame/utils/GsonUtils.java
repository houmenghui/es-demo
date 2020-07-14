package com.esdemo.frame.utils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by pc on 2017/10/3.
 */
@Slf4j
public final class GsonUtils {
    private static Gson gson = new Gson();

    private static List<LinkedHashTreeMap> fromJson2ListMap(String json) {
        try {
            return gson.fromJson(json, new TypeToken<List<LinkedHashTreeMap>>() {
            }.getType());
        } catch (Exception e) {
            log.error("JSON转换失败",e);
            return null;
        }
    }

    /**
     * 将json转化为list
     *
     * @param json  json字符串
     * @param clazz List的类型
     * @param <T>   List的类型
     * @return list
     */
    public static <T> List<T> fromJson2List(String json, Class<T> clazz) {
        try {
            List<T> result = new ArrayList<>();
            List<LinkedHashTreeMap> linkedHashTreeMaps = fromJson2ListMap(json);
            if (linkedHashTreeMaps == null) {
                return gson.fromJson(json, new TypeToken<List<T>>() {
                }.getType());
            }
            for (LinkedHashTreeMap map : linkedHashTreeMaps) {
                T t = gson.fromJson(gson.toJson(map), clazz);
                result.add(t);
            }
            return result;
        } catch (Exception e) {
            log.error("JSON转换失败",e);
            return null;
        }
    }

    /**
     * 将json转化为set
     *
     * @param json  json字符串
     * @param clazz set的类型
     * @param <T>   set的类型
     * @return set
     */
    public static <T> Set<T> fromJson2Set(String json, Class<T> clazz) {
        try {
            Set<T> result = new HashSet<>();
            List<LinkedHashTreeMap> linkedHashTreeMaps = fromJson2ListMap(json);
            if (linkedHashTreeMaps == null) {
                return gson.fromJson(json, new TypeToken<Set<T>>() {
                }.getType());
            }
            for (LinkedHashTreeMap map : linkedHashTreeMaps) {
                T t = gson.fromJson(gson.toJson(map), clazz);
                result.add(t);
            }
            return result;
        } catch (Exception e) {
            log.error("JSON转换失败",e);
            return null;
        }
    }

    /**
     * @param json
     * @param keyClass
     * @param valueClass
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> fromJson2Map(String json,
                                                Class<K> keyClass,
                                                Class<V> valueClass) {
        try {
            Map<Object, Object> tempMap = gson.fromJson(json, new TypeToken<Map<Object, Object>>() {
            }.getType());
            Map<K, V> resultMap = new HashMap<>();
            for (Map.Entry<Object, Object> entry : tempMap.entrySet()) {
                K k = gson.fromJson(gson.toJson(entry.getKey()), keyClass);
                V v = gson.fromJson(gson.toJson(entry.getValue()), valueClass);
                resultMap.put(k, v);
            }
            return resultMap;
        } catch (Exception e) {
            log.error("JSON转换失败",e);
            return null;
        }
    }

    /**
     * @param json
     * @param valueClass
     * @param <V>
     * @return
     */
    public static <V> Map<String, V> fromJson2Map(String json, Class<V> valueClass) {
        try {
            return fromJson2Map(json, String.class, valueClass);
        } catch (Exception e) {
            log.error("JSON转换失败",e);
            return null;
        }
    }

    /**
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T fromJson2Bean(String json, Class<T> tClass) {
        try {
            return gson.fromJson(json, tClass);
        } catch (Exception e) {
            log.error("JSON转换失败",e);
            return null;
        }
    }

    /**
     * 转化为json字符串
     *
     * @param o 对象
     * @return
     */
    public static String toJson(Object o) {
        return gson.toJson(o);
    }
}
