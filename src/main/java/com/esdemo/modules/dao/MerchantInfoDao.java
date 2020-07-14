package com.esdemo.modules.dao;

import com.esdemo.frame.enums.AcqMerAuditStatus;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description
 * @date 2019/5/21
 */
@Mapper
public interface MerchantInfoDao {

    /**
     * 校验身份证与银行卡是否黑名单、白名单
     * @param rollNo 编号
     * @param rollType 名单类型1：商户编号 2：身份证号 3：银行卡号 4：钱包T0出账 5：商户号白名单 6：实名认证白名单'
     * @param rollBelong 1：白名单  2：黑名单
     * @return
     */
    Map<String,Object> findBlacklist(@Param("rollNo")String  rollNo, @Param("rollType")String  rollType, @Param("rollBelong")String  rollBelong);

    /**
     * 商户结算卡
     * @param accountNo
     * @return
     */
    Map<String,Object> querySettleAccountNo(@Param("accountNo")String  accountNo);

    /**
     * 年龄限制
     * @param PARAM_KEY
     * @return
     */
    Map<String,Object> queryAgeLimit(@Param("PARAM_KEY")String  PARAM_KEY);

    /**
     * 结算卡
     * @param bank_name1
     * @param bank_name2
     * @return
     */
    List<Map<String,Object>> queryCnaps(@Param("bank_name1")String  bank_name1, @Param("bank_name2")String  bank_name2);

    /**
     * 根据sn查机具
     * @param sn
     * @return
     */
    Map<String, Object> querySn(@Param("sn")String sn);

    /**
     * 根据sn和代理商编号查机具
     * @param agentNo
     * @param sn
     * @return
     */
    Map<String,Object>checkAgentSn(@Param("agentNo")String  agentNo,@Param("sn")String sn);

    /**
     * 查询商户可以适用的业务产品
     * @param merType
     * @param agentNo
     * @param terType
     * @return
     */
    List<Map<String,Object>> getMerProductList(@Param("merType")String merType,@Param("agentNo")String agentNo,@Param("terType")String terType);

    List<ServiceInfo> getServiceInfoByParams(@Param("agent_no") String agent_no, @Param("bp_id") String bp_id);

    Map<String,Object>  queryBpId(@Param("bpId")String bpId);

    Map<String,Object> queryMerchantInfo(@Param("params")Map<String, String> params);

    List<ServiceRate> getServiceRatedByParams(@Param("one_agent_no") String one_agent_no,@Param("bp_id") String bp_id);

    List<ServiceQuota> getServiceQuotaByParams(@Param("one_agent_no") String one_agent_no,@Param("bp_id") String bp_id);

    List<AddRequireItem> getRequireItemByParams(@Param("agent_no") String agent_no,@Param("bp_id") String bp_id);

    /**
     * 查询MCC
     * @param syskey
     * @param parentId
     * @return
     */
    List<Map<String,Object>> queryMerType(@Param("syskey")String syskey,@Param("parentId")String parentId);

    /**
     * 在同一个组织下，统计一个身份证注册的商户数
     * @param id_card_no 身份证号码
     * @param team_id 组织ID
     * @return
     * @author ZengJA
     * @date 2017-08-25 01:12:20
     */
    Map<String, Object> countMerByIdCardInTeam(@Param("id_card_no") String id_card_no, @Param("team_id") String team_id);

    Map<String, Object> countMerByIdCard(@Param("id_card_no") String id_card_no);

    List<Map<String,Object>> getServiceRateReq(@Param("one_agent_no") String one_agent_no,@Param("bp_id") String bp_id);

    List<Map<String,Object>> getServiceQuotaReq(@Param("one_agent_no") String one_agent_no,@Param("bp_id") String bp_id);

    Map<String,Object> selectRiskRule(@Param("ruleNo")String ruleNo);

