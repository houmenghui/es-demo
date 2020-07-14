package com.esdemo.modules.service.impl;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.esdemo.frame.utils.ClientInterface;
import com.esdemo.frame.utils.DESPlus;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.modules.service.OpenPlatformService;
import com.esdemo.modules.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2018/1/12/012.
 */
@Service
@Slf4j
public class OpenPlatformServiceImpl implements OpenPlatformService {

    private static final String cardAuthUrl = "/card/receive?";
    /**
     * DES 加密密钥
     */
    private static final String desKey = "79FA5FC67215457F8F832CA3C9FF73BA";
    /**
     * appKey
     */
    private static final String appKey = "qz0n9e4s";

    @Resource
    private SysDictService sysDictService;

    /**
     * 鉴权-支持二三四要素
     * @param name 开户名
     * @param idCard 身份证号码
     * @param bankCode 银行卡号
     * @param phoneNum 银行预留手机号
     * @return
     * @author ZengJA
     * @date 2017-11-16 16:34:55
     */
    @Override
    public Map<String, String> doAuthen(String bankCode,String name,String idCard,String phoneNum){
        Map<String, String> jsonMap = new HashMap<String, String>();

        String respCode=null;
        String respMsg=null;
        String orderNumber = createOrderNo();
        Map<String, String> cardAuth = cardAuth(orderNumber,bankCode,name,idCard,phoneNum);
        String resultCode = cardAuth.get("resultCode");
        if("true".equalsIgnoreCase(resultCode)){
            respCode = "00";//明确成功
            respMsg = "验证成功";
        }else if("false".equalsIgnoreCase(resultCode)){
            respCode = "05";//明确失败
            respMsg = cardAuth.get("content");
        }else{
            respCode = "-1";//状态未知，可重新查询
            respMsg = cardAuth.get("content");
        }
        jsonMap.put("errCode","00".equals(respCode) ? "00" : "05");
        jsonMap.put("errMsg","00".equals(respCode) ? "验证成功:" : "验证失败：" + respMsg);
        return jsonMap;
    }

    private String createOrderNo() {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            sb.append(r.nextInt(8999) + 1000);
        }
        String orderNo = System.currentTimeMillis() + sb.toString();
        return orderNo.substring(15, orderNo.length());
    }

    /**
     * 二三四要素鉴权<br>
     * 手机号可传可不传，传则为四要素，反之则为三要素
     *
     * @param orderNo     订单号
     * @param accountNo   银行卡号
     * @param accountName 姓名
     * @param idCardNo    身份证号
     * @param mobileNo    手机号
     * @return Map, 如果resultCode值为true则为成功;retry重试;false失败,content存失败信息;
     * @date 2017年11月10日 下午3:43:06
     * @author ZengJA
     */
    private Map<String, String> cardAuth(String orderNo, String accountNo, String accountName, String idCardNo, String mobileNo) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("resultCode", "retry");
        String baseUrl = sysDictService.getDictSysValue("OPEN_PLATFORM_SERVICE_URL");
        String api = baseUrl + cardAuthUrl;
        JSONObject js = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("orderNo", orderNo);
        data.put("accountNo", accountNo);
        data.put("accountName", accountName);
        data.put("idCardNo", idCardNo);
        data.put("mobileNo", mobileNo);

        if (com.esdemo.frame.utils.StringUtils.isNotBlank(accountNo)) {
            if (com.esdemo.frame.utils.StringUtils.isNotBlank(accountName) && com.esdemo.frame.utils.StringUtils.isBlank(idCardNo)) {
                js.put("bizName", "cardAuth2a");//二要素:卡号+姓名
            } else if (com.esdemo.frame.utils.StringUtils.isNotBlank(idCardNo) && com.esdemo.frame.utils.StringUtils.isBlank(accountName)) {
                js.put("bizName", "cardAuth2b");//二要素:卡号+身份证
            } else if (com.esdemo.frame.utils.StringUtils.isNotBlank(idCardNo) && com.esdemo.frame.utils.StringUtils.isNotBlank(accountName)) {
                js.put("bizName", "cardAuth3");//三要素:卡号+身份证+姓名
            } else {
                result.put("content", "缺少必要参数>身份证或姓名");
                return result;
            }
            if (com.esdemo.frame.utils.StringUtils.isNotBlank(mobileNo)) {
                js.put("bizName", "cardAuth4");
            }
            js.put("data", data);
            String res = request(api, js);
            if (com.esdemo.frame.utils.StringUtils.isNotBlank(res)) {
                js = JSONUtil.parseObj(res);
                String head = js.getStr("head");
                Map<String, String> jsMap = GsonUtils.fromJson2Map(head, String.class);
                String resultCode = jsMap.get("resultCode");
                if ("FAIL".equalsIgnoreCase(resultCode)) {
                    result.put("resultCode", "false");
                } else if ("SUCCESS".equalsIgnoreCase(resultCode)) {
                    result.put("resultCode", "true");
                }
                result.put("content", "SUCCESS".equalsIgnoreCase(resultCode) ? orderNo : jsMap.get("resultMsg"));
            } else {
                log.info("开放平台，鉴权返回空");
                result.put("content", "验证异常，请稍后重试!");
            }
        } else {
            result.put("content", "缺少必要参数>卡号");
        }
        return result;
    }


    /**
     * 加密请求数据
     * @param api
     * @param js
     * @return
     */
    private String request(String api, JSONObject js) {
        StringBuilder url = new StringBuilder(api);
        String req = js.toString();
        log.info("请求参数:{}", req);
        try {
            req = new DESPlus(desKey).encrypt(req);// 加密数据
        } catch (Exception e) {
            e.printStackTrace();
        }
        url.append("appKey=").append(appKey).append("&data=").append(req);
        log.info("完整请求:{}", url);
        String res = ClientInterface.postRequest(url.toString());
        log.info("响应数据:{}", res);
        try {
            res = new DESPlus(desKey).decrypt(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("解密数据:{}", res);
        return res;
    }
}
