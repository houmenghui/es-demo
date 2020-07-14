package com.esdemo.modules.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.utils.ClientInterface;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.SnReceiveInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.dao.MachineManageDao;
import com.esdemo.modules.service.FunctionManageService;
import com.esdemo.modules.service.MachineManageService;
import com.esdemo.modules.service.MerchantInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author lmc liuks
 * @date 2019/5/16 14:51
 */
@Slf4j
@Service
public class MachineManageServiceImpl implements MachineManageService {
    @Resource
    private MachineManageDao machineManageDao;
    @Resource
    private FunctionManageService functionManageService;

    @Resource
    private MerchantInfoService merchantInfoService ;
    /**
     * 获取该用户下所有机具信息
     * 筛选字段
     * select_type    1-下发  2-全部
     * title_type    1-全部机具  2-我的机具
     * is_all    0-未勾中全选  1-勾中全选
     *  sn
     * terminal_id
     * psam_no
     * open_status     分配状态
     * mername_no
     * agentname_no
     * sn_min  (select_type=1下选填，但是sn_min和sn_max必须同时填或者不填)
     * sn_max  (select_type=1下选填，但是sn_min和sn_max必须同时填或者不填)
     */
    public ResponseBean getAllByCondition(UserInfoBean userInfoBean, String params) {

        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        String is_all = StringUtils.filterNull(params_map.get("is_all"));
        //设置分页信息，分别是当前页数和每页显示的总记录数
        long count = 0;
        List<Map<String, Object>> list = null;
        //全选
        if ("1".equals(is_all)) {
            list = machineManageDao.getAllByCondition(userInfoBean, params_map);
            return ResponseBean.success(list, list.size());
        }

        //第一页查询总页数，其它页不查询
        if (pageNo == 1) {
            Page page = PageHelper.startPage(pageNo, pageSize);
            list = machineManageDao.getAllByCondition(userInfoBean, params_map);
            count = page.getTotal();
        } else {
            PageHelper.startPage(pageNo, pageSize, false);
            list = machineManageDao.getAllByCondition(userInfoBean, params_map);
        }
        return ResponseBean.success(list, count);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public ResponseBean manageTerminal(UserInfoBean userInfoBean, String params) {
        //处理字段
        //receive_agent_no  (select_type=1 必填)
        //receive_agent_node  (select_type=1 必填)
        //select_type    1-下发  2-回收
        //sn_array_str        123456,123457,1234587
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String select_type = StringUtils.filterNull(params_map.get("select_type"));
        String receive_agent_no = StringUtils.filterNull(params_map.get("receive_agent_no"));
        String receive_agent_node = StringUtils.filterNull(params_map.get("receive_agent_node"));
        String sn_array_str = StringUtils.filterNull(params_map.get("sn_array_str"));
        String agent_no = userInfoBean.getAgentNo();
        String agent_node = userInfoBean.getAgentNode();

        if ("".equals(sn_array_str)) {
            return ResponseBean.error("下发机具sn号不能为空");
        }
        //下发操作
        if ("1".equals(select_type)) {
            return distributeTerminal(agent_no, userInfoBean.getAgentName(), sn_array_str, receive_agent_no,  receive_agent_node);
        }

        //回收操作
        if ("2".equals(select_type)) {
            return takeBackTerminal(agent_no, agent_node, userInfoBean.getAgentName(), sn_array_str);
        }
        return ResponseBean.error(null, "操作失败");
    }

    /**
     * 机具下发
     * @param agent_no
     * @param current_agent_name
     * @param sn_array_str
     * @param receive_agent_no
     * @param receive_agent_node
     * @return
     */
    private ResponseBean distributeTerminal(String agent_no,String current_agent_name, String sn_array_str,String receive_agent_no, String receive_agent_node) {
        String parent_agent_no = StringUtils.filterNull(machineManageDao.getAgentInfoByAgentNo(receive_agent_no).get("parent_id"));
        //下发的代理商必须是直属的
        if (!parent_agent_no.equals(agent_no)) {
            return ResponseBean.error("跨级代理商，仅允许下发直属代理商");
        }

        String[] sn_array = sn_array_str.split(",");
        int length = sn_array.length;
        //需要执行的机具更新sn字符串
        String can_exe_sql_str = "";
        //分片sql的list
        ArrayList<String> sn_sql = new ArrayList<>();
        //流动记录统计成功的sql
        String sql_success_str = "";
        //遍历去除超级推机具并封装成小于500分片的机具sql
        for (int i = 0; i < length; i++) {
            String sn = sn_array[i];

            //sn加引号
            can_exe_sql_str += "'"+ sn + "',";
            //大于500需要进行分批执行
            if(i != 0 && i % 500 == 0 && !"".equals(can_exe_sql_str)) {
                //去除最后一个逗号
                can_exe_sql_str = can_exe_sql_str.substring(0, can_exe_sql_str.length() - 1);
                sn_sql.add(can_exe_sql_str);
                //重置需要执行的机具sql
                can_exe_sql_str = "";
                continue;
            }
            //收尾工作
            if(i == length - 1) {
                if(!"".equals(can_exe_sql_str)) {
                    can_exe_sql_str = can_exe_sql_str.substring(0, can_exe_sql_str.length() - 1);
                    sn_sql.add(can_exe_sql_str);
                }
            }
        }

        for (String sql : sn_sql) {
            int num = machineManageDao.updateTerToSend(sql, receive_agent_no, receive_agent_node);
            if (num > 0) {
                sql_success_str += sql + ",";
            }
        }

        //流动记录
        if(!"".equals(sql_success_str)){
            //去除sql的引号
            sql_success_str = sql_success_str.replace("'", "");
            int success_num = sql_success_str.split(",").length;
            if(success_num == length){
                return ResponseBean.success(null, "下发成功");
            }
        }
        return ResponseBean.success(null, "下发成功,有部分机具参与活动不支持下发操作");
    }


    /**
     *  机具回收
     * @param agent_no
     * @param agent_node
     * @param current_agent_name
     * @param sn_array_str
     * @return
     */
    private ResponseBean takeBackTerminal(String agent_no,String agent_node, String current_agent_name, String sn_array_str) {
        String[] sn_temp_array = sn_array_str.split(",");
        JSONArray jsonArray = new JSONArray();
        int fail_count = 0;
        int count = sn_temp_array.length;
        //分片sql的list
        ArrayList<String> sn_sql = new ArrayList<>();
        //分片sql
        String sql_sn_array_str = "";
        //记录原始的sn号对应的agent_no
        Map<String, String> sn_agent_map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String sn_temp = sn_temp_array[i];
            Map<String, Object> map = machineManageDao.getTermInfoBySn(sn_temp);
            String sn_agent_no = StringUtils.filterNull(map.get("agent_no"));
            sn_agent_map.put(sn_temp, sn_agent_no);
            String parent_id = StringUtils.filterNull(machineManageDao.getAgentInfoByAgentNo(sn_agent_no).get("parent_id"));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sn", sn_temp);
            //回收机具仅允许回收直属下级代理商且未绑定商户的机具
            if (sn_agent_no.equals(agent_no)) {
                fail_count++;
                jsonObject.put("fail_result", "自己的机具无需回收");
                jsonArray.add(jsonObject);
                continue;
            }

            if (!parent_id.equals(agent_no)) {
                fail_count++;
                jsonObject.put("fail_result", "仅允许回收直属下级机具");
                jsonArray.add(jsonObject);
                continue;
            }

            if ("2".equals(map.get("open_status"))) {
                fail_count++;
                jsonObject.put("fail_result", "已绑定商户机具");
                jsonArray.add(jsonObject);
                continue;
            }

            //拼接需要执行的sn的sql
            sql_sn_array_str += "'"+sn_temp + "',";

            //大于500需要进行分批执行
            if(i != 0 && i % 500 == 0 && !"".equals(sql_sn_array_str)) {
                //去除最后一个逗号
                sql_sn_array_str = sql_sn_array_str.substring(0, sql_sn_array_str.length() - 1);
                sn_sql.add(sql_sn_array_str);
                //重置需要执行的机具sql
                sql_sn_array_str = "";
                continue;
            }
        }

        //收尾工作
        if(!"".equals(sql_sn_array_str)) {
            sql_sn_array_str = sql_sn_array_str.substring(0, sql_sn_array_str.length() - 1);
            sn_sql.add(sql_sn_array_str);
        }

        //执行成功的sql
        String sql_success_str = "";
        for (String sql : sn_sql) {
            //回收操作
            int num = machineManageDao.updateTerToBack(sql, agent_no, agent_node);
            if (num <= 0) {
                //只记录回收失败的
                String[] fail_sn_array = sql.split(",");
                for (String sn_temp : fail_sn_array) {
                    JSONObject fail_json = new JSONObject();
                    fail_json.put("sn", sn_temp);
                    fail_json.put("fail_result", "回收失败");
                    jsonArray.add(fail_json);
                }
                fail_count = fail_count + fail_sn_array.length;
            } else {
                sql_success_str += sql + ",";
            }
        }

        //回收有失败需要返回失败信息
        if (fail_count > 0) {
            JSONObject data_json = new JSONObject();
            data_json.put("success_count", count - fail_count);
            data_json.put("fail_count", fail_count);
            data_json.put("sn_array", jsonArray);
            return ResponseBean.success(data_json);
        }
        return ResponseBean.success(null, "全部回收成功");
    }

