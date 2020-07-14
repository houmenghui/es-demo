package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.dao.TerminalApplyRecordDao;
import com.esdemo.modules.service.TerminalApplyRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 机具申请
 * @date 2019/5/24
 */
@Service
@Slf4j
public class TerminalApplyRecordServiceImpl implements TerminalApplyRecordService {

    @Resource
    private TerminalApplyRecordDao terminalApplyRecordDao;

    @Override
    public List<Map<String, Object>> getTerminalApplyRecord(Map<String, String> paramMap) {
        List<Map<String, Object>> list = terminalApplyRecordDao.getTerminalApplyRecord(paramMap);
        if(list.size()>0){
            String need_operation;//是否需要处理(Y需要/N不需要)
            String agent_no_one;//一级代理商编号
            String agent_no_parent;//所属代理商编号
            String status;//状态 0:待直属处理  1:已处理  2:待一级处理
            String curr_agent_no = paramMap.get("user_id");
            for (Map<String, Object> map : list) {
                need_operation = "N";
                status = String.valueOf(map.get("status"));
                agent_no_one = String.valueOf(map.get("agent_no_one"));
                if("2".equalsIgnoreCase(status) && curr_agent_no.equalsIgnoreCase(agent_no_one)){//待一级处理
                    need_operation = "Y";
                }
                agent_no_parent = String.valueOf(map.get("agent_no_parent"));
                if("0".equalsIgnoreCase(status) && curr_agent_no.equalsIgnoreCase(agent_no_parent)){//待直属处理
                    need_operation = "Y";
                }
                map.put("need_operation",need_operation);
            }
        }
        return list;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public Map<String,Object> updateModifyTerminalApplyRecord(Map<String, String> params,UserInfoBean userInfoBean) {
        Map<String,Object> resultMap = new HashMap<>();
        boolean status = false;
        String msg = "机具申请记录处理失败";
        String user_id = params.get("user_id");
        String agentNo = userInfoBean.getAgentNo();
        if (StringUtils.isBlank(user_id) || !agentNo.equals(user_id)){
            log.info("参数user_id 为 {} 与当前登录代理商编号 {} 不相等",user_id,agentNo);
            msg = "无权操作";
            resultMap.put("status", status);
            resultMap.put("msg", msg);
            return resultMap;
        }
        if (StringUtils.isBlank(params.get("record_id"))){
            msg = "被修改的机具申请记录ID不能为空";
            resultMap.put("status", status);
            resultMap.put("msg", msg);
            return resultMap;
        }
        String agent_node = getAgentInfoByNo(user_id).getAgentNode();//当前登陆用户对应的代理商节点

        String terSn = params.get("ter_sn");
        if(StringUtils.isNotBlank(terSn)){
            params.put("SN",terSn);
            Map<String,Object> terInfo = getTerminalInfo(params);
            if (terInfo == null) {
                msg = "输入的SN不存在";
            } else {
                String open_status = String.valueOf(terInfo.get("open_status"));
                if ("2".equalsIgnoreCase(open_status)) {
                    msg = "SN被使用！";
                } else {
                    String agent_node_ter = String.valueOf(terInfo.get("agent_node"));//机具所属代理商节点
                    if (!agent_node_ter.equals(agent_node)) {//如果当前机具所属代理商不是当前进行操作的代理商
                        msg = "该机具已下发给其他代理商，请重新输入！";
                    } else {
                        params.clear();
                        params.put("agent_node", agent_node);
                        params.put("SN", terSn);
                        status = terminalApplyRecordDao.updateModifyTerminalApplyRecord(params)==1;
//                        msg = status ? "机具申请记录处理成功" : msg;
                        //机具申请处理成功后，将机具下发给商户所有的代理商
                        if (status) {
                            String merchantNo = params.get("merchant_no");
                            params.clear();
                            params.put("merchant_no",merchantNo);
                            Map<String,Object> mer = getMerInfo(params);
                            if(mer!=null){
                                String agent_no = String.valueOf(mer.get("agent_no"));
                                String parent_node = String.valueOf(mer.get("parent_node"));
                                params.clear();
                                params.put("agent_no", agent_no);
                                params.put("agent_node", parent_node);
                                params.put("SN", terSn);
                                distributionTerminalInfo(params);
                                log.info("商户[" + merchantNo + "]申请机具，处理成功，并将机具分配给代理商;参数:"+params);
                            }
                        }
                    }
                }
            }
        } else {
            params.put("agent_node", agent_node);
            params.put("SN", terSn);
            terminalApplyRecordDao.updateModifyTerminalApplyRecord(params);
//            msg = status ? "机具申请记录处理成功" : msg;
        }

        log.info("机具申请记录处理结果:"+status+";参数:"+params);
        resultMap.put("status", status);
        resultMap.put("msg", msg);
        return resultMap;
    }

    @Override
    public AgentInfo getAgentInfoByNo(String id) {
        return terminalApplyRecordDao.getAgentInfoByNo(id);
    }

    @Override
    public Map<String, Object> getTerminalInfo(Map<String, String> params) {
        return terminalApplyRecordDao.getTerminalInfo(params);
    }

    @Override
    public Map<String, Object> getMerInfo(Map<String, String> params) {
        return terminalApplyRecordDao.getMerInfo(params);
    }

    @Override
    public boolean distributionTerminalInfo(Map<String, String> params) {
        return terminalApplyRecordDao.distributionTerminalInfo(params)==1;
    }

    @Override
    public int countTerminalApplyRecord(String agentNode) {
        return terminalApplyRecordDao.countTerminalApplyRecord(agentNode);
    }

    @Override
    public List<Map<String, Object>> getAllActivityInfo() {
        return terminalApplyRecordDao.getAllActivityInfo();
    }
}
