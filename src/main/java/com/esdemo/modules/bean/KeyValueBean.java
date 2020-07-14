package com.esdemo.modules.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-22 09:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyValueBean {
    private String key;
    private Object value;
    private String description;

    public KeyValueBean(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