    /**
     * 校验是否为一审或平台审0-否，1-是
     * @param oneAgentNO
     * @return
     */
    int isApprove(@Param("oneAgentNO")String oneAgentNO);

    List<Map<String,Object>> queryAuditorManager(@Param("bpId")String bpId);

    Map<String,Object> isSuperPuserUser(@Param("userId")String userId);

    /**
     * 根据商户经营地址归属对应集群
     */
    int updateMerGroupCity(@Param("merNo")String merNo);

    Map<String, Object> getMerMobilephone(@Param("mobilephone") String mobilephone, @Param("teamID") String teamID);

    /**
     * 保存商户用户实体类信息
     *
     * @param merchantNo
     * @param userId
     * @return
     */
    int insertMerchantUserEntity(@Param("merchantNo") String merchantNo, @Param("userId") String userId);

    /**
     * 更新用户名
     *
     * @param userName
     * @param userId
     * @return
     */
    int updateUserName(@Param("userName") String userName, @Param("userId") String userId);

    /**
     * 更新编号
     *
     * @param entity_id
     * @param userId
     * @return
     */
    int updateEntity(@Param("entity_id") String entity_id, @Param("userId") String userId);

    /**
     * 根据手机号查询用户信息
     *
     * @param mobilephone
     * @return
     */
    UserInfo getMobilephone(@Param("mobilephone") String mobilephone, @Param("teamID") String teamID);

    /**
     * 保存代理商用户实体类信息
     *
     * @param userEntityInfo
     * @return
     */
    int insertAgentUserEntity(@Param("userEntityInfo") UserEntityInfo userEntityInfo);

    /**
     * 保存用户信息
     *
     * @param userInfo
     * @return
     */
    int insertUserInfo(@Param("userInfo") UserInfo userInfo);

    /**
     * 修改是否有商户账号状态
     */
    int updateMerCountBymerNo(@Param("merNo")String merNo,@Param("merCount")int merCount);

    /**
     * 查询商户的服务费率信息
     * @author JA.Zeng
     * @param bp_id
     * @param merchant_no
     * @return
     * @date 2016年5月5日 下午6:14:49
     */
    List<Map<String,Object>> queryMerSerRate(@Param("merchant_no") String merchant_no,@Param("bp_id") String bp_id,@Param("one_agentNo") String one_agentNo);

    /**
     * 查询商户的服务限额信息
     * @param bp_id
     * @param merchant_no
     * @return
     * @date 2016年5月5日 下午6:17:01
     */
    List<Map<String,Object>> queryMerSerQuota(@Param("merchant_no") String merchant_no,@Param("bp_id") String bp_id,@Param("one_agentNo") String one_agentNo);

    /**
     * 查询商户银行卡信息
     * @param merchant_no
     * @param bp_id
     * @return
     */
    List<Map<String,Object>> queryMerCardInfo(@Param("merchant_no") String merchant_no,@Param("bp_id") String bp_id);

    MerchantInfo queryMerInfo(@Param("parent_node") String parent_node, @Param("merchant_no") String merchant_no);

    MerchantInfo selectByMerchantNo(@Param("merchant_no") String merchant_no);

    /**
     * 查出sysvalue相应的各称
     * @param syskey
     * @param sysValue
     * @return
     */
    Map<String,Object> queryTypeMcc(@Param("syskey")String syskey,@Param("sysValue")String sysValue);

    List<Map<String,Object>> queryTerRequireItem(@Param("merchant_no") String merchant_no,@Param("bp_id") String bp_id);

    Map<String, Object> getFunctionManage(@Param("code")  String code);

    Map<String, Object> getFunctionManageByAgentNo(@Param("agentNo") String agentNo,@Param("code") String code);

    Map<String, Object> getAcqMerInfoByAcqIntoNo(@Param("acqIntoNo") String acqIntoNo);

    List<Map<String, Object>> getAcqMerInfoIdFile(@Param("acqIntoNo") String acqIntoNo);