    /**
     * 根据成功执行的sn字符串数组获取以代理商编号分组代理商信息
     * @param sql_success_str
     * @param sn_agent_map
     * @return
     */
    private Map<String, SnReceiveInfo> groupByAgentNo(String sql_success_str, Map<String, String> sn_agent_map){
        String[] sn_array = sql_success_str.split(",");
        ArrayList<String> agent_no_array = new ArrayList<>();
        HashMap<String, SnReceiveInfo> map = new HashMap<>();
        for(String sn : sn_array){
            String agent_no = sn_agent_map.get(sn);
            if(!agent_no_array.contains(agent_no)){
                agent_no_array.add(agent_no);
                SnReceiveInfo snReceiveInfo = new SnReceiveInfo();
                snReceiveInfo.setAgentNo(agent_no);
                snReceiveInfo.setSnStr(sn);
                snReceiveInfo.setSuccessCount(1);
                map.put(agent_no, snReceiveInfo);
            }else{
                SnReceiveInfo snReceiveInfo = map.get(agent_no);
                snReceiveInfo.setSnStr(snReceiveInfo.getSnStr() + "," + sn);
                snReceiveInfo.setSuccessCount(snReceiveInfo.getSuccessCount() + 1);
                map.put(agent_no, snReceiveInfo);
            }
        }
        return map;
    }

