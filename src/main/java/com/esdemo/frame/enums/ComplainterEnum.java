package com.esdemo.frame.enums;

import cn.hutool.core.bean.BeanUtil;
import com.esdemo.frame.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description： 投诉类型
 * @Author：zhangly
 * @Date：2019/6/6 16:02
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum ComplainterEnum {

    CUSTOMER("1", "对客户服务不满意"),
    SUPER("2", "对上级代理不满意"),
    OTHER("3", "其他");

    private String type;
    private String name;

    public static String getNameByType(String type) {
        if (StringUtils.isBlank(type)) {
            return "";
        }
        ComplainterEnum[] values = ComplainterEnum.values();
        for (ComplainterEnum value : values) {
            if (type.equals(value.getType())) {
                return value.getName();
            }
        }
        return "";
    }

    public static List<Map<String, Object>> getAllComplainterToMap() {
        List<Map<String, Object>> res = new ArrayList<>();
        ComplainterEnum[] values = ComplainterEnum.values();
        for (ComplainterEnum value : values) {
            res.add(BeanUtil.beanToMap(value));
        }
        return res;
    }
}
