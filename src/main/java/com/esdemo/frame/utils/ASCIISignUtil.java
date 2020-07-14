package com.esdemo.frame.utils;

import com.esdemo.frame.utils.md5.Md5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

public class ASCIISignUtil {

    private static final Logger log = LoggerFactory.getLogger(ASCIISignUtil.class.getName());

    /**
     *
     * @param params
     *            参数集合
     * @param signKey
     *            参与签名秘钥
     * @return sign
     */
    public final static String sortASCIISign(Map<String, String> params, String signKey) {
        StringBuffer signStr = new StringBuffer();
        // 所有参与传参的参数按照ASCII排序（升序）
        TreeMap<String, Object> tempMap = new TreeMap<String, Object>();
        for (String key : params.keySet()) {
            if (StringUtils.isNotBlank(StringUtils.filterNull(params.get(key)))) {
                tempMap.put(key, params.get(key));
            }
        }
        for (String key : tempMap.keySet()) {
            signStr.append(key).append("=").append(tempMap.get(key)).append("&");
        }
        signStr = signStr.append("key=").append(signKey);
        log.info("签名串：{}",signStr.toString());
        // MD5加密,结果转换为大写字符
        String sign = Md5.MD5Encode(signStr.toString()).toUpperCase();
        return sign;
    }
}
