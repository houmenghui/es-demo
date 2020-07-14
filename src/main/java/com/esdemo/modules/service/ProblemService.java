package com.esdemo.modules.service;

import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：问题建议Service
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
public interface ProblemService {

    /**
     * 查询所有问题类型
     *
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getAllProblemType();

    /**
     * 提交问题信息
     *
     * @param params
     * @return
     */
    int insertProblemInfo(Map<String, String> params);
}
