package com.esdemo.modules.service.impl;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.dao.MerchantDao;
import com.esdemo.modules.dao.TransOrderDao;
import com.esdemo.modules.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：订单业务层实现(数据库)
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private TransOrderDao transOrderDao;
    @Resource
    private MerchantDao merchantDao;

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo
     * @return
     */
    @Override
    public Map<String, Object> queryOrderDetailByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        List<Map<String, Object>> orders = transOrderDao.queryTransOrderByParams(params);
        return CollectionUtils.isEmpty(orders) ? null : orders.get(0);
    }

    /**
     * 获取订单的代理商分润
     *
     * @param orderNo
     * @param selectProfitFields profits_+对应代理商等级
     * @return
     */
    @Override
    public Map<String, BigDecimal> queryAgentProfitByOrderNo(String orderNo, String[] selectProfitFields) {
        if (null == selectProfitFields || selectProfitFields.length == 0) {
            return null;
        }
        return transOrderDao.queryAgentProfitByOrderNo(orderNo, selectProfitFields);
    }

    /**
     * 根据组织机构id获取组织名称
     *
     * @param teamId
     * @return
     */
    @Override
    public String getTeamNameByTeamId(String teamId) {

        return StringUtils.isBlank(teamId) ? "" : merchantDao.getTeamNameByTeamId(teamId);
    }


    /**
     * 根据主组织获取所有子组织
     *
     * @param teamId
     * @return
     */
    @Override
    public List<Map<String, Object>> getEntryTeamByTeamId(String teamId) {

        return StringUtils.isBlank(teamId) ? new ArrayList<>() : merchantDao.getEntryTeamByTeamId(teamId);
    }

    /**
     * 获取卡信息
     *
     * @param accountNo
     * @return
     */
    @Override
    public Map<String, Object> queryCardInfoByAccountNo(String accountNo) {
        return StringUtils.isBlank(accountNo) ? new HashMap<>() : transOrderDao.queryCardInfoByAccountNo(accountNo);
    }
}
