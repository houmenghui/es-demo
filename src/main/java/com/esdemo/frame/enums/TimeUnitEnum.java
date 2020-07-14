package com.esdemo.frame.enums;

import com.esdemo.frame.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: zhangly
 * @create: 2020/04/16
 */
@AllArgsConstructor
@Getter
public enum TimeUnitEnum {
    DAY("day"),
    MONTH("month");

    private String unit;

    public static TimeUnitEnum getByTimeUnitCode(String unitCode) {
        if (StringUtils.isBlank(unitCode)) {
            return null;
        }
        TimeUnitEnum[] values = TimeUnitEnum.values();
        for (TimeUnitEnum value : values) {
            if (unitCode.equals(value.getUnit())) {
                return value;
            }
        }
        return null;
    }
}
