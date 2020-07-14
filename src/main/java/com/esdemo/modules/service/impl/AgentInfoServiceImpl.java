package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.*;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.dao.AgentInfoDao;
import com.esdemo.modules.dao.MerchantInfoDao;
import com.esdemo.modules.dao.SysDictDao;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.SmsService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.common.collect.Tuple;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author tgh
 * @description
 * @date 2019/5/20
 */
@Service
@Slf4j
public class AgentInfoServiceImpl implements AgentInfoService {
    private static final DecimalFormat format = new java.text.DecimalFormat("0.00");

    @Resource
    private AgentInfoDao agentInfoDao;

    @Resource
    private SeqService seqService;

    @Resource
    private MerchantInfoDao merchantInfoDao;

    @Resource
    private SmsService smsService;

    @Resource
    private SysDictDao sysDictDao;

    @Override
    public AgentInfo queryAgentInfoByNo(String agentNo) {
        return agentInfoDao.getAgentInfoByNo(agentNo);
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public ResponseBean insertAgentExpand(AgentInfo agentInfo) {
        String parentId = agentInfo.getParentId();
        AgentInfo parentAgent = agentInfoDao.selectByAgentNo(parentId);//上级代理商信息
        boolean correct = smsService.validateSmsCode(agentInfo.getMobilephone(), parentAgent.getTeamId().toString(), agentInfo.getSmsCode());
        if (!correct) {
            return ResponseBean.error("短信验证码有误");
        }

        //保存拓展代理商基本信息
        agentInfo.setRegistType("1");//拓展代理 registType为1是拓展代理,其他为空
        agentInfo.setTeamId(parentAgent.getTeamId());
        if (agentInfoDao.existAgentByMobilephoneAndTeamId(agentInfo) > 0) {
            return ResponseBean.error("该组织下的手机号码或邮箱或代理商名称已存在!");
        }
        AgentUserInfo agentUser = agentInfoDao.selectAgentUser(agentInfo.getMobilephone(), Constants.TEAM_ID_999);
        if (agentUser != null) {
            return ResponseBean.error("代理商手机号已注册!");
        }
        //设置代理商编号
        String agentNo = seqService.createKey("agent_no");
        agentInfo.setAgentNo(agentNo);
        //节点后面加上“-”
        agentInfo.setAgentNode(parentAgent.getAgentNode() + agentNo + "-");
        agentInfo.setRegistType("1");//拓展代理商保存为1,其他为空
        int level = Integer.parseInt(parentAgent.getAgentLevel()) + 1;
        agentInfo.setAgentLevel(Integer.toString(level));
        agentInfo.setParentId(parentId);
        agentInfo.setOneLevelId(parentAgent.getOneLevelId());
        agentInfo.setIsOem(parentAgent.getIsOem());
        agentInfo.setCountLevel(parentAgent.getCountLevel());
        agentInfo.setIsApprove(parentAgent.getIsApprove());
        agentInfo.setAgentType(parentAgent.getAgentType());
        agentInfo.setAgentOem(parentAgent.getAgentOem());
        agentInfo.setCreator(agentInfo.getUserId()); //用户
        agentInfo.setStatus("1");
        int num = agentInfoDao.insertAgentInfo(agentInfo);
        if (num < 1) {
            log.info("保存代理商基本信息成功 {} 条数据", num);
            throw new AppException("保存代理商失败！");
        }

        //保存拓展代理商业务产品信息,查上级
        List<Map<String, Object>> bpIdList = merchantInfoDao.querybpd(parentId);
        if (bpIdList.size() < 1) {
            return ResponseBean.error("上级代理商没有业务产品!");
        }
        List<JoinTable> bpList = new ArrayList<>();
        List<String> bpIdListStr = new ArrayList<>();
        List<Long> bpIds = new ArrayList<>();
        for (Map<String, Object> map : bpIdList) {
            String bpId = map.get("bp_id").toString();
            JoinTable product = new JoinTable();
            product.setKey1(Integer.parseInt(bpId));
            product.setKey2(1);
            product.setKey3(agentNo);
            bpList.add(product);
            bpIds.add(Long.valueOf(bpId));
            bpIdListStr.add(bpId);
        }
        agentInfoDao.insertAgentProductList(bpList);
        log.info("插入代理商业务产品" + agentNo);
        agentInfoDao.setDefaultBpFlagIs1(agentNo, bpIdListStr);
        log.info("更新代理商业务产品" + agentNo);

        //分润,欢乐返不需要保存,默认为空

        //创建代理商管理员
        agentUser = new AgentUserInfo();
        agentUser.setUserName(agentInfo.getAgentName());
        String userId = seqService.createKey("user_no_seq", new BigInteger("1000000000000000000"));
        agentUser.setUserId(userId);
        agentUser.setTeamId(Constants.TEAM_ID_999);
        agentUser.setMobilephone(agentInfo.getMobilephone());
        agentUser.setPassword(new Md5PasswordEncoder().encodePassword(agentInfo.getSafePassword(), agentInfo.getMobilephone()));
        agentUser.setEmail(agentInfo.getEmail());
        agentInfoDao.insertAgentUser(agentUser);

        AgentUserEntity entity = new AgentUserEntity();
        entity.setEntityId(agentInfo.getAgentNo());
        entity.setUserId(agentUser.getUserId());
        entity.setIsAgent("1");
        agentInfoDao.insertAgentEntity(entity);
        agentInfoDao.insertAgentRole(entity.getId());
        log.info("创建管理员" + agentNo);

        //开设代理商账户
        try {
            String acc = ClientInterface.createAgentAccount(agentInfo.getAgentNo());
            Map<String, Object> accMap = GsonUtils.fromJson2Map(acc, Object.class);
            String status = accMap.get("status") == null ? "" : accMap.get("status").toString();
            if ("true".equals(status)) {
                acc = ClientInterface.createAgentAccount(agentInfo.getAgentNo(), "224106");
            }
            if ("true".equals(status)) {
                agentInfoDao.updateAgentAccount(agentNo, 1);
            } else {
                log.info("开立代理商账户失败：{}", acc);
                return ResponseBean.error("开立代理商账户失败");
            }
        } catch (Exception e) {
            log.error("开立代理商账户异常", e);
            throw new AppException("代理商添加成功,账户开立失败请联系客服开户");
        }
        return ResponseBean.of(true, "恭喜您,代理商后台开通成功");
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public Map<String, Object> insertAgent(Map<String, Object> map, UserInfoBean userInfoBean) {
        Map<String, Object> resultMap = new HashMap<>();
        String entityId = userInfoBean.getAgentNo();
        AgentInfo agentInfo = GsonUtils.fromJson2Bean(map.get("agentInfo").toString(), AgentInfo.class);
        List<String> bpIdList = GsonUtils.fromJson2List(map.get("bpIdList").toString(), String.class);
        List<Map> shareDataList = GsonUtils.fromJson2List(map.get("shareDataList").toString(), Map.class);
//        List<Map<String, String>> bps = (List<Map<String, String>>) map.get("productsAgentList");
        //客户端传过来的欢乐返活动数据信息
        List<Map> happyBackList = GsonUtils.fromJson2List(map.get("happyBackDataList").toString(), Map.class);
        AgentInfo parentAgent = agentInfoDao.selectByAgentNo(entityId);
        AgentInfo agent = new AgentInfo();
        if (bpIdList.size() < 1) {
            throw new AppException("请选择业务产品！");
        }
        String parentId = parentAgent.getAgentNo();
        if ("0".equals(parentId)) {
            parentId = agentInfo.getAgentNo();
        }
        agent.setLinkName(agentInfo.getLinkName());
        //对私需要查询银行联号
        if ("对私".equals(agentInfo.getAccountType())) {
            agentInfoDao.queryBankNo(agentInfo.getAccountNo());
        }else {
            agent.setCnapsNo(agentInfo.getCnapsNo());
        }
        agent.setSubBank(agentInfo.getSubBank());
        agent.setMobilephone(agentInfo.getMobilephone());
        agent.setTeamId(Long.parseLong(Constants.TEAM_ID_999));
        agent.setAgentName(agentInfo.getAgentName());
        agent.setEmail(agentInfo.getEmail());

        if (agentInfoDao.existAgentByMobilephoneAndTeamId(agentInfo) > 0) {
            throw new AppException("该组织下的手机号码或邮箱或代理商名称已存在！");
        }

        AgentUserInfo agentUser = agentInfoDao.selectAgentUser(agent.getMobilephone(), Constants.TEAM_ID_999);
        if (agentUser != null) {
            throw new AppException("代理商手机号已注册");
        }
        String agentNo = seqService.createKey("agent_no");
        agent.setAgentNo(agentNo);

        //保存欢乐返活动信息
        AgentInfo currentAgentInfo = agentInfoDao.selectByAgentNo(entityId);
        //满奖不满扣设置
        Map<String, Object> supportRankMap = getSupportRank(currentAgentInfo);
        if (!(Boolean)supportRankMap.get("fullPrizeLevelFlag")){
            log.info("满奖配置超过数据字典配置级别");
            throw new AppException("非法操作");
        }
        if (!(Boolean)supportRankMap.get("notFullDeductLevelFlag")){
            log.info("不满扣配置超过数据字典配置级别");
            throw new AppException("非法操作");
        }
        //保存到agent_activity
        for (Map<String, String> agentActivity : happyBackList) {
            String activityTypeNo = agentActivity.get("activityTypeNo");
            BigDecimal taxRate = new BigDecimal(agentActivity.get("taxRate"));
            BigDecimal cashBackAmount = new BigDecimal(agentActivity.get("cashBackAmount"));
            BigDecimal repeatRegisterAmount = new BigDecimal(agentActivity.get("repeatRegisterAmount"));
            BigDecimal repeatRegisterRatio = new BigDecimal(agentActivity.get("repeatRegisterRatio"));
            if (cashBackAmount == null || taxRate == null) {
                throw new AppException("下发返现金额和税额百分比不能为空！");
            }
            Map<String, Object> mapParentAgent = selectByActivityTypeNo(entityId, agentActivity.get("activityTypeNo"));
            if (mapParentAgent == null) {
                mapParentAgent = selectDefaultStatus();
            }
            BigDecimal cashBackAmountParentAgent = mapParentAgent.get("cash_back_amount") == null ? BigDecimal.ZERO : new BigDecimal(mapParentAgent.get("cash_back_amount").toString());
            BigDecimal taxRateParentAgent = mapParentAgent.get("tax_rate") == null ? new BigDecimal("1") : new BigDecimal(mapParentAgent.get("tax_rate").toString());//需求要求如果一级代理商没有设置,默认为100%
            if ((cashBackAmount.multiply(taxRate).divide(new BigDecimal("100"))).compareTo(cashBackAmountParentAgent.multiply(taxRateParentAgent)) > 0) {
                throw new AppException("下级代理商的返现不得高于上级代理商的返现!");
            }
            BigDecimal repeatRegisterAmountParentAgent = mapParentAgent.get("repeat_register_amount") == null ? BigDecimal.ZERO : new BigDecimal(mapParentAgent.get("repeat_register_amount").toString());
            BigDecimal repeatRegisterRatioParentAgent = mapParentAgent.get("repeat_register_ratio") == null ? new BigDecimal("1") : new BigDecimal(mapParentAgent.get("repeat_register_ratio").toString());//需求要求如果一级代理商没有设置,默认为100%
            if ((repeatRegisterAmount.multiply(repeatRegisterRatio).divide(new BigDecimal("100"))).compareTo(repeatRegisterAmountParentAgent.multiply(repeatRegisterRatioParentAgent)) > 0) {
                throw new AppException("下级代理商的重复返现不得高于上级代理商的重复返现!");
            }
            agentActivity.put("taxRate", taxRate.divide(new BigDecimal("100")).toString());
            agentActivity.put("agentNo", agentNo);
            agentActivity.put("agentNode", currentAgentInfo.getAgentNode() + agentNo + "-");
            agentActivity.put("repeatRegisterAmount", repeatRegisterAmount.toString());
            agentActivity.put("repeatRegisterRatio", repeatRegisterRatio.divide(new BigDecimal("100")).toString());

            //===========欢乐返设置,满奖不满扣========start=======
            happyBackFullOrNotFull(parentAgent, agentActivity, activityTypeNo);
            //===========欢乐返设置,满奖不满扣========end=======
        }
        insertAgentActivity(happyBackList);

        //节点后面加上“-”
        agent.setAgentNode(parentAgent.getAgentNode() + agentNo + "-");
        int level = Integer.parseInt(parentAgent.getAgentLevel()) + 1;
        agent.setAgentLevel(Integer.toString(level));
        agent.setParentId(parentId);
        agent.setOneLevelId(parentAgent.getOneLevelId());
        //agent.setTeamId(parentAgent.getTeamId());
        agent.setTeamId(Long.parseLong(Constants.TEAM_ID_999));
        agent.setIsOem(parentAgent.getIsOem());
        agent.setCountLevel(parentAgent.getCountLevel());
        agent.setIsApprove(parentAgent.getIsApprove());
        agent.setAgentType(parentAgent.getAgentType());
        agent.setAgentOem(parentAgent.getAgentOem());
        agent.setCreator(agentInfo.getUserId()); //用户
        agent.setStatus("1");
        agent.setProvince(agentInfo.getProvince());
        agent.setCity(agentInfo.getCity());
        agent.setArea(agentInfo.getArea());
        agent.setAddress(agentInfo.getAddress());
        agent.setSaleName(agentInfo.getSaleName());
        agent.setAgentArea(agentInfo.getAgentArea());
        agent.setAccountNo(agentInfo.getAccountNo());
        agent.setAccountName(agentInfo.getAccountName());
        agent.setAccountType(agentInfo.getAccountType());
        agent.setAccountProvince(agentInfo.getAccountProvince());
        agent.setAccountCity(agentInfo.getAccountCity());
        agent.setSubBank(agentInfo.getSubBank());
        agent.setBankName(agentInfo.getBankName());
        agent.setPhone(agentInfo.getPhone());
        if ("对公".equals(agentInfo.getAccountType())) {
            agent.setAccountType("1");
        }
        if ("对私".equals(agentInfo.getAccountType())) {
            agent.setAccountType("2");
        }
        int num = agentInfoDao.insertAgentInfo(agent);
        if (num < 1) {
            throw new AppException("保存代理商失败！");
        }

        List<JoinTable> bpList = new ArrayList<>();
        List<Long> bpIds = new ArrayList<>();
        for (String bpId : bpIdList) {
            JoinTable product = new JoinTable();
            product.setKey1(Integer.parseInt(bpId));
            product.setKey2(1);
            product.setKey3(agentNo);
            bpList.add(product);
            bpIds.add(Long.valueOf(bpId));
        }
        agentInfoDao.insertAgentProductList(bpList);
        log.info("插入代理商业务产品" + agentNo);

        agentInfoDao.setDefaultBpFlagIs1(agentNo, bpIdList);
        log.info("更新代理商业务产品" + agentNo);

        //分润设置
        List<AgentShareRule> shareList = new ArrayList<>();

        for (Map<String, String> shareMap : shareDataList) {
            AgentShareRule agentShareRule = new AgentShareRule();
            agentShareRule.setProfitType("5");
            String costRateType = shareMap.get("costRateType");
            agentShareRule.setCostRateType(costRateType);
            if (StringUtils.equals(costRateType, "1")) {
                agentShareRule.setPerFixCost(new BigDecimal(shareMap.get("cost")));
            } else if (StringUtils.equals(costRateType, "2")) {
                agentShareRule.setCostRate(new BigDecimal(shareMap.get("cost")));
            } else {
                throw new AppException("该代理商成本分润类型设置不正确.");
            }
            agentShareRule.setServiceId(shareMap.get("serviceId"));
            agentShareRule.setCardType(shareMap.get("cardType"));
            agentShareRule.setHolidaysMark(shareMap.get("holidaysMark"));
            agentShareRule.setShareProfitPercent(new BigDecimal(shareMap.get("shareProfitPercent")));
            agentShareRule.setAgentId(agentNo);
            agentShareRule.setCheckStatus("1");
            agentShareRule.setLockStatus("0");
            agentShareRule.setServiceName(shareMap.get("serviceTypeName"));
            AgentShareRule parentAgentShareRule = agentInfoDao.getSameTypeParentAgentShare(agentShareRule);
            if (parentAgentShareRule == null) {
                throw new AppException("上级代理商 " + agentShareRule.getServiceName() + " 的分润规则没配置");
            } else if (isChildrenRuleLessThanParent(parentAgentShareRule, agentShareRule)) {
                throw new AppException("该代理商 " + agentShareRule.getServiceName() + " 的分润成本比上级低.");
            }
            compareServiceRate(agentShareRule, agent.getOneLevelId(), agentNo);
            shareList.add(agentShareRule);
        }

        agentInfoDao.insertAgentShareList(shareList);
        List<Map<String, Long>> leaderAndMember = agentInfoDao.getLeaderAndMember(agentNo);
        // 如果不为空,说明有对应需要添加. todo4lvsw ss
        if (!CollectionUtils.isEmpty(leaderAndMember)) {
            for (Map<String, Long> temp : leaderAndMember) {
                Long leader = temp.get("leader");
                Long member = temp.get("member");
                // 此次更新的分润信息如果有包含队长的分润信息,队员才进行修改
                if (bpIds.contains(leader) && bpIds.contains(member)) {
                    agentInfoDao.insertMemberAgentShare(leader + "", member + "", agentNo);
                }
            }
        }
        log.info("插入代理商分润信息代理商编号为" + agentNo);

        //创建代理商管理员
        agentUser = new AgentUserInfo();
        agentUser.setUserName(agent.getAgentName());
        String userId = seqService.createKey("user_no_seq", new BigInteger("1000000000000000000"));
        agentUser.setUserId(userId);
        agentUser.setTeamId(Constants.TEAM_ID_999);
        agentUser.setMobilephone(agent.getMobilephone());
        agentUser.setPassword(new Md5PasswordEncoder().encodePassword("123456", agent.getMobilephone()));
        agentUser.setEmail(StringUtils.isBlank(agent.getEmail()) ? null : agent.getEmail());
        agentInfoDao.insertAgentUser(agentUser);

        AgentUserEntity entity = new AgentUserEntity();
        entity.setEntityId(agent.getAgentNo());
        entity.setUserId(agentUser.getUserId());
        entity.setIsAgent("1");
        agentInfoDao.insertAgentEntity(entity);
        agentInfoDao.insertAgentRole(entity.getId());

        log.info("创建管理员" + agentNo);
        //开设代理商账户
        try {
            String acc = ClientInterface.createAgentAccount(agent.getAgentNo());
            Map<String, Object> accMap = GsonUtils.fromJson2Map(acc, Object.class);
            String status = accMap.get("status") == null ? "" : accMap.get("status").toString();
            if ("true".equals(status)) {
                acc = ClientInterface.createAgentAccount(agent.getAgentNo(), "224106");
            }
            if ("true".equals(status)) {
                agentInfoDao.updateAgentAccount(agentNo, 1);
            } else {
                log.info("开立代理商账户失败：{}", acc);
            }
        } catch (Exception e) {
            log.error("开立代理商账户异常", e);
            throw new AppException("代理商添加成功,账户开立失败请联系客服开户");
        }
        resultMap.put("status", true);
        resultMap.put("msg", "代理商添加成功");
        return resultMap;
    }

    private void happyBackFullOrNotFull(AgentInfo parentAgent, Map<String, String> agentActivity, String activityTypeNo) {
        BigDecimal fullPrizeAmount = toBigDecimal(agentActivity.get("fullPrizeAmount"));
        BigDecimal notFullDeductAmount = toBigDecimal(agentActivity.get("notFullDeductAmount"));
        BigDecimal repeatFullPrizeAmount = toBigDecimal(agentActivity.get("repeatFullPrizeAmount"));
        BigDecimal repeatNotFullDeductAmount = toBigDecimal(agentActivity.get("repeatNotFullDeductAmount"));
        agentActivity.put("fullPrizeAmount", fullPrizeAmount.toString());
        agentActivity.put("notFullDeductAmount", notFullDeductAmount.toString());
        agentActivity.put("repeatFullPrizeAmount", repeatFullPrizeAmount.toString());
        agentActivity.put("repeatNotFullDeductAmount", repeatNotFullDeductAmount.toString());
        // 拿到上级的满奖不满扣金额
        AgentActivity parent = agentInfoDao.findAgentActivityByParentAndType(parentAgent.getAgentNo(),
                activityTypeNo);
        if (parent == null) {
            log.info("上级满奖不满扣配置为 {} =====", parent);
            throw new AppException("上级没有配置满奖不满扣金额");
        }
        BigDecimal parentFullPrizeAmount = parent.getFullPrizeAmount();
        BigDecimal parentNotFullDeductAmount = parent.getNotFullDeductAmount();
        BigDecimal parentRepeatFullPrizeAmount = parent.getRepeatFullPrizeAmount();
        BigDecimal parentRepeatNotFullDeductAmount = parent.getRepeatNotFullDeductAmount();
        if (fullPrizeAmount != null && parentFullPrizeAmount != null && fullPrizeAmount.compareTo(parentFullPrizeAmount) > 0) {
            throw new AppException("首次注册满奖金额需 ≤ " + parentFullPrizeAmount + "元");
        }
        if (notFullDeductAmount != null && parentNotFullDeductAmount != null && notFullDeductAmount.compareTo(parentNotFullDeductAmount) > 0) {
            throw new AppException("首次注册不满扣金额需 ≤ " + parentNotFullDeductAmount + "元");
        }
        if (repeatFullPrizeAmount != null && parentRepeatFullPrizeAmount != null && repeatFullPrizeAmount.compareTo(parentRepeatFullPrizeAmount) > 0) {
            throw new AppException("重复注册满奖金额需 ≤ " + parentRepeatFullPrizeAmount + "元");
        }
        if (repeatNotFullDeductAmount != null && parentRepeatNotFullDeductAmount != null
                && repeatNotFullDeductAmount.compareTo(parentRepeatNotFullDeductAmount) > 0) {
            throw new AppException("重复注册不满扣金额需 ≤ " + parentRepeatNotFullDeductAmount + "元");
        }
    }

    /**
     * String 转 BigDecimal
     * @param str
     * @return
     */
    private BigDecimal toBigDecimal(String str) {
        return new BigDecimal(StringUtils.isBlank(str) ? "0" : str);
    }

    @Override
    public Map<String,Object> getSupportRank(AgentInfo currentAgentInfo) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("fullPrizeLevelFlag", false);
        resultMap.put("notFullDeductLevelFlag", false);
        SysDict sysDict = sysDictDao.getByKey("agent_oem_prize_buckle_rank_" + currentAgentInfo.getAgentOem());
        String value = sysDict == null ? "" : sysDict.getSysValue();
        if (!StringUtils.isEmpty(value)) {
            int indexOf = value.indexOf("-");
            Integer fullPrizeLevel = Integer.valueOf(value.substring(0, indexOf));//支持满奖代理商级别,如果配置1,表示可以支持2级
            Integer notFullDeductLevel = Integer.valueOf(value.substring(indexOf + 1, value.length()));//支持不满扣代理商级别
            Integer agentLevel = Integer.valueOf(currentAgentInfo.getAgentLevel());
            log.info("当前登录代理商级别 {},数据字典配置满奖级别 {},配置不满扣级别 {}",agentLevel,fullPrizeLevel,notFullDeductLevel);
            if (fullPrizeLevel >= agentLevel && currentAgentInfo.getFullPrizeSwitch() == 1){
                resultMap.put("fullPrizeLevelFlag", true);
            }
            if (notFullDeductLevel >= agentLevel && currentAgentInfo.getNotFullDeductSwitch() == 1){
                resultMap.put("notFullDeductLevelFlag", true);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> selectByActivityTypeNo(String agentNo, String activityTypeNo) {
        return agentInfoDao.selectByActivityTypeNo(agentNo, activityTypeNo);
    }

    @Override
    public Map<String, Object> selectDefaultStatus() {
        return agentInfoDao.selectDefaultStatus();
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public int insertAgentActivity(List happyBackList) {
        return agentInfoDao.insertAgentActivity(happyBackList);
    }

    /**
     * 判断子级的分润规则不能大于父级的分润规则
     *
     * @param parentAgentShareRule 父级分润规则
     * @param childrenRule         子级分润规则
     * @return
     */
    private static boolean isChildrenRuleLessThanParent(AgentShareRule parentAgentShareRule, AgentShareRule childrenRule) {
        if (!StringUtils.equals(parentAgentShareRule.getProfitType(), "5")) {
            return true;
        }
        if (!StringUtils.equals(parentAgentShareRule.getCostRateType(), childrenRule.getCostRateType())) {
            return true;
        }
        // 1-每笔固定金额，
        if (StringUtils.equals(parentAgentShareRule.getCostRateType(), "1")) {
            return childrenRule.getPerFixCost().compareTo(parentAgentShareRule.getPerFixCost()) < 0;
        }
        // 2-扣率
        if (StringUtils.equals(parentAgentShareRule.getCostRateType(), "2")) {
            return childrenRule.getCostRate().compareTo(parentAgentShareRule.getCostRate()) < 0;
        }
        return true;
    }

    private void compareServiceRate(AgentShareRule rule, String oneLevelId, String agentNo) {
        Map<String, Object> serviceRateMap = agentInfoDao.getSameTypeRootAgentMinServiceRate(rule, oneLevelId, agentNo);
        if (serviceRateMap == null || serviceRateMap.size() == 0) {
            throw new AppException("该代理商的服务费率没有配置.");
        }
        String serviceName = MapUtils.getString(serviceRateMap, "service_name");
        String bpName = MapUtils.getString(serviceRateMap, "bp_name");
        BigDecimal rate = new BigDecimal(MapUtils.getString(serviceRateMap, "rate"));
        BigDecimal singleNumAmount = new BigDecimal(MapUtils.getString(serviceRateMap, "single_num_amount"));
        String isTx = MapUtils.getString(serviceRateMap, "isTx");

        if (StringUtils.equals(isTx, "1")) {
            if (rule.getPerFixCost().compareTo(singleNumAmount) > 0) {
                throw new AppException("代理商(" + bpName + "-" + serviceName + ")的分润成本"
                        + rule.getPerFixCost().setScale(2) + " 元高于服务费率 " + singleNumAmount.setScale(2) + "元");
            }
        } else {
            if (rule.getCostRate().compareTo(rate) > 0) {
                throw new AppException("代理商(" + bpName + "-" + serviceName + ")的分润成本"
                        + rule.getCostRate().setScale(2) + "%高于服务费率 " + rate.setScale(2) + "%");
            }
        }
    }

    @Override
    public Tuple<List<AgentInfo>, Long> listDirectChildren(String parentId, PageRequest page) {
        long count = agentInfoDao.countDirectChildren(parentId);
        if (count == 0) {
            return new Tuple<>(new ArrayList<>(), count);
        }
        return new Tuple<>(agentInfoDao.listDirectChildren(parentId, page), count);
    }

    @Override
    public Tuple<List<AgentInfo>, Long> listAgentInfoByKeyword(UserInfoBean loginUserInfo, String agentType, String keyword, PageRequest page) {
        long count = agentInfoDao.countAgentInfoByKeyword(loginUserInfo, agentType, keyword);
        if (count == 0) {
            return new Tuple<>(new ArrayList<>(), count);
        }
        return new Tuple<>(agentInfoDao.listAgentInfoByKeyword(loginUserInfo, agentType, keyword, page), count);
    }

    /**
     * 修改安全手机
     *
     * @param agentNo
     * @param safePhone
     * @return
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int updateSafePhone(String agentNo, String safePhone) {
        return agentInfoDao.updateSafePhone(agentNo, safePhone);
    }

    /**
     * 修改安全密码
     *
     * @param agentNo
     * @param safePassword
     * @return
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int updateSafePassword(String agentNo, String safePassword) {
        return agentInfoDao.updateSafePassword(agentNo, safePassword);
    }

    @Override
    public AgentInfo selectBelongAgent(String agentNo, String entityId) {
        return agentInfoDao.selectBelongAgent(agentNo,entityId);
    }

    @Override
    public AgentInfo queryAgentInfo(String agentNo) {
        return agentInfoDao.selectByAgentNo(agentNo);
    }

    @Override
    public List<AgentInfo> queryAgentInfoList(Map<String, String> agentInfo, UserInfoBean userInfoBean) {
        /**
         * 1.代理商名称/编号没输入:
         *      a. 是: 查当前登录代理商所有下级代理商,不包含当前登录代理商
         *      b. 否: 给提示信息 ===> 请输入需要查询的代理商信息
         * 2.代理商名称/编号输入:
         *      a. 是: 查所输入的代理商所有下级代理商,包含所输入的代理商
         *      b. 否: 查输入代理商自己
         */
        String agentName = agentInfo.get("agentName");//查询条件代理商名称或编号
        if (StringUtils.isBlank(agentName) && "SELF".equals(agentInfo.get("hasChild"))) {
            throw new AppException("请输入需要查询的代理商信息");
        }
        String entityId = userInfoBean.getAgentNo();
        agentName = StringUtils.isNotBlank(agentName) ? agentName : entityId;
        AgentInfo byAgent = null;
        if (StringUtils.isNumeric(agentName)) {
            byAgent = agentInfoDao.getAgentInfoByNo(agentName);
        } else {
            byAgent = agentInfoDao.getAgentInfoByName(agentName);
        }
        if (byAgent == null || !getPermiss(entityId, agentName)) {
            return null;
        }
        agentInfo.put("agentName", byAgent.getAgentNo());
        agentInfo.put("entityId", entityId);
        agentInfo.put("agentNode", byAgent.getAgentNode() + "%");
        Integer agentLevelParams = Integer.valueOf(byAgent.getAgentLevel()) + 1;
        agentInfo.put("nextAgentLevel", agentLevelParams.toString());
        Integer pageNo = Integer.valueOf(agentInfo.get("pageNo"));
        Integer pageSize = Integer.valueOf(agentInfo.get("pageSize"));
        if(pageNo == 1) {
            PageHelper.startPage(pageNo, pageSize, true);
        } else {
            PageHelper.startPage(pageNo, pageSize, false);
        }
        List<AgentInfo> list = agentInfoDao.queryAgentInfoList(agentInfo);
        return list;
    }

    //通过代理商ID获取到代理的业务产品
    @Override
    public List<BusinessProductDefine> getAgentProductList(String agentNo) {
        return agentInfoDao.getAgentProductList(agentNo);
    }

    private boolean getPermiss(String curAgentNo, String agentNo) {
        if (curAgentNo.equals(agentNo)) {
            return true;
        }
        int count = agentInfoDao.hasPermiss(curAgentNo, agentNo);
        return count > 0 ? true : false;
    }

    @Override
    public List<AgentShareRule> getAgentShareList(String param) {
        List<AgentShareRule> list = agentInfoDao.getAgentShareList(param);
        for (AgentShareRule rule : list) {
            ServiceInfo serviceInfo = agentInfoDao.selectByServiceId(rule.getServiceId());
            String serviceType = serviceInfo.getServiceType();
            if ("10000".equals(serviceType) || "10001".equals(serviceType)) {//提现服务,取 代理商成本每笔固定值
                BigDecimal perFixCost = rule.getPerFixCost();
                rule.setIncome(perFixCost == null ? "0" : perFixCost.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            } else {
                BigDecimal costRate = rule.getCostRate();
                rule.setIncome(costRate == null ? "" : costRate.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            }
            rule.setShareSet(profitExpression(rule));
            rule.setId(rule.getId() == null ? -1L : rule.getId());
            String profitType = rule.getProfitType();
            setZh(rule, profitType);
        }
        return list;
    }

    private void setZh(AgentShareRule rule, String profitType) {
        switch (profitType) {
            case "1":
                rule.setProfitTypeZh("每笔固定收益额");
                break;
            case "2":
                rule.setProfitTypeZh("每笔固定收益率");
                break;
            case "3":
                rule.setProfitTypeZh("每笔固定收益率+保底封顶");
                break;
            case "4":
                rule.setProfitTypeZh("每笔固定收益金额+固定收益率");
                break;
            case "5":
                rule.setProfitTypeZh("商户签约费率与代理商成本费率差额百分比分润");
                break;
            case "6":
                rule.setProfitTypeZh("商户签约费率与代理商成本费率差额按交易量阶梯百分比分润");
                break;
            default:
                break;
        }
    }

    @Override
    public List<ServiceRate> getAgentRateList(String agentNo) {
        AgentInfo info = agentInfoDao.getAgentInfoByNo(agentNo);
        List<ServiceRate> list = agentInfoDao.getAgentRate(info.getOneLevelId(), agentNo);
        for (ServiceRate rate : list) {
            rate.setMerRate(profitExpression(rate));
            rate.setId(rate.getId() == null ? -1L : rate.getId());
            rate.setServiceId(rate.getServiceId() == null ? -1L : rate.getServiceId());
        }
        return list;
    }

    @Override
    public List<ServiceQuota> getAgentQuotaList(String agentNo) {
        AgentInfo info = agentInfoDao.getAgentInfoByNo(agentNo);
        List<ServiceQuota> agentQuotaList = agentInfoDao.getAgentQuota(info.getOneLevelId(), agentNo);
        for (ServiceQuota serviceQuota : agentQuotaList) {
            serviceQuota.setId(serviceQuota.getId() == null ? -1L : serviceQuota.getId());
            serviceQuota.setServiceId(serviceQuota.getServiceId() == null ? -1L : serviceQuota.getServiceId());

        }
        return agentQuotaList;
    }

    @Override
    public List<ServiceRate> getAgentServices(List<String> bpIds, String agentNo) {
        if (CollectionUtils.isEmpty(bpIds)) {
            return null;
        }
        List<String> learderOrIndividualBpId = agentInfoDao.getLearderOrIndividualBpId(bpIds, agentNo);
        if (CollectionUtils.isEmpty(learderOrIndividualBpId)) {
            return null;
        }
        return agentInfoDao.getNewAgentServicesByBpId(learderOrIndividualBpId);
    }

    @Override
    public List<HappyBackData> selectHappyBack(String agentNo) {
        return agentInfoDao.selectHappyBack(agentNo);
    }

    @Override
    public String getFunctionManagerByNum(String funcNum) {
        return agentInfoDao.getFunctionManagerByNum(funcNum);
    }

    /**
     * 获取所有直接下级代理商信息
     *
     * @param parentId
     * @return
     */
    @Override
    public List<AgentInfo> getAllDirectChildren(String parentId) {
        return agentInfoDao.getAllDirectChildren(parentId);
    }

    @Override
    public List<Map<String, String>> selectPoster(UserInfoBean userInfoBean) {
        String fileNames = agentInfoDao.selectPoster(userInfoBean.getTeamId());
        String[] split = fileNames.split(",");
        List<Map<String, String>> resultList = new ArrayList<>();
        for (String att : split) {
            Map<String, String> map = new HashMap<>();
            if (StringUtils.isNotEmpty(att)) {
                String urlStr = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, att,
                        new Date(Calendar.getInstance().getTime().getTime() * 3600 * 1000));
                map.put("fileName", urlStr);
                map.put("url", urlStr);
                resultList.add(map);
            }
        }
        return resultList;
    }

    //分润表达式
    public String profitExpression(AgentShareRule rule) {
        if (rule == null || StringUtils.isEmpty(rule.getProfitType())) return "";
        String profitExp = null;
        switch (rule.getProfitType()) {
            case "1":
                profitExp = rule.getPerFixIncome() == null ? "" : format.format(rule.getPerFixIncome());
                break;
            case "2":
                profitExp = rule.getPerFixInrate() == null ? "" : format.format(rule.getPerFixInrate()) + "%";
                break;
            case "3":
                profitExp = format.format(rule.getSafeLine()) + "~" + format.format(rule.getPerFixInrate()) + "%~" + format.format(rule.getCapping());
                break;
            case "4":
                profitExp = format.format(rule.getPerFixInrate()) + "%+" + format.format(rule.getPerFixIncome());
                break;
            case "5":
                if (StringUtils.equals(rule.getCostRateType(), "1")) {
                    profitExp = rule.getPerFixCost().setScale(2,BigDecimal.ROUND_HALF_UP) + "";
                } else if (StringUtils.equals(rule.getCostRateType(), "2")) {
                    profitExp = rule.getCostRate().setScale(2,BigDecimal.ROUND_HALF_UP) + "";
                } else {
                    profitExp = "";
                }
//
//				profitExp=rule.getCostRate()==null?"":format.format(rule.getCostRate())+"%";;
                break;
            default:
                break;
        }
        return profitExp;
    }

    //费率表达式
    public String profitExpression(ServiceRate rule) {
        if (rule == null || org.apache.commons.lang3.StringUtils.isEmpty(rule.getRateType())) return "";
        String profitExp = null;
        switch (rule.getRateType()) {
            case "1":
                profitExp = rule.getSingleNumAmount() == null ? "" : format.format(rule.getSingleNumAmount());
                break;
            case "2":
                profitExp = rule.getRate() == null ? "" : format.format(rule.getRate()) + "%";
                break;
            case "3":
                profitExp = format.format(rule.getSafeLine()) + "~" + format.format(rule.getRate()) + "%~" + format.format(rule.getCapping());
                break;
            case "4":
                profitExp = format.format(rule.getRate()) + "%+" + format.format(rule.getSingleNumAmount());
                break;
            case "5":
                StringBuffer sb = new StringBuffer();
                sb.append(format.format(rule.getLadder1Rate())).append("%").append("<").append(format.format(rule.getLadder1Max()))
                        .append("<").append(format.format(rule.getLadder2Rate())).append("%");
                if (rule.getLadder2Max() != null) {
                    sb.append("<").append(format.format(rule.getLadder2Max()))
                            .append("<").append(format.format(rule.getLadder3Rate())).append("%");
                    if (rule.getLadder3Max() != null) {
                        sb.append("<").append(format.format(rule.getLadder3Max()))
                                .append("<").append(format.format(rule.getLadder4Rate())).append("%");
                        if (rule.getLadder4Max() != null) {
                            sb.append("<").append(format.format(rule.getLadder4Max()));
                        }
                    }
                }
                profitExp=sb.toString();
            default :
                break;
        }
        return profitExp;
    }

    @Override
    public Map<String, Object> selectActivityBySn(String sn) {
        return agentInfoDao.selectActivityBySn(sn);
    }

    @Override
    public List<Map<String,Object>> getAgentInfoByLevel(int level) {
        return agentInfoDao.getAgentInfoByLevel(level);
    }

    @Override
    public int getMaxAgentLevel() {
        return agentInfoDao.getMaxAgentLevel();
    }

    @Override
    public Map<String, Object> getTodoStatus(String currAgentNo) {
        return agentInfoDao.getTodoStatus(currAgentNo);
    }
}
