package com.esdemo.frame.utils;

import cn.hutool.json.JSONObject;
import com.auth0.jwt.JWTSigner;
import com.esdemo.frame.utils.external.CoreApiEnum;
import com.esdemo.frame.utils.external.ExternalApiUtils;
import com.esdemo.frame.utils.md5.Md5;
import com.esdemo.modules.bean.AccountJson2Bean;
import com.esdemo.modules.bean.AccountJsonBean;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.esdemo.frame.utils.external.AccountApiEnum.*;
import static com.esdemo.frame.utils.external.FlowmoneyApiEnum.NOW_TRANSFER;

@Slf4j
public class ClientInterface {
	private static final Gson GSON = MapTypeAdapter.newGson();
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientInterface.class);
	private String host;
	private Map<String,String> params;
	private Map<String,String> headers;
	private CloseableHttpClient client;
	public ClientInterface(String host, Map<String,String> headers, Map<String,String> params){
		this.host=host;
		this.params=params;
		this.headers=headers;
		this.client= HttpClients.createDefault();
	}
	public ClientInterface(String host, Map<String,String> params){
		this(host,null,params);
	}
	public String postRequest(){
		try {
			HttpPost post=new HttpPost(host);
			if(headers!=null)
				post.setHeaders(setHeaders());
			post.setEntity(setParams());
			HttpResponse res=client.execute(post);
			if(res.getStatusLine().getStatusCode()==200){
				return EntityUtils.toString(res.getEntity());
			}
		} catch (IOException e) {
			log.error("异常{}", e);
		}finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("异常{}", e);
			}
		}
		return "";
	}
	
	public String getRequest(){
		try {
			HttpGet get=new HttpGet(host+"?"+ EntityUtils.toString(setParams(),"ISO-8859-1"));
			if(headers!=null)
				get.setHeaders(setHeaders());
			HttpResponse res=client.execute(get);
			if(res.getStatusLine().getStatusCode()==200){
				return EntityUtils.toString(res.getEntity());
			}
		} catch (IOException e) {
			log.error("异常{}", e);
		}finally {
			try {
				client.close();
			} catch (IOException e) {
				log.error("异常{}", e);
			}
		}
		return "";
	}
	
	public UrlEncodedFormEntity setParams() throws UnsupportedEncodingException{
		if(params!=null){
			List<NameValuePair> list=new ArrayList<NameValuePair>();
			for(String key:params.keySet()){
				list.add(new BasicNameValuePair(key,params.get(key)));
			}
			return new UrlEncodedFormEntity(list);
		}
		return null;
	}
	
	public Header[] setHeaders() throws UnsupportedEncodingException{
		if(headers!=null){
			Header[] headerArray=new Header[headers.size()];
			int i=0;
			for(String key:headers.keySet()){
				headerArray[i]=new BasicHeader(key,params.get(key));
				i++;
			}
			return headerArray;
		}
		return null;
	}
	
	/**
	 * post账号接口。给claims添加必要的参数，并转换为token，然后提交
	 * 
	 * @param url
	 * @param claims
	 * @return
	 */
	public static String postAccountApi(String url, Map<String, Object> claims) {
		final long iat = System.currentTimeMillis() / 1000l; // issued at claim
		claims.put("exp", iat + 60L);
		claims.put("iat", iat);
		claims.put("jti", UUID.randomUUID().toString());

		Map<String, String> params = new HashMap<>();
		params.put("token", new JWTSigner(Constants.ACCOUNT_API_SECURITY).sign(claims));
		return new ClientInterface(url, params).postRequest();
	}
	
	public static void main(String[] args) {
//		Map<String,String> params=new HashMap<>();
//		params.put("baseInfo", "{'serviceName':'','serviceType':'','rateCard':'','rateHolidays':'','quotaHolidays':'','quotaCard':'','fixedRate':'','fixedQuota':'','rateCheckStatus':'','rateLockStatus':'','quotaCheckStatus':'','quotaLockStatus':''}");
//		params.put("pageNo", "1");
//		params.put("pageSize", "10");
//		System.out.println(new ClientInterface("http://192.168.3.180:8088/boss2/service/queryServiceList", params).postRequest());
//		
		Map<String,Object> claims=new HashMap<>();
		claims.put("accountType","A");
		claims.put("userId","7411");
		final long iat = System.currentTimeMillis() / 1000l; // issued at claim 
		claims.put("exp", iat+60L);
		claims.put("iat", iat);

		String p=new JWTSigner("zouruijin").sign(claims);
		Map<String,String> params=new HashMap<>();
		params.put("token", p);
		System.out.println(new ClientInterface(ExternalApiUtils.getAccountPath(CREATE_DEFAULT_EXT_ACCOUNT), params).postRequest());
	}

	public static void syncZfMerchantUpdate(String merchantNo, String newBpId, String operationAgentNo) {
		String accessUrl = ExternalApiUtils.getCorePath(CoreApiEnum.ZF_MERCHANT_UPDATE);
		Map<String, Object> marMap = new HashMap<>();
		marMap.put("merchantNo", merchantNo);
		marMap.put("bpId", newBpId);
		marMap.put("operator", operationAgentNo);
		marMap.put("changeSettleCard", "0");
		List<String> channelList = new ArrayList<>();
		channelList.add("ZF_ZQ");
		marMap.put("channelCode", channelList);
		String paramStr = GsonUtils.toJson(marMap);
		LOGGER.info("ZFZQ_ACCESS_URL:" + accessUrl + "\n paramStr:" + paramStr);
		String result = HttpUtils.doPost(accessUrl, paramStr, true);
		LOGGER.error("调用上游同步返回数据:" + result);
	}

	public static String createAgentAccount(String agentNo){
		return createAgentAccount(agentNo,"224105");
	}

	public static String createAgentAccount(String agentNo,String subjectNo){
		Map<String,Object> claims=new HashMap<>();
		claims.put("accountType","A");
		claims.put("userId",agentNo);
		claims.put("subjectNo",subjectNo);
		return createAccountPublicParams(claims);
	}

	public static String createMerchantAccount(String merchantNo){
		Map<String,Object> claims=new HashMap<>();
		claims.put("accountType","M");
		claims.put("userId",merchantNo);
		return createAccountPublicParams(claims);
	}
	private static String createAccountPublicParams(Map<String, Object> claims) {
		final long iat = System.currentTimeMillis() / 1000l; // issued at claim
		claims.put("exp", iat + 60L);
		claims.put("iat", iat);
		Map<String, String> params = new HashMap<>();
		params.put("token", new JWTSigner(Constants.ACCOUNT_API_SECURITY).sign(claims));
		return new ClientInterface(ExternalApiUtils.getAccountPath(CREATE_DEFAULT_EXT_ACCOUNT), params).postRequest();
	}

	/**
	 * 获取代理商账号余额
	 * 
	 * @param agentNo
	 * @return {"msg":"查询成功","balance":"0.00","avaliBalance":0,"status":true}
	 */
	public static String getAgentAccountBalance(String agentNo) {
		final HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("selectType", "2");
		claims.put("accountType", "A");
		claims.put("userId", agentNo);
		claims.put("accountOwner", "000001");
		claims.put("subjectNo", "224105");
		claims.put("currencyNo", "1");

		String result = ClientInterface.postAccountApi(ExternalApiUtils.getAccountPath(FIND_EXT_ACCOUNT_BALANCE), claims);
		return result;
	}

	/**
	 * 获取代理商交易记录
	 * 
	 * @param agentNo
	 * @param recordDate1
	 *            起始日期
	 * @param recordDate2
	 *            截止日期
	 * @param debitCreditSide
	 *            收入\支出？
	 * @return {"msg":"查询成功","data":{"pageNum":0,"pageSize":10,"size":0,
	 *         "orderBy":null,"startRow":0,"endRow":0,"total":0,"pages":0,"list"
	 *         :[],"firstPage":0,"prePage":0,"nextPage":0,"lastPage":0,
	 *         "isFirstPage":false,"isLastPage":true,"hasPreviousPage":false,
	 *         "hasNextPage":false,"navigatePages":8,"navigatepageNums":[]},
	 *         "status":true}
	 */
	public static String selectAgentAccountTransInfoList(String agentNo, Date recordDate1, Date recordDate2,
			String debitCreditSide) {
		final HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("selectType", "2");
		claims.put("accountType", "A");
		claims.put("userId", agentNo);
		claims.put("accountOwner", "000001");
		claims.put("subjectNo", "224106");
		claims.put("currencyNo", "1");

		claims.put("recordDate1", recordDate1);
		claims.put("recordDate2", recordDate2);
		claims.put("debitCreditSide", debitCreditSide);

		String result = ClientInterface.postAccountApi(ExternalApiUtils.getAccountPath(FIND_EXT_ACCOUNT_TRANS_INFO_LIST), claims);
		return result;
	}
	
	public static String postRequest(String url){
		Map<String,String> params=new HashMap<>();
		return new ClientInterface(url, params).postRequest();
	}
	
	/**
	 * 获取商户交易记录
	 * 
	 * @param merNo
	 * @param recordDate1
	 *            起始日期
	 * @param recordDate2
	 *            截止日期
	 * @param debitCreditSide
	 *            收入\支出？
	 * @return {"msg":"查询成功","data":{"pageNum":0,"pageSize":10,"size":0,
	 *         "orderBy":null,"startRow":0,"endRow":0,"total":0,"pages":0,"list"
	 *         :[],"firstPage":0,"prePage":0,"nextPage":0,"lastPage":0,
	 *         "isFirstPage":false,"isLastPage":true,"hasPreviousPage":false,
	 *         "hasNextPage":false,"navigatePages":8,"navigatepageNums":[]},
	 *         "status":true}
	 */
	public static String selectAgentAccountTransInfoList(String merNo, String recordDate1, String recordDate2,
			String debitCreditSide,int page,int pageSize) {
		final HashMap<String, Object> claims = setPublicParams();
		claims.put("accountType", "A");
		claims.put("userId", merNo);
		claims.put("accountOwner", "000001");
		claims.put("subjectNo", "224105");
		claims.put("currencyNo", "1");

		claims.put("recordDate1", recordDate1);
		claims.put("recordDate2", recordDate2);
		claims.put("debitCreditSide", debitCreditSide);
		
		Map<String,String> params=new HashMap<>();
		params.put("token", new JWTSigner(Constants.ACCOUNT_API_SECURITY).sign(claims));
		params.put("page", String.valueOf(page));
		params.put("pageSize",String.valueOf(pageSize));
		return new ClientInterface(ExternalApiUtils.getAccountPath(FIND_EXT_ACCOUNT_TRANS_INFO_LIST), params).postRequest();
	}

	public static String transfer(String transferId,String settleType,String userType){
		Map<String,String> params=new HashMap<>();
		params.put("transferId", transferId);
		params.put("settleType", settleType);
		params.put("userType", userType);//结算类型:1.商户,2.代理商
		return new ClientInterface(ExternalApiUtils.getFlowmoneyPath(NOW_TRANSFER), params).postRequest();
	}

	public static AccountJsonBean selectShareByDay(String agentNo, String selectAgentNo, String startTime, String endTime,
												   String statu, int page, int pageSize) {
		final HashMap<String, Object> claims = setPublicParams();

		claims.put("agentNo", agentNo);
		claims.put("selectAgentNo", selectAgentNo);
		claims.put("transDate1", startTime);
		claims.put("transDate2", endTime);
		claims.put("enterAccountStatus", statu);

		Map<String,String> params=new HashMap<>();
		params.put("token", new JWTSigner(Constants.ACCOUNT_API_SECURITY).sign(claims));
		params.put("page", String.valueOf(page));
		params.put("pageSize",String.valueOf(pageSize));
		String json = new ClientInterface(ExternalApiUtils.getAccountPath(FIND_AGENT_PROFIT_DAY_SETTLE_LIST), params).postRequest();
		LOGGER.info("每日分润报表获取到的数据：" + json);
		if (StringUtils.isBlank(json)){
			return null;
		}
		return GSON.fromJson(json, AccountJsonBean.class);
	}

	private static HashMap<String, Object> setPublicParams() {
		final HashMap<String, Object> claims = new HashMap<String, Object>();
		final long iat = System.currentTimeMillis() / 1000L;
		final long exp = iat + 300L;
		final String jti = UUID.randomUUID().toString();
		claims.put("exp", exp);
		claims.put("iat", iat);
		claims.put("jti", jti);
		return claims;
	}

	public static AccountJson2Bean findAgentProfitByBusinessProduct(String loginAgentNode, String startTime,String agentLevel) {
		final HashMap<String, Object> claims = setPublicParams();
		claims.put("agentNode", loginAgentNode);
		claims.put("transDate", startTime);
		claims.put("agentLevel", agentLevel);

		Map<String,String> params=new HashMap<>();
		params.put("token", new JWTSigner(Constants.ACCOUNT_API_SECURITY).sign(claims));
		String json = new ClientInterface(ExternalApiUtils.getAccountPath(PROFIT_BUSINESS_PRODUCT), params).postRequest();
		LOGGER.info("每日按业务产品汇总获取到的数据：" + json);
		if (StringUtils.isBlank(json)){
			return null;
		}
		return GSON.fromJson(json, AccountJson2Bean.class);
	}

	public static AccountJson2Bean selectSevenDayShareList(String loginAgentNode,String agentLevel) {
		final HashMap<String, Object> claims = setPublicParams();
		claims.put("agentNode", loginAgentNode);
		claims.put("agentLevel", agentLevel);
		Map<String,String> params=new HashMap<>();
		params.put("token", new JWTSigner(Constants.ACCOUNT_API_SECURITY).sign(claims));
//		params.put("page", StringUtils.filterNull(page));
//		params.put("pageSize",StringUtils.filterNull(pageSize));
		String json = new ClientInterface(ExternalApiUtils.getAccountPath(FIND_AGENT_PROFIT_SETTLE_COLLEC_SEVEN_DAY), params).postRequest();
		LOGGER.info("七日分润获取到的数据：" + json);
		if (StringUtils.isBlank(json)){
			return null;
		}
		return GSON.fromJson(json, AccountJson2Bean.class);
	}

	public static String getRegisterSource(String url, String token,String projectName,Map<String,Object> map,List<Map<String, Object>> list) throws UnsupportedEncodingException {
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("distinct_id", map.get("user_id"));
		jsonObject.put("time", new Date());
		jsonObject.put("type", "profile_set");

		final HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("merchant_no", map.get("merchant_no"));
		claims.put("gender", map.get("gender"));
		claims.put("submit_time", sf1.format(map.get("submit_time")));
		claims.put("$city", map.get("$city"));
		for(int i=0;i<list.size();i++) {
			claims.put("business_product"+(i+1), list.get(i).get("bp_name"));
			claims.put("product_type"+(i+1), list.get(i).get("type_name"));
		}
		claims.put("agent_no", map.get("agent_no"));
		claims.put("merchant_status", map.get("merchant_status"));
		claims.put("sales", map.get("sales"));
		claims.put("user_type", map.get("user_type"));
		claims.put("user_id", map.get("user_id"));
		claims.put("$province", map.get("$province"));
		claims.put("bank_name", map.get("bank_name"));
		claims.put("recmand_source", map.get("recmand_source"));
		claims.put("sign_source", map.get("sign_source"));
		claims.put("orgnize_id", map.get("orgnize_id"));
		claims.put("vip_level", map.get("vip_level"));
		claims.put("first_level_agent_no", map.get("first_level_agent_no"));
		claims.put("registration_time", sf1.format(map.get("registration_time")));
		claims.put("birthday", map.get("birthday"));
		claims.put("birthyear", map.get("birthyear"));
		claims.put("first_level_agent_name",map.get("first_level_agent_name"));
		claims.put("first_level_agent_sales",map.get("first_level_agent_sales"));
		claims.put("happy_active_type",map.get("happy_active_type"));
		claims.put("happy_active_name",map.get("happy_active_name"));
		claims.put("source_system", map.get("source_system"));
		claims.put("merchant_type", map.get("merchant_type"));

		jsonObject.put("properties", claims);

		log.info("请求路径：{},参数：{}",url, jsonObject.toString());
		String base64 = null;
		try {
			base64 = Base64Utils.encode(jsonObject.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String urlEncode = URLEncoder.encode(base64,"UTF-8");
		final String finalUrl = String.format("%s?project=%s&token=%s&data=%s&zip=0",url,projectName,token,urlEncode);
		log.info("===请求接口地址url=====>" + finalUrl);
		String returnStr = new ClientInterface(finalUrl, null).getRequest2();
		log.info("返回结果:{}", returnStr);
		return returnStr;
	}

	private String getRequest2(){
		try {
			HttpGet get = new HttpGet(host);
			if(headers != null)
				get.setHeaders(setHeaders());
			HttpResponse res = client.execute(get);
			if(res.getStatusLine().getStatusCode() == 200){
				return EntityUtils.toString(res.getEntity());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

    public static String cjtMerToCjtMer(String url,String merchantNo,String sn,String signKey) {
        url += "/cjt/merToCjtMer";
        final HashMap<String, String> claims = new HashMap<String, String>();
        claims.put("merchantNo", merchantNo);
        claims.put("sn", sn);
        claims.put("signData", ASCIISignUtil.sortASCIISign(claims,signKey));
        log.info("===请求接口地址url=====>" + url);
        log.info("请求路径：{},参数：{}",url, claims);
        String returnStr = new ClientInterface(url, claims).postRequest();
        log.info("返回结果:{}", returnStr);
        return returnStr;
    }

	/**
	 * 机具解绑,智能盛POS调core清除长token
	 * @param merchantNo
	 * @return
	 */
	public static String cleanLongToken(String merchantNo) {
		Map<String, String> claims = new HashMap<>();
		claims.put("merchantNo", merchantNo);
		String url = ExternalApiUtils.getCorePath(CoreApiEnum.CLEAN_LONG_TOKEN);
		log.info("机具解绑,清除长token,url:{},参数：merchantNo:{}", url, merchantNo);
		String returnStr = new ClientInterface(url, claims).postRequest();
		log.info("返回结果:{}", returnStr);
		return returnStr;
	}

	/**
	 * 机具解绑-调用core调上游解绑机具
	 * @return
	 */
	public static String merBindOrUnBindTmstpos(String sn, String merNo, String mbpId, String type) {
		String url = ExternalApiUtils.getCorePath(CoreApiEnum.MER_BINDORUNBIND_TMSTPOS);
		Map<String, String> claims = new HashMap<>();
		claims.put("sn", sn);
		claims.put("merchantNo", merNo);
		claims.put("mbpId", mbpId);
		claims.put("type", type);
		log.info("调用机具的绑定或解绑,url:{},参数：{}", url,  GsonUtils.toJson(claims));
		String returnStr = new ClientInterface(url, claims).postRequest();
		log.info("返回结果:{}", returnStr);
		return returnStr;
	}

	/**
	 * 调用9楼接口获取token
	 * @param agentNo
	 * @return
	 */
	public static String getFloor9Token(String agentNo,String key,String url){
		Map<String, Object> claims = new HashMap<>();
		String createTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String appId = "1000";
//		agentNo = "23655";
		claims.put("account", agentNo);
		claims.put("createTime", createTime);
		claims.put("sign", Md5.md5Str(agentNo + createTime + appId));
		claims.put("appId", appId);
//		url += "http://192.168.9.66:8088/agent-web/login/createToken?appId=" + appId;
		url += "login/createToken?appId=" + appId;
		String paramsJson = GsonUtils.toJson(claims);
		log.info("调用9楼接口获取token,url:{},参数：{}", url, paramsJson);
		String encrypt = "";
		try{
			encrypt = new DESPlus(key).encrypt(paramsJson);
		}catch (Exception e){
			e.printStackTrace();
		}
		Map<String,String> params = new HashMap<>();
		params.put("params", encrypt);
		log.info("========="+encrypt);
		log.info("参数 ===== > " + GsonUtils.toJson(params));
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-type", "application/json");
		String returnStr = HttpUtils.doPost(url,headers,params,true);
		log.info("9楼token接口返回结果:{}", returnStr);
		return returnStr;
	}

	/**
	 * 给安卓下发图片地址
	 * @param userId
	 * @return
	 */
	public static String cjtShareForAliYun(String userId) {
		final HashMap<String, String> claims = new HashMap<String, String>();
		claims.put("recommendedUserId", userId);
		claims.put("source", "V2-agent");
		String corePath = ExternalApiUtils.getCorePath(CoreApiEnum.CJT_SHARE_FOR_ALIYUN);
//		String corePath = "http://192.168.4.12:88/core2/cjt/cjtShareForAliYun";
		LOGGER.info("给安卓下发图片地址,调有core接口,url:{},参数：{}", corePath, GsonUtils.toJson(claims));
		String result = new ClientInterface(corePath, claims).postRequest();
		log.info("返回结果:{}", result);
		return result;
	}

	/**
	 * 130风控,身份证注册限制,调有core接口
	 * * 三个参数:
	 * 			 传身份证号或结算卡号，idCardNo,settleCardNo
	 * 			 hmac：MD5Utils.MD5Encode(specialPara+type, Constants.PAY_KEY,"UTF-8");
	 * @return
	 */
	public static Map<String,Object> risk130(String idCardNo,String settleCardNo,String risk130Key){
		log.info("130风控参数====>idCardNo {},settleCardNo {} ", idCardNo, settleCardNo);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("bols", true);
		Map<String, String> claims = new HashMap<>();
		claims.put("idCardNo", idCardNo);
		claims.put("settleCardNo", settleCardNo);
		claims.put("hmac", Md5.MD5Encode(idCardNo + settleCardNo, risk130Key,"UTF-8"));
		String corePath = ExternalApiUtils.getCorePath(CoreApiEnum.RISK_130_URL);
		LOGGER.info("130风控,身份证注册限制,调有core接口,url:{},参数：{}", corePath, GsonUtils.toJson(claims));
		String result = new ClientInterface(corePath, claims).postRequest();
		LOGGER.info("返回结果:{}", result);
		if(StringUtils.isNotBlank(result)){
			Map<String, Map> msg = GsonUtils.fromJson2Map(result, Map.class);
			Map<String, Object> map = msg.get("header");
			log.info("header==========>" + GsonUtils.toJson(map));
			//succeed为false，取错误码为R001的，代表触发风控
			if(!(Boolean) map.get("succeed") && "R001".equals(map.get("error"))){
				log.info("---已被风控限制-------------");
				resultMap.put("msg",map.get("errMsg"));
				resultMap.put("bols", false);
			}
		}
		return resultMap;
	}
}
