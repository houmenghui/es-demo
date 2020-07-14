package com.esdemo.frame.enums;

import com.esdemo.frame.enums.fmc.FmConfig003;
import com.esdemo.frame.enums.fmc.FmConfig058;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-11-26 17:10
 */
@AllArgsConstructor
@Getter
public enum FunctionManageConfigEnums {
    FUNCTION_MANAGE_003("003", FmConfig003.class), // 代理商自定义费率
    FUNCTION_MANAGE_058("058", FmConfig058.class); // 代理商自定义费率管控

    private String code;
    private Class<?> jsonClass;
}
