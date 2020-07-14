package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.modules.bean.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description
 * @date 2019/5/21
 */
public interface MerchantInfoService {

    /**
     * 校验结算账号
     *
     * @param settleAccountNo 结算账号
     * @return
     */
    Map<String, Object> checkSettleAccountNo(String settleAccountNo);

    /**
     * 校验身份证
     *
     * @param IdCardNo
     * @return
     */
    Map<String, Object> validateCard(String IdCardNo);

    /**
     * 校验三码(开户名,身份证,结算卡号)认证
     */
    Map<String, Object> checkBaseInfoCheck(Map<String, String> params);

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
    Map<String, String> doAuthen(String bankCode, String name, String idCard, String phoneNum);

    /**
     * 获取支行信息
     *
     * @param params
     * @return
     */
    Map<String, Object> getBankAndCnap(Map<String, String> params);

    /**
     * 查询商户可以适用的业务产品
     *
     * @param params
     * @return
     */
    Map<String, Object> getMerProductList(Map<String, String> params);

    /**
     * 收单商户进件大小类
     *
     * @param sysKey
     * @param parentId
     * @return
     */
    List<Map<String, Object>> getAcqMerMccList(String sysKey, String parentId);

    /**
     * 根据代理商编号，业务产品编号，获取服务列表
     *
     * @param agent_no
     * @param pb_id
     * @return
     */
    List<ServiceInfo> getServiceInfoByParams(String agent_no, String pb_id);

    List<Map<String, Object>> getMerchantInfoList(Map<String, String> merchantInfo,UserInfoBean userInfoBean);


    Map<String, Object> queryBpInfo(String bpId);

    /**
     * 验证商户是否注册过
     *
     * @param params
     * @return
     */
    Map<String, Object> checkRegistRules(Map<String, String> params);

    List<ServiceRate> getServiceRatedByParams(String agent_no, String pb_id);

    /**
     * 根据代理商编号，业务产品编号，获取服务限额列表
     *
     * @param agent_no
     * @param pb_id
     * @return
     */
    List<ServiceQuota> getServiceQuotaByParams(String agent_no, String pb_id);

    /**
     * 根据代理商编号，业务产品编号，获取业务产品进件资质项列表
     *
     * @param agent_no
     * @param pb_id
     * @return
     */
    List<AddRequireItem> getRequireItemByParams(String agent_no, String pb_id);

    List<Map<String, Object>> queryMerType(String syskey, String parentId);

    /**
     * 我要进件
     *
     * @param params
     * @param userInfoBean
     * @return
     */
    ResponseBean insertMerchantInfo(Map<String, Object> params, UserInfoBean userInfoBean, HttpServletRequest request);

    Map<String, Object> checkRegister(Map<String, Object> params);


    /**
     * 校验费率真格式
     *
     * @param list
     * @param listQuota
     * @return
     */
    Map<String, Object> checkquota(List<MerServiceQuota> list, List<Map<String, Object>> listQuota);

    /**
     * 校验费率格式
     *
     * @param list
     * @return
     */
    Map<String, Object> checkRate(List<MerServiceRate> list, List<Map<String, Object>> listRate);

    /**
     * 新增商户
     *
     * @param params
     * @return 商户编号
     */
    String addMer(Map<String, Object> params);

    /**
     * 保存商户管理员人员信息
     *
     * @param userInfo
     * @return
     * @throws Exception
     */
    int insertMerchantUserInfo(UserInfo userInfo, UserEntityInfo userEntityInfo);

    /**
     * 根据手机号和组织ID获取用户信息
     *
     * @param phoneNo
     * @param teamID
     * @return
     * @throws Exception
     */
    UserInfo getMobilephone(String phoneNo, String teamID);

    /**
     * 商户详情查询
     *
     * @param params
     * @param userInfoBean
     * @return
     */
    Map<String, Object> queryMerItemDetails(Map<String, String> params, UserInfoBean userInfoBean);

    /**
     * 查询商户的服务费率信息
     *
     * @param queryMerSerRate
     * @param bp_id
     * @param oneAgentNo
     * @return
     */
    List<Map<String, Object>> queryMerSerRate(String queryMerSerRate, String bp_id, String oneAgentNo);

