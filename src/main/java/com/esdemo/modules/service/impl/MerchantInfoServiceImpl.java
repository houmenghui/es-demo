package com.esdemo.modules.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.*;
import com.esdemo.frame.utils.md5.Md5;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.dao.*;
import com.esdemo.modules.service.AcqMerchantService;
import com.esdemo.modules.service.MerchantInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tgh
 * @description
 * @date 2019/5/21
 */
@Service
@Slf4j
public class MerchantInfoServiceImpl implements MerchantInfoService {
    private static final String IP = "http://api.eeepay.cn";
    private static final String cardAuthUrl = "/card/receive?";
    private static final Pattern pattern = Pattern.compile("^\\d+$|^\\d+\\.\\d+$");
    private static final Pattern pattern1 = Pattern.compile("^\\d*[0-9]\\d*$");
    /**
     * DES 加密密钥
     */
    private static final String desKey = "79FA5FC67215457F8F832CA3C9FF73BA";
//    private static final String desKey = "7DD59DDE7B5745DF92A4B776C7BC0AD8";
    /**
     * appKey
     */
//    private static final String appKey = "bxlh5o8v";
    private static final String appKey = "qz0n9e4s";

    @Resource
    private MerchantInfoDao merchantInfoDao;

    @Resource
    private MerchantDao merchantDao;

    @Resource
    private SeqService seqService;

    @Resource
    private AgentInfoDao agentInfoDao;

    @Resource
    private SysDictDao sysDictDao;

    @Resource
    private SysConfigDao sysConfigDao;

    @Resource
    private AcqMerchantService acqMerchantService;

    @Resource
    private AcqMerchantDao acqMerchantDao;

    @Override
    public Map<String, Object> checkSettleAccountNo(String settleAccountNo) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", false);
        Map<String, Object> map = merchantInfoDao.findBlacklist(settleAccountNo, "3", "2");
        if (map != null) {
            resultMap.put("msg", "该银行卡已被列入黑名单！");
            return resultMap;
        }

        Map<String, Object> accountNoInfo = merchantInfoDao.querySettleAccountNo(settleAccountNo);
        if (accountNoInfo == null) {
            resultMap.put("msg", "没有该账号的信息");
            return resultMap;
        }

