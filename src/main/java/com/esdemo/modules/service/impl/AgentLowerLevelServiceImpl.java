package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.utils.ClientInterface;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.bean.Vo.*;
import com.esdemo.modules.dao.AgentInfoDataDao;
import com.esdemo.modules.dao.AgentLowerLevelDao;
import com.esdemo.modules.service.AgentLowerLevelDataService;
import com.esdemo.modules.service.AgentLowerLevelService;
import com.esdemo.modules.service.OpenPlatformService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 代理商开设下级查询service
 * @author liuks
 */
@Service
@Slf4j
public class AgentLowerLevelServiceImpl implements AgentLowerLevelService {

    @Resource
    private AgentLowerLevelDao agentLowerLevelDao;

    @Resource
    private OpenPlatformService openPlatformService;

    @Resource
    private SeqService seqService;

    @Resource
    private AgentInfoDataDao agentInfoDataDao;

    @Resource
    private AgentLowerLevelDataService agentLowerLevelDataService;

    @Resource
    private AgentLowerLevelService agentLowerLevelService;


    @Override
    public ResponseBean getAgentLowerLevelToBeSetList(int pageNo, int pageSize, UserInfoBean userInfoBean) {
        AgentLowerLevelFilter queryInfo=new AgentLowerLevelFilter();
        queryInfo.setLowerStatus(3);
        queryInfo.setAgentNo(userInfoBean.getAgentNo());
        queryInfo.setShareRuleInit("1");
        queryInfo.setRegistType("1");

        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 10 ? 10 : pageSize;
        long count = 0;
        List<AgentLowerLevelInfoVo> list = null;
        //第一页查询总页数，其它页不查询
        if (pageNo == 1) {
            Page page = PageHelper.startPage(pageNo, pageSize);
            list = agentLowerLevelService.getAgentLowerLevelAllList(userInfoBean, queryInfo);
            count = page.getTotal();
        } else {
            PageHelper.startPage(pageNo, pageSize, false);
            list = agentLowerLevelService.getAgentLowerLevelAllList(userInfoBean, queryInfo);
        }
        return ResponseBean.success(list, count);
    }

    @Override
    public List<AgentLowerLevelInfoVo> getAgentLowerLevelAllList(UserInfoBean userInfoBean, AgentLowerLevelFilter queryInfo) {
        List<AgentLowerLevelInfoVo> list=agentLowerLevelDao.getAgentLowerLevelAllList(userInfoBean,queryInfo);
        if(list!=null&&list.size()>0){
            for(AgentLowerLevelInfoVo item:list){
                item.setMobilephone(StringUtils.mask4MobilePhone(item.getMobilephone()));
            }
        }
        return list;
    }

    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int setAgentToBeSetIgnore(String agentNo) {
        return agentInfoDataDao.setAgentToBeSetIgnore(agentNo,"3");
    }

    @Override
    public AgentLowerLevelInfoVo getAgentLowerLevelDetail(String agentNo) {
        return agentLowerLevelDao.getAgentLowerLevelDetail(agentNo);
    }

    @Override
    public ResponseBean getBindingSettlementCardBeforeData(UserInfoBean userInfoBean) {
        AgentCardInfoVo info= agentLowerLevelDao.getBindingSettlementCardBeforeData(userInfoBean.getAgentNo());
        return ResponseBean.success(info);
    }

    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public ResponseBean setBindingSettlementCard(AccountCardUp accountCard, UserInfoBean userInfoBean) {
        AgentCardInfoVo oldInfo=agentLowerLevelDao.getBindingSettlementCardBeforeData(userInfoBean.getAgentNo());
        //校验
        ResponseBean check=checkCard(accountCard,oldInfo,true);
        if(!check.isSuccess()){
            return check;
        }
        accountCard.setAgentNo(userInfoBean.getAgentNo());
        int num=agentLowerLevelDao.setBindingSettlementCard(accountCard);
        if(num>0){
            return ResponseBean.success(null,"绑定结算卡成功");
        }else{
            return ResponseBean.error("绑定结算卡失败");
        }
    }

    @Override
    public ResponseBean checkAgentBase(AgentLowerLevelUp agentInfo, UserInfoBean userInfoBean) {
        //校验
        ResponseBean checkAgent=checkAgent(agentInfo,userInfoBean);
        if(!checkAgent.isSuccess()){
            return checkAgent;
        }
        return ResponseBean.success(null,"ok");
    }

    @Override
    public ResponseBean checkAgentCard(AccountCardUp accountCard, UserInfoBean userInfoBean) {
        ResponseBean checkCard=checkCard(accountCard,null,false);
        if(!checkCard.isSuccess()){
            return checkCard;
        }
        return ResponseBean.success(null,"ok");
    }

    /**
     * 校验代理商信息
     * @param agentInfo
     * @param userInfoBean
     * @return
     */
    private ResponseBean checkAgent(AgentLowerLevelUp agentInfo,UserInfoBean userInfoBean){
        if(agentInfo==null){
            return ResponseBean.error("请上传代理商基础信息");
        }
        if (StringUtils.isNotBlank(agentInfo.getAgentName())) {
            if(StringUtils.isSpecialChar(agentInfo.getAgentName())){
                return ResponseBean.error("代理商名称不能包含以下字符+/?\\\\$&';#=:");
            }
            AgentLowerLevelInfoVo info=agentLowerLevelDao.checkAgentName(agentInfo.getAgentName(),userInfoBean.getTeamId());
            if(info!=null){
                return ResponseBean.error("代理商名称已存在");
            }
        }else{
            return ResponseBean.error("代理商名称不能为空");
        }
        if (StringUtils.isNotBlank(agentInfo.getMobilephone())) {
            if(!StringUtils.isMobile(agentInfo.getMobilephone())){
                return ResponseBean.error("手机号码格式不正确");
            }
            AgentLowerLevelInfoVo info=agentLowerLevelDao.checkAgentPhone(agentInfo.getMobilephone(),userInfoBean.getTeamId());
            if(info!=null){
                return ResponseBean.error("手机号码已存在");
            }
        }else{
            return ResponseBean.error("手机号码不能为空");
        }
        if (!StringUtils.isNotBlank(agentInfo.getProvince())||!StringUtils.isNotBlank(agentInfo.getCity())
            ||!StringUtils.isNotBlank(agentInfo.getArea())||!StringUtils.isNotBlank(agentInfo.getAddress())) {
            return ResponseBean.error("代理商地址不能为空");
        }
        if(StringUtils.isNotBlank(agentInfo.getEmail())){
            if(!StringUtils.isEmail(agentInfo.getEmail())){
                return ResponseBean.error("邮箱地址格式不正确");
            }
            String userID=agentLowerLevelDao.checkUserInfoEmail(agentInfo.getEmail(),userInfoBean.getTeamId());
            if(StringUtils.isNotBlank(userID)){
                return ResponseBean.error("邮箱地址已存在");
            }
        }else{
            agentInfo.setEmail(null);
        }
        return ResponseBean.success(null,"ok");
    }
    /**
     * 校验结算卡
     * @param accountCard
     * @param oldInfo
     * @param sourceSta true 本级绑定结算卡 2 false 开设下级
     * @return
     */
    private ResponseBean checkCard(AccountCardUp accountCard,AgentCardInfoVo oldInfo,boolean sourceSta){
        //参数校验
        if (!StringUtils.isNotBlank(accountCard.getAccountName())) {
            return ResponseBean.error("持卡人姓名不能为空");
        }
        if(oldInfo==null){//新增时候校验
            if (StringUtils.isNotBlank(accountCard.getIdCardNo())) {
                if(accountCard.getIdCardNo().length()!=18){
                    return ResponseBean.error("身份证号码格式不正确");
                }
            }else{
                return ResponseBean.error("身份证号码不能为空");
            }
        }else{//修改本级时校验
            if(oldInfo.getIdCardNo()!=null){//如果代理商已存在身份证号,取商户的
                accountCard.setIdCardNo(oldInfo.getIdCardNo());
            }else{
                if (StringUtils.isNotBlank(accountCard.getIdCardNo())) {
                    if(accountCard.getIdCardNo().length()!=18){
                        return ResponseBean.error("身份证号码格式不正确");
                    }
                }else{
                    return ResponseBean.error("身份证号码不能为空");
                }
            }
        }

        if (!StringUtils.isNotBlank(accountCard.getAccountType())) {
            return ResponseBean.error("账户类型不能为空");
        }
        if (!StringUtils.isNotBlank(accountCard.getAccountNo())) {
            return ResponseBean.error("银行卡号不能为空");
        }
        if (!StringUtils.isNotBlank(accountCard.getBankName())) {
            return ResponseBean.error("开户银行不能为空");
        }
        if(sourceSta&&"2".equals(accountCard.getAccountType())){//本级开设校验4要素，需要预留手机号
            if (StringUtils.isNotBlank(accountCard.getAccountPhone())) {
                if(!StringUtils.isMobile(accountCard.getAccountPhone())){
                    return ResponseBean.error("预留手机号格式不正确");
                }
            }else{
                return ResponseBean.error("预留手机号不能为空");
            }
        }

        if (!StringUtils.isNotBlank(accountCard.getAccountProvince())||!StringUtils.isNotBlank(accountCard.getAccountCity())) {
            return ResponseBean.error("开户地区不能为空");
        }
        if (!StringUtils.isNotBlank(accountCard.getSubBank())) {
            return ResponseBean.error("开户支行不能为空");
        }
        if (!StringUtils.isNotBlank(accountCard.getCnapsNo())) {
            return ResponseBean.error("联行号不能为空");
        }else{
            if(!Pattern.matches("^\\d+$", accountCard.getCnapsNo())){
                return ResponseBean.error("联行号格式不正确");
            }
        }
        if("2".equals(accountCard.getAccountType())){//对私需要校验4要素
            //四要素校验
            Map<String, String> respMsg=null;
            if(sourceSta){
                respMsg=openPlatformService.doAuthen(accountCard.getAccountNo(), accountCard.getAccountName(), accountCard.getIdCardNo(), accountCard.getAccountPhone());
            }else{
                respMsg=openPlatformService.doAuthen(accountCard.getAccountNo(), accountCard.getAccountName(), accountCard.getIdCardNo(), null);
            }

            String errCode = respMsg.get("errCode");
            String errMsg_ = respMsg.get("errMsg");
            boolean flag = "00".equalsIgnoreCase(errCode);
            log.info("身份证验证结果：是否成功:{};开户名:{};银行卡号:{};身份证:{};手机号:{};验证结果:{};错误信息:{};",new Object[]
                    {flag,accountCard.getAccountName(),accountCard.getAccountNo(),accountCard.getIdCardNo(),accountCard.getAccountPhone(),errCode,errMsg_});
            if (!flag) {
                log.info("身份证验证失败");
                if(sourceSta){
                    return ResponseBean.error("四要素验证失败,开户名、身份证、银行卡号、手机号不匹配");
                }else{
                    return ResponseBean.error("三要素验证失败,开户名、身份证、银行卡号不匹配");
                }
            }
        }
        return ResponseBean.success("证验通过");
    }

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

