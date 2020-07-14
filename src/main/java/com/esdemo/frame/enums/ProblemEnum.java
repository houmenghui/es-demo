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
 * @Description： 问题类型
 * @Author：zhangly
 * @Date：2019/6/6 16:02
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum ProblemEnum {

    FUN("4", "功能异常"),
    PROFIT("5", "分润结算"),
    PRODUCT("6", "产品建议"),
    COMPLAINTER("7", "投诉"),
    CANCEL("8", "注销");

    private String type;
    private String name;

    public static String getNameByType(String type) {
        if (StringUtils.isBlank(type)) {
            return "";
        }
        ProblemEnum[] values = ProblemEnum.values();
        for (ProblemEnum value : values) {
            if (type.equals(value.getType())) {
                return value.getName();
            }
        }
        return "";
    }

    public static List<Map<String, Object>> getAllProblemToMap() {
        List<Map<String, Object>> res = new ArrayList<>();
        ProblemEnum[] values = ProblemEnum.values();
        for (ProblemEnum value : values) {
            res.add(BeanUtil.beanToMap(value));
        }
        return res;
    }
}
