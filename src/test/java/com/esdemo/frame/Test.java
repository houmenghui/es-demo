package com.esdemo.frame;

import com.esdemo.frame.utils.RSAUtils;
import com.esdemo.frame.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-16 17:52
 */
public class Test {
    @org.junit.Test
    public void test() throws UnsupportedEncodingException {
        String utf8 = URLDecoder.decode("{\"appName\":\"%E7%9B%9B%E4%BB%A3%E5%AE%9D\",\"clientVersion\":\"1.0\",\"device_id\":\"[JPUSHService registrationID]?:@\",\"mbsc_phoneOS\":\"1\",\"platform\":\"ios\",\"sign\":\"B753BB428AB15A6ABCC9797DEF9B39E3\"}", "UTF8");
        System.out.println(utf8);
    }

    @org.junit.Test
    public void test2() throws Exception {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJ9s1qlOyv9qpuaTqauW6fUftzE50rVk3yVPZwv1aO1Ch/XSEz76xCwkyvqpaqceRXrPpdBmO5+ruJ+I8osOHo7L5GWEOcMOO+8izp9hXKBBrmRMD4Egpn00k9DhVIEKp/vyddZPS/doxB8onhN6poTJDLdFLFVEicMf52caN9GQIDAQAB";
        byte[] bytes = RSAUtils.encryptByPublicKey("abc88".getBytes(), publicKey);
        System.out.println(Base64.encodeBase64String(bytes));
    }

    @org.junit.Test
    public void test3() throws IOException {
        Connection.Response execute = Jsoup.connect("http://192.168.3.172:3306").ignoreContentType(true).execute();
        System.out.println(execute.body());
    }
    @org.junit.Test
    public  void test4(){
        List<Map<String, Object>> list2 = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("X","1");
        map.put("Y","2");
        list2.add(map);

        Map<String,Object> map1 = new HashMap<>();
        map1.put("X","3");
        map1.put("Y","4");
        list2.add(map1);

        Map<String,Object> map2 = new HashMap<>();
        map2.put("X","5");
        map2.put("Y","6");
        list2.add(map2);


        Map<String,String> dataMap = list2.stream().collect(Collectors.toMap((item) -> StringUtils.filterNull(item.get("X")), (item) -> StringUtils.filterNull(item.get("Y"))));
        System.out.println(dataMap);

    }
}
