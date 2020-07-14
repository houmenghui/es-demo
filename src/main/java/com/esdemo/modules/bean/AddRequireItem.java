package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 业务产品对应的进件项信息
 * @date 2019/6/14
 */
@Data
public class AddRequireItem {
    private String dataAll;// 资料是否记录进件项内容：1-是，2-否
    private String example;//示例
    private String exampleType;//示例类型:1-图片，2-文件，3-文字
    private String photoAddress;//图片的地址
    private String itemId;// 进件要求项ID
    private String itemName;//要求项名称
    private String photo;//图片来源：1 只允许拍照，2 拍照和相册
    private String remark;// 备注
    private String checkStatus;//是否需要审核：1-是，2-否
    private String checkMsg;// 审核错误提
}