    int addAcqMerFileInfo(@Param("map") Map<String, String> map);

    int updateAcqMerFileInfo(@Param("map") Map<String, String> map);

    /**
     * 进件审核意见
     * @param itemId
     * @return
     */
    @Select(
            "select * from examinations_log where item_no = #{itemId} ORDER BY create_time DESC  limit 1 "
    )
    Map<String,Object> queryExaminationOpinions(@Param("itemId")String  itemId);

    @Select(" select * from pos_cnaps where cnaps_no =  #{cnapsNo}  ")
    Map<String,Object> queryCnapsInfo(@Param("cnapsNo") String cnapsNo);

    /**
     *收单商户进件大类小类查询
     * @param sysKey
     * @param parentId
     * @return
     */
    List<Map<String, Object>> getAcqMerMccList(@Param("sysKey") String sysKey,@Param("parentId") String parentId);

    @Select("SELECT" +
            "        uei.user_id,mi.merchant_no,mi.province $province,mi.city $city,mi.create_time registration_time" +
            "        ,mi.agent_no agent_no,mi.`status` merchant_status,ai.sale_name sales" +
            "        ,mi.one_agent_no first_level_agent_no,mi.recommended_source recmand_source" +
            "        ,mi.id_card_no,mi.team_id orgnize_id,'商户' user_type" +
            "        ,'' vip_level,jd.device_id device_id,mri.content bank_name " +
            "        ,mi.create_time  submit_time, mi.register_source sign_source " +
            "        from user_entity_info uei " +
            "        LEFT JOIN merchant_info mi on uei.user_type = 2 and mi.merchant_no = uei.entity_id" +
            "        LEFT JOIN agent_info ai on ai.agent_no = mi.agent_no " +
            "        LEFT JOIN merchant_require_item mri on mri.merchant_no =   mi.merchant_no and mri.mri_id = '4' " +
            "        LEFT JOIN jpush_device jd on jd.user_no =   mi.merchant_no  " +
            " where mi.merchant_no = #{merchantNo}")
    @ResultType(Map.class)
    Map<String, Object> getUserByMerNo(@Param("merchantNo") String merchantNo);

    @Select("select bpd.bp_name,hp.type_name from terminal_info ti " +
            "LEFT JOIN business_product_define bpd on bpd.bp_id = ti.bp_id  " +
            "LEFT JOIN hardware_product hp on hp.hp_id = ti.type " +
            "where ti.merchant_no = #{merchantNo} ")
    @ResultType(Map.class)
    List<Map<String, Object>> getBpHpByMerNo(@Param("merchantNo") String merchantNo);

    /**
     * 判断是否是超级推机具
     * @param sn
     * @return
     */
    @Select("SELECT count(0) FROM terminal_info ti ,cjt_team_hardware cth " +
            "WHERE ti.type = cth.hp_id AND cth.team_id = #{teamId} and ti.sn = #{sn}")
    @ResultType(Integer.class)
    Integer selectSuperPushTerminal(@Param("teamId")String teamId,@Param("sn") String sn);

    /**
     * 根据业务产品ID查询到teamIdtgh
     * @param bpId
     * @return
     */
    @Select("select team_id from business_product_define where bp_id=#{bpId} ")
    @ResultType(String.class)
    String selectTeamIdByBpId(String bpId);

    String selectTeamEntryId(@Param("sn") String sn);

    /**
     * 商户进件列表查询
     * @param param
     * @return
     */
    @SelectProvider(type=MerchantInfoDao.SqlProvider.class,method="getMbpList")
    @ResultType(Map.class)
    List<Map<String, Object>> getMbpList(@Param("param")Map<String, String> param,@Param("userInfoBean")UserInfoBean userInfoBean);

    @InsertProvider(type = SqlProvider.class, method = "addAcqMerInfo")
    int addAcqMerInfo(@Param("map") AcqMerInfo map);

