package com.esdemo.modules.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：订单业务层(数据库)
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface OrderService {

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo
     * @return
     */
    Map<String, Object> queryOrderDetailByOrderNo(String orderNo);

    /**
     * 获取订单的代理商分润
     *
     * @param orderNo
     * @param selectProfitFields profits_+对应代理商等级
     * @return
     */
    Map<String, BigDecimal> queryAgentProfitByOrderNo(String orderNo, String[] selectProfitFields);

    /**
     * 根据组织机构id获取组织名称
     *
     * @param teamId
     * @return
     */
    String getTeamNameByTeamId(String teamId);

    /**
     * 根据主组织获取所有子组织
     *
     * @param teamId
     * @return
     */
    List<Map<String, Object>> getEntryTeamByTeamId(String teamId);

    /**
     * 获取卡信息
     *
     * @param accountNo
     * @return
     */
    Map<String, Object> queryCardInfoByAccountNo(String accountNo);
}
