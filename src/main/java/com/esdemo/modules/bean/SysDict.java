package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 14:21
 */
@Data
public class SysDict {
    private Integer id;
    private String sysKey;
    private String sysName;
    private String sysValue;
    private String orderNo;
    private String status;
    private String remark;
    private String type;
    private String parentId;
}