    @SelectProvider(type=SqlProvider.class,method="getAcqMerInfoItemList")
    List<Map<String, Object>> getAcqMerInfoItemList(@Param("map") Map<String, Object> map);

    int addMerInfo(@Param("params")Map params);

    int addMerService(@Param("params")Map params);

    int addMerServiceRate(@Param("params")Map params);

    int addMerServiceQuota(@Param("params")Map params);

    int addMerRequireItem(@Param("params")MerRequireItem params);

    int updateMerRequireItem(@Param("merchantNo") String merchantNo, @Param("item") MerRequireItem item);

    int addMerBusinessProduct(@Param("params")Map params);

    @InsertProvider(type = SqlProvider.class, method = "addGathCode")
    int addGathCode(@Param("params")Map params);

    @UpdateProvider(type = SqlProvider.class, method = "updateTerMerNo")
    int updateTerMerNo(@Param("params")Map params);

    @UpdateProvider(type = SqlProvider.class, method = "updateAcqMerInfo")
    int updateAcqMerInfo(@Param("map") AcqMerInfo acqMerInfo);

    int updateAcqMerFileInfoStatus(@Param("acq_into_no") String acq_into_no);

    List<Map<String,Object>> queryMerListBykey(@Param("merchantKey") String merchantKey, @Param("agentNode") String agentNode);

    List<Map<String,Object>>querybpd(@Param("agentNo")String  agentNo);

    List<Map<String,Object>> getHardwareProductByAgentOem(@Param("agentNo")String agentNo);

    List<Map<String,Object>> getHardwareProduct();

    List<Map<String,Object>> selectServiceList(@Param("bpId") String bpId);

    List<Map<String, Object>> getAgentBusiness(@Param("agentNo")String agentNo);

    void updateMerchant(@Param("merchantNo") String merchantNo, @Param("merchantInfo") MerchantInfo merchantInfo);

    void updateMbpStatus(@Param("merchantNo") String merchantNo, @Param("status") String status);

    void deleteExaminationsLog(@Param("merchantNo") String merchantNo);

    @Select("select count(1) from sensitive_words where #{keyword} like concat('%',key_word,'%') and status = '1'")
    @ResultType(Integer.class)
    int hasSensitiveWords(@Param("keyword") String keyword);

