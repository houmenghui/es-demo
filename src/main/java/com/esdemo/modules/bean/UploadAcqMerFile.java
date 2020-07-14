package com.esdemo.modules.bean;

import com.google.protobuf.ByteString;
import lombok.Data;

/**
 * @author tgh
 * @description 收单商户进件附件
 * @date 2019/5/24
 */
@Data
public class UploadAcqMerFile {
    private String fileName;//文件名称(包含后缀)(传)
    private ByteString file;//文件内容(传)
    private String fileType;//文件类型
}
