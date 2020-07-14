package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 09:53
 */
@Mapper
public interface AccessDao {

    int canAccessTheMerchant(@Param("agentNode") String agentNode,
                             @Param("merchantNo") String merchantNo,
                             @Param("isOwn") boolean isOwn);

    int canAccessTheAgent(@Param("agentNode") String agentNode,
                              @Param("agentNo")  String agentNo);

    List<String> getRepayMerNoByV2MerKey(@Param("v2MerKey")String v2MerKey, @Param("agentNode")String currAgentNode, @Param("isOwn")boolean isOwn);

    int canAccessTheMerchantWithKey(@Param("agentNode") String agentNode, @Param("merchantKey") String merchantKey, @Param("isOwn") boolean isOwn);
}