    private String shareRuleIdConvert(AgentShareRuleInfo item){
        StringBuffer sb=new StringBuffer();
        sb.append(item.getBpId()).append("-");
        sb.append(item.getServiceId()).append("-");
        sb.append(item.getCardType()).append("-");
        sb.append(item.getHolidaysMark());
        return sb.toString();
    }

    /**
     * 校验业务产品服务费率设置
     * @param bpList
     * @param userInfoBean
     * @return
     */
    @Override
    public ResponseBean checkbpService(List<AgentBpIdInfoVo> bpList, String agentNo, UserInfoBean userInfoBean) {
        AgentLowerLevelInfoVo info=null;
        if(StringUtils.isNotBlank(agentNo)){
            info=agentLowerLevelDao.getAgentLowerLevelDetail(agentNo);
            if(info==null){
                return ResponseBean.error("该代理商不存在!");
            }else{
                if(!info.getParentId().equals(userInfoBean.getAgentNo())){
                    return ResponseBean.error("当前登入代理商没有权限操作该代理商!");
                }
            }
        }
        Map<String,Object> checkResultMap=new HashMap<String,Object>();
        ResponseBean responseBean=checkBpList(agentNo,bpList,userInfoBean,checkResultMap,false);
        if(!responseBean.isSuccess()){
            if(responseBean.getCode()==3005){
                checkResultMap.put("errorMap",new AgentShareRuleErrorVo(null,responseBean.getMessage(),"3000"));
                return ResponseBean.error(3000,responseBean.getMessage(),checkResultMap);
            }
            checkResultMap.put("errorMap",new AgentShareRuleErrorVo(null,"分润参数设置有误","3000"));
            return ResponseBean.error(3000,"分润参数设置有误",checkResultMap);
        }
        return responseBean;
    }

