package com.esdemo.modules.bean;

import com.esdemo.frame.utils.MapTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.esdemo.frame.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/2.
 */
public class AccountJson2Bean {
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

    public List<Map<String,Object>> getData() {
        if (StringUtils.isBlank(this.data)){
            return null;
        }else{
            return gson.fromJson(this.data, new TypeToken<List<Map<String,Object>>>(){}.getType());
        }
    }
    public void setData(String data) {
        this.data = data;
    }

}
