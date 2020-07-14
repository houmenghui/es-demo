package com.esdemo.modules.service;

import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:08
 */
public interface TestService {

    Map<String, Object> getAgentByWrite(String agentNo);

    Map<String, Object> getAgentByRead(String agentNo);

    List<Map<String, Object>> getAgent(String agentNo);

    List<Map<String, Object>> getAgent2(String agentNo);
}
