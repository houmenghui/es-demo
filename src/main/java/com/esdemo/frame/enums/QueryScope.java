package com.esdemo.frame.enums;

import com.esdemo.frame.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：查询范围
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum QueryScope {

    ALL("ALL"), //全部
    OFFICAL("OFFICAL"), //直属
    CHILDREN("CHILDREN");  //下级

    private String scopeCode;

    public static QueryScope getByScopeCode(String scopeCode) {
        if (StringUtils.isBlank(scopeCode)) {
            return ALL;
        }
        QueryScope[] values = QueryScope.values();
        for (QueryScope value : values) {
            if (scopeCode.equals(value.getScopeCode())) {
                return value;
            }
        }
        return ALL;
    }

}