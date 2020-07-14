package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.dao.AgentInfoDao;
import com.esdemo.modules.dao.AgentShareDao;
import com.esdemo.modules.service.AgentShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 修改分润
 * @date 2019/6/13
 */
@Service
@Slf4j
public class AgentShareServiceImpl implements AgentShareService {

    @Resource
    private AgentInfoDao agentInfoDao;

    @Resource
    private AgentShareDao agentShareDao;

    @Override
    public List<ProfitUpdateRecord> selectAgentShare(Long shareId,UserInfoBean userInfoBean) {
        return agentShareDao.selectAgentShare(shareId,userInfoBean.getAgentNo());
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public ResponseBean updateAgentShare(AgentShareRuleTask share, UserInfoBean userInfoBean) {
        //查出要被修改的代理商分润信息
        Long shareId = share.getShareId();
        AgentShareRule rule = agentInfoDao.getAgentShare(shareId);
        String costRateType = rule.getCostRateType();
//        share.setServiceType(serviceType);
        share.setServiceId(rule.getServiceId());
        share.setCardType(rule.getCardType());
        share.setHolidaysMark(rule.getHolidaysMark());
        share.setShareProfitPercentHistory(rule.getShareProfitPercent());
        // 如果是提现业务
        String cost = share.getCost();
        if ("1".equals(costRateType)) {
            share.setCostHistory(rule.getPerFixCost().setScale(2,BigDecimal.ROUND_HALF_UP));
            share.setCostRateType("1");
            share.setPerFixCost(new BigDecimal(cost));
        } else {
            share.setCostHistory(rule.getCostRate().setScale(2,BigDecimal.ROUND_HALF_UP));
            share.setCostRateType("2");
            share.setCostRate(new BigDecimal(cost));
        }
        share.setCheckStatus(1);
        share.setCostRate(rule.getCostRate());
        share.setPerFixIncome(rule.getPerFixIncome());
        share.setPerFixInrate(rule.getPerFixInrate());
        share.setSafeLine(rule.getSafeLine());
        share.setCapping(rule.getCapping());
        share.setLadder(rule.getLadder());
        share.setCostCapping(rule.getCostCapping());
        share.setCostSafeline(rule.getCostSafeline());
        share.setLadder1Max(rule.getLadder1Max());
        share.setLadder1Rate(rule.getLadder1Rate());
        share.setLadder2Max(rule.getLadder2Max());
        share.setLadder2Rate(rule.getLadder2Rate());
        share.setLadder3Max(rule.getLadder3Max());
        share.setLadder3Rate(rule.getLadder3Rate());
        share.setLadder4Max(rule.getLadder4Max());
        share.setLadder4Rate(rule.getLadder4Rate());

        //修改分润逻辑判断,还要插入分润修改记录
        AgentInfo agentInfo = agentInfoDao.selectByAgentNo(userInfoBean.getAgentNo());//当前登录代理商
        Date efficientDate = share.getEfficientDate();
        share.setEfficientDate(efficientDate);
        if(efficientDate.getTime() < System.currentTimeMillis()){
            return ResponseBean.error("新增分润失败,生效日期必须大于等于当前日期！");
        }
        //如果填写的生效日期小于当前日期，则返回
        /* 设置值之后,没有任何操作,先注释掉
        AgentShareRule agentShareRule = new AgentShareRule();
//		agentShareRule.setProfitType(share.getProfitType());
        // 现在的分润类型只剩下第5种
        agentShareRule.setProfitType("5");
        agentShareRule.setIncome(share.getIncome());
        agentShareRule.setCost(share.getCost());
        agentShareRule.setLadderRate(share.getLadderRate());
        agentShareRule.setEfficientDate(share.getEfficientDate());
        agentShareRule.setShareProfitPercent(share.getShareProfitPercent());*/

        /* tgh 需求新增操作:优化需求可以设置,要求去掉这个限制
        int count = agentInfoDao.queryAgentShareListTaskByEfficientDate(share.getShareId(), share.getEfficientDate());
        if (count >= 1) {
            throw new AppException("已经存在相同生效日期的分润记录.");
        }*/
        AgentShareRule parentShareRule = agentShareDao.getSameTypeParentAgentShare(shareId);
        if (parentShareRule == null) {
            throw new AppException("上级代理商该分润规则没有配置");
        }
        isChildrenRuleLessThanParent(parentShareRule, share);
        compareServiceRate(parentShareRule, share, agentInfo.getOneLevelId(), agentShareDao.findAgentNo(shareId));
        agentShareDao.insertAgentShareListTask(share);
        log.info("========== 新增task表记录产生 id = {} ============",share.getId());
        // 之后判断插入的记录是否为队长的记录,如果是,则队员相应也要增加
        Long leaderShareId = share.getShareId();
        List<Long> bpIds = agentShareDao.queryMemberBpId(leaderShareId);
        if (!CollectionUtils.isEmpty(bpIds)) {
            for (Long memberBpId : bpIds) {
                Long memberShareId = agentShareDao.getMemberShareId(memberBpId, leaderShareId);
                if (memberShareId != null) {
                    share.setShareId(memberShareId);
                    agentShareDao.insertAgentShareListTask(share);
                }
            }
        }
        //========= start ====== tgh 需求新增操作: 插入修改记录 =========
        ProfitUpdateRecord record = new ProfitUpdateRecord();
        record.setShareId(shareId.toString());
        if ("1".equals(share.getCostRateType())){
            record.setCostHistory(share.getCostHistory() == null ? "" : share.getCostHistory() + "元");
            record.setCost((new BigDecimal(share.getCost()).setScale(2, BigDecimal.ROUND_HALF_UP)) + "元");
        }else if("2".equals(share.getCostRateType())){
            record.setCostHistory(share.getCostHistory() == null ? "" : share.getCostHistory() + "%");
            record.setCost((new BigDecimal(share.getCost()).setScale(2, BigDecimal.ROUND_HALF_UP)) + "%");
        }
        record.setShareProfitPercentHistory(share.getShareProfitPercentHistory());
        record.setShareProfitPercent(share.getShareProfitPercent());
        record.setEfficientDate(share.getEfficientDate());
        record.setEffectiveStatus("0");
        record.setAuther(agentInfo.getAgentNo());
        record.setShareTaskId(share.getId());
        if (agentShareDao.insertShareUpdateRecord(record) > 0){
            return ResponseBean.success("分润修改成功");
        }else {
            throw new AppException("修改失败");
        }
        //=========== end ==================
    }

    /**
     * 下级代理商的分润信息不得低于上级代理商的分润信息
     *
     * @param parentAgentShareRule 上级代理商的分润信息
     * @param rule                 下级代理商的分润信息
     */
    private void isChildrenRuleLessThanParent(AgentShareRule parentAgentShareRule, AgentShareRuleTask rule) {
        // 1. 判断上下级的分润类型是否一致(大类),profitType必须是5
        log.info("parentAgentShareRule: " + parentAgentShareRule);
        log.info("rule: " + rule);
        if (!"5".equals(parentAgentShareRule.getProfitType())) {
            throw new AppException("代理商(" + parentAgentShareRule.getAgentNo() + ")服务("
                    + parentAgentShareRule.getServiceId() + ")的分润类型不是固定成本类型");
        }
        String parentCostRateType = parentAgentShareRule.getCostRateType();
        // 2. 判断上下级的分润类型是否一致(小类),必须都是固定金额,或者必须都是固定扣率
        if (!StringUtils.equals(parentCostRateType, rule.getCostRateType())) {
            log.info("=====上级分润类型 {},下级分润类型 {} ======",parentCostRateType, rule.getCostRateType());
            throw new AppException("代理商(" + parentAgentShareRule.getAgentNo() + ")服务("
                    + parentAgentShareRule.getServiceId() + ")与下级代理商(" + rule.getAgentNo() + ")分润类型不一致");
        }
        // 3. 如果都是固定金额,则进行比较
        if (StringUtils.equals(parentCostRateType, "1")) { // 1-每笔固定金额，
            if (rule.getPerFixCost().compareTo(parentAgentShareRule.getPerFixCost()) < 0) {
                throw new AppException("代理商(" + rule.getAgentNo() + ")" + rule.getServiceId() + "的分润成本 "
                        + rule.getPerFixCost() + "元比上级代理商(" + parentAgentShareRule.getAgentNo() + ")的成本"
                        + parentAgentShareRule.getPerFixCost().setScale(4) + " 元低");
            }
        } else if (StringUtils.equals(parentCostRateType, "2")) { // 2 扣率
            // 4. 否则都是固定扣率,则进行比较
            if (rule.getCostRate().compareTo(parentAgentShareRule.getCostRate()) < 0) {
                throw new AppException("代理商(" + rule.getAgentNo() + ")" + rule.getServiceId() + "的分润成本 "
                        + rule.getCostRate() + "%比上级代理商(" + parentAgentShareRule.getAgentNo() + ")的成本"
                        + parentAgentShareRule.getCostRate().setScale(4) + " %低");
            }
        } else { // 其他异常
            // 5. 不是固定金额,也不是固定扣率,则抛出异常
            throw new AppException("代理商(" + rule.getAgentNo() + ")" + rule.getServiceId() + "的分润成本与上级代理商("
                    + parentAgentShareRule.getAgentNo() + ")的类型不一致");
        }
    }

    private void compareServiceRate(AgentShareRule parentShareRule, AgentShareRuleTask rule, String oneLevelId, String agentNo) {
        Map<String, Object> serviceRateMap = agentShareDao.getSameTypeRootAgentMinServiceRate(rule, oneLevelId, agentNo);
        if (serviceRateMap == null || serviceRateMap.size() == 0) {
            throw new AppException("该代理商的服务费率没有配置.");
        }
        String serviceName = MapUtils.getString(serviceRateMap, "service_name");
            String bpName = MapUtils.getString(serviceRateMap, "bp_name");
            BigDecimal rate = new BigDecimal(MapUtils.getString(serviceRateMap, "rate"));
            BigDecimal singleNumAmount = new BigDecimal(MapUtils.getString(serviceRateMap, "single_num_amount"));
            String isTx = MapUtils.getString(serviceRateMap, "isTx");
        if (StringUtils.equals(isTx, "1")) {
            BigDecimal shareProfitPercent = rule.getShareProfitPercent();
            BigDecimal perFixCost = rule.getPerFixCost();
            BigDecimal multiply = perFixCost.multiply(shareProfitPercent).divide(new BigDecimal("100"));
            if (multiply.compareTo(singleNumAmount) > 0) {
                throw new AppException("代理商(" + bpName + "-" + serviceName + ")的分润成本"
                        + multiply.setScale(4) + " 元高于服务费率 " + singleNumAmount.setScale(4) + "元");
            }
        } else {
            if (rule.getCostRate().compareTo(rate) > 0) {
                throw new AppException("代理商(" + bpName + "-" + serviceName + ")的分润成本"
                        + rule.getCostRate().setScale(4) + " %高于服务费率 " + rate.setScale(4) + " %");
            }
        }
        compareServiceRateNew(parentShareRule,rule,oneLevelId,agentNo);
    }

    private void compareServiceRateNew(AgentShareRule parentShareRule, AgentShareRuleTask rule,
                                       String oneLevelId, String agentNo) {
        Map<String, Object> serviceRateMap = agentShareDao.getSameTypeRootAgentMaxServiceRate(parentShareRule, oneLevelId, agentNo);
        if (serviceRateMap == null || serviceRateMap.size() == 0) {
            throw new AppException("该代理商的服务费率没有配置.");
        }

        log.info("parentShareRule="+parentShareRule);
        log.info("serviceRateMap="+serviceRateMap);
        log.info("rule="+rule);


        String cardType = MapUtils.getString(serviceRateMap, "card_type");
        BigDecimal rate = new BigDecimal(MapUtils.getString(serviceRateMap, "rate"));
        BigDecimal singleNumAmount = new BigDecimal(MapUtils.getString(serviceRateMap, "single_num_amount"));
        String isTx = MapUtils.getString(serviceRateMap, "isTx");
        String bpStr = "(" + parentShareRule.getBpName() + "-" + parentShareRule.getServiceName() + ")";
        if (StringUtils.equals(isTx, "1")) {
            Map<String, Object> agentShareRuleMap = agentShareDao.selectAgentShareRule((String) serviceRateMap.get("agent_no"), rule.getServiceId(), cardType);
            log.info("agentShareRuleMap="+agentShareRuleMap);
            if (agentShareRuleMap == null) {
                throw new AppException(bpStr + "上级代理商没有配置此产品");
            }
            if (agentShareRuleMap.get("per_fix_cost") == null || agentShareRuleMap.get("share_profit_percent") == null) {
                throw new AppException(bpStr + "上级代理商成本扣率或者分润百分比没有配置");
            }
            BigDecimal aResult = (singleNumAmount.subtract(parentShareRule.getPerFixCost())).multiply(parentShareRule.getShareProfitPercent());
            BigDecimal myCostRate = rule.getPerFixCost();
            BigDecimal myShareProfitPercent = rule.getShareProfitPercent();
            BigDecimal bResult = (singleNumAmount.subtract(myCostRate).multiply(myShareProfitPercent));
            if (aResult.compareTo(bResult) < 0) {
                throw new AppException(bpStr + "下级提现分润大于上级，请重新设置");
            }

        } else {
            //在原判断逻辑上，在修改代理商固定分润百分比时，
            // 被修改的代理商(商户签约扣率 - 代理商成本扣率) * 代理商分润百分比
            // 不能高于上级的(商户签约扣率 - 代理商成本扣率) * 代理商分润百分比，
            // 如：1级代理商设置2级代理商的分润百分比时，设置的不能高于1级的，以此类推

            Map<String, Object> agentShareRuleMap = agentShareDao.selectAgentShareRule(agentNo, rule.getServiceId(), cardType);
            log.info("agentShareRuleMap="+agentShareRuleMap);
            if (agentShareRuleMap == null) {
                throw new AppException(bpStr + "上级代理商没有配置此产品");
            }
            if (agentShareRuleMap.get("cost_rate") == null || agentShareRuleMap.get("share_profit_percent") == null) {
                throw new AppException(bpStr + "上级代理商成本扣率或者分润百分比没有配置");
            }
            BigDecimal aResult = (rate.subtract(rule.getCostRate())).multiply(rule.getShareProfitPercent());
            BigDecimal myCostRate = parentShareRule.getCostRate();
            BigDecimal myShareProfitPercent = parentShareRule.getShareProfitPercent();
            BigDecimal bResult = (rate.subtract(myCostRate).multiply(myShareProfitPercent));
            if (aResult.compareTo(bResult) > 0) {
                throw new AppException(bpStr + "下级交易分润大于上级，请重新设置");
            }
        }
    }

    @Override
    public List<AgentShareRule> getAgentShareList(String param) {
        return agentShareDao.getAgentShareList(param);
    }
}