    class SqlProvider {
        public String getMbpList(Map<String,Object> param) {
            Map<String, String>  map = (Map<String, String> )param.get("param");
            UserInfoBean  userInfoBean = (UserInfoBean)param.get("userInfoBean");

            StringBuffer sb=new StringBuffer();
            sb.append(" SELECT distinct ");
            sb.append("   m.merchant_no,m.merchant_name,m.mobilephone,b.status,bp.bp_id,b.create_time,bp.bp_name,b.id,t.sn ");
            sb.append(" FROM merchant_info m ");
            sb.append("   JOIN  merchant_business_product b ON m.merchant_no = b.merchant_no ");
            sb.append("   JOIN  business_product_define bp ON b.bp_id=bp.bp_id ");
            sb.append("   JOIN  agent_info ai ON ai.agent_no = m.agent_no ");
            sb.append("   LEFT JOIN  terminal_info t ON (m.merchant_no = t.merchant_no and bp.bp_id = t.bp_id) ");
            sb.append(" where  1=1 ");
            //如果前端传入空字符，则默认当前登入代理商
            String agentNo=StringUtils.filterNull(map.get("agent_no"));
            //是否包含下级
            String lowerStatus=StringUtils.filterNull(map.get("include_son"));
            if(!"".equals(agentNo)){
                if ("1".equals(lowerStatus)) {//是，全链条
                    sb.append(" and ai.agent_node like concat((select agent_node from agent_info where agent_no=#{param.agent_no}),'%') ");
                }else if("2".equals(lowerStatus)){
                    sb.append(" and ai.agent_no=#{param.agent_no} ");
                }else if("3".equals(lowerStatus)){
                    sb.append(" and ai.parent_id=#{param.agent_no} ");
                }else{
                    sb.append(" and ai.agent_node like concat((select agent_node from agent_info where agent_no=#{param.agent_no}),'%') ");
                }
            }else{
                if ("1".equals(lowerStatus)) {//是，全链条
                    sb.append(" and ai.agent_node like concat((select agent_node from agent_info where agent_no=#{userInfoBean.agentNo}),'%') ");
                }else if("2".equals(lowerStatus)){
                    sb.append(" and ai.agent_no=#{userInfoBean.agentNo} ");
                }else if("3".equals(lowerStatus)){
                    sb.append(" and ai.parent_id=#{userInfoBean.agentNo} ");
                }else{
                    sb.append(" and ai.agent_node like concat((select agent_node from agent_info where agent_no=#{userInfoBean.agentNo}),'%') ");
                }
            }
            if (!"".equals(StringUtils.filterNull(map.get("bp_id")))) {
                sb.append(" and bp.bp_id = #{param.bp_id} ");
            }
            if (!"".equals(StringUtils.filterNull(map.get("merchant_name")))) {
                sb.append(" and ( m.merchant_name like concat('%',#{param.merchant_name},'%') or  m.merchant_no = #{param.merchant_name} or  m.mobilephone = #{param.merchant_name}) ");
            }
            if (!"".equals(StringUtils.filterNull(map.get("pos_type")))) {
                sb.append(" and t.type = #{param.pos_type} ");
            }
            if (!"".equals(StringUtils.filterNull(map.get("merchant_status")))) {
                sb.append(" and b.status = #{param.merchant_status} ");
            }
            if (!"".equals(StringUtils.filterNull(map.get("create_start_date")))) {
                sb.append(" and b.create_time >= concat(#{param.create_start_date},' 00:00:00') ");
            }
            if (!"".equals(StringUtils.filterNull(map.get("create_end_date")))) {
                sb.append(" and b.create_time <= concat(#{param.create_end_date},' 23:59:59') ");
            }
            sb.append(" order by b.create_time desc ");
            return sb.toString();
        }

