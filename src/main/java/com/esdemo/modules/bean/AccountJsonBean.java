package com.esdemo.modules.bean;

import com.esdemo.frame.utils.MapTypeAdapter;
import com.google.gson.Gson;
import com.esdemo.frame.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/2.
 */
public class AccountJsonBean {
    private static Gson gson = MapTypeAdapter.newGson();
    private String msg;
    private String name;
    private boolean status;
    private String data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Data getData() {
        if (StringUtils.isBlank(this.data)){
            return null;
        }else{
            return gson.fromJson(this.data, Data.class);
        }
    }

    public void setData(String data) {
        this.data = data;
    }

    public static class Data{
        private int total;
        private List<Map<String,Object>> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<Map<String, Object>> getList() {
            return list;
        }

        public Data setList(List<Map<String, Object>> list) {
            this.list = list;
            return this;
        }
    }
}
