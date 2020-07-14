package com.esdemo.modules.service;

import java.util.Map;

public interface TaskRecordService {

    Map<String,Object> timeTaskRecordByRunningNo(String runningNo);

    Map<String,Object> getCurrentTaskRecordByInterfaceName(String interfaseName);

    int insertTimeTaskRecord(Map<String,Object > timeTaksRecord);

    int updateTimeTaskRecordStatus(String runningNo,String status);

    void taskRunningCallback(String interfaceName);
}
