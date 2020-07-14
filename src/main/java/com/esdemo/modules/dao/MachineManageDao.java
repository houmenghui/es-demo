package com.esdemo.modules.dao;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.UserInfoBean;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/16 15:06
 */
@Mapper
public interface MachineManageDao {
    /**
     * 获取该用户下所有机具信息
     */
    @SelectProvider(type = MachineManageDao.SqlProvider.class, method = "getAllByCondition")
    @ResultType(List.class)
    List<Map<String, Object>> getAllByCondition(@Param("userInfoBean") UserInfoBean userInfoBean, @Param("params_map") Map<String, Object> params_map);

    /**
     * 获取代理商信息
     */
    @Select(
            "select * from agent_info where agent_no=#{agent_no} "
    )
    Map<String, Object> getAgentInfoByAgentNo(@Param("agent_no") String agent_no);

    /**
     * 获取代理商信息
     */
    @Select(
            "select id from agent_info where agent_no=#{agent_no} "
    )
    String getAgentInfo(@Param("agent_no") String agent_no);

    /**
     * 获取机具信息
     */
    @Select(
            "select * from terminal_info where sn=#{sn} "
    )
    Map<String, Object> getTermInfoBySn(@Param("sn") String sn);

    /**
     * 下发机具
     */
    @UpdateProvider(type = MachineManageDao.SqlProvider.class, method = "updateTerToSend")
    int updateTerToSend(@Param("sql_sn_array") String sql_sn_array, @Param("agent_no") String agent_no, @Param("agent_node") String agent_node);


    /**
     * 回收机具
     */
    @UpdateProvider(type = MachineManageDao.SqlProvider.class, method = "updateTerToBack")
    int updateTerToBack(@Param("sql_sn_array") String sql_sn_array, @Param("agent_no") String agent_no, @Param("agent_node") String agent_node);

    /**
     * 查询代理商功能开关
     */
    @Select(
            "select * from function_manage where function_number=#{function_number} "
    )
    Map<String, Object> getFunctionManage(@Param("function_number") String function_number);

    /**
     * 查询活动信息
     */
    @Select(
            "select id from activity_detail where merchant_no=#{merchant_no} and active_sn=#{sn} limit 1 "
    )
    String getIsTakeActivity(@Param("merchant_no") String merchant_no, @Param("sn") String sn);

    /**
     * 解绑机具
     */
    @Update(
            "update terminal_info set merchant_no=null,open_status='1' where sn=#{sn} "
    )
    int terminalRelease(@Param("sn") String sn);

    /**
     * 流动记录
     */
    /**
     * 查询活动信息
     */
    @Insert(
            "insert terminal_operate(agent_no,for_operater,oper_num,sn_array,oper_detail_type,oper_type,create_time) values (#{agent_no},#{for_operater},#{oper_num},#{sn_array},#{oper_detail_type},#{oper_type},now()) "
    )
    int insTerminalOperate(@Param("agent_no") String agent_no, @Param("for_operater") String for_operater, @Param("oper_num") int oper_num, @Param("sn_array") String sn_array, @Param("oper_detail_type") String oper_detail_type, @Param("oper_type") String oper_type);
    /**
     * 流动记录
     */
    @Insert(" replace  into agent_terminal_operate(agent_no,sn,oper_detail_type,oper_type,create_time)"
            + "values(#{agent_no},#{sn},#{oper_detail_type},#{oper_type},#{date})")
    int insertTerminalOperateTime(@Param("agent_no") String agent_no,@Param("sn") String sn,@Param("oper_detail_type") String oper_detail_type,@Param("oper_type")  String oper_type,@Param("date") Date date);
    /**
     * 机具流动记录查询
     */
    @SelectProvider(type = MachineManageDao.SqlProvider.class, method = "getSnSendAndRecInfo")
    @ResultType(List.class)
    List<Map<String, Object>> getSnSendAndRecInfo(@Param("params_map") Map<String, Object> params_map);

    /**
     * 机具流动详情查询
     */
    @Select(
            "select sn_array from terminal_operate where id=#{id} "
    )
    String getSnSendAndRecDetail(@Param("id") String id);

    /**
     * 获取代理商权限控制信息
     */
    @Select(
            "select id from agent_function_manage where agent_no=#{agent_no} and function_number=#{function_number} "
    )
    String getAgentFunction(@Param("agent_no") String agent_no, @Param("function_number") String function_number);

    @Select("SELECT " +
            "    count(*)  " +
            "FROM " +
            "    function_manage f_mana " +
            "    JOIN agent_function_manage_blacklist a_f_mana ON f_mana.function_number = a_f_mana.function_number " +
            "    AND f_mana.function_switch = 1  " +
            "    AND f_mana.function_number = '051' " +
            "    AND a_f_mana.agent_no = #{agentNo}  " +
            "    AND a_f_mana.blacklist = 1  " +
            "    AND a_f_mana.contains_lower =0")
    long countBlacklistNotContains(String agentNo);

