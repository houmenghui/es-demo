package com.esdemo.frame.utils;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Title：agentApi2
 * @Description：http实现
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
@Slf4j
public class HttpUtils {

    // utf-8字符编码
    public static final String CHARSET_UTF_8 = "utf-8";
    // HTTP内容类型。
    public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";

    // HTTP内容类型。相当于form表单的形式，提交数据
    public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

    // HTTP内容类型。相当于form表单的形式，提交数据
    public static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";

    /**
     * 发送get请求
     *
     * @param httpUrl
     * @param params
     * @return
     */
    public static String doGet(String httpUrl, Map<String, String> params, Map<String, String> headers) {
        String res = "";
        try {
            URIBuilder builder = new URIBuilder(httpUrl);
            if (!CollectionUtils.isEmpty(params)) {
                Set<String> set = params.keySet();
                List<NameValuePair> listPair = new LinkedList<>();
                for (String key : set) {
                    BasicNameValuePair param = new BasicNameValuePair(key, params.get(key));
                    listPair.add(param);
                }
                builder.setParameters(listPair);
            }
            HttpGet httpGet = new HttpGet(builder.build());
            //设置请求头
            if (!CollectionUtils.isEmpty(headers)) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpGet.setHeader(header.getKey(), header.getValue());
                }
            }
            log.info("======================================发送GET请求URL：{}", httpUrl);
            log.info("======================================发送GET请求参数：{}", JSONUtil.toJsonStr(params));
            log.info("======================================发送GET请求头：{}", CollectionUtils.isEmpty(headers) ? "" : JSONUtil.toJsonStr(headers));

            res = HttpClientHelper.sendHttpGet(httpGet);
            log.info("======================================发送GET响应信息：{}", res);
        } catch (Exception e) {
            log.error("异常{}", e);
            log.info("发送HttpGet请求异常{}", e);
        }
        return res;
    }

    /**
     * 发送post请求
     *
     * @param httpUrl 请求地址
     * @param headers 请求头
     * @param params  参数格式:
     *                非json (key1=value1&key2=value2)
     * @param isJson  是否json格式
     * @return
     */
    public static String doPost(String httpUrl, Map<String, String> headers, String params, boolean isJson) {
        String res = "";
        try {
            // 创建httpPost
            HttpPost httpPost = new HttpPost(httpUrl);
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, CHARSET_UTF_8);
                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                if (isJson) {
                    stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
                }
                httpPost.setEntity(stringEntity);
            }
            //设置请求头
            if (!CollectionUtils.isEmpty(headers)) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpPost.setHeader(header.getKey(), header.getValue());
                }
            }
            log.info("======================================发送POST请求URL：{}", httpUrl);
            log.info("======================================发送POST请求参数：{}", params);
            log.info("======================================发送POST请求头：{}", CollectionUtils.isEmpty(headers) ? "" : JSONUtil.toJsonStr(headers));
            res = HttpClientHelper.sendHttpPost(httpPost);
            log.info("======================================发送POST响应信息：{}", res);
        } catch (Exception e) {
            log.error("异常{}", e);
            log.info("发送HttpPost请求异常{}", e);
        }
        return res;
    }

    /**
     * 发送post请求
     *
     * @param httpUrl 请求地址
     * @param params  参数格式:
     *                非json (key1=value1&key2=value2)
     * @param isJson  是否json格式
     * @return
     */
    public static String doPost(String httpUrl, String params, boolean isJson) {
        return doPost(httpUrl, null, params, isJson);
    }

    /**
     * 发送 post请求（带文件）
     *
     * @param httpUrl    地址
     * @param strParams  常规参数
     * @param fileParams 附件
     */
    public static String doMultipartPost(String httpUrl, Map<String, String> strParams, Map<String, File> fileParams) {
        String res = "";
        try {

            // 创建httpPost
            HttpPost httpPost = new HttpPost(httpUrl);
            MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
            if (!CollectionUtils.isEmpty(strParams)) {
                for (String key : strParams.keySet()) {
                    meBuilder.addPart(key, new StringBody(strParams.get(key), ContentType.TEXT_PLAIN));
                }
            }
            if (!CollectionUtils.isEmpty(fileParams)) {
                for (String key : fileParams.keySet()) {
                    FileBody fileBody = new FileBody(fileParams.get(key));
                    meBuilder.addPart(key, fileBody);
                }
            }
            log.info("======================================发送Multipart POST请求URL：{}", httpUrl);
            log.info("======================================发送Multipart POST请求常规参数：{}", JSONUtil.toJsonStr(strParams));
            log.info("======================================发送Multipart POST请求附件参数：{}", JSONUtil.toJsonStr(fileParams));
            HttpEntity reqEntity = meBuilder.build();
            httpPost.setEntity(reqEntity);
            res = HttpClientHelper.sendHttpPost(httpPost);
            log.info("======================================发送Multipart POST响应信息：{}", res);

        } catch (Exception e) {
            log.error("异常{}", e);
            log.info("发送带附件的HttpPost请求异常{}", e);
        }
        return res;
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl
     * @param paramsMap
     * @return
     */
    public static String doPost(String httpUrl, Map<String, String> headers, Map<String, String> paramsMap, boolean isJson) {
        String params = WebUtils.buildSignSrc(false, paramsMap, null);
        if (isJson) {
            params = JSONUtil.toJsonStr(paramsMap);
        }
        return doPost(httpUrl, headers, params, isJson);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl
     * @param paramsMap
     * @return
     */
    public static String doPost(String httpUrl, Map<String, String> paramsMap, boolean isJson) {
        return doPost(httpUrl, null, paramsMap, isJson);
    }
}
