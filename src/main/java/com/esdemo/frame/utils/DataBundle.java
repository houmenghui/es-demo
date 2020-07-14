package com.esdemo.frame.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DataBundle {
    private Map data = new HashMap();

    public Map getData() {
        return data;
    }

    public DataBundle bind(String key, Object val) {
        data.put(key, val);
        return this;
    }

    public <T> T get(String key) {
        return (T) data.get(key);
    }

    public Object getObject(String key) {
        return data.get(key);
    }

    private <T> T convert(String key, Class<T> tClass) {
        T val = get(key);

        if (val == null) {
            return tClass.isAssignableFrom(Boolean.class) ? (T) Boolean.FALSE : null;
        }
        if (tClass.isAssignableFrom(String.class)) {
            val = (T) String.valueOf(val);
        } else if (tClass.isAssignableFrom(Integer.class)) {
            val = (T) Integer.valueOf(val.toString());
        } else if (tClass.isAssignableFrom(Double.class)) {
            val = (T) Double.valueOf(val.toString());
        } else if (tClass.isAssignableFrom(Long.class)) {
            val = (T) Long.valueOf(val.toString());
        } else if (tClass.isAssignableFrom(Boolean.class)) {
            val = (T) Boolean.valueOf(val.toString());
        } else if (tClass.isAssignableFrom(BigDecimal.class)) {
            val = (T) new BigDecimal(val.toString());
        }
        return val;
    }

    public String getString(String key) {
        return convert(key, String.class);
    }

    public Integer getInteger(String key) {
        return convert(key, Integer.class);
    }

    public Double getDouble(String key) {
        return convert(key, Double.class);
    }

    public Long getLong(String key) {
        return convert(key, Long.class);
    }

    /**
     * 获取 boolean 类型值，如果值明确为true，则返回true，否则均返回false。为非真即假原则
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        return convert(key, Boolean.class);
    }

    public BigDecimal getBigDecimal(String key) {
        return convert(key, BigDecimal.class);
    }

    private DataBundle() {
    }

    public static DataBundle build() {
        return new DataBundle();
    }

    public static DataBundle build(Map params) {
        DataBundle dataBundle = new DataBundle();
        if(params!=null){
            dataBundle.data.putAll(params);
        }
        return dataBundle;
    }

    public DataBundle clear() {
        data.clear();
        return this;
    }

    public DataBundle remove(String key) {
        data.remove(key);
        return this;
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}