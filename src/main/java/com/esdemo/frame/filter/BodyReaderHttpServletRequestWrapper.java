package com.esdemo.frame.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Title：agentApi2
 * @Description：解决请求体中的流只能读取一次的问题
 * @Author：zhangly
 * @Date：2019/5/13 14:36
 * @Version：1.0
 */
@Slf4j
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    //用于将流保存下来
    private final String body;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        StringBuilder wholeStr = new StringBuilder();
        try (
                BufferedReader reader = request.getReader()
        ) {
            if (null != reader) {
                String str = "";
                while ((str = reader.readLine()) != null) {
                    //逐行读取body体里面的内容；
                    wholeStr.append(str);
                }
            }
        } catch (Exception e) {
            log.error("异常{}", e);
        }
        body = wholeStr.toString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            public boolean isFinished() {
                return false;
            }

            public boolean isReady() {
                return false;
            }

            public void setReadListener(ReadListener readListener) {
            }

            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ServletInputStream inputStream = this.getInputStream();
        return null == inputStream ? null : new BufferedReader(new InputStreamReader(inputStream));
    }

    public String getBody() {
        return this.body;
    }
}