        public String updateAcqMerInfo(Map<String,Object> param) {
            AcqMerInfo acqMerInfo = (AcqMerInfo)param.get("map");
            return new SQL() {
                {
                    UPDATE("acq_merchant_info");
                    if(StringUtils.isNotEmpty(acqMerInfo.getChange_mer_business_info())){
                        SET("change_mer_business_info=#{map.change_mer_business_info}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getMerchant_type())){
                        SET("merchant_type=#{map.merchant_type}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getMerchant_name())){
                        SET("merchant_name=#{map.merchant_name}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getOne_scope())){
                        SET("one_scope=#{map.one_scope}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getTwo_scope())){
                        SET("two_scope=#{map.two_scope}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getProvince())){
                        SET("province=#{map.province}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getCity())){
                        SET("city=#{map.city}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getDistrict())){
                        SET("district=#{map.district}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getAddress())){
                        SET("address=#{map.address}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getLegal_person())){
                        SET("legal_person=#{map.legal_person}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getLegal_person_id())){
                        SET("legal_person_id=#{map.legal_person_id}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getId_valid_start())){
                        SET("id_valid_start=#{map.id_valid_start}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getId_valid_end())){
                        SET("id_valid_end=#{map.id_valid_end}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getAccount_type())){
                        SET("account_type=#{map.account_type}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getBank_no())){
                        SET("bank_no=#{map.bank_no}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getAccount_name())){
                        SET("account_name=#{map.account_name}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getAccount_bank())){
                        SET("account_bank=#{map.account_bank}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getAccount_province())){
                        SET("account_province=#{map.account_province}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getAccount_city())){
                        SET("account_city=#{map.account_city}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getAccount_district())){
                        SET("account_district=#{map.account_district}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getBank_branch())){
                        SET("bank_branch=#{map.bank_branch}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getLine_number())){
                        SET("line_number=#{map.line_number}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getCharter_name())){
                        SET("charter_name=#{map.charter_name}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getCharter_no())){
                        SET("charter_no=#{map.charter_no}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getCharter_valid_start())){
                        SET("charter_valid_start=#{map.charter_valid_start}");
                    }
                    if(StringUtils.isNotEmpty(acqMerInfo.getCharter_valid_end())){
                        SET("charter_valid_end=#{map.charter_valid_end}");
                    }
                    SET("update_time=now()");
                    SET("audit_status=1");
                    WHERE("acq_into_no = #{map.acq_into_no}");
                }
            }.toString();
        }

        public String addAcqMerInfo(Map<String,Object> param) {
            AcqMerInfo acqMerInfo = (AcqMerInfo)param.get("map");
            SQL sql =  new SQL();
            sql.INSERT_INTO("acq_merchant_info");
            if(StringUtils.isNotEmpty(acqMerInfo.getMerchant_no())){
                sql.VALUES("general_merchant_no","#{map.merchant_no}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getChange_mer_business_info())){
                sql.VALUES("change_mer_business_info","#{map.change_mer_business_info}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getMerchant_type())){
                sql.VALUES("merchant_type","#{map.merchant_type}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getMerchant_name())){
                sql.VALUES("merchant_name","#{map.merchant_name}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getOne_scope())){
                sql.VALUES("one_scope","#{map.one_scope}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getTwo_scope())){
                sql.VALUES("two_scope","#{map.two_scope}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getProvince())){
                sql.VALUES("province","#{map.province}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getCity())){
                sql.VALUES("city","#{map.city}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getDistrict())){
                sql.VALUES("district","#{map.district}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getAddress())){
                sql.VALUES("address","#{map.address}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getLegal_person())){
                sql.VALUES("legal_person","#{map.legal_person}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getLegal_person_id())){
                sql.VALUES("legal_person_id","#{map.legal_person_id}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getId_valid_start())){
                sql.VALUES("id_valid_start","#{map.id_valid_start}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getId_valid_end())){
                sql.VALUES("id_valid_end","#{map.id_valid_end}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getAccount_type())){
                sql.VALUES("account_type","#{map.account_type}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getBank_no())){
                sql.VALUES("bank_no","#{map.bank_no}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getAccount_name())){
                sql.VALUES("account_name","#{map.account_name}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getAccount_bank())){
                sql.VALUES("account_bank","#{map.account_bank}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getAccount_province())){
                sql.VALUES("account_province","#{map.account_province}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getAccount_city())){
                sql.VALUES("account_city","#{map.account_city}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getAccount_district())){
                sql.VALUES("account_district","#{map.account_district}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getBank_branch())){
                sql.VALUES("bank_branch","#{map.bank_branch}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getLine_number())){
                sql.VALUES("line_number","#{map.line_number}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getCharter_name())){
                sql.VALUES("charter_name","#{map.charter_name}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getCharter_no())){
                sql.VALUES("charter_no","#{map.charter_no}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getCharter_valid_start())){
                sql.VALUES("charter_valid_start","#{map.charter_valid_start}");
            }
            if(StringUtils.isNotEmpty(acqMerInfo.getCharter_valid_end())){
                sql.VALUES("charter_valid_end","#{map.charter_valid_end}");
            }
            {sql.VALUES("acq_into_no", "#{map.acq_into_no}");}
            if(StringUtils.isNotEmpty(acqMerInfo.getInto_source())){
                sql.VALUES("into_source","#{map.into_source}");
            }else{sql.VALUES("into_source","1");
            }
            {sql.VALUES("audit_status", "1");}
            {sql.VALUES("create_time", "now()");}
            {sql.VALUES("agent_no", "#{map.agent_no}");}
            {sql.VALUES("one_agent_no", "#{map.one_agent_no}");}
            return sql.toString();
        }

        /**
         * merchant_info 表添加数据
         * @author
         * @param params
         * @return
         * @date 2016年5月3日 上午11:01:03
         */
        public String addGathCode(Map params) {
            final Map<String, String> ps = (Map<String, String>)params.get("params");
            SQL sql =  new SQL();
            sql.INSERT_INTO("gather_code");
            String tmp = "";
            tmp = ps.get("gather_code");
            if (StringUtils.isNotEmpty(tmp))
            {sql.VALUES("gather_code", "#{params.gather_code}");}
            {sql.VALUES("status", "2");}
            {sql.VALUES("create_time", "now()");}
            {sql.VALUES("material_type", "1");}
            {sql.VALUES("sn", "nextval('gather_code_seq')");}
            tmp = ps.get("gather_name");
            if (StringUtils.isNotEmpty(tmp))
            {sql.VALUES("gather_name", "#{params.gather_name}");}
            tmp = ps.get("device_sn");
            if (StringUtils.isNotEmpty(tmp))
            {sql.VALUES("device_sn", "#{params.device_sn}");}
            tmp = ps.get("merchant_no");
            if (StringUtils.isNotEmpty(tmp))
            {sql.VALUES("merchant_no", "#{params.merchant_no}");}

            return sql.toString();
        }

        public String updateTerMerNo(Map params) {
            final Map<String, String> ps = (Map<String, String>)params.get("params");
            String tmp = "";
            return new SQL() {
                {
                    UPDATE("terminal_info");
                    SET("merchant_no=#{params.merchantNo} ");
                    SET("bp_id=#{params.bpId} ");
                    SET("open_status='2' ");
                    SET("terminal_id=nextval('terminal_id_seq') ");
                    SET("START_TIME=now() ");
                    StringBuffer sb = new StringBuffer();
                    sb.append("('");
                    sb.append(ps.get("sns"));
                    sb.append("')");
                    WHERE("  sn  in "+ sb.toString() );

                }
            }.toString();
        }

        public String getAcqMerInfoItemList(Map<String,Object> param){
            final Map<String,String> map=(Map)param.get("map");
            return new SQL(){{
                SELECT("am.acq_into_no,am.merchant_name,am.create_time,am.audit_status,am.general_merchant_no," +
                        "(SELECT examination_opinions FROM acq_merchant_info_log where acq_merchant_info_id=am.id ORDER BY create_time DESC LIMIT 1) examination_opinions");
                FROM("acq_merchant_info am");
                System.out.println("--------"+map);
                if(StringUtils.isNotBlank(map.get("agent_no"))){
                    WHERE(" am.agent_no =#{map.agent_no}");
                }
                if(StringUtils.isNotBlank(map.get("audit_status"))){
                    if(AcqMerAuditStatus.INVALID.getStatus().equals(map.get("audit_status"))){
                        RIGHT_OUTER_JOIN("acq_merchant acm ON am.acq_into_no = acm.acq_merchant_code AND acm.acq_status = '0' AND acm.merchant_no = am.general_merchant_no");
                    }else{
                        WHERE(" am.audit_status =#{map.audit_status}");
                    }
                }
                if(StringUtils.isNotBlank(map.get("merchant_name"))){
                    WHERE(" am.merchant_name  like \"%\"#{map.merchant_name}\"%\"");
                }
                if(StringUtils.isNotBlank(map.get("create_starttime"))){
                    WHERE(" am.create_time >= #{map.create_starttime}");
                }
                if(StringUtils.isNotBlank(map.get("create_endtime"))){
                    WHERE(" am.create_time <= #{map.create_endtime}\" 23:59:59\"");
                }
                ORDER_BY(" am.create_time desc");
            }}.toString();
        }
    }
}
