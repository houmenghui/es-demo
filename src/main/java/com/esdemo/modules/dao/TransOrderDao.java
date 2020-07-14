package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：交易订单数据层
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
@Mapper
public interface TransOrderDao {

    List<Map<String, Object>> queryTransOrderByParams(@Param("params") Map<String, Object> params);

    Map<String, BigDecimal> queryAgentProfitByOrderNo(@Param("orderNo") String orderNo, @Param("selectProfitFields") String[] selectProfitFields);

    Map<String, Object> queryCardInfoByAccountNo(@Param("accountNo") String accountNo);
}