    /**
     * 校验业务产品服务费率
     * @param agentNo 当前操作代理商编号
     * @param bpList
     * @param userInfoBean
     * @param checkResultMap
     * @param checkResultMap 是否是编辑状态
     * @return
     */
    private ResponseBean checkBpList(String agentNo,List<AgentBpIdInfoVo> bpList, UserInfoBean userInfoBean,Map<String,Object> checkResultMap,boolean isEdit){
        //当前代理商编号
        Map<String,AgentShareRuleErrorVo> errorBpListMap=new HashMap<>();
        String bpName="";

        //获取上级的代理的业务产品
        Map<String, AgentBpIdInfo> mapParent=getMapBpIdAgent(agentLowerLevelDao.getAgentBpId(userInfoBean.getAgentNo()),userInfoBean.getAgentNo(),userInfoBean.getOneLevelId());

        //校验业务产品权限
        if(bpList!=null&&bpList.size()>0){
            for(AgentBpIdInfoVo bpItem:bpList){
                if(mapParent.get(bpItem.getBpId().toString())==null){
                    return ResponseBean.error(3005,"勾选业务产品暂不可代理");
                }
            }
        }else{
            //如果新增方式，需要校验BPList为空
            //如果修改，则不需要校验
            if(isEdit){//修改时,如
                List<AgentBpIdInfo> bpListOld=agentLowerLevelDao.getAgentBpId(agentNo);
                if(bpListOld==null||bpListOld.size()<=0){
                    return ResponseBean.error(3005,"请选择需要代理的业务产品");
                }
                return ResponseBean.success(null,"校验成功");
            }else{
                return ResponseBean.error(3005,"请选择需要代理的业务产品");
            }
        }
        //数据划分
        ClassificationMbListInfo dataInfo= classificationMbList(bpList);
        Map<String,List<AgentBpIdInfoVo>> mapBp=dataInfo.getMapBp();
        List<AgentBpIdInfoVo> bpListAdd=dataInfo.getBpListAdd();
        boolean resultCheck=false;
        //有组数据处理
        if(mapBp.size()>0){
            //每组遍历group
            for(Map.Entry<String,List<AgentBpIdInfoVo>> entry:mapBp.entrySet()){
                boolean groupSta=false;
                String groupNo=entry.getKey();
                //查询这个组内,队长BPId 多少
                String captainBpId=agentLowerLevelDao.getGroupBpId(userInfoBean.getAgentNo(),groupNo);

                //查询当前代理商已勾选的业务产品
                List<String> bpIdListOld=null;
                if(StringUtils.isNotBlank(agentNo)){
                    bpIdListOld=agentLowerLevelDao.getGroupBpIdList(agentNo,groupNo);
                }

                //前端勾选组内数据，都会把全队勾选的BPID传送过来
                List<AgentBpIdInfoVo> list=entry.getValue();
                if(list!=null&&list.size()>0){
                    //防止无序，先遍历取出队长的数据
                    AgentBpIdInfoVo captainInfo=null;//队长实体
                    String bpIds="";
                    String bpNameStr="";
                    boolean bpStatus=false;//是否存在新勾选的业务产品
                    Map<String,String> bpCheckMap=new HashMap<String,String>();
                    for(AgentBpIdInfoVo item:list){
                        String bpId=item.getBpId().toString();
                        //遍历相同group,取出队长的值
                        if(captainBpId.equals(bpId)){
                            captainInfo=item;
                        }else{
                            if(bpIdListOld!=null&&bpIdListOld.size()>0){
                                //存在新勾选队员的业务产品
                                if(!bpStatus&&!bpIdListOld.contains(bpId)){
                                    bpStatus=true;
                                }
                            }
                        }
                        bpCheckMap.put(bpId,"1");
                        bpIds=bpIds+"'"+bpId+"',";
                        bpNameStr=bpNameStr+mapParent.get(bpId).getAgentShowName()+",";
                    }
                    //如果已存在勾选组内数据，防止前端少上传已勾选的组内BpID
                    if(bpIdListOld!=null&&bpIdListOld.size()>0){
                        for(String bpStr:bpIdListOld){
                            if(bpCheckMap.get(bpStr)==null){
                                bpIds=bpIds+"'"+bpStr+"',";
                            }
                        }
                    }
                    bpIds=bpIds.substring(0,bpIds.length()-1);//组内勾选BPID集合

                    //校验是否存在未生效的队长分润
                    if(StringUtils.isNotBlank(agentNo)){
                        Integer shareId=agentLowerLevelDao.getShareTaskCheck(captainBpId,agentNo);//无记录返回null
                        if(bpStatus&&shareId!=null){
                            return ResponseBean.error(3005,mapParent.get(captainBpId).getAgentShowName()+
                                    "存在待生效的结算价设置,暂不可勾选组内其他业务产品");
                        }
                    }

                    //查询已经保存同组队长的分润值
                    List<AgentShareRuleInfo> oldShareList=null;
                    Map<String,AgentShareRuleInfo> oldShareMap=null;
                    if(StringUtils.isNotBlank(agentNo)){
                        oldShareList= agentLowerLevelDao.getShareByBpId(captainBpId,agentNo);
                        oldShareMap=getShareMap(oldShareList);
                    }

                    //获取上级队长分润列表
                    Map<String,AgentShareRuleInfo> shareMapParent=mapParent.get(captainBpId).getAgentShareMap();
                    if(shareMapParent.size()>0){
                        if(captainInfo!=null){//已上传队长分润信息了
                            //遍历上传的分润值
                            List<AgentShareRuleInfoVo> captainBpShareList=captainInfo.getAgentShare();
                            //用来标识已经校验过上传的哪些服务
                            Map<String,String> checkMap=new HashMap<String,String>();
                            if(captainBpShareList!=null&&captainBpShareList.size()>0){

                                for(AgentShareRuleInfoVo addShareItem:captainBpShareList){
                                    //查看是否有融合 5-10001-0-0,5-10001-1-0
                                    String[] idStrs=addShareItem.getId().split(",");

                                    if(idStrs!=null&&idStrs.length>0){
                                        String errorStr="";
                                        for(String id:idStrs){//id为融合服务ID； 5-10001-0-0
                                            checkMap.put(id,"1");
                                            AgentShareRuleInfo sharePercent=shareMapParent.get(id);//上级对应服务分润设置


                                            if(addShareItem.getCost()==null||addShareItem.getCost().compareTo(BigDecimal.ZERO)<0
                                                    ||addShareItem.getShare()==null||addShareItem.getShare().compareTo(BigDecimal.ZERO)<0){
                                                errorStr="代理商成本不能小于0";
                                                break;
                                            }
                                            BigDecimal manageRate =getMinServiceManageRate(bpIds,userInfoBean.getOneLevelId(),sharePercent,"");
                                            log.info("服务组合ID："+id+"费率校验异常,商户签约扣率/商户每笔提现手续费小于0,商户签约扣率/商户每笔提现手续费为:"+manageRate);
                                            if(manageRate.compareTo(new BigDecimal(-1))<0){
                                                errorStr="该服务的本级结算价设置异常";
                                                break;
                                            }
                                            //查询商户签约扣率/商户每笔提现手续费
                                            BigDecimal result=manageRate.subtract(addShareItem.getCost()).multiply(addShareItem.getShare());
                                            BigDecimal resultParent=manageRate.subtract(getCost(sharePercent)).multiply(sharePercent.getShareProfitPercent());
                                            log.info("服务组合ID："+id+"费率校验,商户签约扣率/商户每笔提现手续费为:"+manageRate+",本级值："+addShareItem.getCost()+";"+addShareItem.getShare()+
                                                    ",上级级值："+getCost(sharePercent)+";"+sharePercent.getShareProfitPercent());
                                            if(resultParent.compareTo(BigDecimal.ZERO)<0){
                                                errorStr="本级服务结算价设置异常";
                                                break;
                                            }
                                            if(result.compareTo(BigDecimal.ZERO)<0){
                                                errorStr=sharePercent.getCashOutStatus().intValue()==1?"代理商成本大于商户提现手续费":"代理商成本大于商户费率";
                                                break;
                                            }
                                            if(result.compareTo(resultParent)>0){
                                                errorStr="代理商成本输入有误,最终分润高于本级";
                                                break;
                                            }
                                            if(bpStatus&&oldShareMap!=null){
                                                AgentShareRuleInfo shareOld=oldShareMap.get(id);//上级对应服务分润设置
                                                if(shareOld!=null){
                                                    BigDecimal resultOld=manageRate.subtract(getCost(shareOld)).multiply(shareOld.getShareProfitPercent());
                                                    if(resultOld.compareTo(BigDecimal.ZERO)<0){
                                                        errorStr="本级服务结算价设置异常";
                                                        break;
                                                    }
                                                    if(result.compareTo(resultOld)>0){
                                                        errorStr="代理商成本输入有误,最终分润高于本级";
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if(!"".equals(errorStr)){
                                            groupSta=true;
                                            errorBpListMap.put(addShareItem.getId(),new AgentShareRuleErrorVo(addShareItem.getId(),errorStr,"C10001"));
                                        }
                                    }
                                }

                                //根据已经保存的服务费率,校验服务费率
                                if(oldShareList!=null&&oldShareList.size()>0){
                                    for(AgentShareRuleInfo oldItem:oldShareList){

                                        String id=shareRuleIdConvert(oldItem);
                                        if(checkMap.get(id)!=null){//已经处理过了
                                            continue;
                                        }
                                        AgentShareRuleInfo sharePercent=shareMapParent.get(id);//上级对应服务分润设置
                                        //结算价设置异常，请稍后重试
                                        BigDecimal manageRate =getMinServiceManageRate(bpIds,userInfoBean.getOneLevelId(),sharePercent,"");
                                        log.info("服务组合ID："+id+"费率校验异常,商户签约扣率/商户每笔提现手续费小于0,商户签约扣率/商户每笔提现手续费为:"+manageRate);
                                        if(manageRate.compareTo(new BigDecimal(-1))<0){
                                            groupSta=true;
                                            errorBpListMap.put(id,new AgentShareRuleErrorVo(id,"该服务的本级结算价设置异常","C10002"));
                                            continue;
                                        }
                                        //查询商户签约扣率/商户每笔提现手续费
                                        BigDecimal result=manageRate.subtract(getCost(oldItem)).multiply(oldItem.getShareProfitPercent());
                                        BigDecimal resultParent=manageRate.subtract(getCost(sharePercent)).multiply(sharePercent.getShareProfitPercent());
                                        log.info("服务组合ID："+id+"费率校验,商户签约扣率/商户每笔提现手续费为:"+manageRate+",本级值："+getCost(oldItem)+";"+oldItem.getShareProfitPercent()+
                                                ",上级级值："+getCost(sharePercent)+";"+sharePercent.getShareProfitPercent());
                                        if(resultParent.compareTo(BigDecimal.ZERO)<0){
                                            groupSta=true;
                                            errorBpListMap.put(id,new AgentShareRuleErrorVo(id,"本级服务结算价设置异常","C10003"));
                                            continue;
                                        }
                                        if(result.compareTo(BigDecimal.ZERO)<0){
                                            groupSta=true;
                                            errorBpListMap.put(id,new AgentShareRuleErrorVo(id,
                                                    sharePercent.getCashOutStatus().intValue()==1?"代理商成本大于商户提现手续费":"代理商成本大于商户费率","C10004"));
                                            continue;
                                        }
                                        if(result.compareTo(resultParent)>0){
                                            groupSta=true;
                                            errorBpListMap.put(id,new AgentShareRuleErrorVo(id,"代理商成本输入有误,最终分润高于本级","C10005"));
                                            continue;
                                        }
                                        if(bpStatus&&oldShareMap!=null){
                                            AgentShareRuleInfo shareOld=oldShareMap.get(id);//上级对应服务分润设置
                                            if(shareOld!=null){
                                                BigDecimal resultOld=manageRate.subtract(getCost(shareOld)).multiply(shareOld.getShareProfitPercent());
                                                if(resultOld.compareTo(BigDecimal.ZERO)<0){
                                                    groupSta=true;
                                                    errorBpListMap.put(id,new AgentShareRuleErrorVo(id,"本级服务结算价设置异常","C10006"));
                                                    continue;
                                                }
                                                if(result.compareTo(resultOld)>0){
                                                    groupSta=true;
                                                    errorBpListMap.put(id,new AgentShareRuleErrorVo(id,"代理商成本输入有误,最终分润高于本级","C10007"));
                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                }
                            }else{
                                groupSta=true;
                                log.info("业务产品:"+mapParent.get(captainBpId).getAgentShowName()+"未设置队长分润");
                            }
                        }else{
                            groupSta=true;
                            log.info("业务产品:"+mapParent.get(captainBpId).getAgentShowName()+"未设置队长分润");
                        }
                    }else{
                        groupSta=true;
                        log.info("上级代理商代理的业务产品:"+mapParent.get(captainBpId).getAgentShowName()+"无服务分润设置");
                    }
                    if(groupSta){
                        resultCheck=true;
                        bpName=bpName+bpNameStr;
                    }
                }
            }
        }
        //无分组数据处理
        if(bpListAdd.size()>0){
            for(AgentBpIdInfoVo item:bpListAdd) {
                boolean groupNoSta=false;
                Map<String, AgentShareRuleInfo> shareMapParent = mapParent.get(item.getBpId().toString()).getAgentShareMap();//根据业务BPid拿回上级分润
                //上传的服务费率
                List<AgentShareRuleInfoVo> agentShare = item.getAgentShare();
                if (agentShare != null && agentShare.size() > 0) {
                    for (AgentShareRuleInfoVo addShareItem : agentShare) {
                        //查看是否有融合 5-10001-0-0,5-10001-1-0
                        String[] idStrs = addShareItem.getId().split(",");
                        if (idStrs != null && idStrs.length > 0) {
                            String errorStr = "";
                            for (String id : idStrs) {//id为融合服务ID； 5-10001-0-0
                                AgentShareRuleInfo sharePercent = shareMapParent.get(id);//上级对应服务分润设置

                                if(addShareItem.getCost()==null||addShareItem.getCost().compareTo(BigDecimal.ZERO)<0
                                        ||addShareItem.getShare()==null||addShareItem.getShare().compareTo(BigDecimal.ZERO)<0){
                                    errorStr="代理商成本不能小于0";
                                    break;
                                }
                                BigDecimal manageRate = getMinServiceManageRate("'" + item.getBpId() + "'", userInfoBean.getOneLevelId(),
                                        sharePercent, sharePercent.getServiceId());
                                log.info("服务组合ID："+id+"费率校验异常,商户签约扣率/商户每笔提现手续费小于0,商户签约扣率/商户每笔提现手续费为:"+manageRate);
                                if(manageRate.compareTo(new BigDecimal(-1))<0){
                                    errorStr="该服务的上级结算价设置异常";
                                    break;
                                }
                                //查询商户签约扣率/商户每笔提现手续费
                                BigDecimal result = manageRate.subtract(addShareItem.getCost()).multiply(addShareItem.getShare());
                                BigDecimal resultParent = manageRate.subtract(getCost(sharePercent)).multiply(sharePercent.getShareProfitPercent());
                                log.info("服务组合ID：" + id + "费率校验,商户签约扣率/商户每笔提现手续费为:" + manageRate + ",本级值：" + addShareItem.getCost() + ";" + addShareItem.getShare() +
                                        ",上级级值：" + getCost(sharePercent) + ";" + sharePercent.getShareProfitPercent());
                                if(resultParent.compareTo(BigDecimal.ZERO)<0){
                                    errorStr="本级服务结算价设置异常";
                                    break;
                                }
                                if(result.compareTo(BigDecimal.ZERO)<0){
                                    errorStr=sharePercent.getCashOutStatus().intValue()==1?"代理商成本大于商户提现手续费":"代理商成本大于商户费率";
                                    break;
                                }
                                if(result.compareTo(resultParent)>0){
                                    errorStr="代理商成本输入有误,最终分润高于本级";
                                    break;
                                }
                            }
                            if (!"".equals(errorStr)) {
                                groupNoSta=true;
                                errorBpListMap.put(addShareItem.getId(),new AgentShareRuleErrorVo(addShareItem.getId(),errorStr,"C10008"));
                            }
                        }
                    }

                } else {
                    groupNoSta=true;
                    log.info("上级代理商代理的业务产品:" + mapParent.get(item.getBpId().toString()).getAgentShowName() + "无服务分润设置");
                }
                if(groupNoSta){//如果每一个业务产品出错，设置相关
                    resultCheck=true;
                    bpName=bpName+mapParent.get(item.getBpId().toString()).getAgentShowName()+",";
                }
            }
        }
        if(resultCheck){
            checkResultMap.put("bpErrorMap",errorBpListMap);
            checkResultMap.put("bpName",bpName.substring(0,bpName.length()-1));
            return ResponseBean.error(1004,"no");
        }
        return ResponseBean.success(null,"保存成功");
    }

    /**
     * 将服务分润数据封装成MAP
     * @param list
     * @return
     */
    private Map<String,AgentShareRuleInfo> getShareMap(List<AgentShareRuleInfo> list){
        Map<String,AgentShareRuleInfo> shareMap=new HashMap<String,AgentShareRuleInfo>();

        if(list!=null&&list.size()>0){
            for(AgentShareRuleInfo shareItem:list){
                String id=shareRuleIdConvert(shareItem);//ID转换合并
                shareItem.setId(id);
                shareMap.put(id,shareItem);
            }
        }
        return shareMap;
    }
    /**
     * 根据bpID查询出组内最小的 商户费率/商户固定成本
     * @param bpIds
     * @param oneLevelId
     * @return
     */
    private BigDecimal getMinServiceManageRate(String bpIds,String oneLevelId,AgentShareRuleInfo sharePercent,String serviceId){
        BigDecimal manageRate=new BigDecimal(-1);
        List<ServiceManageRateVo> list=agentLowerLevelDao.getServiceManageRateList(bpIds, oneLevelId,sharePercent,serviceId);
        if(list!=null&&list.size()>0){
            for(ServiceManageRateVo item:list){
                BigDecimal itemRate=null;
                if("1".equals(item.getRateType())){
                    itemRate=item.getSingleNumAmount();
                }else{
                    itemRate=item.getRate();
                }
                if(manageRate.compareTo(new BigDecimal(-1))==0){
                    manageRate=itemRate;
                }else{
                    if(manageRate.compareTo(itemRate)>0){//如果缓存值大于遍历值，取遍历值
                        manageRate=itemRate;
                    }
                }
            }
        }
        return manageRate;
    }

    /**
     * 封装上级的业务组、服务费率，方便后面计算取值
     * @param bpList
     * @param agentNo
     * @param oneAgentNo
     * @return
     */
    private Map<String, AgentBpIdInfo> getMapBpIdAgent(List<AgentBpIdInfo> bpList, String agentNo, String oneAgentNo){
        Map<String, AgentBpIdInfo> map=new HashMap<String, AgentBpIdInfo>();
        if(bpList!=null&&bpList.size()>0){
            for(AgentBpIdInfo item:bpList){
                if("1".equals(item.getAllowIndividualApply())){
                    List<AgentShareRuleInfo> shareRuleList=agentLowerLevelDao.getAgentShare(item.getBpId(),agentNo,oneAgentNo);
                    Map<String,AgentShareRuleInfo> shareMap=new HashMap<String,AgentShareRuleInfo>();
                    if(shareRuleList!=null&&shareRuleList.size()>0){
                        for(AgentShareRuleInfo shareItem:shareRuleList){
                            String id=shareRuleIdConvert(shareItem);//ID转换合并
                            shareItem.setId(id);
                            shareMap.put(id,shareItem);
                        }
                    }
                    item.setAgentShareMap(shareMap);

                }
                map.put(item.getBpId().toString(),item);
            }
        }
        return map;
    }


    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public ResponseBean addAgentLowerLevel(AgentLowerLevelVo params, UserInfoBean userInfoBean,Map<String, String> agentNoMap) {
       //前端传送的试JSON字符串
        AgentLowerLevelUp agentInfo= params.getAgentInfo();
        AccountCardUp accountCard=params.getAccountCard();
        List<AgentBpIdInfoVo> bpList=params.getBpList();
        List<AgentActivityVo> happyBack=params.getHappyBack();
        List<AgentActivityVo> newHappyGive=params.getNewHappyGive();

        Map<String,Object> checkResultMap=new HashMap<String,Object>();
        //校验-代理商基本信息
        ResponseBean checkAgent=checkAgent(agentInfo,userInfoBean);
        if(!checkAgent.isSuccess()){
            checkResultMap.put("errorMap",new AgentShareRuleErrorVo(null,checkAgent.getMessage(),"1000"));
            return ResponseBean.error(1000,checkAgent.getMessage(),checkResultMap);
        }
        //校验-结算卡信息
        ResponseBean checkCard=checkCard(accountCard,null,false);
        if(!checkCard.isSuccess()){
            checkResultMap.put("errorMap",new AgentShareRuleErrorVo(null,checkCard.getMessage(),"2000"));
            return ResponseBean.error(2000,checkCard.getMessage(),checkResultMap);
        }

        //校验-代理业务产品服务费率设置
        ResponseBean resultRate=checkBpList(null,bpList,userInfoBean,checkResultMap,false);

        //校验-活动
        ResponseBean happyBackResult=checkAgentActivity(happyBack,userInfoBean,"1",checkResultMap);
        ResponseBean newHappyGiveResult=checkAgentActivity(newHappyGive,userInfoBean,"2",checkResultMap);

        if(!resultRate.isSuccess()||!happyBackResult.isSuccess()||!newHappyGiveResult.isSuccess()){
            String errorMsg="提交失败,存在如下校验不通过,请修改后重新提交.\n";
            int sta=0;
            Map<String,Object> resultMap=new HashMap<String,Object>();
            if(!resultRate.isSuccess()){
                if(resultRate.getCode()==3005){
                    resultMap.put("errorMap",new AgentShareRuleErrorVo(null,resultRate.getMessage(),"3000"));
                    return ResponseBean.error(3000,resultRate.getMessage(),resultMap);
                }
                errorMsg=errorMsg+"结算价中\""+checkResultMap.get("bpName").toString()+"\""+"校验不通过\n";
                sta=sta+1;
            }
            if(!happyBackResult.isSuccess()||!newHappyGiveResult.isSuccess()){
                String activityName="";
                if(!happyBackResult.isSuccess()){
                    activityName=activityName+(checkResultMap.get("activityName")==null?"":checkResultMap.get("activityName").toString())+",";
                    sta=sta+1;
                }
                if(!newHappyGiveResult.isSuccess()){
                    activityName=activityName+(checkResultMap.get("activityNameNew")==null?"":checkResultMap.get("activityNameNew").toString())+",";
                    sta=sta+1;
                }
                errorMsg=errorMsg+"活动设置中\""+activityName.substring(0,activityName.length()-1)+"\""+"校验不通过\n";
            }
            errorMsg=errorMsg+"请检查是否输入错误后重新提交";
            resultMap.put("bpErrorMap",checkResultMap.get("bpErrorMap"));
            resultMap.put("activityErrorMap",checkResultMap.get("activityErrorMap"));
            resultMap.put("errorMap",new AgentShareRuleErrorVo(null,errorMsg,sta==1?"3000":"4000"));
            return ResponseBean.error(sta==1?3000:4000,errorMsg,resultMap);
        }

        //创建出代理编号，独立事务
        String agentNo = seqService.createKey("agent_no");
        if(!StringUtils.isNotBlank(agentNo)){
            return ResponseBean.error(5000,"获取代理商编号失败",null);
        }
        agentNoMap.put("agentNo",agentNo);

        //先创建保存代理基本信息
        addAgentBaseInfo(userInfoBean,agentInfo,agentNo,accountCard);
        //保存业务产品
        addAgentShare(userInfoBean,agentNo,bpList);

        //保存活动数据
        addAgentActivity(happyBack,userInfoBean,agentNo);
        addAgentActivity(newHappyGive,userInfoBean,agentNo);

        // 创建代理商管理员
        addAgentUserInfo(agentInfo,userInfoBean,agentNo);

        //修改结算价操作状态
        agentInfoDataDao.setAgentToBeSetIgnore(agentNo,"2");
        return ResponseBean.success("代理商新增成功");
    }

    /**
     * 校验活动数据
     */
    private ResponseBean checkAgentActivity(List<AgentActivityVo> activityList,UserInfoBean userInfoBean,String subType,Map<String,Object> checkResultMap){
        Map<String,Object> errorMap=new HashMap<String,Object>();
        Map<String,AgentActivityVo> map=getMapActivity(agentLowerLevelDao.getAgentActivity(userInfoBean.getAgentNo(),subType),userInfoBean.getAgentNo());
        StringBuffer activityName=new StringBuffer();
        log.info("校验活动数据");
        boolean checkSta=false;
        if(activityList!=null&&activityList.size()>0){
            log.info("校验活动数据--"+activityList.size());
            for(AgentActivityVo activityVo:activityList){
                //两种活动返现是共享的key值--校验返现数据
                //上级配置值
                AgentActivityVo agentActivityPercent=map.get(getAgentActivityKey(activityVo));
                boolean rowCheckSta=false;
                Map<String,AgentShareRuleErrorVo> amountErrorMap=new HashMap<String,AgentShareRuleErrorVo>();

                if(activityVo.getCashBackAmount()==null ||BigDecimal.ZERO.compareTo(activityVo.getCashBackAmount())>0
                        ||activityVo.getTaxRate()==null ||BigDecimal.ZERO.compareTo(activityVo.getTaxRate())>0
                ) {
                    rowCheckSta=true;
                    amountErrorMap.put("cashBack",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"返现金额设置异常","D0000"));
                }else{
                    BigDecimal amountPercent= agentActivityPercent.getCashBackAmount().multiply(agentActivityPercent.getTaxRate()).divide(new BigDecimal(100));
                    BigDecimal amount= activityVo.getCashBackAmount().multiply(activityVo.getTaxRate()).divide(new BigDecimal(100));
                    if(BigDecimal.ZERO.compareTo(amountPercent)>0){
                        rowCheckSta=true;
                        amountErrorMap.put("cashBack",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"本级返现设置异常","D0001"));
                    }else{
                        if(amountPercent.compareTo(amount)<0){
                            rowCheckSta=true;
                            amountErrorMap.put("cashBack",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"下级获得的返现金额需 ≤ "+amountPercent+"元","D0002"));
                        }
                    }
                }


                if("1".equals(agentActivityPercent.getSubType())){//欢乐返
                    //校验满奖
                    if(agentActivityPercent.getFullPrizeSwitch().intValue()==1){
                        //校验满扣金额
                        if(activityVo.getFullPrizeAmount()==null ||BigDecimal.ZERO.compareTo(activityVo.getFullPrizeAmount())>0
                                ||activityVo.getRewardRate()==null ||BigDecimal.ZERO.compareTo(activityVo.getRewardRate())>0
                        ) {
                            rowCheckSta=true;
                            amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"满奖金额设置异常","D0003"));
                        }else{
                            BigDecimal amountPercentFull= agentActivityPercent.getFullPrizeAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal amountFull= activityVo.getFullPrizeAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            if(BigDecimal.ZERO.compareTo(amountPercentFull)>0){
                                rowCheckSta=true;
                                amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"本级满奖金额设置异常","D0004"));
                            }else{
                                if(amountPercentFull.compareTo(amountFull)<0){
                                    rowCheckSta=true;
                                    amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"下级获得的满奖金额需 ≤ "+amountPercentFull+"元","D0005"));
                                }
                            }
                        }
                    }else{
                        //如果开关关闭，不管前端传入什么值，重置为0
                        activityVo.setFourRewardAmount(BigDecimal.ZERO);
                    }

                    //校验不满扣
                    if(agentActivityPercent.getNotFullDeductSwitch().intValue()==1){
                        if(activityVo.getNotFullDeductAmount()==null ||BigDecimal.ZERO.compareTo(activityVo.getNotFullDeductAmount())>0) {
                            rowCheckSta=true;
                            amountErrorMap.put("notFull",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"不满扣金额设置异常","D0006"));
                        }else{
                            if(BigDecimal.ZERO.compareTo(agentActivityPercent.getNotFullDeductAmount())>0){
                                rowCheckSta=true;
                                amountErrorMap.put("notFull",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"本级不满扣金额设置异常","D0007"));
                            }else{
                                if(agentActivityPercent.getNotFullDeductAmount().compareTo(activityVo.getNotFullDeductAmount())<0){
                                    rowCheckSta=true;
                                    amountErrorMap.put("notFull",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"下级不满扣金额需 ≤ "+agentActivityPercent.getNotFullDeductAmount()+"元","D0008"));
                                }
                            }
                        }
                    }else{
                        //如果开关关闭，不管前端传入什么值，重置为0
                        activityVo.setNotFullDeductAmount(BigDecimal.ZERO);
                    }
                }else if("2".equals(agentActivityPercent.getSubType())){
                    int rewardLevel=agentActivityPercent.getRewardLevel().intValue();
                    String errorStr="";

                    if(rewardLevel==0){
                        //根据层级限制重置考核周期数据
                        activityVo.setOneRewardAmount(BigDecimal.ZERO);
                        activityVo.setTwoRewardAmount(BigDecimal.ZERO);
                        activityVo.setThreeRewardAmount(BigDecimal.ZERO);
                        activityVo.setFourRewardAmount(BigDecimal.ZERO);
                    }else if(rewardLevel==1){
                        activityVo.setTwoRewardAmount(BigDecimal.ZERO);
                        activityVo.setThreeRewardAmount(BigDecimal.ZERO);
                        activityVo.setFourRewardAmount(BigDecimal.ZERO);

                        //先校验考核奖励比例
                        if(activityVo.getRewardRate()==null||BigDecimal.ZERO.compareTo(activityVo.getRewardRate())>0
                            ||activityVo.getOneRewardAmount()==null|| BigDecimal.ZERO.compareTo(activityVo.getOneRewardAmount())>0
                        ){
                            rowCheckSta=true;
                            amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"考核金额设置异常","D0009"));
                        }else{
                            BigDecimal oneRewardAmount=activityVo.getOneRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal oneRewardAmountPercent=agentActivityPercent.getOneRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));

                            if(oneRewardAmountPercent.compareTo(oneRewardAmount)<0){
                                errorStr="第1次考核获得的奖励金额需 ≤ "+oneRewardAmountPercent+"元\n";
                            }
                            if(!"".equals(errorStr)){
                                rowCheckSta=true;
                                amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),errorStr.substring(0,errorStr.lastIndexOf("\n")),"D0010"));
                            }

                        }

                    }else if(rewardLevel==2){
                        activityVo.setThreeRewardAmount(BigDecimal.ZERO);
                        activityVo.setFourRewardAmount(BigDecimal.ZERO);
                        //先校验考核奖励比例
                        if(activityVo.getRewardRate()==null||BigDecimal.ZERO.compareTo(activityVo.getRewardRate())>0
                            ||activityVo.getOneRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getOneRewardAmount())>0
                            ||activityVo.getTwoRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getTwoRewardAmount())>0
                        ){
                            rowCheckSta=true;
                            amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"考核金额设置异常","D0011"));
                        }else{
                            BigDecimal oneRewardAmount=activityVo.getOneRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal twoRewardAmount=activityVo.getTwoRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));

                            BigDecimal oneRewardAmountPercent=agentActivityPercent.getOneRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal twoRewardAmountPercent=agentActivityPercent.getTwoRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));

                            if(oneRewardAmountPercent.compareTo(oneRewardAmount)<0){
                                errorStr="第1次考核获得的奖励金额需 ≤ "+oneRewardAmountPercent+"元\n";
                            }
                            if(twoRewardAmountPercent.compareTo(twoRewardAmount)<0){
                                errorStr="第2次考核获得的奖励金额需 ≤ "+twoRewardAmountPercent+"元\n";
                            }
                            if(!"".equals(errorStr)){
                                rowCheckSta=true;
                                amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),errorStr.substring(0,errorStr.lastIndexOf("\n")),"D0012"));
                            }
                        }

                    }else if(rewardLevel==3){

                        activityVo.setFourRewardAmount(BigDecimal.ZERO);
                        //先校验考核奖励比例
                        if(activityVo.getRewardRate()==null||BigDecimal.ZERO.compareTo(activityVo.getRewardRate())>0
                                ||activityVo.getOneRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getOneRewardAmount())>0
                                ||activityVo.getTwoRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getTwoRewardAmount())>0
                                ||activityVo.getThreeRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getThreeRewardAmount())>0
                        ){
                            rowCheckSta=true;
                            amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"考核金额设置异常","D0013"));
                        }else{
                            BigDecimal oneRewardAmount=activityVo.getOneRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal twoRewardAmount=activityVo.getTwoRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal threeRewardAmount=activityVo.getThreeRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));

                            BigDecimal oneRewardAmountPercent=agentActivityPercent.getOneRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal twoRewardAmountPercent=agentActivityPercent.getTwoRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal threeRewardAmountPercent=agentActivityPercent.getThreeRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));

                            if(oneRewardAmountPercent.compareTo(oneRewardAmount)<0){
                                errorStr="第1次考核获得的奖励金额需 ≤ "+oneRewardAmountPercent+"元\n";
                            }
                            if(twoRewardAmountPercent.compareTo(twoRewardAmount)<0){
                                errorStr="第2次考核获得的奖励金额需 ≤ "+twoRewardAmountPercent+"元\n";
                            }
                            if(threeRewardAmountPercent.compareTo(threeRewardAmount)<0){
                                errorStr="第3次考核获得的奖励金额需 ≤ "+agentActivityPercent.getThreeRewardAmount()+"元\n";
                            }
                            if(!"".equals(errorStr)){
                                rowCheckSta=true;
                                amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),errorStr.substring(0,errorStr.lastIndexOf("\n")),"D0014"));
                            }
                        }

                    }else if(rewardLevel==4){

                        //先校验考核奖励比例
                        if(activityVo.getRewardRate()==null||BigDecimal.ZERO.compareTo(activityVo.getRewardRate())>0
                                ||activityVo.getOneRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getOneRewardAmount())>0
                                ||activityVo.getTwoRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getTwoRewardAmount())>0
                                ||activityVo.getThreeRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getThreeRewardAmount())>0
                                ||activityVo.getFourRewardAmount()==null||BigDecimal.ZERO.compareTo(activityVo.getFourRewardAmount())>0

                        ){
                            rowCheckSta=true;
                            amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"考核金额设置异常","D0015"));
                        }else{
                            BigDecimal oneRewardAmount=activityVo.getOneRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal twoRewardAmount=activityVo.getTwoRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal threeRewardAmount=activityVo.getThreeRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal fourRewardAmount=activityVo.getFourRewardAmount().multiply(activityVo.getRewardRate()).divide(new BigDecimal(100));

                            BigDecimal oneRewardAmountPercent=agentActivityPercent.getOneRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal twoRewardAmountPercent=agentActivityPercent.getTwoRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal threeRewardAmountPercent=agentActivityPercent.getThreeRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));
                            BigDecimal fourRewardAmountPercent=agentActivityPercent.getFourRewardAmount().multiply(agentActivityPercent.getRewardRate()).divide(new BigDecimal(100));

                            if(oneRewardAmountPercent.compareTo(oneRewardAmount)<0){
                                errorStr="第1次考核获得的奖励金额需 ≤ "+oneRewardAmountPercent+"元\n";
                            }
                            if(twoRewardAmountPercent.compareTo(twoRewardAmount)<0){
                                errorStr="第2次考核获得的奖励金额需 ≤ "+twoRewardAmountPercent+"元\n";
                            }
                            if(threeRewardAmountPercent.compareTo(threeRewardAmount)<0){
                                errorStr="第3次考核获得的奖励金额需 ≤ "+agentActivityPercent.getThreeRewardAmount()+"元\n";
                            }
                            if(fourRewardAmountPercent.compareTo(fourRewardAmount)<0){
                                errorStr="第4次考核获得的奖励金额需 ≤ "+agentActivityPercent.getFourRewardAmount()+"元\n";
                            }
                            if(!"".equals(errorStr)){
                                rowCheckSta=true;
                                amountErrorMap.put("fullPrize",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),errorStr.substring(0,errorStr.lastIndexOf("\n")),"D0016"));
                            }
                        }
                    }
                    //校验新欢乐送-不达标扣款设置
                    if(agentActivityPercent.getDeductionStatus().intValue()==1){

                        if(activityVo.getDeductionAmount()==null || BigDecimal.ZERO.compareTo(activityVo.getDeductionAmount())>0){
                            rowCheckSta=true;
                            amountErrorMap.put("notFull",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"不达标扣款设置异常","D0018"));
                        }else{
                            if( BigDecimal.ZERO.compareTo(agentActivityPercent.getDeductionAmount())>0){
                                rowCheckSta=true;
                                amountErrorMap.put("notFull",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"本级的不达标扣款设置异常","D0017"));
                            }else{
                                if(agentActivityPercent.getDeductionAmount().compareTo(activityVo.getDeductionAmount())<0){
                                    rowCheckSta=true;
                                    amountErrorMap.put("notFull",new AgentShareRuleErrorVo(activityVo.getActivityTypeNo(),"下级不满扣金额需 ≤ "+agentActivityPercent.getDeductionAmount()+"元","D0019"));
                                }
                            }
                        }
                    }else{
                        //如果开关关闭，不管前端传入什么值，重置为0
                        activityVo.setDeductionAmount(BigDecimal.ZERO);
                    }
                }
                //如果校验一个子类型有错误，标记
                if(rowCheckSta){
                    checkSta=true;
                    activityName.append(agentActivityPercent.getActivityTypeName()+",");
                    errorMap.put(agentActivityPercent.getActivityTypeNo(),amountErrorMap);
                }
            }
        }
        if(checkSta){
            checkResultMap.put("activityErrorMap",errorMap);
            if("1".equals(subType)){
                checkResultMap.put("activityName",activityName.toString().substring(0,activityName.toString().length()-1));
            }else{
                checkResultMap.put("activityNameNew",activityName.toString().substring(0,activityName.toString().length()-1));
            }
            return ResponseBean.error("保存失败");
        }
        log.info("校验活动数据完成.....");
        return ResponseBean.success(null,"保存成功");
    }

    /**
     * 新增保存活动
     * @param activityList
     * @param userInfoBean
     * @param agentNo
     */
    private void addAgentActivity(List<AgentActivityVo> activityList,UserInfoBean userInfoBean,String agentNo) {
        if(activityList!=null&&activityList.size()>0){
            //反正下发数据 一个子类型在2个组织中,2条数据
            for(AgentActivityVo addItem:activityList){
                //返回的数据是少量的，单独查询回来数据
                AgentActivity parentInfo =agentLowerLevelDao.getAgentActivityInfo(userInfoBean.getAgentNo(),addItem.getActivityTypeNo());
                //先更新本级数据
                //前端传送过来是不带%号的数据,存入时/100
                if(addItem.getTaxRate()!=null){
                    addItem.setTaxRate(addItem.getTaxRate().divide(new BigDecimal(100)));
                }
                if(addItem.getRewardRate()!=null){
                    addItem.setRewardRate(addItem.getRewardRate().divide(new BigDecimal(100)));
                }
                agentLowerLevelDao.addAgentActivity(addItem,parentInfo,agentNo);
            }
        }
    }

    /**
     * 获取上级活动数据
     * @param list
     * @param agentNo 操作数据的上级代理商
     * @return 返回的map是 组织+子类型编号
     */
    private Map<String,AgentActivityVo> getMapActivity(List<AgentActivityVo> list,String agentNo){
        AgentInfoData agentInfo=agentInfoDataDao.getAgentInfoData(agentNo);
        //获取数据字典满奖，满扣层级上限控制
        Map<String,String> oemSwitch=agentLowerLevelDataService.getAgentOemPrizeBuckleRank();
        Map<String,AgentActivityVo> map=new HashMap<String,AgentActivityVo>();
        if(list!=null&&list.size()>0){
            for(AgentActivityVo item:list){
                //将活动的返现比例改成100%不带单位
                if(item.getTaxRate()!=null){
                    item.setTaxRate(item.getTaxRate().multiply(new BigDecimal(100)));
                }
                if(item.getRewardRate()!=null){
                    item.setRewardRate(item.getRewardRate().multiply(new BigDecimal(100)));
                }
                //设置开关
                if("1".equals(item.getSubType())){
                    agentLowerLevelDataService.saveSwitch(oemSwitch,item,agentInfo);
                }else if("2".equals(item.getSubType())){
                    agentLowerLevelDataService.saveRewardLevel(item);
                }
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
        String key=item.getTeamId()+","+item.getActivityTypeNo();
        return key;
    }


    /**
     * 新增代理商基本信息
     * @param userInfoBean
     * @param agentInfo
     * @param agentNo
     * @param accountCard
     * @return
     */
    private int addAgentBaseInfo(UserInfoBean userInfoBean,AgentLowerLevelUp agentInfo,String agentNo,AccountCardUp accountCard){
        //查询上级信息
        AgentInfoData agentParent=agentInfoDataDao.getAgentInfoData(userInfoBean.getAgentNo());
        int num=agentInfoDataDao.addAgentLowerLevel(userInfoBean,agentParent,agentInfo,agentNo,accountCard);
        return num;
    }

    /**
     * 新增保存服务分润信息
     * @param userInfoBean
     * @param agentNo
     * @param bpList
     */
    private void addAgentShare(UserInfoBean userInfoBean,String agentNo,List<AgentBpIdInfoVo> bpList){
        //获取上级的代理的业务产品
        Map<String, AgentBpIdInfo> mapParent=getMapBpIdAgent(agentLowerLevelDao.getAgentBpId(userInfoBean.getAgentNo()),userInfoBean.getAgentNo(),userInfoBean.getOneLevelId());

        //数据划分
        ClassificationMbListInfo dataInfo= classificationMbList(bpList);
        Map<String,List<AgentBpIdInfoVo>> mapBp=dataInfo.getMapBp();
        List<AgentBpIdInfoVo> bpListAdd=dataInfo.getBpListAdd();

        //有组数据处理
        if(mapBp.size()>0){
            //每组遍历group
            for(Map.Entry<String,List<AgentBpIdInfoVo>> entry:mapBp.entrySet()){
                String groupNo=entry.getKey();
                //查询这个组内,队长BPId 多少
                String captainBpId=agentLowerLevelDao.getGroupBpId(userInfoBean.getAgentNo(),groupNo);
                List<AgentBpIdInfoVo> list=entry.getValue();
                if(list!=null&&list.size()>0){
                    //防止无序，先遍历取出队长的数据
                    AgentBpIdInfoVo captainInfo=null;//队长实体
                    List<AgentBpIdInfoVo> memberList=new ArrayList<AgentBpIdInfoVo>();//队员list
                    for(AgentBpIdInfoVo item:list){
                        //遍历相同group,取出队长的值
                        if(captainBpId.equals(item.getBpId().toString())){
                            captainInfo=item;
                        }else{
                            memberList.add(item);
                        }
                        //设置代理业务组
                        agentLowerLevelDao.addAgentBusinessProduct(item,agentNo);
                    }
                    //获取上级队长分润列表
                    Map<String,AgentShareRuleInfo> shareMapParent=mapParent.get(captainBpId).getAgentShareMap();

                    if(shareMapParent.size()>0){
                        if(captainInfo!=null){//已上传队长分润信息了
                            //遍历上级分润列表，根据上传数据更新，上级值>=上传值
                            //队长数据转换成Map
                            Map<String,AgentShareRuleInfo> addMap=splitShareRuleVo(captainInfo,shareMapParent);

                            for(Map.Entry<String,AgentShareRuleInfo> shareEntry:shareMapParent.entrySet()){
                                AgentShareRuleInfo shareParent=shareEntry.getValue();
                                if(addMap.get(shareParent.getId())!=null){
                                    AgentShareRuleInfo addShare=addMap.get(shareParent.getId());
                                    //新增分润
                                    agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,addShare,agentNo,"");

                                    //复制生成队员数据
                                    for(AgentBpIdInfoVo item: memberList){
                                        //复制不同bpid,serviceType,卡类型,节假日类型一样的数据
                                        String serviceId_Bp=agentLowerLevelDao.findGroupServiceId(item.getBpId(),shareParent.getServiceType2(),
                                                shareParent.getCardType(),shareParent.getHolidaysMark(),userInfoBean.getOneLevelId());
                                        agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,addShare,agentNo,serviceId_Bp);

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //无分组数据处理
        if(bpListAdd.size()>0){
            for(AgentBpIdInfoVo item:bpListAdd){
                Map<String,AgentShareRuleInfo> shareMapParent=mapParent.get(item.getBpId().toString()).getAgentShareMap();//根据业务BPid拿回上级分润
                //解析转换数据
                Map<String,AgentShareRuleInfo> addMap=splitShareRuleVo(item,shareMapParent);

                //设置代理业务组
                agentLowerLevelDao.addAgentBusinessProduct(item,agentNo);

                if(addMap.size()>0){
                    for(Map.Entry<String,AgentShareRuleInfo> shareEntry:addMap.entrySet()){
                        AgentShareRuleInfo addShare=shareEntry.getValue();
                        AgentShareRuleInfo shareParent=shareMapParent.get(addShare.getId());
                        //新增分润
                        agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,addShare,agentNo,"");
                    }
                }
            }
        }
    }

    /**
     * 新增时保存代理用户信息
     */
    private void addAgentUserInfo(AgentLowerLevelUp agentInfo,UserInfoBean userInfoBean,String agentNo) {
        AgentUserInfo agentUser = agentInfoDataDao.selectAgentUser(agentInfo.getMobilephone(), userInfoBean.getTeamId());
        if (agentUser == null) {
            agentUser = new AgentUserInfo();
            agentUser.setUserName(agentInfo.getAgentName());
            String userId = seqService.createKey("user_no_seq", new BigInteger("1000000000000000000"));
            agentUser.setUserId(userId);
            agentUser.setTeamId(userInfoBean.getTeamId());
            String mobilephoneParam = agentInfo.getMobilephone();

            agentUser.setMobilephone(mobilephoneParam);
            agentUser.setPassword(new Md5PasswordEncoder().encodePassword("123456", mobilephoneParam));

            agentUser.setEmail(agentInfo.getEmail());
            agentInfoDataDao.insertAgentUser(agentUser);
        }
        AgentUserEntity entity = agentInfoDataDao.selectAgentUserEntity(agentUser.getUserId(),agentNo);
        if (entity == null) {
            entity = new AgentUserEntity();
            entity.setEntityId(agentNo);
            entity.setUserId(agentUser.getUserId());
            entity.setIsAgent("1");
            agentInfoDataDao.insertAgentEntity(entity);
        } else {
            log.info("代理商手机号："+agentInfo.getMobilephone()+",组织:"+userInfoBean.getTeamId()+"已注册过");
        }
    }

    /**
     * 开通账户系统账户
     * @param agentNo
     */
    @Override
    public void openAgentAccount(final String agentNo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String acc = ClientInterface.createAgentAccount(agentNo, "224105");
                    log.info("开通代理商账户(224105) --> " + acc);
                    agentLowerLevelService.updateAgentAccount(agentNo,1);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("开通代理商账户异常", e);
                }
            }
        }).start();
    }

    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int updateAgentAccount(String agentNo, int status) {
        int num=agentInfoDataDao.updateAgentAccount(agentNo, status);
        return 0;
    }

    /**
     * 修改服务费率和活动数据
     * @param agentNo 当前操作代理商
     * @param params
     * @param userInfoBean
     * @return
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public ResponseBean editAgentLowerLevel(String agentNo,AgentLowerLevelVo params, UserInfoBean userInfoBean) {
        //前端传送的试JSON字符串
        List<AgentBpIdInfoVo> bpList=params.getBpList();
        List<AgentActivityVo> happyBack=params.getHappyBack();
        List<AgentActivityVo> newHappyGive=params.getNewHappyGive();
        if(bpList!=null){
            log.info("业务组"+Arrays.toString(bpList.toArray()));
        }
        if(happyBack!=null){
            log.info("欢乐返"+Arrays.toString(happyBack.toArray()));
        }
        if(newHappyGive!=null){
            log.info("新欢乐送"+Arrays.toString(newHappyGive.toArray()));
        }

        AgentLowerLevelInfoVo info=agentLowerLevelDao.getAgentLowerLevelDetail(agentNo);
        if(info==null){
            return ResponseBean.error("该代理商不存在!");
        }else{
            if(!info.getParentId().equals(userInfoBean.getAgentNo())){
                return ResponseBean.error("当前登入代理商没有权限操作该代理商!");
            }
        }

        Map<String,Object> checkResultMap=new HashMap<String,Object>();
        //校验-代理业务产品服务费率设置
        ResponseBean resultRate=checkBpList(agentNo,bpList,userInfoBean,checkResultMap,true);

        //校验-活动
        ResponseBean happyBackResult=checkAgentActivity(happyBack,userInfoBean,"1",checkResultMap);
        ResponseBean newHappyGiveResult=checkAgentActivity(newHappyGive,userInfoBean,"2",checkResultMap);

        if(!resultRate.isSuccess()||!happyBackResult.isSuccess()||!newHappyGiveResult.isSuccess()){
            String errorMsg="提交失败,存在如下校验不通过,请修改后重新提交.\n";
            int sta=0;
            Map<String,Object> resultMap=new HashMap<String,Object>();
            if(!resultRate.isSuccess()){
                if(resultRate.getCode()==3005){
                    resultMap.put("errorMap",new AgentShareRuleErrorVo(null,resultRate.getMessage(),"3000"));
                    return ResponseBean.error(3000,resultRate.getMessage(),resultMap);
                }
                errorMsg=errorMsg+"结算价中\""+checkResultMap.get("bpName").toString()+"\""+"校验不通过\n";
                sta=sta+1;
            }
            if(!happyBackResult.isSuccess()||!newHappyGiveResult.isSuccess()){
                String activityName="";
                if(!happyBackResult.isSuccess()){
                    activityName=activityName+(checkResultMap.get("activityName")==null?"":checkResultMap.get("activityName").toString())+",";
                    sta=sta+1;
                }
                if(!newHappyGiveResult.isSuccess()){
                    activityName=activityName+(checkResultMap.get("activityNameNew")==null?"":checkResultMap.get("activityNameNew").toString())+",";
                    sta=sta+1;
                }
                errorMsg=errorMsg+"活动设置中\""+activityName.substring(0,activityName.length()-1)+"\""+"校验不通过\n";
            }
            errorMsg=errorMsg+"请检查是否输入错误后重新提交";
            resultMap.put("bpErrorMap",checkResultMap.get("bpErrorMap"));
            resultMap.put("activityErrorMap",checkResultMap.get("activityErrorMap"));
            resultMap.put("errorMap",new AgentShareRuleErrorVo(null,errorMsg,sta==1?"3000":"4000"));
            return ResponseBean.error(sta==1?3000:4000,errorMsg,resultMap);
        }


        //保存业务产品
        saveAgentShare(userInfoBean,agentNo,bpList);
        //保存活动数据
        saveAgentActivity(happyBack,userInfoBean,agentNo);
        saveAgentActivity(newHappyGive,userInfoBean,agentNo);

        //修改结算价操作状态
        agentInfoDataDao.setAgentToBeSetIgnore(agentNo,"2");

        return ResponseBean.success(null,"代理商修改保存成功");
    }

    /**
     * 修改时-保存活动
     * @param activityList
     * @param userInfoBean
     * @param agentNo
     */
    private void saveAgentActivity(List<AgentActivityVo> activityList,UserInfoBean userInfoBean,String agentNo) {
        log.info("活动数据");
        if(activityList!=null&&activityList.size()>0){
            //反正下发数据 一个子类型在2个组织中,2条数据
            for(AgentActivityVo addItem:activityList){
                //存入的数据前端不带%,数据/100
                if(addItem.getTaxRate()!=null){
                    addItem.setTaxRate(addItem.getTaxRate().divide(new BigDecimal(100)));
                }
                if(addItem.getRewardRate()!=null){
                    addItem.setRewardRate(addItem.getRewardRate().divide(new BigDecimal(100)));
                }

                //返回的数据是少量的，单独查询回来数据
                AgentActivity parentInfo =agentLowerLevelDao.getAgentActivityInfo(userInfoBean.getAgentNo(),addItem.getActivityTypeNo());
                //查询当前是否存在了
                AgentActivity infoOld =agentLowerLevelDao.getAgentActivityInfo(agentNo,addItem.getActivityTypeNo());
                if(infoOld!=null){
                    log.info("活动数据新增");
                    agentLowerLevelDao.updateAgentActivity(addItem,parentInfo,agentNo);
                }else{
                    //先更新本级数据
                    log.info("活动数据修改");
                    agentLowerLevelDao.addAgentActivity(addItem,parentInfo,agentNo);
                }
                //更新链条值为0的所有子集
                if("1".equals(parentInfo.getSubType())){//判断是否需要更新子链条
                    if((addItem.getFullPrizeAmount()!=null&&addItem.getFullPrizeAmount().compareTo(BigDecimal.ZERO)==0)
                        ||(addItem.getNotFullDeductAmount()!=null&&addItem.getNotFullDeductAmount().compareTo(BigDecimal.ZERO)==0)
                    ){
                        log.info("活动数据欢乐返下级的子类型");
                        agentLowerLevelDao.updateAgentActivityLower(addItem,parentInfo,agentNo);
                    }

                }else if("2".equals(parentInfo.getSubType())){
                    if((addItem.getOneRewardAmount()!=null&&addItem.getOneRewardAmount().compareTo(BigDecimal.ZERO)==0)
                            ||(addItem.getTwoRewardAmount()!=null&&addItem.getTwoRewardAmount().compareTo(BigDecimal.ZERO)==0)
                            ||(addItem.getThreeRewardAmount()!=null&&addItem.getThreeRewardAmount().compareTo(BigDecimal.ZERO)==0)
                            ||(addItem.getFourRewardAmount()!=null&&addItem.getFourRewardAmount().compareTo(BigDecimal.ZERO)==0)
                            ||(addItem.getDeductionAmount()!=null&&addItem.getDeductionAmount().compareTo(BigDecimal.ZERO)==0)
                    ) {
                        log.info("活动数据新欢乐送下级的子类型");
                        agentLowerLevelDao.updateAgentActivityLower(addItem, parentInfo, agentNo);
                    }
                }
            }
        }
    }

    /**
     * 将数据划分
     * @param bpList
     * @return
     */
    private ClassificationMbListInfo classificationMbList(List<AgentBpIdInfoVo> bpList){
        //数据划分
        Map<String,List<AgentBpIdInfoVo>> mapBp=new HashMap<String,List<AgentBpIdInfoVo>>();
        List<AgentBpIdInfoVo> bpListAdd=new ArrayList<AgentBpIdInfoVo>();
        if(bpList!=null&&bpList.size()>0){
            for(AgentBpIdInfoVo bpItem:bpList){
                if(StringUtils.isNotBlank(bpItem.getGroupNo())){
                    if(mapBp.get(bpItem.getGroupNo())!=null){
                        List<AgentBpIdInfoVo> addList=mapBp.get(bpItem.getGroupNo());
                        addList.add(bpItem);
                        mapBp.put(bpItem.getGroupNo(),addList);
                    }else{
                        List<AgentBpIdInfoVo> addList=new ArrayList<AgentBpIdInfoVo>();
                        addList.add(bpItem);
                        mapBp.put(bpItem.getGroupNo(),addList);
                    }
                }else{
                    bpListAdd.add(bpItem);
                }
            }
        }
        return new ClassificationMbListInfo(mapBp,bpListAdd);
    }

    /**
     * 修改时-保存分润信息
     * @param userInfoBean
     * @param agentNo
     * @param bpList
     */
    private void saveAgentShare(UserInfoBean userInfoBean,String agentNo,List<AgentBpIdInfoVo> bpList){
        //获取上级的代理的业务产品
        Map<String, AgentBpIdInfo> mapParent=getMapBpIdAgent(agentLowerLevelDao.getAgentBpId(userInfoBean.getAgentNo()),userInfoBean.getAgentNo(),userInfoBean.getOneLevelId());

        //生成 当天的23：59:59.000
        Calendar day = new GregorianCalendar();
        day.set(Calendar.HOUR_OF_DAY, 23);
        day.set(Calendar.MINUTE, 59);
        day.set(Calendar.SECOND, 59);
        day.set(Calendar.MILLISECOND, 0);
        Date date=day.getTime();

        //数据划分
        ClassificationMbListInfo dataInfo= classificationMbList(bpList);
        Map<String,List<AgentBpIdInfoVo>> mapBp=dataInfo.getMapBp();
        List<AgentBpIdInfoVo> bpListAdd=dataInfo.getBpListAdd();

        //有组数据处理
        if(mapBp.size()>0){
            //每组遍历group
            for(Map.Entry<String,List<AgentBpIdInfoVo>> entry:mapBp.entrySet()){
                String groupNo=entry.getKey();
                //查询这个组内,队长BPId 多少
                String captainBpId=agentLowerLevelDao.getGroupBpId(userInfoBean.getAgentNo(),groupNo);
                List<String> bpIdListOld=null;//查询勾选的BPID
                if(StringUtils.isNotBlank(agentNo)){
                    bpIdListOld=agentLowerLevelDao.getGroupBpIdList(agentNo,groupNo);
                }

                List<AgentBpIdInfoVo> list=entry.getValue();
                if(list!=null&&list.size()>0){
                    //防止无序，先遍历取出队长的数据
                    AgentBpIdInfoVo captainInfo=null;//队长实体
                    List<String> memberList=new ArrayList<String>();//队员list
                    for(AgentBpIdInfoVo item:list){
                        //遍历相同group,取出队长的值
                        if(captainBpId.equals(item.getBpId().toString())){
                            captainInfo=item;
                        }else{
                            memberList.add(item.getBpId().toString());
                        }
                        String checkBpId=agentLowerLevelDao.getAgentBpIdByAgentNo(agentNo,item.getBpId().toString());
                        if(!StringUtils.isNotBlank(checkBpId)){//当值未存储过
                            log.info("设置业务产品:"+item.getBpId().toString()+"代理商编号："+agentNo);
                            //设置代理业务组
                            agentLowerLevelDao.addAgentBusinessProduct(item,agentNo);
                        }
                    }
                    if(bpIdListOld!=null&&bpIdListOld.size()>0){
                        for(String bpStr:bpIdListOld){
                            //不是队长,且队员未上传
                            if(!captainBpId.equals(bpStr)&&!memberList.contains(bpStr)){
                                memberList.add(bpStr);
                            }
                        }
                    }
                    //获取上级队长分润列表
                    Map<String,AgentShareRuleInfo> shareMapParent=mapParent.get(captainBpId).getAgentShareMap();

                    if(shareMapParent.size()>0){
                        if(captainInfo!=null){//已上传队长分润信息了
                            //遍历上级分润列表，根据上传数据更新，上级值>=上传值
                            //队长数据转换成Map
                            Map<String,AgentShareRuleInfo> addMap=splitShareRuleVo(captainInfo,shareMapParent);

                            for(Map.Entry<String,AgentShareRuleInfo> shareEntry:shareMapParent.entrySet()){
                                AgentShareRuleInfo shareParent=shareEntry.getValue();
                                if(addMap.get(shareParent.getId())!=null){
                                    AgentShareRuleInfo addShare=addMap.get(shareParent.getId());
                                    String shareId=agentLowerLevelDao.getAgentShareRuleByServiceId(agentNo,shareParent);
                                    if(StringUtils.isNotBlank(shareId)){//这条服务分润已存在
                                        //查询是否存在分润设置，有存入备份表，先删除同时间段的，再新增
                                        log.info("代理商存在分润:"+shareId+"代理商编号："+agentNo+"已存在,更新到定时表");
                                        agentLowerLevelDao.deleteAgentShareRuleTask(shareId,date);
                                        agentLowerLevelDao.insertAgentShareRuleTask(shareParent,addShare,shareId,date);
                                    }else{
                                        //新增分润
                                        log.info("代理商不存在分润,代理商编号："+agentNo+"直接生效");
                                        agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,addShare,agentNo,"");
                                    }
                                    //复制生成队员数据
                                    for(String memBpStr: memberList){
                                        //复制不同bpid,serviceType,卡类型,节假日类型一样的数据
                                        String serviceId_Bp=agentLowerLevelDao.findGroupServiceId(Long.valueOf(memBpStr),shareParent.getServiceType2(),
                                                shareParent.getCardType(),shareParent.getHolidaysMark(),userInfoBean.getOneLevelId());

                                        log.info("队员复制同服务ID:"+serviceId_Bp+"代理商编号："+agentNo+",原服务ID"+shareParent.getServiceId());

                                        String shareId_bp=agentLowerLevelDao.getAgentShareRuleByServiceIdBp(agentNo,serviceId_Bp,shareParent);

                                        if(StringUtils.isNotBlank(shareId_bp)){
                                            //查询是否存在分润设置，有存入备份表，先删除同时间段的，再新增
                                            log.info("代理商存在分润:"+shareId_bp+"代理商编号："+agentNo+"已存在,更新到定时表");
                                            agentLowerLevelDao.deleteAgentShareRuleTask(shareId_bp,date);
                                            agentLowerLevelDao.insertAgentShareRuleTask(shareParent,addShare,shareId_bp,date);
                                        }else{
                                            //如果队长分润已存在,且队员不存在，先复制队长以前的数据,在添加新增的数据到定时表
                                            if(StringUtils.isNotBlank(shareId)){
                                                log.info("代理商队员不存在分润,队长原先存在分润，先复制队长当前分润,在新增最新的定时分润");
                                                agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,shareParent,agentNo,serviceId_Bp);
                                                agentLowerLevelDao.deleteAgentShareRuleTask(shareParent.getShareId(),date);
                                                agentLowerLevelDao.insertAgentShareRuleTask(shareParent,addShare,shareParent.getShareId(),date);
                                            }else{
                                                log.info("代理商队员不存在分润，队长原先分润也不存在，直接新增最新的定时分润");
                                                agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,addShare,agentNo,serviceId_Bp);
                                            }
                                        }
                                    }
                                }else{
                                    //如果未上传数据，目前情况不走该逻辑，前端都是批量上传数据
                                    AgentShareRuleInfo addShare=agentLowerLevelDao.getAgentShareRuleInfoByServiceId(agentNo,shareParent);
                                    //复制生成队员数据
                                    for(String memBpStr: memberList){
                                        //复制不同bpid,serviceType,卡类型,节假日类型一样的数据
                                        String serviceId_Bp=agentLowerLevelDao.findGroupServiceId(Long.valueOf(memBpStr),shareParent.getServiceType2(),
                                                shareParent.getCardType(),shareParent.getHolidaysMark(),userInfoBean.getOneLevelId());
                                        String shareId_bp=agentLowerLevelDao.getAgentShareRuleByServiceIdBp(agentNo,serviceId_Bp,shareParent);
                                        if(StringUtils.isNotBlank(shareId_bp)){
                                            //查询是否存在分润设置，有存入备份表，先删除同时间段的，再新增
                                            log.info("未上传数据分润,代理商队员不存在分润,在新增最新的定时分润");
                                            agentLowerLevelDao.deleteAgentShareRuleTask(shareId_bp,date);
                                            agentLowerLevelDao.insertAgentShareRuleTask(shareParent,addShare,shareId_bp,date);
                                        }else{
                                            //如果队长分润已存在,且队员不存在，先复制队长以前的数据,在添加新增的数据到定时表
                                            //老数据,直接复制队长的就可以
                                            log.info("未上传数据分润,代理商队员不存在分润,直接新增最新的定时分润");
                                            agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,addShare,agentNo,serviceId_Bp);
                                        }
                                    }
                                }
                            }

                        }else{//未上传队长分润信息
                            //暂时没有这种情况
                        }
                    }
                }
            }
        }
        //无分组数据处理
        if(bpListAdd.size()>0){
            for(AgentBpIdInfoVo item:bpListAdd){
                Map<String,AgentShareRuleInfo> shareMapParent=mapParent.get(item.getBpId().toString()).getAgentShareMap();//根据业务BPid拿回上级分润
                //解析转换数据
                Map<String,AgentShareRuleInfo> addMap=splitShareRuleVo(item,shareMapParent);

                //设置代理业务组
                String checkBpId=agentLowerLevelDao.getAgentBpIdByAgentNo(agentNo,item.getBpId().toString());
                if(!StringUtils.isNotBlank(checkBpId)){//当值未存储过
                    //设置代理业务组
                    agentLowerLevelDao.addAgentBusinessProduct(item,agentNo);
                }
                if(addMap.size()>0){
                    for(Map.Entry<String,AgentShareRuleInfo> shareEntry:addMap.entrySet()){
                        AgentShareRuleInfo addShare=shareEntry.getValue();
                        AgentShareRuleInfo shareParent=shareMapParent.get(addShare.getId());
                        String shareId=agentLowerLevelDao.getAgentShareRuleByServiceId(agentNo,shareParent);
                        if(StringUtils.isNotBlank(shareId)){//这条服务分润已存在
                            //修改数据，上传到分润备份表
                            //查询备份表是否已存在如果存在则删除，再新增
                            agentLowerLevelDao.deleteAgentShareRuleTask(shareId,date);
                            agentLowerLevelDao.insertAgentShareRuleTask(shareParent,addShare,shareId,date);
                        }else{
                            //新增分润
                            agentLowerLevelDao.insertAgentShareRuleDetail(shareParent,addShare,agentNo,"");
                        }

                    }
                }
            }
        }
    }

    /**
     * 将上传的数据转换拆解成单个服务Map
     * @param captainInfo
     * @param shareMapPercent
     * @return
     */
    private Map<String,AgentShareRuleInfo> splitShareRuleVo(AgentBpIdInfoVo captainInfo,Map<String,AgentShareRuleInfo> shareMapPercent){
        Map<String,AgentShareRuleInfo> addMap=new HashMap<String,AgentShareRuleInfo>();
        if(captainInfo!=null){
            List<AgentShareRuleInfoVo> list=captainInfo.getAgentShare();
            if(list!=null&&list.size()>0){
                for(AgentShareRuleInfoVo itemVo:list){
                    String[] idStrs=itemVo.getId().split(",");
                    if(idStrs!=null&&idStrs.length>0) {
                        for (String id : idStrs) {
                            AgentShareRuleInfo sharePercent=shareMapPercent.get(id);//上级对应服务分润设置
                            //当前代理商信息
                            AgentShareRuleInfo addInfo=new AgentShareRuleInfo();
                            addInfo.setShareProfitPercent(itemVo.getShare());
                            if("5".equals(sharePercent.getProfitType())){
                                if("1".equals(sharePercent.getCostRateType())){
                                    addInfo.setPerFixCost(itemVo.getCost());
                                }else{
                                    addInfo.setCostRate(itemVo.getCost());
                                }
                            }
                            addInfo.setId(id);//拆解单个后的服务ID，如：5-1001-0-0
                            addMap.put(id,addInfo);
                        }
                    }
                }
            }
        }
        return addMap;
    }

}
