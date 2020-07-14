package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description
 * @date 2019/5/24
 */
@Data
public class AcqMerFileInfo {
    private String id ;
    private String create_time ;//创建时间
    private String file_type ;//文件类型
    private String file_url ;//文件地址
    private String status ;//文件状态 1 正常 2失效
    private String acq_into_no ;//进件编号
    private String audit_status; //'审核状态 1.待审核 2.审核通过 3 审核不通过',
}