    @Select("SELECT " +
            "    count(*) " +
            "FROM " +
            "    function_manage f_mana " +
            "    JOIN agent_function_manage_blacklist a_f_mana ON f_mana.function_number = a_f_mana.function_number " +
            "    JOIN agent_info a_info ON a_info.agent_no = a_f_mana.agent_no " +
            "    JOIN agent_info a_info2 ON a_info2.agent_node LIKE CONCAT( a_info.agent_node, '%' )  " +
            "    AND f_mana.function_switch = 1  " +
            "    AND f_mana.function_number = '051'  " +
            "    AND a_info2.agent_node = #{agentNode}  " +
            "    AND a_f_mana.blacklist = 1  " +
            "    AND a_f_mana.contains_lower =1;")
    long countBlacklistContains(String agentNode);

    /*
    查询当前代理商勾选的欢乐返子类型
    */
    @Select(
            "select aa.activity_type_no,aht.activity_type_name " +
                    " from agent_activity aa " +
                    "   LEFT JOIN activity_hardware_type aht ON aht.activity_type_no = aa.activity_type_no " +
                    " where aa.agent_no=#{agent_no} "
    )
    @ResultType(List.class)
    List<Map<String, Object>>getActivityTypes(@Param("agent_no") String agent_no);

    class SqlProvider {
        public String getAllByCondition(Map<String, Object> params_map) {
            final Map<String, Object> map = (Map<String, Object>) params_map.get("params_map");
            final UserInfoBean userInfoBean = (UserInfoBean) params_map.get("userInfoBean");
            // sn
            //terminal_id
            //psam_no
            //open_status     分配状态
            //mername_no
            //agentname_no
            //sn_min  (handler_type=1下选填，但是sn_min和sn_max必须同时填或者不填)
            //sn_max  (handler_type=1下选填，但是sn_min和sn_max必须同时填或者不填)
            String sql = new SQL() {
                {
                    SELECT(" ti.id,ti.SN,ti.terminal_id,ti.merchant_no,ti.PSAM_NO,ti.agent_no,ti.agent_node,ti.open_status,ti.type,ti.allot_batch," +
                            "ti.model,ti.tmk,ti.tmk_tpk,ti.tmk_tak,ti.pos_type,ti.need_check," +
                            "DATE_FORMAT(ti.last_check_in_time, '%Y-%m-%d %H:%i:%S') as last_check_in_time,ti.cashier_no,ti.serial_no,ti.batch_no," +
                            "ti.single_share_amount,ti.bp_id,ti.collection_code,aht.activity_type_name activity_type,ti.recommended_source,ti.terminal_name," +
                            "ti.channel,ti.activity_type_no,DATE_FORMAT(ti.create_time, '%Y-%m-%d %H:%i:%S') as create_time," +
                            "DATE_FORMAT(ti.start_time, '%Y-%m-%d %H:%i:%S') as start_time,ai.agent_name," +
                            "mi.merchant_name,IFNULL(mi.hlf_active,0) AS hlf_active ");
                    FROM(" terminal_info ti ");
                    LEFT_OUTER_JOIN(" agent_info ai on ti.agent_no=ai.agent_no ");
                    LEFT_OUTER_JOIN(" merchant_info mi on ti.merchant_no=mi.merchant_no ");
                    LEFT_OUTER_JOIN(" activity_hardware_type aht on ti.activity_type_no=aht.activity_type_no ");
                    WHERE(" 1=1 ");
                    //select_type  1-可下发  2-全部 3 可回收 可下发的筛选只有sn大小筛选

                    String selectType=StringUtils.filterNull(map.get("select_type"));
                    if ("2".equals(selectType)) {
                        //title_type    1-全部机具  2-我的机具
                        if ("1".equals(StringUtils.filterNull(map.get("title_type")))) {
                            WHERE(" ti.agent_node like concat(#{userInfoBean.agentNode},'%') ");
                        } else {
                            WHERE(" ti.agent_no=#{userInfoBean.agentNo} ");
                        }
                        if (!"".equals(StringUtils.filterNull(map.get("sn")))) {
                            WHERE(" ti.sn =#{params_map.sn} ");
                        }
                        if (!"".equals(StringUtils.filterNull(map.get("terminal_id")))) {
                            WHERE(" ti.terminal_id=#{params_map.terminal_id} ");
                        }
                        if (!"".equals(StringUtils.filterNull(map.get("psam_no")))) {
                            WHERE(" ti.psam_no=#{params_map.psam_no} ");
                        }
                        if (!"".equals(StringUtils.filterNull(map.get("open_status")))) {
                            WHERE(" ti.open_status=#{params_map.open_status} ");
                        }
                        if (!"".equals(StringUtils.filterNull(map.get("activity_type_no")))) {
                            WHERE(" aht.activity_type_no=#{params_map.activity_type_no} ");
                        }
                        if (!"".equals(StringUtils.filterNull(map.get("mername_no")))) {

                            WHERE(" (mi.merchant_no=#{params_map.mername_no} or mi.merchant_name like concat('%',#{params_map.mername_no},'%') or mi.mobilephone=#{params_map.mername_no}) ");
                        }
                        //如果前端传入空字符，则默认当前登入代理商
                        String agentNo=StringUtils.filterNull(map.get("agentname_no"));
                        //是否包含下级
                        String lowerStatus=StringUtils.filterNull(map.get("lowerStatus"));
                        if (!"".equals(agentNo)) {
                            if("1".equals(lowerStatus)){//是，全链条
                                WHERE("ai.agent_node like concat((select agent_node from agent_info where agent_no=#{params_map.agentname_no}),'%') ");
                            }else if("2".equals(lowerStatus)){//否，本级
                                WHERE("ai.agent_no=#{params_map.agentname_no} ");
                            }else if("3".equals(lowerStatus)){//仅下级
                                WHERE("ai.parent_id=#{params_map.agentname_no} ");
                            }else{
                                WHERE("ai.agent_node like concat((select agent_node from agent_info where agent_no=#{params_map.agentname_no}),'%') ");
                            }
                        }else{
                            if("1".equals(lowerStatus)){//是，全链条
                                WHERE("ai.agent_node like concat((select agent_node from agent_info where agent_no=#{userInfoBean.agentNo}),'%') ");
                            }else if("2".equals(lowerStatus)){//否，本级
                                WHERE("ai.agent_no=#{userInfoBean.agentNo} ");
                            }else if("3".equals(lowerStatus)){//仅下级
                                WHERE("ai.parent_id=#{userInfoBean.agentNo} ");
                            }else{
                                WHERE("ai.agent_node like concat((select agent_node from agent_info where agent_no=#{userInfoBean.agentNo}),'%') ");
                            }
                        }
                        //商户激活状态
                        String activeSta=StringUtils.filterNull(map.get("hlf_active"));
                        if ("1".equals(activeSta)) {
                            WHERE("mi.hlf_active=1 ");
                        }else if("0".equals(activeSta)){
                            WHERE("(mi.hlf_active=0 or mi.hlf_active is null) ");
                        }
                    } else if("1".equals(selectType)){
                        //可下发 ，只展示代理为自己的机具
                        WHERE(" ti.open_status='1' and ti.agent_no=#{userInfoBean.agentNo} ");
                    }else if("3".equals(selectType)){
                        //可回收
                        WHERE("ai.parent_id=#{userInfoBean.agentNo} ");//当前登入直属下级
                        WHERE("ti.open_status!='2' ");
                    }
                    //机具sn段号判断
                    if (!"".equals(StringUtils.filterNull(map.get("sn_min"))) && !"".equals(StringUtils.filterNull(map.get("sn_max")))){
                        WHERE(" ti.sn >=#{params_map.sn_min} and ti.sn <=#{params_map.sn_max} ");
                    }
                    if (!"".equals(StringUtils.filterNull(map.get("sn_min"))) && "".equals(StringUtils.filterNull(map.get("sn_max")))){
                        WHERE(" ti.sn =#{params_map.sn_min} ");
                    }
                    if ("".equals(StringUtils.filterNull(map.get("sn_min"))) && !"".equals(StringUtils.filterNull(map.get("sn_max")))){
                        WHERE(" ti.sn =#{params_map.sn_max} ");
                    }
                    //WHERE(" ti.PSAM_NO NOT LIKE 'jh%' ");
                    //排除机具收款码 和统计逻辑保持一致，原则上是 不相等的只有 jh开头的
                    WHERE(" ti.PSAM_NO = ti.SN ");
                    ORDER_BY("ti.create_time asc");
                }
            }.toString();
            return sql;
        }

