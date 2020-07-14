package com.esdemo.frame;

import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.md5.Md5;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-08-08 15:39
 */
public class TestEsDataMigrate {

    @Test
    public void test() throws IOException {
        String merchantNo = "258121004516026";
        String oldAgent = "1359791";
        String timestamp = System.currentTimeMillis() +"";
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("sign", Md5.md5Str("timestamp=" + timestamp + "&key=46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty"));
        String body = Jsoup.connect("http://agentapi2.sqianbao.cn/agentApi2/esDataMigrate/merchantMigrate/" + merchantNo + "/" + oldAgent)
                .header("app-info", GsonUtils.toJson(map))
                .ignoreContentType(true)
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    public void test1() throws IOException {
        String merchantNo = "258121004455246";
        String oldAgent = "1085492";
        String timestamp = System.currentTimeMillis() +"";
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("sign", Md5.md5Str("timestamp=" + timestamp + "&key=46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty"));
        String body = Jsoup.connect("http://agentapi2.sqianbao.cn/agentApi2/esDataMigrate/merchantMigrate/" + merchantNo + "/" + oldAgent)
                .header("app-info", GsonUtils.toJson(map))
                .ignoreContentType(true)
                .execute()
                .body();
        System.out.println(body);
    }


    @Test
    public void changeMerchantProducts2() throws IOException {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        FileUtils.readLines(new File("H:\\workCode\\agentApi2\\src\\test\\resources\\changeMerchantProducts.txt"), "utf8")
                .forEach(item -> {

                    System.out.println(String.format("正在处理第 %d 个, 商户号为 %s", atomicInteger.incrementAndGet(), item));
                    String timestamp = System.currentTimeMillis() +"";
                    Map<String, String> map = new HashMap<>();
                    map.put("timestamp", timestamp);
                    map.put("sign", Md5.md5Str("timestamp=" + timestamp + "&key=46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty"));
                    try {
                        String body = Jsoup.connect("http://agentapi2.sqianbao.cn/agentApi2/esDataMigrate/changeMerchantProducts/" + item)
                                .header("app-info", GsonUtils.toJson(map))
                                .ignoreContentType(true)
                                .execute()
                                .body();
                        System.out.println(body);
                        Thread.sleep(150);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }


    @Test
    public void merchantMigrate4File() throws IOException {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        FileUtils.readLines(new File("H:\\workCode\\agentApi2\\src\\test\\resources\\merchant.txt"), "utf8")
                .forEach(item -> {
                    String[] split = item.split("\\s+");
                    String merchantNo = split[0];
                    String oldAgentNo = split[1];
                    System.out.println(String.format("正在处理第 %d 个, 商户号为 %s, 代理商编号 %s", atomicInteger.incrementAndGet(), merchantNo, oldAgentNo));
                    String timestamp = System.currentTimeMillis() +"";
                    Map<String, String> map = new HashMap<>();
                    map.put("timestamp", timestamp);
                    map.put("sign", Md5.md5Str("timestamp=" + timestamp + "&key=46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty"));
                    try {
                        String body = Jsoup.connect("http://agentapi2.sqianbao.cn/agentApi2/esDataMigrate/merchantMigrate/" + merchantNo + "/" + oldAgentNo)
                                .header("app-info", GsonUtils.toJson(map))
                                .ignoreContentType(true)
                                .execute()
                                .body();
                        System.out.println(body);
                        Thread.sleep(150);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }




    @Test
    public void changeMerchantProducts() throws IOException {
        String merchantNo = "258121000001248";
        String timestamp = System.currentTimeMillis() +"";
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("sign", Md5.md5Str("timestamp=" + timestamp + "&key=46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty"));
        String body = Jsoup.connect("http://192.168.1.145:9000/agentApi2/esDataMigrate/changeMerchantProducts/" + merchantNo)
                .header("app-info", GsonUtils.toJson(map))
                .ignoreContentType(true)
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    public void agentMigrate() throws IOException {
        String migrateAgentNode = "0-1461-1463-1464-";
        String newParentId = "1471";
        String timestamp = System.currentTimeMillis() +"";
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("sign", Md5.md5Str("timestamp=" + timestamp + "&key=46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty"));
        String body = Jsoup.connect("http://192.168.1.145:9000/agentApi2/esDataMigrate/agentMigrate/" + migrateAgentNode + "/" + newParentId)
                .header("app-info", GsonUtils.toJson(map))
                .ignoreContentType(true)
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    public void testPaser() throws IOException {
        File file = new File("C:\\Users\\666666\\Desktop\\test1.txt");
        String utf8 = FileUtils.readFileToString(file, "utf8");
        List<InnerData> s = GsonUtils.fromJson2List(utf8, InnerData.class);
        s.forEach(item -> System.out.println(item.get_source().getOrder_no() + "\t" + item.get_source().getAgent_node()));
    }

    @Data
    public static class InnerData{
        private String _index;
        private String _type;
        private String _id;
        private String _score;
        private String _routing;
        private Source _source;
    }
    @Data
    public static class Source{
        private String order_no;
        private String agent_node;
    }
}
