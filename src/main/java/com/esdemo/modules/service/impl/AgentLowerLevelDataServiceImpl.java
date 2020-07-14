package com.esdemo.modules.service.impl;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.AgentInfoData;
import com.esdemo.modules.bean.AgentShareRuleInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.bean.Vo.*;
import com.esdemo.modules.dao.AgentInfoDataDao;
import com.esdemo.modules.dao.AgentLowerLevelDao;
import com.esdemo.modules.service.AgentLowerLevelDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class AgentLowerLevelDataServiceImpl implements AgentLowerLevelDataService {

    @Resource
    private AgentLowerLevelDao agentLowerLevelDao;
    @Resource
    private AgentInfoDataDao agentInfoDataDao;

    /**
     * 新开数据下发
     * @param userInfoBean
     * @return
     */
    @Override
    public ResponseBean getAgentDetailAdd(UserInfoBean userInfoBean) {
        Map<String,Object> map=new HashMap<String,Object>();
        List<AgentBpIdInfoVo> bpListParent=agentLowerLevelDao.getAgentBpIdByOrder(userInfoBean.getAgentNo());
        if(bpListParent!=null&&bpListParent.size()>0){
            for(AgentBpIdInfoVo item:bpListParent){
                if("1".equals(item.getAllowIndividualApply())){//有组队长和其他的
                    item.setAgentShare(shareRuleMerge(item.getBpId(),userInfoBean.getAgentNo(),userInfoBean.getOneLevelId()));//当前登入代理商
                }
            }
        }
        map.put("bpListParent",bpListParent);

        //欢乐返
        List<AgentActivityVo> happyBackParent=getAgentActivity(userInfoBean.getAgentNo(),"1",1,true);
        map.put("happyBackParent",happyBackParent);
        //新欢乐送
        List<AgentActivityVo> newHappyGiveParent=getAgentActivity(userInfoBean.getAgentNo(),"2",1,true);
        map.put("newHappyGiveParent",newHappyGiveParent);
        return ResponseBean.success(map);
    }

    //获取代理服务分润，合并转换
    private List<AgentShareRuleInfoVo> shareRuleMerge(Long bpId,String agentNo,String oneAgentNo){
        List<AgentShareRuleInfo> shareRuleList=agentLowerLevelDao.getAgentShare(bpId,agentNo,oneAgentNo);
        Map<String,List<AgentShareRuleInfo>> map=new HashMap<String,List<AgentShareRuleInfo>>();
        boolean wxServiceSta=false;
        int wxNum=-1;
        boolean wxWithdrawalServiceSta=false;
        int wxWithdrawalNum=-1;
        for(AgentShareRuleInfo itemShare:shareRuleList){
            if(itemShare.getCashOutStatus().intValue()==1){//提现服务
                String linkServiceType=agentLowerLevelDao.getLinkServiceType(itemShare.getServiceId());
                if("10002".equals(linkServiceType) ||"10003".equals(linkServiceType) ||"10005".equals(linkServiceType)
                        ||"10006".equals(linkServiceType) ||"10011".equals(linkServiceType) ||"10012".equals(linkServiceType)
                ){
                    //判断服务成本固定价是否修改一致
                    if(wxWithdrawalNum==-1){
                        wxWithdrawalNum=itemShare.getIsPriceUpdate().intValue();
                    }else{
                        if(itemShare.getIsPriceUpdate().intValue()!=wxWithdrawalNum){
                            wxWithdrawalServiceSta=true;
                        }
                    }
                    saveMap(itemShare,map,"wxWithdrawalService");
                    continue;
                }
            }else{//交易服务
                if("10002".equals(itemShare.getServiceType()) ||"10003".equals(itemShare.getServiceType()) ||"10005".equals(itemShare.getServiceType())
                        ||"10006".equals(itemShare.getServiceType()) ||"10011".equals(itemShare.getServiceType()) ||"10012".equals(itemShare.getServiceType())
                ){
                    //判断服务成本固定价是否修改一致
                    if(wxNum==-1){
                        wxNum=itemShare.getIsPriceUpdate().intValue();
                    }else{
                        if(itemShare.getIsPriceUpdate().intValue()!=wxNum){
                            wxServiceSta=true;
                        }
                    }
                    saveMap(itemShare,map,"wxService");
                    continue;
                }
            }
            //通用存储方式
            saveMap(itemShare,map,null);
        }
        //如果WX支付宝服务设置费率可编辑状态不一致，则删除map合并的，改为普通合并方式
        saveWxMap("wxService",wxServiceSta,map);
        saveWxMap("wxWithdrawalService",wxWithdrawalServiceSta,map);

        List<AgentShareRuleInfoVo> shareRuleListVo=new ArrayList<AgentShareRuleInfoVo>();
        for(Map.Entry<String,List<AgentShareRuleInfo>> entry:map.entrySet()){
            AgentShareRuleInfoVo info=getShareRuleVo(entry.getKey(),entry.getValue());
            if(info!=null){
                shareRuleListVo.add(info);
            }
        }
        return shareRuleListVo;
    }
    private void saveWxMap(String key,boolean sta,Map<String,List<AgentShareRuleInfo>> map){
        if(sta){
            List<AgentShareRuleInfo> addList=map.get(key);
            if(addList!=null&&addList.size()>0){
                for(AgentShareRuleInfo item:addList){
                    saveMap(item,map,null);
                }
            }
            map.put(key,null);
        }
    }
    private void saveMap(AgentShareRuleInfo itemShare,Map<String,List<AgentShareRuleInfo>> map,String key){
        if(key==null){
            if(itemShare.getIsPriceUpdate().intValue()==1){//低价不可修改时
                key=itemShare.getBpId()+"-"+itemShare.getServiceId()+"-"+itemShare.getShareProfitPercent().toString()+"-"+getCost(itemShare).toString();//其他服务组合key
            }else{
                key=itemShare.getBpId()+"-"+itemShare.getServiceId();//其他服务组合key
            }
        }
        if(map.get(key)==null){
            List<AgentShareRuleInfo> addList=new ArrayList<AgentShareRuleInfo>();
            addList.add(itemShare);
            map.put(key,addList);
        }else{
            List<AgentShareRuleInfo> addList=map.get(key);
            addList.add(itemShare);
        }
    }
    private AgentShareRuleInfoVo getShareRuleVo(String key,List<AgentShareRuleInfo> list){
        if(list!=null&&list.size()>0){
            //校验多条服务配置的值是否一样，如一样下发数据，如不一样下发null
            AgentShareRuleInfo itemFirst=list.get(0);//先取第一个值
            BigDecimal checkShare=itemFirst.getShareProfitPercent();
            BigDecimal checkCost=getCost(itemFirst);
            boolean checkSta=true;

            StringBuffer sb=new StringBuffer();
            StringBuffer cardStr=new StringBuffer();
            StringBuffer holidaysMarkStr=new StringBuffer();

            for(AgentShareRuleInfo item:list){
                sb.append(item.getBpId()).append("-");
                sb.append(item.getServiceId()).append("-");
                sb.append(item.getCardType()).append("-");
                sb.append(item.getHolidaysMark()).append(",");

                cardStr.append(item.getCardType()).append(",");
                holidaysMarkStr.append(item.getHolidaysMark()).append(",");

                if(checkSta){
                    if(!checkShare.equals(item.getShareProfitPercent())
                        ||!checkCost.equals(getCost(item))){
                        checkSta=false;
                        //不跳出，为了合并ID
                    }
                }
            }
            AgentShareRuleInfoVo voItem=new AgentShareRuleInfoVo();
            //设置ID
            voItem.setId(sb.toString().substring(0,sb.toString().length()-1));
            voItem.setCashOutStatus(itemFirst.getCashOutStatus());//是否提现服务一致，直接取第一个就可以
            voItem.setIsPriceUpdate(itemFirst.getIsPriceUpdate());//代理商成本是否可以设置
            if(checkSta){
                voItem.setShare(checkShare);//分润比例
                voItem.setCost(checkCost);
            }else{
                voItem.setShare(null);
                voItem.setCost(null);
            }
            if("wxService".equals(key)){
                voItem.setServiceName("微信/支付宝 支付");
            }else if("wxWithdrawalService".equals(key)){
                voItem.setServiceName("微信/支付宝 关联提现");
            }else{
                String name=itemFirst.getServiceName();
                if(cardStr.indexOf("1,")>=0&&cardStr.indexOf("2,")>=0){
                    name=name+" 借记卡/贷记卡";
                }else if(cardStr.indexOf("1,")>=0&&cardStr.indexOf("2,")<0){
                    name=name+" 贷记卡";
                }else if(cardStr.indexOf("1,")<0&&cardStr.indexOf("2,")>=0){
                    name=name+" 借记卡";
                }
                voItem.setServiceName(name);
            }
            return voItem;

        }
        return null;
    }

    /**
     * 修改数据下发
     * @param agentNo
     * @param userInfoBean
     * @return
     */
    @Override
    public ResponseBean getAgentDetailEdit(String agentNo, UserInfoBean userInfoBean) {
        Map<String,Object> map=new HashMap<String,Object>();
        //下发代理商基础数据
        AgentLowerLevelInfoVo info=agentLowerLevelDao.getAgentLowerLevelDetail(agentNo);
        if(info==null){
            return ResponseBean.error("该代理商不存在!");
        }else{
            if(info.getAgentNode().indexOf(userInfoBean.getAgentNode())<0){
                return ResponseBean.error("当前登入代理商没有权限操作该代理商!");
            }
        }
        if(info.getParentId().equals(userInfoBean.getAgentNo())){
            info.setLowerStatus("1");
        }else{
            info.setLowerStatus("0");
        }
        info.setMobilephone(StringUtils.mask4MobilePhone(info.getMobilephone()));
        map.put("agentInfo",info);

        //获取代理商所代理的业务产品
        getBpList(info,map);
        //查询活动数据
        getAgentActivityList(agentNo,info.getParentId(),map);

        return ResponseBean.success(map);
    }

    /**
     * 修改详情活动活动数据
     * @param agentNo
     * @param agentNoParent
     * @param map
     */
    private void getAgentActivityList(String agentNo,String agentNoParent,Map<String,Object> map){
        agentNoParent="0".equals(agentNoParent)?agentNo:agentNoParent;
        //欢乐返
        List<AgentActivityVo> happyBack=getAgentActivity(agentNo,"1",0,false);
        List<AgentActivityVo> happyBackParent=getAgentActivity(agentNoParent,"1",1,false);

        List<AgentActivityVo>  happyBackDetail=agentActivityFuseDetail(happyBack,happyBackParent);
        map.put("happyBackDetail",happyBackDetail);

        List<AgentActivityVo>  happyBackEdit=agentActivityFuseEdit(happyBack,happyBackParent);
        map.put("happyBack",happyBackEdit);

        //新欢乐送
        List<AgentActivityVo> newHappyGive=getAgentActivity(agentNo,"2",0,false);
        List<AgentActivityVo> newHappyGiveParent=getAgentActivity(agentNoParent,"2",1,false);

        List<AgentActivityVo> newHappyGiveDetail=agentActivityFuseDetail(newHappyGive,newHappyGiveParent);
        map.put("newHappyGiveDetail",newHappyGiveDetail);
        List<AgentActivityVo>  newHappyGiveEdit=agentActivityFuseEdit(newHappyGive,newHappyGiveParent);
        map.put("newHappyGive",newHappyGiveEdit);

    }

    private  List<AgentActivityVo> agentActivityFuseEdit(List<AgentActivityVo> list,List<AgentActivityVo> listParent) {
        //复制父级List
        List<AgentActivityVo> newList=new ArrayList<AgentActivityVo>(listParent);
        Map<String,AgentActivityVo> ownMap=getAgentActivityMap(list);
        if(newList!=null&&newList.size()>0) {
            for (AgentActivityVo item : newList) {
                //先把值复制到父级节点中
                item.setParentValue(getAgentActivityParentVo(item));
                AgentActivityVo itemOwn=ownMap.get(getAgentActivityKey(item));
                if(itemOwn!=null){
                    item.setLockStatus(1);
                    setAgentActivityVo(item,itemOwn);
                }else{
                    item.setLockStatus(0);
                    setAgentActivityVo(item,null);
                }
            }
        }
        return newList;
    }


    private  List<AgentActivityVo> agentActivityFuseDetail(List<AgentActivityVo> list,List<AgentActivityVo> listParent){
        //复制当前List
        List<AgentActivityVo> newList=new ArrayList<AgentActivityVo>(list);
        //返回map的key值是 组织+子类型编号
        Map<String,AgentActivityVo> parentMap=getAgentActivityMap(listParent);
        if(newList!=null&&newList.size()>0) {
            for (AgentActivityVo item : newList) {
                item.setLockStatus(1);
                AgentActivityVo itemParent=parentMap.get(getAgentActivityKey(item));
                if(itemParent!=null){
                    item.setParentValue(getAgentActivityParentVo(itemParent));
                    //获取父级的开关设置
                    item.setFullPrizeSwitch(itemParent.getFullPrizeSwitch());
                    item.setNotFullDeductSwitch(itemParent.getNotFullDeductSwitch());
                    item.setRewardLevel(itemParent.getRewardLevel());
                    item.setDeductionStatus(itemParent.getDeductionStatus());
                }
            }
        }
        return newList;
    }

    /**
     * 设置活动 子级-父级数据覆盖
     * @param item
     * @param itemParent 如果该值为空，则为重置数据，如果有值，则为赋值
     */
    private void setAgentActivityVo(AgentActivityVo item,AgentActivityVo itemParent){
        if(itemParent!=null){
            item.setAgentNo(itemParent.getAgentNo());
            item.setAgentNo(itemParent.getAgentNode());
            item.setCashBackAmount(itemParent.getCashBackAmount());
            item.setTaxRate(itemParent.getTaxRate());
            item.setFullPrizeAmount(itemParent.getFullPrizeAmount());
            item.setNotFullDeductAmount(itemParent.getNotFullDeductAmount());
            item.setOneRewardAmount(itemParent.getOneRewardAmount());
            item.setTwoRewardAmount(itemParent.getTwoRewardAmount());
            item.setThreeRewardAmount(itemParent.getThreeRewardAmount());
            item.setFourRewardAmount(itemParent.getFourRewardAmount());
            item.setDeductionAmount(itemParent.getDeductionAmount());
            item.setRewardRate(itemParent.getRewardRate());
        }else{//重置为空
            item.setAgentNo(null);
            item.setAgentNo(null);
            item.setCashBackAmount(null);
            item.setTaxRate(null);
            item.setFullPrizeAmount(null);
            item.setNotFullDeductAmount(null);
            item.setOneRewardAmount(null);
            item.setTwoRewardAmount(null);
            item.setThreeRewardAmount(null);
            item.setFourRewardAmount(null);
            item.setDeductionAmount(null);
            item.setRewardRate(null);
        }
    }

    /**
     * 构建父级数据实体
     * @param itemParent
     * @return
     */
    private AgentActivityParentVo getAgentActivityParentVo(AgentActivityVo itemParent){
        AgentActivityParentVo parenInfo=new AgentActivityParentVo();
        parenInfo.setAgentNo(itemParent.getAgentNo());
        parenInfo.setAgentNode(itemParent.getAgentNode());
        parenInfo.setCashBackAmount(itemParent.getCashBackAmount());
        parenInfo.setTaxRate(itemParent.getTaxRate());
        parenInfo.setFullPrizeAmount(itemParent.getFullPrizeAmount());
        parenInfo.setNotFullDeductAmount(itemParent.getNotFullDeductAmount());
        parenInfo.setOneRewardAmount(itemParent.getOneRewardAmount());
        parenInfo.setTwoRewardAmount(itemParent.getTwoRewardAmount());
        parenInfo.setThreeRewardAmount(itemParent.getThreeRewardAmount());
        parenInfo.setFourRewardAmount(itemParent.getFourRewardAmount());
        parenInfo.setDeductionAmount(itemParent.getDeductionAmount());
        parenInfo.setRewardRate(itemParent.getRewardRate());
        return parenInfo;
    }

    /**
     * 奖活动List 转换成MAP方便后续取值
     * @param list
     * @return
     */
    private Map<String,AgentActivityVo> getAgentActivityMap(List<AgentActivityVo> list){
        Map<String,AgentActivityVo> map=new HashMap<String,AgentActivityVo>();
        if(list!=null&&list.size()>0){
            for(AgentActivityVo item: list){
                map.put(getAgentActivityKey(item),item);
            }
        }
        return map;
    }

    /**
     * 活动组织key
     * @param item
     * @return
     */
    private String getAgentActivityKey(AgentActivityVo item){
        String key=item.getActivityTypeNo();
        return key;
    }

    /**
     * 获取活动数据
     * @param agentNo
     * @param subType 欢乐返类型
     * @param sta 0本级 1上级
     * @param addSta 是否是新增时调用
     * @return
     */
    private  List<AgentActivityVo> getAgentActivity(String agentNo,String subType,int sta,boolean addSta){
        List<AgentActivityVo> list=agentLowerLevelDao.getAgentActivity(agentNo,subType);
        AgentInfoData agentInfo=agentInfoDataDao.getAgentInfoData(agentNo);
        //获取数据字典满奖，满扣层级上限控制
        Map<String,String> oemSwitch=getAgentOemPrizeBuckleRank();
        Map<String,AgentActivityCheckData> checkMap=new HashMap<String,AgentActivityCheckData>();
        Map<String,String> repeatMap=new HashMap<String,String>();

        //去除子类型相同的数据
        Iterator<AgentActivityVo> it = list.iterator();
        while(it.hasNext()){
            AgentActivityVo item=it.next();
            if(repeatMap.get(item.getActivityTypeNo())!=null){
                it.remove();
                continue;
            }
            repeatMap.put(item.getActivityTypeNo(),"1");
            //将活动的返现比例改成100%不带单位
            if(item.getTaxRate()!=null){
                item.setTaxRate(item.getTaxRate().multiply(new BigDecimal(100)));
            }
            if(item.getRewardRate()!=null){
                item.setRewardRate(item.getRewardRate().multiply(new BigDecimal(100)));
            }
            if(sta==1){
                //设置开关
                if("1".equals(item.getSubType())){
                    saveSwitch(oemSwitch,item,agentInfo);
                }else if("2".equals(item.getSubType())){
                    saveRewardLevel(item);
                }
                if(addSta){
                    if(StringUtils.isNotBlank(item.getGroupNo())){
                        String key=getAgentActivityVoCheckKey(item);
                        if(checkMap.get(key)==null){
                            checkMap.put(key,new AgentActivityCheckData(true,item));
                        }else{
                            AgentActivityCheckData checkData=checkMap.get(key);
                            if(checkData.isCheckSta()){//如果相同则校验，不同不处理
                                if(!checkDataInfo(item,checkData.getCheckInfo())){
                                    checkMap.put(key,new AgentActivityCheckData(false,null));
                                }
                            }
                        }
                    }
                }
            }
        }
        //遍历完成后，将值复制到数据对象里
        if(sta==1&&addSta){
            for(AgentActivityVo item:list) {
                if (StringUtils.isNotBlank(item.getGroupNo())) {
                    if(checkMap.get(getAgentActivityVoCheckKey(item)).isCheckSta()){
                        item.setActivityValueSameStatus(1);
                    }else{
                        item.setActivityValueSameStatus(0);
                    }
                }
            }
        }
        return list;
    }

    private String getAgentActivityVoCheckKey(AgentActivityVo item){
        return item.getTeamId().toString()+"-"+item.getGroupNo();
    }


    /**
     * 判断满奖满扣是否一样
     * 校验AP2个对象相应属性是否一样
     * @param itemA
     * @param itemB
     * @return
     */
    private boolean checkDataInfo(AgentActivityVo itemA,AgentActivityVo itemB){
        if("1".equals(itemB.getSubType())){//欢乐返
            BigDecimal fullPrizeAmount=itemA.getFullPrizeAmount()==null?BigDecimal.ZERO:itemA.getFullPrizeAmount();
            BigDecimal notFullDeductAmount=itemA.getNotFullDeductAmount()==null?BigDecimal.ZERO:itemA.getNotFullDeductAmount();
            BigDecimal rewardRate=itemA.getRewardRate()==null?BigDecimal.ZERO:itemA.getRewardRate();

            BigDecimal fullPrizeAmountB=itemB.getFullPrizeAmount()==null?BigDecimal.ZERO:itemB.getFullPrizeAmount();
            BigDecimal notFullDeductAmountB=itemB.getNotFullDeductAmount()==null?BigDecimal.ZERO:itemB.getNotFullDeductAmount();
            BigDecimal rewardRateB=itemB.getRewardRate()==null?BigDecimal.ZERO:itemB.getRewardRate();
            //开关状态 ，数据一致才融合
            if(itemA.getFullPrizeSwitch().intValue()==itemB.getFullPrizeSwitch().intValue()
                    &&itemA.getNotFullDeductSwitch().intValue()==itemB.getNotFullDeductSwitch().intValue()
                    &&fullPrizeAmount.compareTo(fullPrizeAmountB)==0
                    &&notFullDeductAmount.compareTo(notFullDeductAmountB)==0
                    &&rewardRate.compareTo(rewardRateB)==0){
                return true;
            }
        }else if("2".equals(itemB.getSubType())){//新欢乐送
            BigDecimal oneRewardAmount=itemA.getOneRewardAmount()==null?BigDecimal.ZERO:itemA.getOneRewardAmount();
            BigDecimal twoRewardAmount=itemA.getTwoRewardAmount()==null?BigDecimal.ZERO:itemA.getTwoRewardAmount();
            BigDecimal threeRewardAmount=itemA.getThreeRewardAmount()==null?BigDecimal.ZERO:itemA.getThreeRewardAmount();
            BigDecimal fourRewardAmount=itemA.getFourRewardAmount()==null?BigDecimal.ZERO:itemA.getFourRewardAmount();
            BigDecimal deductionAmount=itemA.getDeductionAmount()==null?BigDecimal.ZERO:itemA.getDeductionAmount();
            BigDecimal rewardRate=itemA.getRewardRate()==null?BigDecimal.ZERO:itemA.getRewardRate();

            BigDecimal oneRewardAmountB=itemB.getOneRewardAmount()==null?BigDecimal.ZERO:itemB.getOneRewardAmount();
            BigDecimal twoRewardAmountB=itemB.getTwoRewardAmount()==null?BigDecimal.ZERO:itemB.getTwoRewardAmount();
            BigDecimal threeRewardAmountB=itemB.getThreeRewardAmount()==null?BigDecimal.ZERO:itemB.getThreeRewardAmount();
            BigDecimal fourRewardAmountB=itemB.getFourRewardAmount()==null?BigDecimal.ZERO:itemB.getFourRewardAmount();
            BigDecimal deductionAmountB=itemB.getDeductionAmount()==null?BigDecimal.ZERO:itemB.getDeductionAmount();
            BigDecimal rewardRateB=itemB.getRewardRate()==null?BigDecimal.ZERO:itemB.getRewardRate();

            //开关状态 ，数据一致才融合
            if(itemA.getRewardLevel().intValue()==itemB.getRewardLevel().intValue()
                    &&itemA.getDeductionStatus().intValue()==itemB.getDeductionStatus().intValue()
                    &&oneRewardAmount.compareTo(oneRewardAmountB)==0 &&twoRewardAmount.compareTo(twoRewardAmountB)==0
                    &&threeRewardAmount.compareTo(threeRewardAmountB)==0 &&fourRewardAmount.compareTo(fourRewardAmountB)==0
                    &&deductionAmount.compareTo(deductionAmountB)==0 &&rewardRate.compareTo(rewardRateB)==0){
                return true;
            }
        }
        return false;
    }
    /**
     * 保存活动开关
     * @param oemSwitch
     * @param item
     * @param agentInfo
     */
    public void saveSwitch( Map<String,String> oemSwitch,AgentActivityVo item,AgentInfoData agentInfo){
        String strs=oemSwitch.get("agent_oem_prize_buckle_rank_"+item.getTeamId().toString());
        String str1=null;//满奖层级
        String str2=null;//满扣层级
        if(StringUtils.isNotBlank(strs)){
            //后台数据有2个值
            String[] strList=strs.split("-");
            if(strList.length>=2){
                str1=strList[0];
                str2=strList[1];
            }
        }
        if(agentInfo.getFullPrizeSwitch().intValue()==1){//满奖开关 1-打开，0-关闭
            item.setFullPrizeSwitch(0);
            //如果打开，则判断是否超过上限
            int num1=Integer.parseInt(agentInfo.getAgentLevel())+1;//下级代理商等级
            if(StringUtils.isNotBlank(str1)){
                if(Integer.parseInt(str1)+2>num1){
                    item.setFullPrizeSwitch(1);
                }
            }
        }else{
            item.setFullPrizeSwitch(0);
        }

        if(agentInfo.getNotFullDeductSwitch().intValue()==1){//不满扣开关 1-打开，0-关闭
            item.setNotFullDeductSwitch(0);
            //如果打开，则判断是否超过上限
            int num1=Integer.parseInt(agentInfo.getAgentLevel())+1;//下级代理商等级
            if(StringUtils.isNotBlank(str2)){
                if(Integer.parseInt(str2)+2>num1){
                    item.setNotFullDeductSwitch(1);
                }
            }
        }else{
            item.setNotFullDeductSwitch(0);
        }
    }
    /**
     * 下发奖励考核周期
     * @param item
     */
    public void saveRewardLevel(AgentActivityVo item){
        ActivityTypeVo info=agentLowerLevelDao.getActivityTypeVo(item.getActivityTypeNo());
        //设置不达标扣款显示
        if(info.getDeductionLimitDays()==null||info.getDeductionLimitDays().intValue()==0){
            item.setDeductionStatus(0);
        }else{
            item.setDeductionStatus(1);
        }
        if(info.getOneLimitDays()==null||info.getOneLimitDays().intValue()==0){
            item.setRewardLevel(0);
            return;
        }
        if(info.getTwoLimitDays()==null||info.getTwoLimitDays().intValue()==0){
            item.setRewardLevel(1);
            return;
        }
        if(info.getThreeLimitDays()==null||info.getThreeLimitDays().intValue()==0){
            item.setRewardLevel(2);
            return;
        }
        if(info.getFourLimitDays()==null||info.getFourLimitDays().intValue()==0){
            item.setRewardLevel(3);
            return;
        }
        item.setRewardLevel(4);
    }

    //获取OEM链条开关
    public Map<String,String> getAgentOemPrizeBuckleRank(){
        Map<String,String> map=new HashMap<String, String>();
        List<Map<String,Object>> list= agentInfoDataDao.getAgentOemPrizeBuckleRank();
        if(list!=null&&list.size()>0){
            for(Map<String,Object> item:list){
                map.put(item.get("sys_key").toString(),item.get("sys_value").toString());
            }
        }
        return map;
    }
    /**
     * 获取服务LIST
     * @param info 当前操作代理商
     */
    private void getBpList(AgentLowerLevelInfoVo info,Map<String,Object> map){
        if(info!=null){
            //获取代理商所代理的业务产品
            List<AgentBpIdInfoVo> bpList=getBpList(info.getAgentNo(),info.getOneLevelId());
            List<AgentBpIdInfoVo> bpListParent=getBpList("0".equals(info.getParentId())?info.getAgentNo():info.getParentId(),info.getOneLevelId());

            List<AgentBpIdInfoVo> bpListDetail =getbpListDetail(bpList,bpListParent);
            map.put("bpListDetail",bpListDetail);

            List<AgentBpIdInfoVo> bpListEdit = getbpListEdit(bpList,bpListParent);
            map.put("bpList",bpListEdit);
        }
    }
    //获取修改List
    private List<AgentBpIdInfoVo> getbpListEdit(List<AgentBpIdInfoVo> bpList,List<AgentBpIdInfoVo> bpListParent){
        List<AgentBpIdInfoVo> newList=new ArrayList<AgentBpIdInfoVo>(bpListParent);//复制List
        Map<String,AgentBpIdInfoVo> bpMap=getBpMap(bpList);
        if(newList!=null&&newList.size()>0) {
            for (AgentBpIdInfoVo itemParent : newList) {
                AgentBpIdInfoVo itemBp=bpMap.get(itemParent.getBpId().toString());
                Map<String,AgentShareRuleInfoVo> shareMap=null;
                //设置锁定勾选值
                if(itemBp!=null){
                    itemParent.setLockStatus(1);
                    itemParent.setAgentNo(itemBp.getAgentNo());
                    shareMap=getShareMap(itemBp.getAgentShare());
                }else{
                    itemParent.setLockStatus(0);
                }

                //遍历上级服务分润设置分润值
                List<AgentShareRuleInfoVo> shareListParent=itemParent.getAgentShare();
                if(shareListParent!=null&&shareListParent.size()>0) {
                    for (AgentShareRuleInfoVo shareItemParent : shareListParent) {
                        BigDecimal cost=null;
                        BigDecimal share=null;
                        if(shareMap!=null){
                            AgentShareRuleInfoVo infoNow=shareMap.get(shareItemParent.getId());//当前代理商分润
                            if(infoNow!=null){
                                cost=infoNow.getCost();
                                share=infoNow.getShare();
                            }
                        }
                        shareItemParent.setParentValue(new AgentShareRuleInfoParentVo(shareItemParent.getCost(),shareItemParent.getShare()));
                        shareItemParent.setCost(cost);
                        shareItemParent.setShare(share);
                    }
                }
            }
        }
        return newList;
    }

    //获取详情List
    private List<AgentBpIdInfoVo> getbpListDetail(List<AgentBpIdInfoVo> bpList,List<AgentBpIdInfoVo> bpListParent){
        //不改变原list值，拼凑详情
        List<AgentBpIdInfoVo> newList=new ArrayList<AgentBpIdInfoVo>(bpList);//复制List
        Map<String,AgentBpIdInfoVo> bpMapParent=getBpMap(bpListParent);
        if(newList!=null&&newList.size()>0){
            for(AgentBpIdInfoVo item:newList){
                AgentBpIdInfoVo itemParent=bpMapParent.get(item.getBpId().toString());
                item.setLockStatus(1);//设置锁定勾选
                Map<String,AgentShareRuleInfoVo> shareParentMap=getShareMap(itemParent.getAgentShare());
                List<AgentShareRuleInfoVo> shareList=itemParent.getAgentShare();
                if(shareList!=null&&shareList.size()>0){
                    for(AgentShareRuleInfoVo shareItem:shareList){
                        AgentShareRuleInfoVo parent=shareParentMap.get(shareItem.getId());
                        shareItem.setParentValue(new AgentShareRuleInfoParentVo(parent.getCost(),parent.getShare()));
                    }
                }
            }
        }
        return newList;
    }
    private Map<String,AgentShareRuleInfoVo> getShareMap(List<AgentShareRuleInfoVo> list){
        Map<String,AgentShareRuleInfoVo> map=new HashMap<String,AgentShareRuleInfoVo>();
        if(list!=null&&list.size()>0){
            for(AgentShareRuleInfoVo item: list){
                map.put(item.getId(),item);
            }
        }
        return map;
    }
    //
    /**
     * 获取代理商所代理的业务产品
     * @param agentNo 当前登入代理商
     * @param oneAgentNo  一级代理商
     * @return
     */
    private List<AgentBpIdInfoVo> getBpList(String agentNo,String oneAgentNo){
        List<AgentBpIdInfoVo> bpList=agentLowerLevelDao.getAgentBpIdByOrder(agentNo);
        if(bpList!=null&&bpList.size()>0){
            for(AgentBpIdInfoVo item:bpList){
                if("1".equals(item.getAllowIndividualApply())){//有组队长和其他的
                    item.setAgentShare(getAgentShareRuleList(item.getBpId(),agentNo,oneAgentNo));//当前操作代理商
                }
            }
        }
        return bpList;
    }
    private Map<String,AgentBpIdInfoVo> getBpMap(List<AgentBpIdInfoVo> bpList){
        Map<String,AgentBpIdInfoVo> bpMap=new HashMap<>();
        if(bpList!=null&&bpList.size()>0){
            for(AgentBpIdInfoVo item: bpList){
                bpMap.put(item.getBpId().toString(),item);
            }
        }
        return bpMap;
    }

    //获取代理商分润，转换成前端下发实体
    private List<AgentShareRuleInfoVo> getAgentShareRuleList(Long bpId, String agentNo, String oneAgentNo){
        List<AgentShareRuleInfoVo> shareRuleListVo=new ArrayList<AgentShareRuleInfoVo>();//传送到前端的分润设置
        List<AgentShareRuleInfo> shareRuleList=agentLowerLevelDao.getAgentShare(bpId,agentNo,oneAgentNo);
        if(shareRuleList!=null&&shareRuleList.size()>0){
            for(AgentShareRuleInfo itemShare:shareRuleList){
                shareRuleListVo.add(getShareRuleVo(itemShare));
            }
        }
        return shareRuleListVo;
    }
    private AgentShareRuleInfoVo getShareRuleVo(AgentShareRuleInfo item){
        AgentShareRuleInfoVo voItem=new AgentShareRuleInfoVo();
        voItem.setId(shareRuleIdConvert(item));
        voItem.setShare(item.getShareProfitPercent());//分润比例
        voItem.setCashOutStatus(item.getCashOutStatus());
        voItem.setServiceName(item.getServiceName());
        voItem.setIsPriceUpdate(item.getIsPriceUpdate());
        voItem.setCost(getCost(item)); //代理商成本
        String name=item.getServiceName();
        if("1".equals(item.getCardType())){
            name=name+" 贷记卡";
        }else if("2".equals(item.getCardType())){
            name=name+" 借记卡";
        }
        voItem.setServiceName(name);
        return voItem;
    }

    /**
     * 单个实体ID转换
     * @param item
     * @return
     */
    private String shareRuleIdConvert(AgentShareRuleInfo item){
        StringBuffer sb=new StringBuffer();
        sb.append(item.getBpId()).append("-");
        sb.append(item.getServiceId()).append("-");
        sb.append(item.getCardType()).append("-");
        sb.append(item.getHolidaysMark());
        return sb.toString();
    }

    /**
     * 获取服务费率
     * @param item
     * @return
     */
    private BigDecimal getCost(AgentShareRuleInfo item){
        BigDecimal cost=BigDecimal.ZERO;
        if("5".equals(item.getProfitType())){
            if("1".equals(item.getCostRateType())){
                cost=item.getPerFixCost();
            }else{
                cost=item.getCostRate();
            }
        }
        return cost;
    }
}