    /**
     * 查询商户的服务限额信息
     *
     * @param merchantId 商户编号
     * @param bpId       业务产品
     * @param oneAgentNo
     * @return
     * @author JA.Zeng
     * @date 2016年5月5日 下午6:18:59
     */
    List<Map<String, Object>> queryMerSerQuota(String merchantId, String bpId, String oneAgentNo);

    /**
     * 查询商户信息及结算卡信息
     *
     * @param merchant_no
     * @param bp_id
     * @return
     */
    List<Map<String, Object>> queryMerCardInfo(String merchant_no, String bp_id);

    /**
     * 查询单个商户
     *
     * @param parent_node
     * @param merchant_no
     * @return
     */
    MerchantInfo queryMerInfo(String parent_node, String merchant_no);

    /**
     * 查询商户进件图
     *
     * @param merchant_no
     * @return
     */
    List<Map<String, Object>> queryMerRequireItem(String merchant_no, String bp_id);

    /**
     * 功能总开关
     *
     * @param one_agent_no
     * @param function_number
     * @return
     */
    boolean getAcqMerRecSwitch(String one_agent_no, String function_number);

    /**
     * 商户进件列表查询
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> getAcqMerInfoItemList(Map<String, Object> map);

    /**
     * 收单商户进件详情
     *
     * @param acqIntoNo
     * @return
     */
    Map<String, Object> getAcqMerInfoByAcqIntoNo(String acqIntoNo);

    /**
     * 收单商户进件附件
     *
     * @param id
     * @return
     */
    List<Map<String, Object>> getAcqMerInfoIdFile(String id);

    Map<String, Object> checkAcqMerRegister(AcqMerInfo acqMerInfo);

    /**
     * 收单商户进件添加
     *
     * @param acqMerInfo
     * @return
     */
    int addAcqMerInfo(AcqMerInfo acqMerInfo);

    /**
     * 收单商户进件修改
     *
     * @param acqMerInfo
     * @return
     */
    void updateAcqMerInfo(AcqMerInfo acqMerInfo);

    /**
     * 收单商户进件附件添加
     *
     * @param map
     * @return
     */
    int addAcqMerFileInfo(Map<String, String> map);

    /**
     * 收单商户进件附件修改
     *
     * @param map
     * @return
     */
    int updateAcqMerFileInfo(Map<String, String> map);

    /**
     * 收单商户进件
     *
     * @param map
     * @return
     */
    ResponseBean insertAcqMerInfo(Map<String, Object> map, UserInfoBean userInfoBean, HttpServletRequest request);

    /**
     * 根据关键字查询商户列表
     *
     * @param merchantKey 关键字
     * @param agentNode   代理商节点
     * @return
     */
    List<Map<String, Object>> queryMerListBykey(String merchantKey, String agentNode);

    /**
     * 查询业务产品列表
     *
     * @param agentNo
     * @return
     */
    List<Map<String, Object>> getBpId(String agentNo);

    /**
     * 机具种类查询
     *
     * @param agentNo
     * @return
     */
    List<Map<String, Object>> getHardProduct(String agentNo);

    /**
     * 获取代理商业务产品
     *
     * @param agentNo
     * @return
     */
    List<Map<String, Object>> getAgentBusiness(String agentNo);

    /**
     * 获取代理商所属产品（组织机构）
     *
     * @param agentNo
     * @param isShowNull 是否展示为空
     * @return
     */
    List<Map<String, Object>> getAgentTeams(String agentNo, boolean isShowNull);

    MerchantInfo queryMerchantInfo(String merchantNo);

    ResponseBean updateMerchantInfo(String merchantNo, Map<String, Object> paramsMap, UserInfoBean userInfoBean, HttpServletRequest request);

    Integer selectSuperPushTerminal(String teamId, String sn);

    String selectTeamIdByBpId(String bpId);

    Map<String, Object> getUserByMerNo(String merchantNo);

    List<Map<String, Object>> getBpHpByMerNo(String merchantNo);

    SysDict getByKey(String key);

    String getStringValueByKey(String key);

    MerchantInfo selectByMerchantNo(String merchantNo);
}