        String cardType = (String) accountNoInfo.get("card_type");
        String bankNoTemp = (String) accountNoInfo.get("bank_no");
        String bankName = (String) accountNoInfo.get("bank_name");
        log.info("-----设置提现银行卡-----bankNoTemp-------" + bankNoTemp + "-------------");
        if ("0".equals(bankNoTemp)) {
            resultMap.put("msg", "暂不支持该银行卡！");
            return resultMap;
        } else if ("1".equals(bankNoTemp)) {
            resultMap.put("msg", "提现银行卡有误！");
            return resultMap;
        }
        if (cardType == null || "".equals(cardType)) {
            resultMap.put("msg", "暂不支持该卡！");
            return resultMap;
        } else if ("贷记卡".equals(cardType) || "准贷记卡".equals(cardType)) {
            resultMap.put("msg", "不支持信用卡提现！");
            return resultMap;
        }
        if (bankName == null || "".equals(bankName)) {
            resultMap.put("msg", "不支持该银行卡的开户行！");
            return resultMap;
        }
        resultMap.put("msg", "获取开户行成功");
        resultMap.put("status", true);
        resultMap.put("bankName", bankName);
        return resultMap;
    }

    /**
     * 校验身份证
     */
    @Override
    public Map<String, Object> validateCard(String IdCardNo) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", false);
        String tmp = "";
        if (IdcardUtils.validateCard(IdCardNo)) {
            Map<String, Object> map = merchantInfoDao.findBlacklist(IdCardNo, "2", "2");
            if (map != null) {
                res.put("msg", "身份证号为黑名单不能注册");
                return res;
            }
            int age = IdcardUtils.getAgeByIdCard(IdCardNo);
            Map<String, Object> tmpMap = merchantInfoDao.queryAgeLimit("age_limit");
            tmp = tmpMap.get("PARAM_VALUE").toString();
            if (StringUtils.isNotBlank(tmp)) {
                String[] limits = tmp.split("_");
                int min = Integer.valueOf(limits[0]);
                int max = Integer.valueOf(limits[1]);
                if (age > max) {
                    res.put("msg", "您已超过产品使用年龄");
                    return res;
                } else if (age < min) {
                    res.put("msg", "您还未达到产品使用年龄");
                    return res;
                } else {
                    res.put("msg", "成功");
                    res.put("status", true);
                    return res;
                }
            }
        } else {
            res.put("msg", "身份证号码有误");
            return res;
        }
        return res;
    }

    @Override
    public Map<String, Object> checkBaseInfoCheck(Map<String, String> params) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", false);
        String account_name = params.get("account_name");
        String account_no = params.get("account_no");
        String id_card_no = params.get("id_card_no");
        //要求全部走三码,v2.0.27需求版本要求换成四要素
        String mobilephone = params.get("mobilephone");
        if (StringUtils.isEmpty(account_name) || StringUtils.isEmpty(account_no) || StringUtils.isEmpty(id_card_no)) {
            res.put("msg", "必要参数为空!");
            return res;
        }
        String url = "check_id_name_bank";
        params.put("code_id", url);
        Map<String, String> map_temp = doAuthen(account_no, account_name, id_card_no, mobilephone);
        String errCode = map_temp.get("errCode");
        String errMsg_ = map_temp.get("errMsg");
        String exceptionMsg = map_temp.get("exceptionMsg");
        boolean flag = "00".equalsIgnoreCase(errCode);
        log.info("身份证验证结果：是否成功:{};开户名:{};银行卡号:{};身份证:{};手机号:{};验证结果:{};错误信息:{};异常信息:{};", new Object[]
                {flag, account_name, account_no, id_card_no, mobilephone, errCode, errMsg_, exceptionMsg});
        log.info("验证状态" + flag);

        if (flag) {
            res.put("status", true);
            res.put("msg", "四码(开户名、银行卡号、身份证号、手机号)验证成功!");
            return res;
        } else {
            res.put("msg", "四码(开户名、银行卡号、身份证号、手机号)验证不匹配!");
            return res;
        }
    }

    /**
     * 鉴权-支持二三四要素
     *
     * @param name     开户名
     * @param idCard   身份证号码
     * @param bankCode 银行卡号
     * @param phoneNum 银行预留手机号
     * @return
     * @author ZengJA
     * @date 2017-11-16 16:34:55
     */
    public Map<String, String> doAuthen(String bankCode, String name, String idCard, String phoneNum) {
        Map<String, String> jsonMap = new HashMap<String, String>();

        String respCode = null;
        String respMsg = null;
        String orderNumber = createOrderNo();
        Map<String, String> cardAuth = cardAuth(orderNumber, bankCode, name, idCard, phoneNum);
        String resultCode = cardAuth.get("resultCode");
        if ("true".equalsIgnoreCase(resultCode)) {
            respCode = "00";//明确成功
            respMsg = "验证成功";
        } else if ("false".equalsIgnoreCase(resultCode)) {
            respCode = "05";//明确失败
            respMsg = "验证失败";
        } else {
            respCode = "-1";//状态未知，可重新查询
            respMsg = cardAuth.get("content");
        }
        jsonMap.put("errCode", "00".equals(respCode) ? "00" : "05");
        jsonMap.put("errMsg", "00".equals(respCode) ? "验证成功:" : "验证失败：" + respMsg);
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
        String api = IP + cardAuthUrl;
        JSONObject js = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("orderNo", orderNo);
        data.put("accountNo", accountNo);
        data.put("accountName", accountName);
        data.put("idCardNo", idCardNo);
        data.put("mobileNo", mobileNo);

        if (StringUtils.isNotBlank(accountNo)) {
            if (StringUtils.isNotBlank(accountName) && StringUtils.isBlank(idCardNo)) {
                js.put("bizName", "cardAuth2a");//二要素:卡号+姓名
            } else if (StringUtils.isNotBlank(idCardNo) && StringUtils.isBlank(accountName)) {
                js.put("bizName", "cardAuth2b");//二要素:卡号+身份证
            } else if (StringUtils.isNotBlank(idCardNo) && StringUtils.isNotBlank(accountName)) {
                js.put("bizName", "cardAuth3");//三要素:卡号+身份证+姓名
            } else {
                result.put("content", "缺少必要参数>身份证或姓名");
                return result;
            }
            if (StringUtils.isNotBlank(mobileNo)) {
                js.put("bizName", "cardAuth4");
            }
            js.put("data", data);
            String res = request(api, js);
            if (StringUtils.isNotBlank(res)) {
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
     *
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
            log.error("异常{}", e);
        }
        url.append("appKey=").append(appKey).append("&data=").append(req);
        log.info("完整请求:{}", url);
        String res = ClientInterface.postRequest(url.toString());
        log.info("响应数据:{}", res);
        try {
            res = new DESPlus(desKey).decrypt(res);
        } catch (Exception e) {
            log.error("异常{}", e);
        }
        log.info("解密数据:{}", res);
        return res;
    }

    /**
     * 获取支行信息
     */
    @Override
    public Map<String, Object> getBankAndCnap(Map<String, String> params) {
        Map<String, Object> res = new HashMap<>();
        String cityName = params.get("city_name");
        String account_no = params.get("account_no");
        String merchant_no = params.get("merchant_no");
        String acq_into_no = params.get("acq_into_no");
        String updateFlag = params.get("updateFlag");
        /********************特约商户需求修改********************/
        res.put("status", false);
        if (StringUtils.isEmpty(cityName)) {
            res.put("msg", "必要参数为空!");
            return res;
        }
        //未修改银行卡时带*号，需要反查原始银行卡号，0：未修改
        if ("0".equals(updateFlag)) {
            log.info("根据商户号:{}或进件记录编号:{}反查银行卡号", merchant_no, acq_into_no);
            if (StringUtils.isBlank(merchant_no) && StringUtils.isBlank(acq_into_no)) {
                res.put("msg", "必要参数为空!");
                return res;
            }
            if(StringUtils.isNotBlank(merchant_no)){
                log.info("根据商户号:{}反查银行卡号", merchant_no);
                account_no = acqMerchantService.getMerItemByNoAndMriId(merchant_no, "3");
            }else{
                log.info("根据进件记录编号:{}反查银行卡号", acq_into_no);
                Map<String, Object> acqMerInfoMap = getAcqMerInfoByAcqIntoNo(acq_into_no);
                if(null != acqMerInfoMap && !acqMerInfoMap.isEmpty()){
                    account_no = StringUtils.filterNull(acqMerInfoMap.get("bank_no"));
                }
            }
            log.info("反查的银行卡号为：{}", account_no);
            if(StringUtils.isBlank(account_no)){
                res.put("msg", "数据异常!");
                return res;
            }
        }
        if (!"0".equals(updateFlag) && StringUtils.isEmpty(account_no)) {
            res.put("msg", "必要参数为空!");
            return res;
        }
        /********************特约商户需求修改********************/
        List<Map<String, Object>> bankList = new ArrayList<>();
        Map<String, Object> accountNoMap = merchantInfoDao.querySettleAccountNo(account_no);
        if (accountNoMap == null) {
            res.put("msg", "获取支行信息为空");
            return res;
        }
        String bankName = (String) accountNoMap.get("bank_name");
        List<Map<String, Object>> cnapList = merchantInfoDao.queryCnaps(bankName, cityName);
        if (cnapList.size() < 1) {
            res.put("msg", "获取支行信息为空");
            return res;
        }
        for (Iterator<Map<String, Object>> iterator = cnapList.iterator(); iterator.hasNext(); ) {
            Map<String, Object> map = iterator.next();
            String zhBankName = map.get("bank_name").toString();
            if (zhBankName.indexOf(bankName) > -1 && zhBankName.indexOf(cityName) > -1) {
                bankList.add(map);
            }
        }
        res.put("objectMap", bankList);
        res.put("status", true);
        res.put("msg", "获取支行及联行号信息成功");
        return res;
    }

    @Override
    public List<Map<String, Object>> getAcqMerMccList(String sysKey, String parentId) {
        return merchantInfoDao.getAcqMerMccList(sysKey, parentId);
    }

    @Override
    public List<Map<String, Object>> getMerchantInfoList(Map<String, String> merchantInfo,UserInfoBean userInfoBean) {
        List<Map<String, Object>> list = merchantInfoDao.getMbpList(merchantInfo,userInfoBean);
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("1", "待一审");
        statusMap.put("2", "待平台审核");
        statusMap.put("3", "审核失败");
        statusMap.put("4", "正常");
        statusMap.put("5", "已转自动审件");
        statusMap.put("0", "关闭");
        for (Map<String, Object> map : list) {
            Object id = map.get("id");
            log.info("审核意见Id == > {}", id);
            String examinationOpinions = "";
            Map<String, Object> eoMap = merchantInfoDao.queryExaminationOpinions(id == null ? "" : id.toString());
            if (eoMap != null) {
                examinationOpinions = eoMap.get("examination_opinions").toString();
            }
            String mobilephone = map.get("mobilephone") == null ? "" : StringUtils.mask4MobilePhone(map.get("mobilephone").toString());
            map.put("mobilephone", mobilephone);
            map.put("examination_opinions", examinationOpinions);
            map.put("status_zh", statusMap.get(Objects.toString(map.get("status"))));
        }
        return list;
    }

    @Override
    public Map<String, Object> getMerProductList(Map<String, String> params) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", false);
        String sn = params.get("sn");
        String agentNo = params.get("agent_no");
        Map<String, Object> snInfo = merchantInfoDao.querySn(sn);
        if (snInfo == null) {
            res.put("msg", "机具不存在");
            log.info(sn + "机具不存在");
            return res;
        }
        snInfo = merchantInfoDao.checkAgentSn(agentNo, sn);
        if (snInfo == null) {
            res.put("msg", "机具未分配给您");
            log.info("对不起,机具未分配给您<<<<<机具号=" + sn);
            return res;
        }

        String status = (String) snInfo.get("open_status");//机具状态
        if (!status.equals("1")) {
            res.put("msg", "机具配置有误");
            log.info(sn + "机具配置有误");
            if (status.equals("2")) {
                res.put("msg", "机具已被使用");
                log.info(sn + "机具已被使用");
            }
            return res;
        }
        String terType = (String) snInfo.get("type");
        String bp_type = params.get("bp_type");
        List<Map<String, Object>> list = merchantInfoDao.getMerProductList(bp_type, agentNo, terType);
        res.put("objectMap", list);
        res.put("status", true);
        res.put("msg", "校验SN获取业务产品成功");
        return res;
    }

    @Override
    public List<ServiceInfo> getServiceInfoByParams(String agent_no, String bp_id) {
        return merchantInfoDao.getServiceInfoByParams(agent_no, bp_id);
    }

    @Override
    public Map<String, Object> queryBpInfo(String bpId) {
        return merchantInfoDao.queryBpId(bpId);
    }

    @Override
    public Map<String, Object> checkRegistRules(Map<String, String> params) {
        return merchantInfoDao.queryMerchantInfo(params);
    }

    @Override
    public List<ServiceRate> getServiceRatedByParams(String one_agent_no, String bp_id) {
        return merchantInfoDao.getServiceRatedByParams(one_agent_no, bp_id);
    }

    @Override
    public List<ServiceQuota> getServiceQuotaByParams(String one_agent_no, String bp_id) {
        return merchantInfoDao.getServiceQuotaByParams(one_agent_no, bp_id);
    }

    @Override
    public List<AddRequireItem> getRequireItemByParams(String agent_no, String bp_id) {
        return merchantInfoDao.getRequireItemByParams(agent_no, bp_id);
    }

    @Override
    public List<Map<String, Object>> queryMerType(String syskey, String parentId) {
        return merchantInfoDao.queryMerType(syskey, parentId);
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public ResponseBean insertMerchantInfo(Map<String, Object> paramsMap,
                                           UserInfoBean userInfoBean, HttpServletRequest request) {
        //生成一个商户编号
        String merchant_no = "";
        String agentNo = userInfoBean.getAgentNo();
        MerchantInfo merInfo = GsonUtils.fromJson2Bean(paramsMap.get("merInfo").toString(), MerchantInfo.class);//商户基本信息
        //商户业务产品，默认单次只能选一个业务产品
        MerBusinessProduct merBusPro = GsonUtils.fromJson2Bean(paramsMap.get("merBusPro").toString(), MerBusinessProduct.class);
        String industryType = merInfo.getIndustryType();
        String oneAgentNo = paramsMap.get("oneAgentNo").toString();
        Map<String, Object> checkMap = BeanUtil.beanToMap(merInfo);
        Map<String, Object> mapPro = BeanUtil.beanToMap(merBusPro);
        String bpId = mapPro.get("bpId") == null ? "" : mapPro.get("bpId").toString();
        Map<String, Object> params = new HashMap<>();
        log.info("客户端上传进件业务产品信息<<<<<<" + mapPro);
        params.put("MerBusinessProduct", mapPro);
        params.put("bpId", bpId);
        Map<String, Object> bpInfo = queryBpInfo(mapPro.get("bpId").toString());
        String teamId = (String) bpInfo.get("team_id");//重置teamId
        checkMap.put("teamId", teamId);
        checkMap.put("idCardNo", merInfo.getIdCardNo());
        List<Map<String, String>> merRequireItemInfoMap =
                GsonUtils.fromJson2Bean(paramsMap.get("merRequireItem").toString(), List.class);
        log.info("客户端上传进件进件项信息<<<<<<" + merRequireItemInfoMap);
        String accountNo = "";
        for (Map<String, String> map : merRequireItemInfoMap) {
            if ("3".equals(map.get("mriId"))) {
                accountNo = map.get("content");
            }
        }
        checkMap.put("accountNo", accountNo);
        Map<String, Object> baseInfo = checkRegister(checkMap);
        if ((Boolean) baseInfo.get("status")) {
            log.info("进件保存基本信息校验通过-----------");
            industryType = StringUtils.isEmpty(industryType) ? "1101" : industryType;
            if (StringUtils.isNotEmpty(industryType)) {
                try {
                    merchant_no = getMerchantNo(industryType);
                } catch (SQLException e1) {
                    log.error("商户号生成失败，请重试！", e1);
                    throw new AppException("商户号生成失败，请重试！");
                }
                log.info("进件数据<<<<<" + "代理商编号" + agentNo + "组织ID" + teamId + "一级代理商" + oneAgentNo + "商户号" + merchant_no);
                if (StringUtils.isNotEmpty(merchant_no) && merchant_no.length() == 15) {
                    List<Map<String, Object>> serviceList = merchantInfoDao.selectServiceList(bpId);
//                            GsonUtils.fromJson2Bean(paramsMap.get("serviceInfo").toString(), List.class);
                    log.info("客户端上传进件服务信息<<<<<<" + serviceList);
                    params.put("serviceList", serviceList);
                    params.put("teamId", teamId);

                    Map<String, Object> mapMerInfo = BeanUtil.beanToMap(merInfo);
                    log.info("客户端上传进件商户信息<<<<<<" + mapMerInfo);
                    mapMerInfo.put("merchantNo", merchant_no);
                    mapMerInfo.put("oneAgentNo", oneAgentNo);
                    mapMerInfo.put("teamId", teamId);
                    mapMerInfo.put("saleName", agentNo);
                    params.put("MerchantInfo", mapMerInfo);

                    List<MerRequireItem> merRequireItemInfo = new ArrayList<>();
                    for (Map<String, String> map : merRequireItemInfoMap) {
                        MerRequireItem item = new MerRequireItem();
                        item.setMriId(map.get("mriId"));
                        item.setContent(map.get("content"));
                        merRequireItemInfo.add(item);
                    }

                    //限额
                    List<MerServiceQuota> merServiceQuotaList =
                            GsonUtils.fromJson2Bean(paramsMap.get("merServiceQuota").toString(), List.class);
                    log.info("客户端上传进件限额信息<<<<<<" + merServiceQuotaList);
                    if (merServiceQuotaList != null) {
                        List<Map<String, Object>> listQuota = getServiceQuotaReq(oneAgentNo, mapPro.get("bpId").toString());
                        Map<String, Object> checkquotaMap = checkquota(merServiceQuotaList, listQuota);
                        if ((Boolean) checkquotaMap.get("status")) {
                            params.put("MerServiceQuota", checkquotaMap.get("listMap"));
                        } else {
                            throw new AppException(checkquotaMap.get("msg").toString());
                        }
                    }
                    params.put("MerServiceQuota", merServiceQuotaList);
                    //费率
                    List<MerServiceRate> merServiceRateList =
                            GsonUtils.fromJson2Bean(paramsMap.get("merServiceRate").toString(), List.class);
                    log.info("客户端上传进件费率信息<<<<<<" + merServiceRateList);
                    if (merServiceRateList != null) {
                        List<Map<String, Object>> listRate = getServiceRatedReq(oneAgentNo, mapPro.get("bpId").toString());
                        Map<String, Object> msg = checkRate(merServiceRateList, listRate);
                        if ((Boolean) msg.get("status")) {
                            params.put("MerServiceRate", msg.get("listMap"));
                        } else {
                            throw new AppException(msg.get("msg").toString());
                        }
                    }

                    /**
                     * 取客户端上传的文件
                     * 与客户端约定文件名是{进件项ID.文件后缀名}
                     */
                    MultipartRequest qq = (MultipartRequest) request;
                    Map<String, MultipartFile> maps = qq.getFileMap();
                    MultiValueMap<String, MultipartFile> multiFileMap = qq.getMultiFileMap();
                    List<MultipartFile> fileList = multiFileMap.get("file");
                    if (fileList != null && fileList.size() > 0) {//安卓
                        for (MultipartFile file : fileList) {
                            loadPicture(merRequireItemInfo, file);
                        }
                    } else {
                        for (String key : maps.keySet()) {
                            loadPicture(merRequireItemInfo, maps.get(key));
                        }
                    }
                    //上传文件结束
                    try {
                        params.put("MerRequireItemInfo", merRequireItemInfo);
                        String sns = paramsMap.get("sns").toString();
                        log.info("客户端上传进件SN信息<<<<<< {} 业务产品id ======> {}", sns, bpId);
                        params.put("sns", sns);
                        params.put("oneAgentNo", oneAgentNo);
                        String ok = addMer(params);
                        if ("SUCCESS".equalsIgnoreCase(ok)) {
                            log.info("<<<<<<<<<<<<<<<<<<<<" + merchant_no + "商户进件成功");
                            Map<String, Object> resultMap = new HashMap<>();
                            resultMap.put("merchantNo", merchant_no);
                            resultMap.put("sn", sns);
                            resultMap.put("bpId", bpId);
                            return ResponseBean.success(resultMap, "商户进件成功");
                        } else if ("开设商户账户失败!".equals(ok)) {
                            return ResponseBean.success(null, "开设商户账户失败!");
                        } else {
                            log.info("<<<<<<<<<<<<<<<<<<<<" + merchant_no + "商户进件失败:" + ok);
                            throw new AppException("商户进件失败" + ok);
                        }
                    } catch (AppException e) {
                        return ResponseBean.error(e.getMessage());
                    } catch (Exception e) {
                        log.error("<<<<<<<<<<<<<<<<<<<<" + merchant_no + "商户新增失败", e);
                        throw new AppException("商户新增失败");
                    }
                    //进件流程结束
                } else {
                    throw new AppException("核验商户号失败");
                }
            } else {
                log.error("MCC失败，请重试！");
                throw new AppException("MCC失败，请重试！");
            }
        } else {
            throw new AppException(baseInfo.get("msg").toString());
        }
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public ResponseBean updateMerchantInfo(String merchantNo, Map<String, Object> paramsMap, UserInfoBean userInfoBean, HttpServletRequest request) {
        List<MerRequireItem> merRequireItemInfo = Optional.ofNullable(GsonUtils.fromJson2List(Objects.toString(paramsMap.get("merRequireItem")), MerRequireItem.class))
                .orElse(new ArrayList<>());

        /*
         * 取客户端上传的文件
         * 与客户端约定文件名是{进件项ID.文件后缀名}
         */
        MultipartRequest qq = (MultipartRequest) request;
        Map<String, MultipartFile> maps = qq.getFileMap();
        MultiValueMap<String, MultipartFile> multiFileMap = qq.getMultiFileMap();
        List<MultipartFile> fileList = multiFileMap.get("file");
        if (fileList != null && fileList.size() > 0) {//安卓
            for (MultipartFile file : fileList) {
                loadPicture(merRequireItemInfo, file);
            }
        } else {
            for (String key : maps.keySet()) {
                loadPicture(merRequireItemInfo, maps.get(key));
            }
        }
        int row = 0;
        MerchantInfo merchantInfo = new MerchantInfo();
        boolean wantUpdateMerchantInfo = false;
        Map<String, String> checkMap = new HashMap<>();
        for (MerRequireItem item : merRequireItemInfo) {
            if ("2".equals(item.getMriId())) {
                item.setContent(item.getContent().replaceAll(" ", ""));
            }
            row += merchantInfoDao.updateMerRequireItem(merchantNo, item);
            switch (item.getMriId()) {
                case "2":  // 开户名
                    wantUpdateMerchantInfo = true;
                    String accountName = item.getContent();
                    merchantInfo.setLawyer(accountName);
                    checkMap.put("account_name", accountName);
                    break;
                case "3":   // 开户账号
                    wantUpdateMerchantInfo = true;
                    checkMap.put("account_no", item.getContent());
                    break;
                case "6":   // 开户身份证
                    wantUpdateMerchantInfo = true;
                    String idCardNo = item.getContent();
                    merchantInfo.setIdCardNo(idCardNo);
                    checkMap.put("id_card_no", idCardNo);
                    break;
                case "7":   // 经营地址
                    wantUpdateMerchantInfo = true;

                    String[] adds = item.getContent().split("-");
                    if (adds.length == 3) {
                        merchantInfo.setProvince(adds[0]);
                        merchantInfo.setCity(adds[1]);
                        merchantInfo.setAddress(adds[2]);

                    }
                    if (adds.length == 4) {
                        merchantInfo.setProvince(adds[0]);
                        merchantInfo.setCity(adds[1]);
                        merchantInfo.setDistrict(adds[2]);
                        merchantInfo.setAddress(adds[3]);

                    }
                    merchantInfo.setAddress(item.getContent().replace("-", ""));

                    break;
            }
        }
        String risk130Key = sysConfigDao.getStringValueByKey("RISK130_KEY");
        Map<String, Object> resultMap = ClientInterface.risk130(checkMap.get("id_card_no"), checkMap.get("account_no"), risk130Key);
        if (resultMap != null && !(Boolean) resultMap.get("bols")) {
            throw new AppException(resultMap.get("msg").toString());
        }
        Map<String, Object> map = checkBaseInfoCheck(checkMap);//校验四码
        if (!(Boolean) map.get("status")) {
            throw new AppException(map.get("msg").toString());
        }
        if (row != merRequireItemInfo.size()) {
            throw new AppException("商户附件资料保存失败");
        }
        if (wantUpdateMerchantInfo) {
            merchantInfoDao.updateMerchant(merchantNo, merchantInfo);
        }
        merchantInfoDao.updateMbpStatus(merchantNo, "2");
        // 删除审核记录
        merchantInfoDao.deleteExaminationsLog(merchantNo);
        return ResponseBean.success("修改成功");
    }

    private void loadPicture(List<MerRequireItem> merRequireItemInfo, MultipartFile merFile) {
        String fileName = merFile.getOriginalFilename();
        if (fileName != null && !fileName.equals("")) {
            //fileName为进件项ID，将进件项ID与阿里云上的文件名对应起来
            int random = new Random().nextInt(100000);
            String value = fileName.substring(0, fileName.indexOf("."));
            value = value + "_" + DateUtils.getMessageTextTime() + "_" + random + ".jpg";

            MerRequireItem merFileItem = new MerRequireItem();
            merFileItem.setMriId(fileName.substring(0, fileName.indexOf(".")));
            merFileItem.setContent(value);
            merRequireItemInfo.add(merFileItem);
            //此处调用阿里云的存储方法，将文件流存储至阿里云
            uploadPicture(merFile, value);
        }
    }

    private void uploadPicture(MultipartFile merFile, String value) {
        Date date = new Date();
        try {
            ALiYunOssUtil.saveFile(Constants.ALIYUN_OSS_ATTCH_TUCKET, value, merFile.getInputStream());
            log.info(ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, value, new Date(date.getTime() + 100000)));
        } catch (IOException e) {
            throw new AppException("附件保存失败");
        }
    }

    @Override
    public Map<String, Object> checkRegister(Map<String, Object> params) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", false);
        String id_card_no = params.get("idCardNo").toString();
        String accountNo = params.get("accountNo").toString();
        String mobilephone = params.get("mobilephone").toString();
        String team_id = params.get("teamId").toString();
        String merchant_name = params.get("merchantName").toString();
        log.info("进件参数-------------" + params);
        if (StringUtils.isEmpty(id_card_no) || StringUtils.isEmpty(mobilephone) || StringUtils.isEmpty(team_id) || StringUtils.isEmpty(merchant_name)) {
            res.put("msg", "必要参数为空");
            return res;
        }
        if (!StringUtils.isConSpeCharacters(merchant_name)) {
            res.put("msg", "商户名称包含特殊字符，请重新输入！");
            return res;
        }
        if (merchant_name.indexOf(" ") != -1) {
            res.put("msg", "商户名称包含空格，请重新输入！");
            return res;
        }

        Map<String, String> mobilephoneMap = new HashMap<String, String>();
        mobilephoneMap.put("mobilephone", mobilephone);
        mobilephoneMap.put("teamId", team_id);

        /*//进件总次数,不区分组织
        Integer idCardCountLimit = Integer.valueOf(sysDictDao.getByKey("ID_CARD_COUNT_LIMIT").getSysValue());
        Map<String, Object> map = merchantInfoDao.countMerByIdCard(id_card_no);
        if (map != null){
            Object obj = map.get("reg_count");
            Integer regCount = Integer.valueOf(obj == null ? "0" : StringUtils.filterNull(obj));
            if (regCount > idCardCountLimit) {
                res.put("msg", "您的身份证号码注册次数已超限！");
                return res;
            }
        }*/
        //=======130风控,身份证注册限制====start====
        /*if ("3".equals(item.getMriId())) {
            accountNo = item.getContent();
            break;
        }*/
        String risk130Key = sysConfigDao.getStringValueByKey("RISK130_KEY");
        Map<String, Object> resultMap = ClientInterface.risk130(id_card_no, accountNo, risk130Key);
        if (resultMap != null && !(Boolean) resultMap.get("bols")) {
            res.put("msg", resultMap.get("msg").toString());
            return res;
        }
        //判断当前身份证在当前组织下注册次数是否超过允许注册的次数
        Map<String, Object> mer_count = merchantInfoDao.countMerByIdCardInTeam(id_card_no, team_id);
        if (mer_count != null) {
            Object obj = mer_count.get("reg_count");
            Integer reg_count = Integer.valueOf(obj == null ? "0" : StringUtils.filterNull(obj));
            if (reg_count >= 2) {//目前最多允许注册两次
                res.put("msg", "您的身份证号码注册次数已超限！");
                return res;
            }
        }

        Map<String, Object> mobilephoneMerInfo = merchantInfoDao.queryMerchantInfo(mobilephoneMap);
        if (mobilephoneMerInfo != null) {
            log.info("手机号已注册" + mobilephoneMerInfo);
            res.put("msg", "手机号已注册！");
            return res;
        }
        res.put("status", true);
        return res;
    }

    /**
     * 根据MCC获取商户编号
     *
     * @param mcc
     * @return
     * @throws SQLException
     * @author zengja
     * @date 2015年7月29日 下午4:09:24
     */
    private String getMerchantNo(String mcc) throws SQLException {
        String merchantNoFron = "2" + mcc;
        String merchantNoBack = seqService.createKey("merchant_no_seq", new BigInteger("1000000000"));
        String merchantNo = merchantNoFron + merchantNoBack;
        return merchantNo;
    }

    private List<Map<String, Object>> getServiceQuotaReq(String one_agent_no, String bp_id) {
        return merchantInfoDao.getServiceQuotaReq(one_agent_no, bp_id);
    }

    @Override
    public Map<String, Object> checkquota(List<MerServiceQuota> merServiceQuota, List<Map<String, Object>> listQuota) {

        Map<String, Object> msg = new HashMap<>();
        try {
            if (merServiceQuota.size() != listQuota.size()) {
                msg.put("status", false);
                msg.put("msg", "限额数据出现异常!");
                return msg;
            }
            msg.put("status", true);
            for (int i = 0; i < merServiceQuota.size(); i++) {
                MerServiceQuota quota = merServiceQuota.get(i);
                Map<String, Object> localMap = listQuota.get(i);

                String id = quota.getId();
                String localId = StringUtils.filterNull(localMap.get("id"));
                if (!(id.equals(localId))) {
                    msg.put("status", false);
                    msg.put("msg", "服务限额数据校验出现异常!");
                    break;
                }
                String single_day_amount = quota.getSingleDayAmount();
                String single_count_amount = quota.getSingleCountAmount();
                String single_daycard_amount = quota.getSingleDaycardAmount();
                String single_daycard_count = quota.getSingleDaycardCount();
                String single_min_amount = quota.getSingleMinAmount();//单笔最小交易额
                Matcher m = pattern.matcher(single_day_amount);
                Matcher m1 = pattern.matcher(single_count_amount);
                Matcher m2 = pattern.matcher(single_daycard_amount);
                Matcher m3 = pattern1.matcher(single_daycard_count);
                Matcher m4 = pattern.matcher(single_min_amount);

                if (!m.matches()) {
                    msg.put("status", false);
                    msg.put("msg", "限额格式:单日最大交易额错误!");
                    break;
                }
                if (!m1.matches()) {
                    msg.put("status", false);
                    msg.put("msg", "限额格式:单笔最大交易额错误!");
                    break;
                }
                if (!m2.matches()) {
                    msg.put("status", false);
                    msg.put("msg", "限额格式:单日单卡最大交易额错误!");
                    break;
                }
                if (!m3.matches()) {
                    msg.put("status", false);
                    msg.put("msg", "限额格式:单日单卡最大交易笔数错误!");
                    break;
                }
                if (!m4.matches()) {
                    msg.put("status", false);
                    msg.put("msg", "限额格式:单笔最小交易额错误!");
                    break;
                }
                quota.setCardType(localMap.get("cardType").toString());
                quota.setHolidaysMark(localMap.get("holidaysMark").toString());
                quota.setServiceId(localMap.get("serviceId").toString());

                BigDecimal merDayAmount = new BigDecimal(single_day_amount);
                BigDecimal merCountAmount = new BigDecimal(single_count_amount);
                BigDecimal merDaycardAmount = new BigDecimal(single_daycard_amount);
                int merDaycardCount = Integer.parseInt(single_daycard_count);
                BigDecimal merMinAmount = new BigDecimal(single_min_amount);

                Object singleDayAmount = localMap.get("singleDayAmount");//单日最大交易额
                Object singleCountAmount = localMap.get("singleCountAmount");//单笔最大交易额
                Object singleDaycardAmount = localMap.get("singleDaycardAmount");//单日单卡最大交易额
                Object singleDaycardCount = localMap.get("singleDaycardCount");//单日单卡最大交易笔数
                Object singleMinAmount = localMap.get("singleMinAmount");//数据库中单笔最小交易额

                BigDecimal localDayAmount = new BigDecimal(singleDayAmount.toString());
                BigDecimal localCountAmount = new BigDecimal(singleCountAmount.toString());
                BigDecimal localDaycardAmount = new BigDecimal(singleDaycardAmount.toString());
                BigDecimal localMinAmount = new BigDecimal(singleMinAmount.toString());
                int localDaycardCount = Integer.parseInt(singleDaycardCount.toString());

                if (merDayAmount.compareTo(localDayAmount) > 0) {
                    msg.put("status", false);
                    msg.put("msg", "单日最大交易额必须小于等于指导单日最大交易额!");
                    break;
                }
                if (merCountAmount.compareTo(localCountAmount) > 0) {
                    msg.put("status", false);
                    msg.put("msg", "单笔最大交易额必须小于等于指导单笔最大交易额!");
                    break;
                }
                if (merDaycardAmount.compareTo(localDaycardAmount) > 0) {
                    msg.put("status", false);
                    msg.put("msg", "单日单卡最大交易额必须小于等于指导单日单卡最大交易额!");
                    break;
                }
                if (merDaycardCount > localDaycardCount) {
                    msg.put("status", false);
                    msg.put("msg", "单日单卡最大交易笔数必须小于等于指导单日单卡最大交易笔数!");
                    break;
                }
                if (localMinAmount.compareTo(merMinAmount) > 0) {
                    msg.put("status", false);
                    msg.put("msg", "单笔最小交易额必须大于等于指导单笔最小交易额!");
                    break;
                }

            }
        } catch (Exception e) {
            log.error("校验限额异常" + e.getMessage());
            msg.put("status", false);
            msg.put("msg", "校验限额失败");
            return msg;
        }
        if ((Boolean) msg.get("status")) {
            msg.put("listMap", merServiceQuota);
        }
        return msg;
    }

    private List<Map<String, Object>> getServiceRatedReq(String one_agent_no, String bp_id) {
        List<Map<String, Object>> list = merchantInfoDao.getServiceRateReq(one_agent_no, bp_id);
        return list;
    }

    @Override
    public Map<String, Object> checkRate(List<MerServiceRate> mapMerServiceRate, List<Map<String, Object>> listRate) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("status", true);
        try {
            if (!(mapMerServiceRate.size() == listRate.size())) {
                msg.put("status", false);
                msg.put("msg", "费率格式:服务费率数据出现异常!");
                return msg;
            }

            for (int i = 0; i < mapMerServiceRate.size(); i++) {
                MerServiceRate serviceRate = mapMerServiceRate.get(i);
                String rate = serviceRate.getRate();
                Map<String, Object> localMap = listRate.get(i);
                String id = serviceRate.getId();
                String localId = StringUtils.filterNull(localMap.get("id"));
                if (!(id.equals(localId))) {
                    msg.put("status", false);
                    msg.put("msg", "服务费率数据校验出现异常!");
                    break;
                }
                serviceRate.setService_id(localMap.get("serviceId").toString());
                serviceRate.setHolidays_mark(localMap.get("holidaysMark").toString());
                serviceRate.setCard_type(localMap.get("cardType").toString());
                serviceRate.setRate_type(localMap.get("rateType").toString());
                Object ladder1 = localMap.get("ladder1Max");
                if (ladder1 != null) {
                    serviceRate.setLadder1_max(ladder1.toString());
                }

                String rateType = String.valueOf(localMap.get("rateType"));
                Object sourceRate = localMap.get("rate");
                Object sourceSingleAmount = localMap.get("singleNumAmount");
                if (rateType.equals("1") || rateType.equals("4")) {
                    String singleNumAmount = serviceRate.getSingle_num_amount();
                    Matcher m = pattern.matcher(singleNumAmount);
                    if (!m.matches()) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:每笔固定金额错误!");
                        break;
                    }
                    BigDecimal sAmount = new BigDecimal(sourceSingleAmount.toString());//原固定金额做修改
                    BigDecimal amount = new BigDecimal(singleNumAmount);//商户修改的
                    if (amount.compareTo(sAmount) < 0) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:必须大于等于指导固定金额!");
                        break;
                    }
                }
                if (rateType.equals("2") || rateType.equals("3") || rateType.equals("4")) {
                    Matcher m = pattern.matcher(rate);
                    if (!m.matches()) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:每笔扣率错误!");
                        break;
                    }
                    BigDecimal merRate = new BigDecimal(rate);
                    BigDecimal localRate = new BigDecimal(sourceRate.toString());
                    if (merRate.compareTo(localRate) < 0) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:扣率须大于等于指导扣率!");
                        break;
                    }
                }

                if (rateType.equals("3")) {
                    String safeLine = serviceRate.getSafe_line();
                    String capping = serviceRate.getCapping();
                    Matcher m = pattern.matcher(safeLine);
                    Matcher m1 = pattern.matcher(capping);
                    if (!m.matches() || !m1.matches()) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:每笔扣率+保底封顶错误!");
                        break;
                    }
                    BigDecimal merSafeLine = new BigDecimal(safeLine);//保底
                    BigDecimal mercapping = new BigDecimal(capping);//封顶
                    Object localSafeLine = listRate.get(i).get("safeLine");
                    Object localCapping = listRate.get(i).get("capping");
                    BigDecimal localSafeLine1 = new BigDecimal(localSafeLine.toString());
                    BigDecimal localCapping1 = new BigDecimal(localCapping.toString());
                    if (merSafeLine.compareTo(localSafeLine1) < 0 || mercapping.compareTo(localCapping1) < 0) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:保底封顶金额须大于指定保底封顶金额!");
                        break;
                    }
                }

                if (rateType.equals("5")) {
                    String ladder1Rate = serviceRate.getLadder1_rate();
                    String ladder2Rate = serviceRate.getLadder2_rate();
                    Matcher m = pattern.matcher(ladder1Rate);
                    Matcher m2 = pattern.matcher(ladder2Rate);
                    if (!m.matches() || !m2.matches()) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式：阶梯扣率错误！");
                        break;
                    }
                    BigDecimal merladder1Rate = new BigDecimal(ladder1Rate);
                    BigDecimal merladder2Rate = new BigDecimal(ladder2Rate);
                    Object localLadder1Rate = listRate.get(i).get("ladder1Rate");
                    Object localLadder2Rate = listRate.get(i).get("ladder2Rate");
                    BigDecimal localLadder1Rate1 = new BigDecimal(localLadder1Rate.toString());
                    BigDecimal localLadder2Rate1 = new BigDecimal(localLadder2Rate.toString());
                    if (merladder1Rate.compareTo(localLadder1Rate1) < 0 || merladder2Rate.compareTo(localLadder2Rate1) < 0) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:阶梯扣率须大于指导阶梯扣率!");
                        break;
                    }
                    if (merladder2Rate.compareTo(merladder1Rate) < 0 || merladder2Rate.compareTo(merladder1Rate) == 0) {
                        msg.put("status", false);
                        msg.put("msg", "费率格式:阶梯2费率必须小于前一阶梯费率!");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("校验费率异常" + e.getMessage());
            msg.put("status", false);
            msg.put("msg", "校验费率失败");
            return msg;
        }
        if ((Boolean) msg.get("status")) {
            msg.put("listMap", mapMerServiceRate);
        }
        return msg;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public String addMer(Map<String, Object> params) {
        String msg = "SUCCESS";
        Map<String, String> ps = (Map<String, String>) params.get("MerchantInfo");
        String merchant_no = ps.get("merchantNo");
        String merchant_name = ps.get("merchantName");
        String mobilephone = ps.get("mobilephone");
        String teamId = (String) params.get("teamId");
        String oneAgentNo = (String) params.get("oneAgentNo");
        String bpId = (String) params.get("bpId");
        String sn = (String) params.get("sns");

        // 检查商户名称是否包含敏感词
        if (StringUtils.isNotBlank(merchant_name) && merchantInfoDao.hasSensitiveWords(merchant_name) > 0) {
            msg = "商户名称存在非法词汇，请重新输入";
            return msg;
        }
        //在商户类型选择为“个人”和“个体商户”时需校验“法人姓名”和“开户名”是否一致，
        // 如不一致则提示：法人姓名和开户名不一致，请重新输入
        List<MerRequireItem> merRequireItemInfo = (List<MerRequireItem>) params.get("MerRequireItemInfo");
        String accountName = "";
        if (merRequireItemInfo != null) {
            for (MerRequireItem item : merRequireItemInfo) {
                log.info("进件项=====>" + item);
                if ("2".equals(item.getMriId())) {
                    item.setContent(item.getContent().replaceAll(" ", ""));
                    accountName = item.getContent();
                    break;
                }
            }
        }
        /*//=======130风控,身份证注册限制====start====
        String risk130Key = sysConfigDao.getStringValueByKey("RISK130_KEY");
        Map<String, Object> resultMap = ClientInterface.risk130(ps.get("idCardNo"), accountNo, risk130Key);
        if(resultMap != null && !(Boolean) resultMap.get("bols")){
            return resultMap.get("msg").toString();
        }*/
        //=======130风控,身份证注册限制====end======
        String address = ps.get("address");
        String province = "";
        String city = "";
        String district = "";
        if (address != null) {
            String[] as = address.split("-");
            if (as != null && as.length >= 2) {
                province = as[0];
                city = as[1];
                district = as[2];
            }
        }
        ps.put("province", province);
        ps.put("city", city);
        ps.put("district", district);
        address = address.replace("-", "");
        ps.put("address", address);
        log.info("参数信息1：" + params);
        log.info("商户信息2：" + ps);
        log.info("开户名3：" + accountName);
        if ("1".equals(ps.get("merchantType")) || "2".equals(ps.get("merchantType"))) {
            if (!ps.get("lawyer").equals(accountName)) {
                msg = "法人姓名和开户名不一致，请重新输入";
                return msg;
            }
        }

        //根据boss后台-风控管理-风控规则管理中的114编号风控规则
        // 设置的禁止进件地区屏蔽商户进件中对于的地区选项
        Map<String, Object> riskRuleMap = merchantInfoDao.selectRiskRule("114");
        if (riskRuleMap != null) {
            String provinces = (String) riskRuleMap.get("rules_provinces");
            String citys = (String) riskRuleMap.get("rules_city");

            if (provinces.contains(province) && citys.contains(city)) {

            } else {
                msg = "该地区暂不支持进件";
                return msg;
            }
        }

        int row = merchantInfoDao.addMerInfo(ps);
        if (row != 1) {
            msg = "商户基本信息保存失败";
            throw new AppException(msg);
        }
        {
            row = 0;
            List<Map<String, Object>> merServiceMap = (List<Map<String, Object>>) params.get("serviceList");
            ps = (Map<String, String>) params.get("MerBusinessProduct");
            String bp_id = ps.get("bpId");
            for (Map<String, Object> map : merServiceMap) {
                map.put("merchantNo", merchant_no);
                map.put("bpId", bp_id);
                row += merchantInfoDao.addMerService(map);
            }
            if (row != merServiceMap.size()) {
                msg = "商户服务保存失败";
                throw new AppException(msg);
            }
        }

        {
            row = 0;
            List<Map<String, String>> merServiceRate = (List<Map<String, String>>) params.get("MerServiceRate");
            if (merServiceRate != null) {
                for (Map<String, String> map : merServiceRate) {
                    map.put("merchantNo", merchant_no);
                    row += merchantInfoDao.addMerServiceRate(map);
                }
                if (row != merServiceRate.size()) {
                    msg = "商户交易费率信息保存失败";
                    throw new AppException(msg);
                }
            }
        }

        {
            row = 0;
            List<Map<String, String>> merServiceQuota = (List<Map<String, String>>) params.get("MerServiceQuota");
            if (merServiceQuota != null) {
                for (Map<String, String> map : merServiceQuota) {
                    map.put("merchantNo", merchant_no);
                    row += merchantInfoDao.addMerServiceQuota(map);
                }
                if (row != merServiceQuota.size()) {
                    msg = "商户交易限额信息保存失败";
                    throw new AppException(msg);
                }
            }

        }
        {
            row = 0;
            if (merRequireItemInfo != null) {
                for (MerRequireItem item : merRequireItemInfo) {
                    item.setMerchantNo(merchant_no);
                    item.setStatus("0");
                    row += merchantInfoDao.addMerRequireItem(item);
                }
                if (row != merRequireItemInfo.size()) {
                    msg = "商户附件资料保存失败";
                    throw new AppException(msg);
                }
            }

        }
        {
            row = 0;
            ps = (Map<String, String>) params.get("MerBusinessProduct");
            int isApprove = merchantInfoDao.isApprove(oneAgentNo);
            if (isApprove == 1) {
                ps.put("isApprove", "1");//待一审核
            }
            if (isApprove == 0) {
                ps.put("isApprove", "2");//待平台审核

                List<Map<String, Object>> auditorList = merchantInfoDao.queryAuditorManager(bpId);
                if (auditorList.size() == 1) {
                    ps.put("auditorId", auditorList.get(0).get("auditor_id").toString());
                } else if (auditorList.size() > 1) {//随机取审核人ID
                    Random rand = new Random();
                    int randNum = rand.nextInt(auditorList.size());
                    ps.put("auditorId", auditorList.get(randNum).get("auditor_id").toString());
                }

            }
            ps.put("merchantNo", merchant_no);
            row = merchantInfoDao.addMerBusinessProduct(ps);
            if (row != 1) {
                msg = "商户服务产品插入失败";
                throw new AppException(msg);
            }
        }

        {
            row = 0;
            String sns = (String) params.get("sns");
            if (StringUtils.isNotBlank(sns)) {
                Map<String, Object> snInfo = merchantInfoDao.querySn(sns);
                String psamNo = (String) snInfo.get("PSAM_NO");
                psamNo = psamNo.substring(0, 2);
                if ("jh".equals(psamNo)) {
                    String collection_code = (String) snInfo.get("collection_code");
                    Map<String, String> jhMap = new HashMap<>();
                    jhMap.put("gather_code", collection_code);
                    jhMap.put("gather_name", "初始化收银台");
                    jhMap.put("device_sn", sns);
                    jhMap.put("merchant_no", merchant_no);
                    //写入收款码
                    row = merchantInfoDao.addGathCode(jhMap);
                    if (row <= 0) {
                        msg = "收款码添加失败";
                        throw new AppException(msg);
                    }

                }
                ps.put("sns", sns);
                ps.put("bpId", bpId);
                row = merchantInfoDao.updateTerMerNo(ps);
                System.out.println("更新机具条数为" + row);
                if (row <= 0) {
                    msg = "商户添加机具失败";
                    throw new AppException(msg);
                }
            }

        }
        //保存商户同时保存一份用户信息作为管理员登录

        {    //在商户 app端注册但在代理商中进件
            Map<String, Object> map = merchantInfoDao.getMerMobilephone(mobilephone, teamId);
            if (map != null) {
                String userId = (String) map.get("userid_m");//取出USERID
                if (!StringUtils.isNotBlank(userId)) {//USERID不存在
                    String id = (String) map.get("user_id");
                    merchantInfoDao.insertMerchantUserEntity(merchant_no, id);
                } else {//USERID存在
                    if (merchantInfoDao.isSuperPuserUser(userId) != null) {
                        msg = "超级推用户请在商户APP完善资料";
                        throw new AppException(msg);
                    }
                    String userName = (String) map.get("user_name");
                    String entity_id = (String) map.get("entity_id_m");    //商户编号
                    if (userName == null || userName.equals("")) {
                        row = merchantInfoDao.updateUserName(merchant_name, userId);
                        if (row != 1) {
                            msg = "用户名更新失败";
                            throw new AppException(msg);
                        }
                    }
                    if (entity_id == null || entity_id.equals("")) {
                        row = merchantInfoDao.updateEntity(merchant_no, userId);
                        if (row != 1) {
                            msg = "用户编号更新失败";
                            throw new AppException(msg);
                        }
                    } else {
                        msg = "手机号用户已存在";
                        throw new AppException(msg);
                    }
                }
            } else {//走进件流程插入用户主表次表
                UserEntityInfo userEntityInfo = new UserEntityInfo();
                userEntityInfo.setEntityId(merchant_no);
                userEntityInfo.setManage("1");
                userEntityInfo.setStatus("1");
                userEntityInfo.setUserType("2");
                userEntityInfo.setApply("2");
                UserInfo userInfo = new UserInfo();
                userInfo.setMobilephone(mobilephone);
                userInfo.setTeamId(teamId);
                userInfo.setUserName(merchant_name);
                try {
                    row = insertMerchantUserInfo(userInfo, userEntityInfo);
                } catch (Exception e) {
                    log.error("异常{}", e);
                }
                if (row != 1) {
                    msg = "商户用户信息插入失败";
                    throw new AppException(msg);
                }
            }
        }
        //调用账户接口
        try {
            String acc = ClientInterface.createMerchantAccount(merchant_no);
            Map<String, Object> accMap = GsonUtils.fromJson2Map(acc, Object.class);
            if ((Boolean) accMap.get("status")) {
                merchantInfoDao.updateMerCountBymerNo(merchant_no, 1);
            } else {
                msg = "开设商户账户失败!";
            }
        } catch (Exception e) {
            msg = "开设商户账户失败!";
            log.error("在账户系统生成账号异常", e);
        }

        //V2代理商web端商户进件成功后，根据商户经营地址归属对应集群，
        // 集群归属逻辑与商户APP客户端一致，既有地区集群的。
        merchantInfoDao.updateMerGroupCity(merchant_no);
        return msg;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public int insertMerchantUserInfo(UserInfo userInfo, UserEntityInfo userEntityInfo) {
        UserInfo checkUser = getMobilephone(userInfo.getMobilephone(), userInfo.getTeamId());
        if (checkUser == null) {//没有用户信息但没有应用信息
            String userId = seqService.createKey(Constants.USER_NO_SEQ, new BigInteger(Constants.USER_VALUE));
            String possword = Md5.md5Str("123456{" + userInfo.getMobilephone() + "}");
            userInfo.setPassword(possword);
            userInfo.setUserId(userId);
            int row = merchantInfoDao.insertUserInfo(userInfo);
            if (row != 1) {
                String msg = "失败:用户信息插入失败";
                throw new AppException(msg);
            }
            userEntityInfo.setUserId(userId);
        } else {
            userEntityInfo.setUserId(checkUser.getUserId());
            userInfo.setPassword(checkUser.getPassword());
        }
        int entityrow = merchantInfoDao.insertAgentUserEntity(userEntityInfo);
        if (entityrow != 1) {
            String msg = "失败:商户用户信息插入失败";
            throw new AppException(msg);
        }
        return entityrow;
    }

    @Override
    public UserInfo getMobilephone(String mobilephone, String teamID) {
        return merchantInfoDao.getMobilephone(mobilephone, teamID);
    }

    @Override
    public Map<String, Object> queryMerItemDetails(Map<String, String> params, UserInfoBean userInfoBean) {
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("status", false);
        String entityNode = params.get("agent_node");
        String merchantNo = params.get("merchant_no");//商户编号
        String bpId = params.get("bp_id");//业务产品
        String oneAgentNo = params.get("one_agent_no");//一级代理商编号
        log.info("商户进件详情传参-----" + "商户号" + merchantNo + "业务产品ID" + bpId + "一级代理商编号" + oneAgentNo + "当前登录代理商节点 " + entityNode);
        List<Map<String, Object>> rateList = queryMerSerRate(merchantNo, bpId, oneAgentNo);
        rateList = rate(rateList);
        List<MerServiceRate> merServiceRateList = MyUtil.listToBeans(MerServiceRate.class, rateList);//查询商户的服务限额信息
        resultMap.put("merServiceRateList", merServiceRateList);

        List<Map<String, Object>> quotaList = queryMerSerQuota(merchantNo, bpId, oneAgentNo);
        quotaList = quota(quotaList);
        List<MerServiceQuota> merServiceQuotaList = MyUtil.listToBeans(MerServiceQuota.class, quotaList);
        resultMap.put("merServiceQuotaList", merServiceQuotaList);

        List<Map<String, Object>> prayerList = queryMerCardInfo(merchantNo, bpId);//没有图片的时件项
        List<MerRequireItem> prayerRequireItemList = MyUtil.listToBeans(MerRequireItem.class, prayerList);
        resultMap.put("prayerRequireItemList", prayerRequireItemList);

        resultMap.put("merInfo", queryMerInfo(entityNode, merchantNo));

        List<Map<String, Object>> photoList = queryMerRequireItem(merchantNo, bpId);
        Date expiresDate = new Date(Calendar.getInstance().getTime().getTime() * 3600 * 1000);
        for (Map<String, Object> merRequireItem : photoList) {
            String itemStatus = (String) merRequireItem.get("status");
            String itemId = (String) merRequireItem.get("mri_id");
            String content = "";
            String newContent = "";
            if ("5".equals(itemId)) {
                content = StringUtils.filterNull(merRequireItem.get("content"));
                Map<String, Object> cnapsInfo = merchantInfoDao.queryCnapsInfo(content);
                merRequireItem.put("subBank", cnapsInfo.get("bank_name"));
            }
            if ("0".equals(itemStatus) || "1".equals(itemStatus)) {
                merRequireItem.put("content", merRequireItem.get("photo_address"));//取定义的图片地址
            }
            /*else if("2".equals(itemStatus)){
                merRequireItem.put("content",merRequireItem.get("content"));//取失败的图片地址
            }*/
            content = (String) merRequireItem.get("content");
            newContent = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, content, expiresDate);
            merRequireItem.put("content", newContent);
        }
        List<MerRequireItem> photoRequireItemList = MyUtil.listToBeans(MerRequireItem.class, photoList);
        resultMap.put("photoRequireItemList", photoRequireItemList);
        resultMap.put("status", true);
        resultMap.put("msg", "查询商户进件详情成功");
        log.info("商户号===>" + merchantNo + "查询进件查询详情成功");
        return resultMap;
    }

    @Override
    public List<Map<String, Object>> queryMerSerRate(String merchantId, String bpId, String oneAgentNo) {
        return merchantInfoDao.queryMerSerRate(merchantId, bpId, oneAgentNo);
    }

    @Override
    public List<Map<String, Object>> queryMerSerQuota(String merchantId, String bpId, String oneAgentNo) {
        return merchantInfoDao.queryMerSerQuota(merchantId, bpId, oneAgentNo);
    }

    @Override
    public List<Map<String, Object>> queryMerCardInfo(String merchant_no, String bpId) {
        return merchantInfoDao.queryMerCardInfo(merchant_no, bpId);
    }

    @Override
    public List<Map<String, Object>> queryMerRequireItem(String merchant_no, String bp_id) {
        return merchantInfoDao.queryTerRequireItem(merchant_no, bp_id);
    }

    @Override
    public MerchantInfo queryMerInfo(String parent_node, String merchant_no) {
        MerchantInfo merInfo = merchantInfoDao.queryMerInfo(parent_node, merchant_no);
        if (merInfo == null) {
            return null;
        }
        String merchant_type = merInfo.getMerchantType();
        String business_type = merInfo.getBusinessType();
        String industry_type = merInfo.getIndustryType();
        merInfo.setIndustryMcc(industry_type);
        Map<String, Object> typeMap = merchantInfoDao.queryTypeMcc("sys_mcc", business_type);
        Map<String, Object> mccMap = merchantInfoDao.queryTypeMcc("sys_mcc", industry_type);
        if (StringUtils.equalsIgnoreCase(merchant_type, "1")) {
            merInfo.setMerchantType("个人");
        } else if (StringUtils.equalsIgnoreCase(merchant_type, "2")) {
            merInfo.setMerchantType("个体商户");
        } else if (StringUtils.equalsIgnoreCase(merchant_type, "3")) {
            merInfo.setMerchantType("企业商户");
        }
        if (MapUtils.isNotEmpty(typeMap)) {
            merInfo.setBusinessTypeName(Objects.toString(typeMap.get("sys_name"), ""));
        }
        if (MapUtils.isNotEmpty(mccMap)) {
            merInfo.setIndustryTypeName(Objects.toString(mccMap.get("sys_name"), ""));
        }
//        StringBuilder sb = new StringBuilder();
//        sb.append(StringUtils.trimToEmpty(merInfo.getProvince()))
//                .append("-")
//                .append(StringUtils.trimToEmpty(merInfo.getCity()))
//                .append("-")
//                .append(StringUtils.trimToEmpty(merInfo.getDistrict()));
//        merInfo.setProvince(sb.toString());
//        sb.append("-");
//        String address = merInfo.getAddress();
//        if (StringUtils.isNotBlank(address)) {
//            if (address.startsWith(sb.toString())) {
//                merInfo.setAddress(address.replace(sb.toString(), ""));
//            }
//        }
        return merInfo;
    }

    private List<Map<String, Object>> rate(List<Map<String, Object>> rateList) {
        for (Map<String, Object> map : rateList) {
            takeString(map);
            String rateType = (String) map.get("rate_type");
            if (rateType.equals("1")) {
                Object single_num_amount = map.get("single_num_amount");
                System.out.println(single_num_amount.toString());
                map.put("rate", single_num_amount);
            } else if (rateType.equals("2")) {
                Object rate = map.get("rate");
                StringBuffer sb = new StringBuffer();
                sb.append(rate);
                sb.append("%");
                map.put("rate", sb.toString());
            } else if (rateType.equals("3")) {
                Object rate = map.get("rate");
                Object safe_line = map.get("safe_line");
                Object capping = map.get("capping");
                StringBuffer sb = new StringBuffer();
                sb.append(safe_line + "~" + rate + "%" + "~" + capping);
                map.put("rate", sb.toString());
            } else if (rateType.equals("4")) {
                Object rate = map.get("rate");
                Object single_num_amount = map.get("single_num_amount");
                StringBuffer sb = new StringBuffer();
                sb.append(rate + "%" + "+" + single_num_amount);
                map.put("rate", sb.toString());
            } else if (rateType.equals("5")) {
                Object ladder1_rate = map.get("ladder1_rate");
                Object ladder1_max = map.get("ladder1_max");
                Object ladder2_rate = map.get("ladder2_rate");
                StringBuffer sb = new StringBuffer();
                sb.append(ladder1_rate + "%<" + ladder1_max + "<" + ladder2_rate + "%");
                map.put("rate", sb.toString());
            }
        }
        return rateList;
    }

    private void takeString(Map<String, Object> map) {
        if (map.get("card_type").equals("0")) {
            map.put("card_type", "不限");
        } else if (map.get("card_type").equals("1")) {
            map.put("card_type", "只信用卡");
        } else if (map.get("card_type").equals("2")) {
            map.put("card_type", "只储蓄卡");
        }

        if (map.get("holidays_mark").equals("0")) {
            map.put("holidays_mark", "不限");
        } else if (map.get("holidays_mark").equals("1")) {
            map.put("holidays_mark", "只工作日");
        } else if (map.get("holidays_mark").equals("2")) {
            map.put("holidays_mark", "只节假日");
        }
    }

    private List<Map<String, Object>> quota(List<Map<String, Object>> quotaList) {
        for (Map<String, Object> map : quotaList) {
            takeString(map);
        }
        return quotaList;
    }

    @Override
    public boolean getAcqMerRecSwitch(String one_agent_no, String function_number) {
        Map<String, Object> map = merchantInfoDao.getFunctionManage(function_number);
        String is = (String) map.get("function_switch");
        if (is.equals("1")) {
            is = (String) map.get("agent_control");
            if (is.equals("1")) {
                map = merchantInfoDao.getFunctionManageByAgentNo(one_agent_no, function_number);
                if (map != null && map.size() > 0) {
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getAcqMerInfoItemList(Map<String, Object> map) {
        return merchantInfoDao.getAcqMerInfoItemList(map);
    }

    @Override
    public Map<String, Object> getAcqMerInfoByAcqIntoNo(String acqIntoNo) {
        return merchantInfoDao.getAcqMerInfoByAcqIntoNo(acqIntoNo);
    }

    @Override
    public List<Map<String, Object>> getAcqMerInfoIdFile(String acqIntoNo) {
        return merchantInfoDao.getAcqMerInfoIdFile(acqIntoNo);
    }

    @Override
    public Map<String, Object> checkAcqMerRegister(AcqMerInfo acqMerInfo) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", false);
        String merchant_type = acqMerInfo.getMerchant_type();
        String merchant_name = acqMerInfo.getMerchant_name();
        String one_scope = acqMerInfo.getOne_scope();
        String two_scope = acqMerInfo.getTwo_scope();
        String province = acqMerInfo.getProvince();
        String city = acqMerInfo.getCity();
        //String district = params.get("district");
        String address = acqMerInfo.getAddress();
        String legal_person = acqMerInfo.getLegal_person();
        String legal_person_id = acqMerInfo.getLegal_person_id();
        String id_valid_start = acqMerInfo.getId_valid_start();
        String id_valid_end = acqMerInfo.getId_valid_end();
        String account_type = acqMerInfo.getAccount_type();
        String bank_no = acqMerInfo.getBank_no();
        String account_name = acqMerInfo.getAccount_name();
        String account_bank = acqMerInfo.getAccount_bank();
        String account_province = acqMerInfo.getAccount_province();
        String account_city = acqMerInfo.getAccount_city();
        //String account_district = params.get("account_district");
        String bank_branch = acqMerInfo.getBank_branch();
        String line_number = acqMerInfo.getLine_number();
        String charter_name = acqMerInfo.getCharter_name();
        String charter_no = acqMerInfo.getCharter_no();
        String charter_valid_start = acqMerInfo.getCharter_valid_start();
        String charter_valid_end = acqMerInfo.getCharter_valid_end();

        log.info("进件参数-------------" + acqMerInfo.toString());
        if (StringUtils.isEmpty(merchant_type) ||
                StringUtils.isEmpty(one_scope) || StringUtils.isEmpty(two_scope) ||
                StringUtils.isEmpty(province) || StringUtils.isEmpty(city) ||
                StringUtils.isEmpty(address) || StringUtils.isEmpty(legal_person) ||
                StringUtils.isEmpty(legal_person_id) || StringUtils.isEmpty(id_valid_start) ||
                StringUtils.isEmpty(id_valid_end) || StringUtils.isEmpty(account_type) ||
                StringUtils.isEmpty(bank_no) || StringUtils.isEmpty(account_name) ||
                StringUtils.isEmpty(account_bank) || StringUtils.isEmpty(account_province) ||
                StringUtils.isEmpty(account_city) || StringUtils.isEmpty(charter_name) ||
                StringUtils.isEmpty(charter_no) || StringUtils.isEmpty(charter_valid_start) ||
                StringUtils.isEmpty(charter_valid_end)) {
            res.put("msg", "必要参数为空");
            return res;
        }
        //选择对私时，支行和联行号必填
        if("1".equals(account_type)){
            if(StringUtils.isEmpty(bank_branch) ||StringUtils.isEmpty(line_number)){
                res.put("msg", "必要参数为空");
                return res;
            }
        }
        if (charter_name.length() > 20) {
            res.put("msg", "营业执照名称字数超过20个字符");
            return res;
        }
        if (!StringUtils.isConSpeCharacters(merchant_name)) {
            res.put("msg", "商户名称包含特殊字符，请重新输入！");
            return res;
        }
        if (merchant_name.indexOf(" ") != -1) {
            res.put("msg", "商户名称包含空格，请重新输入！");
            return res;
        }
        if (SposUtils.isUpdateIdCard(acqMerInfo)) {
            Map<String, Object> regAgeScope = validateCard(legal_person_id);
            if (!(Boolean) regAgeScope.get("status")) {
                res.put("msg", regAgeScope.get("msg").toString());
                return res;
            }
        }
        res.put("status", true);
        return res;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public int addAcqMerInfo(AcqMerInfo acqMerInfo) {
        return merchantInfoDao.addAcqMerInfo(acqMerInfo);
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public void updateAcqMerInfo(AcqMerInfo acqMerInfo) {
        //更新商户审核状态
        merchantInfoDao.updateAcqMerInfo(acqMerInfo);
        //更新图片审核状态
        merchantInfoDao.updateAcqMerFileInfoStatus(acqMerInfo.getAcq_into_no());
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public int addAcqMerFileInfo(Map<String, String> map) {
        return merchantInfoDao.addAcqMerFileInfo(map);
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public int updateAcqMerFileInfo(Map<String, String> map) {
        return merchantInfoDao.updateAcqMerFileInfo(map);
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public ResponseBean insertAcqMerInfo(Map<String, Object> paramsMap,
                                         UserInfoBean userInfoBean, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", true);
        Map<String, Object> map = GsonUtils.fromJson2Map(paramsMap.get("acqMerInfo").toString(), Object.class);
        AcqMerInfo acqMerInfo = BeanUtil.toBean(map, AcqMerInfo.class);
        String acq_into_no = acqMerInfo.getAcq_into_no();
        //校验下拉业务产品列表
        String valiAcqMerBusRes = SposUtils.valiAcqMerBus(acqMerInfo);
        if (StringUtils.isNotBlank(valiAcqMerBusRes)) {
            return ResponseBean.error(valiAcqMerBusRes);
        }
        Map<String, Object> baseInfo = checkAcqMerRegister(acqMerInfo);
        if ((Boolean) baseInfo.get("status")) {
            boolean isFileUp = true;
            log.info("进件保存基本信息校验通过-----------");
            //商户名称取营业执照名称
            acqMerInfo.setMerchant_name(acqMerInfo.getCharter_name());
            if (StringUtils.isEmpty(acq_into_no)) {
                //生成进件ID
                UUID randomUUID = UUID.randomUUID();
                String intoNo = randomUUID.toString().substring(0, 8) + new Date().getTime();
                acqMerInfo.setAcq_into_no(intoNo);
                if(StringUtils.isNotBlank(acqMerInfo.getMerchant_no())){
                    //如果没有修改身份证号，则取原始值
                    if (!SposUtils.isUpdateIdCard(acqMerInfo)) {
                        acqMerInfo.setLegal_person_id(acqMerchantService.getMerItemByNoAndMriId(acqMerInfo.getMerchant_no(), "6"));
                    }
                    //如果没有修改银行卡号，则不执行sql字段修改
                    if (!SposUtils.isUpdateBankNo(acqMerInfo)) {
                        acqMerInfo.setBank_no(acqMerchantService.getMerItemByNoAndMriId(acqMerInfo.getMerchant_no(), "3"));
                    }
                }
                addAcqMerInfo(acqMerInfo);
            } else {
                isFileUp = false;
                //如果没有修改身份证号，则不执行sql字段修改
                if (!SposUtils.isUpdateIdCard(acqMerInfo)) {
                    acqMerInfo.setLegal_person_id(null);
                }
                //如果没有修改银行卡号，则不执行sql字段修改
                if (!SposUtils.isUpdateBankNo(acqMerInfo)) {
                    acqMerInfo.setBank_no(null);
                }
                updateAcqMerInfo(acqMerInfo);
            }
            List<String> hasFileType = new ArrayList();
            /**
             * 取客户端上传的文件
             * 与客户端约定文件名是{进件项ID.文件后缀名}
             */
            if ((Boolean) resultMap.get("status")) {
                MultipartRequest qq = (MultipartRequest) request;
                Map<String, MultipartFile> merFiles = qq.getFileMap();
                MultiValueMap<String, MultipartFile> multiFileMap = qq.getMultiFileMap();
                List<MultipartFile> fileList = multiFileMap.get("file");
                if (fileList != null && fileList.size() > 0) {//安卓
                    for (MultipartFile file : fileList) {
                        String fileName = file.getOriginalFilename();
                        String fileType = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
                        hasFileType.add(fileType);
                        acqLoadPicture(acqMerInfo, isFileUp, file, fileName);
                    }
                } else {
                    for (String key : merFiles.keySet()) {
                        MultipartFile merFile = merFiles.get(key);
                        String fileName = merFile.getOriginalFilename();
                        String fileType = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
                        hasFileType.add(fileType);
                        acqLoadPicture(acqMerInfo, isFileUp, merFile, fileName);
                    }
                }
            }//上传文件结束

            if("1".equals(acqMerInfo.getAccount_type())){
                //选择对私的时候要删除之前的开户许可证照片资料
                acqMerchantDao.deleteAcqMerFile("23", acqMerInfo.getAcq_into_no());
            }

            //处理未上传照片，取原商户进件时的资质图片
            if(isFileUp && StringUtils.isNotBlank(acqMerInfo.getMerchant_no())){
                String[] allFileIdArray = new String[]{"9", "10", "11", "12", "13", "30", "14"};
                List<String> arrayToList = Arrays.asList(allFileIdArray);
                List<String> allFileIdList = new ArrayList(arrayToList);
                if("2".equals(acqMerInfo.getAccount_type())){
                    allFileIdList.add("23");
                }
                String fileName = "";
                for(String fileId : allFileIdList){
                    if(hasFileType.size() == 0 || !hasFileType.contains(fileId)){
                        fileName = acqMerchantService.getMerItemByNoAndMriId(acqMerInfo.getMerchant_no(), fileId);
                        if(StringUtils.isNotBlank(fileName)){
                            Map<String, String> fileMap = new HashMap<>();
                            fileMap.put("file_type", fileId);
                            fileMap.put("file_url", fileName);
                            fileMap.put("status", "1");
                            fileMap.put("acq_into_no", acqMerInfo.getAcq_into_no());
                            addAcqMerFileInfo(fileMap);
                        }
                    }
                }
            }
            return ResponseBean.success("提交收单商户进件成功");
        } else {
            throw new AppException(baseInfo.get("msg").toString());
        }
    }

    private void acqLoadPicture(AcqMerInfo acqMerInfo, boolean isFileUp, MultipartFile merFile, String fileName) {
        //fileName为进件项ID，将进件项ID与阿里云上的文件名对应起来
        int random = new Random().nextInt(100000);
        String value = fileName.substring(0, fileName.indexOf("."));
        String fileType = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
        value = value + "_" + DateUtils.getMessageTextTime() + "_" + random + ".jpg";
        //此处调用阿里云的存储方法，将文件流存储至阿里云
        uploadPicture(merFile, value);
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("file_type", fileType);
        fileMap.put("file_url", value);
        fileMap.put("status", "1");
        fileMap.put("acq_into_no", acqMerInfo.getAcq_into_no());
        if (isFileUp) {
            addAcqMerFileInfo(fileMap);
        } else {
            updateAcqMerFileInfo(fileMap);
        }
    }

    /**
     * 根据关键字查询商户列表
     *
     * @param merchantKey 关键字
     * @param agentNode   代理商节点
     * @return
     */
    @Override
    public List<Map<String, Object>> queryMerListBykey(String merchantKey, String agentNode) {
        if (StringUtils.isBlank(merchantKey, agentNode)) {
            return null;
        }
        return merchantInfoDao.queryMerListBykey(merchantKey, agentNode);
    }

    @Override
    public List<Map<String, Object>> getBpId(String agentNo) {
        return merchantInfoDao.querybpd(agentNo);
    }

    @Override
    public List<Map<String, Object>> getHardProduct(String agentNo) {
        if (StringUtils.isNotBlank(agentNo)) {
            return merchantInfoDao.getHardwareProductByAgentOem(agentNo);
        }
        return merchantInfoDao.getHardwareProduct();
    }

    @Override
    public List<Map<String, Object>> getAgentBusiness(String agentNo) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("bp_id", "");
        map.put("bp_name", "全部");
        result.add(map);
        if (StringUtils.isBlank(agentNo)) {
            return result;
        }
        result.addAll(Optional.ofNullable(merchantInfoDao.getAgentBusiness(agentNo)).orElse(new ArrayList<>()));
        return result;
    }

    /**
     * 获取代理商所属产品（组织机构）
     *
     * @param agentNo
     * @param isShowNull 是否展示空数据
     * @return
     */
    @Override
    public List<Map<String, Object>> getAgentTeams(String agentNo, boolean isShowNull) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        if (isShowNull) {
            map.put("team_id", "");
            map.put("team_name", "全部");
            result.add(map);
        }
        if (StringUtils.isBlank(agentNo)) {
            return result;
        }
        result.addAll(Optional.ofNullable(merchantDao.listTeamNameByAgentNo(agentNo)).orElse(new ArrayList<>()));
        return result;
    }

    @Override
    public MerchantInfo queryMerchantInfo(String merchantNo) {
        return merchantDao.queryMerchantInfoByNo(merchantNo);
    }

    @Override
    public Integer selectSuperPushTerminal(String teamId, String sn) {
        return merchantInfoDao.selectSuperPushTerminal(teamId, sn);
    }

    @Override
    public String selectTeamIdByBpId(String bpId) {
        return merchantInfoDao.selectTeamIdByBpId(bpId);
    }

    @Override
    public Map<String, Object> getUserByMerNo(String merchantNo) {
        return merchantInfoDao.getUserByMerNo(merchantNo);
    }

    @Override
    public List<Map<String, Object>> getBpHpByMerNo(String merchantNo) {
        return merchantInfoDao.getBpHpByMerNo(merchantNo);
    }

    @Override
    public SysDict getByKey(String key) {
        return sysDictDao.getByKey(key);
    }

    @Override
    public String getStringValueByKey(String key) {
        return sysConfigDao.getStringValueByKey(key);
    }


    @Override
    public MerchantInfo selectByMerchantNo(String merchantNo) {
        return merchantInfoDao.selectByMerchantNo(merchantNo);
    }
}