    /**
     * 插入流动记录时间
     */
    private int insAgentTerminalOperate(String agent_no, String sn_array, String oper_detail_type, String oper_type, Date date) {
        if (StringUtils.isNotEmpty(sn_array)){
            String[] snArray = sn_array.split(",");
            for (String sn : snArray) {
                machineManageDao.insertTerminalOperateTime(agent_no,sn,oper_detail_type,oper_type,date);
            }

        }
        return 0;
    }

    /**
     * 机具解绑功能
     * @param userInfoBean
     * @param params
     * @return
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public ResponseBean terminalRelease(UserInfoBean userInfoBean, String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String sn = StringUtils.filterNull(params_map.get("sn"));

        int level=userInfoBean.getAgentLevel().intValue();
        if (level!=1&&level!=2) {
            return ResponseBean.error("该代理商没有解绑功能权限");
        }

        boolean status=functionManageService.getAgentTerminalRelease(userInfoBean);
        if(!status){
            return ResponseBean.error("该代理商没有解绑功能权限");
        }

        Map<String, Object> term_info_map = machineManageDao.getTermInfoBySn(sn);
        if(term_info_map==null || term_info_map.isEmpty()){
            return ResponseBean.error("该机具不存在");
        }

        //校验该sn是否是该代理商链条下所有的
        String agent_node = StringUtils.filterNull(term_info_map.get("agent_node"));
        if(!agent_node.startsWith(userInfoBean.getAgentNode())) {
            return ResponseBean.error("该机具不属于该代理商，暂不可解绑");
        }
        String merchant_no = StringUtils.filterNull(term_info_map.get("merchant_no"));
        if("".equals(merchant_no)){
            return ResponseBean.error("该机具未绑定商户，暂不可解绑");
        }
        //如果是2级，判断数据是否是直属商户，原则上机具的代理商编号就是绑定的商户的代理商编号，如不一致，先不管，数据问题
        if (level==2&&!userInfoBean.getAgentNo().equals(term_info_map.get("agent_no"))) {
            return ResponseBean.error("该代理商没有解绑功能权限");
        }

        //判断是否参与满奖活动
        if(!"".equals(merchant_no) && !"".equals(StringUtils.filterNull(machineManageDao.getIsTakeActivity(merchant_no, sn)))){
            return ResponseBean.error("该机具已经参加过活动，暂不可解绑");
        }

        String mbpId= StringUtils.filterNull(term_info_map.get("bp_id"));
        //调用core接口，请求上游解绑机具 type 1解绑 0绑定
        String returnStr =ClientInterface.merBindOrUnBindTmstpos(sn,merchant_no,mbpId,"1");
        if(StringUtils.isNotBlank(returnStr)){
            Map<String, Map> msg = GsonUtils.fromJson2Map(returnStr, Map.class);
            Map headerMap=msg.get("header");
            if(headerMap!=null&&headerMap.get("succeed")!=null){
                boolean succeed=Boolean.parseBoolean(headerMap.get("succeed").toString());
                if(succeed){
                    int num = machineManageDao.terminalRelease(sn);
                    if (num > 0) {
                        return ResponseBean.success(null, "解绑成功");
                    }else {
                        return ResponseBean.error("解绑失败");
                    }
                }
            }
        }
        return ResponseBean.error("解绑失败");
    }


    /**
     * 查询代理商功能开关
     */
    @Override
    public Map<String, Object> getFunctionManage(String function_number) {
        return machineManageDao.getFunctionManage(function_number);
    }

    /**
     * 机具流动记录查询
     */
    @Override
    public List<Map<String, Object>> getSnSendAndRecInfo(Map<String, Object> params_map){
        return machineManageDao.getSnSendAndRecInfo(params_map);
    }

    /**
     * 机具流动详情查询
     */
    @Override
    public String getSnSendAndRecDetail(String id){
        return machineManageDao.getSnSendAndRecDetail(id);
    }

    /**
     * 获取代理商权限控制信息
     */
    @Override
    public String getAgentFunction(String agent_no, String function_number){
        return machineManageDao.getAgentFunction(agent_no, function_number);
    }

    @Override
    public long countBlacklistNotContains(String agentNo){
        return machineManageDao.countBlacklistNotContains(agentNo);
    }
    @Override
    public long countBlacklistContains(String agentNode){
        return machineManageDao.countBlacklistContains(agentNode);
    }

    /**
     *查询当前代理商勾选的欢乐返子类型
     * @param agent_no
     * @return
     */
    @Override
    public List<Map<String, Object>> getActivityTypes(String agent_no) {
        return machineManageDao.getActivityTypes(agent_no);
    }

}

