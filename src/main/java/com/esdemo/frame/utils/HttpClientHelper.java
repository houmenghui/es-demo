package com.esdemo.frame.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * @Title：agentApi2
 * @Description：http客户端辅助类
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
@Slf4j
public class HttpClientHelper {

    // utf-8字符编码
    public static final String CHARSET_UTF_8 = "utf-8";
    // 连接管理器
    private static PoolingHttpClientConnectionManager pool;
    // 请求配置
    private static RequestConfig requestConfig;

    static {
        try {
            log.info("初始化HttpClient~~~~~~~~~~~~~~~开始");
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // 配置同时支持 HTTP 和 HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                    "http", PlainConnectionSocketFactory.getSocketFactory()).register(
                    "https", sslsf).build();
            // 初始化连接管理器
            pool = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            /*
                setDefaultMaxPerRoute() 方法来设置每一个路由的最大连接数，这里的路由是指IP + PORT，
                例如连接池大小(MaxTotal) 设置为500，路由连接数设置为200(DefaultMaxPerRoute)，
                对于www.a.com与www.b.com两个路由来说，
                发起服务的主机连接到每个路由的最大连接数（并发数）不能超过200，两个路由的总连接数不能超过500。
            */
            pool.setMaxTotal(500);
            pool.setDefaultMaxPerRoute(200);
            // 根据默认超时限制初始化requestConfig
            int socketTimeout = 50000;
            int connectTimeout = 60000;
            int connectionRequestTimeout = 10000;
            requestConfig = RequestConfig.custom().
                    //从连接池中获取可用连接的时间
                            setConnectionRequestTimeout(connectionRequestTimeout).
                    //请求获取数据的超时时间,访问一个接口指定时间内无法返回数据,直接放弃此次调用
                            setSocketTimeout(socketTimeout).
                    //连接超时时间，客户端请求服务器与服务器建立连接（三次握手）成功的最大接受时间
                            setConnectTimeout(connectTimeout).build();
            log.info("初始化HttpClient~~~~~~~~~结束");
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(pool)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                //设置共享连接池
                .setConnectionManagerShared(true)
                .build();
        return httpClient;
    }

    /**
     * 发送post请求
     *
     * @param httpPost
     * @return
     */
    public static String sendHttpPost(HttpPost httpPost) {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // 响应内容
        String responseContent = null;
        try {
            //创建默认的httpClient实例.
            httpClient = getHttpClient();
            // 配置请求信息
            httpPost.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpPost);
            // 得到响应实例
            HttpEntity entity = response.getEntity();

            // 可以获得响应头
            /*Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
            for (Header header : headers) {
                log.info(header.getName());
            }*/
            // 判断响应状态
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("HTTP Request is not success, Response code is {}", response.getStatusLine().getStatusCode() + "   " + response.getStatusLine().getReasonPhrase());
            }
            responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
            EntityUtils.consume(entity);

        } catch (Exception e) {
            log.error("异常{}", e);
        } finally {
            // 释放资源
            try{
                if(httpClient != null){
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("异常{}", e);
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error("异常{}", e);
            }
        }
        return responseContent;
    }

    /**
     * 发送get请求
     *
     * @param httpGet
     * @return
     */
    public static String sendHttpGet(HttpGet httpGet) {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // 响应内容
        String responseContent = null;
        try {
            //创建默认的httpClient实例.
            httpClient = getHttpClient();
            // 配置请求信息
            httpGet.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpGet);
            // 得到响应实例
            HttpEntity entity = response.getEntity();

            // 可以获得响应头
            /*Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
            for (Header header : headers) {
                log.info(header.getName());
            }*/
            // 判断响应状态
            if (response.getStatusLine().getStatusCode() != 200) {
                String errorMsg = response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase();
                log.error("HTTP Request is not success, Response code is", errorMsg);
            }
            responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
            EntityUtils.consume(entity);

        } catch (Exception e) {
            log.error("异常{}", e);
        } finally {
            // 释放资源
            try{
                if(httpClient != null){
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("异常{}", e);
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error("异常{}", e);
            }
        }
        return responseContent;
    }
}