        public String getSnSendAndRecInfo(Map<String, Object> params_map) {
            @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) params_map.get("params_map");
            //oper_type  必填，筛选栏类型 1-入库  2-出库
            //date_start  格式YYYY-MM-DD,选填, 但是date_start和date_end 必须同时填或者不填
            //date_end  格式YYYY-MM-DD,选填，但是date_start和date_end 必须同时填或者不填
            String sql = new SQL() {
                {
                    SELECT(" id,agent_no,for_operater,oper_num,sn_array,oper_detail_type,oper_type,DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%S') as create_time ");
                    FROM(" terminal_operate ");
                    WHERE(" agent_no=#{params_map.agent_no} and oper_type=#{params_map.oper_type} ");
                    if (!"".equals(StringUtils.filterNull(map.get("date_start"))) || !"".equals(StringUtils.filterNull(map.get("date_end")))) {
                        WHERE(" create_time >=#{params_map.date_start} and create_time <=#{params_map.date_end} ");
                    }
                    ORDER_BY("create_time desc");
                }
            }.toString();
            return sql;
        }

        /*
       下发机具
       */
        public String updateTerToSend(Map<String, Object> params_map) {
            String sql_sn_array = params_map.get("sql_sn_array").toString();
            String sql = "update terminal_info set agent_no=#{agent_no},agent_node=#{agent_node} where sn in (" + sql_sn_array + ") ";
            return sql;
        }

        /*
        回收机具
         */
        public String updateTerToBack(Map<String, Object> params_map) {
            String sql_sn_array = params_map.get("sql_sn_array").toString();
            String sql = "update terminal_info set agent_no=#{agent_no},agent_node=#{agent_node} where sn in (" + sql_sn_array + ") ";
            return sql;
        }
    }
}
