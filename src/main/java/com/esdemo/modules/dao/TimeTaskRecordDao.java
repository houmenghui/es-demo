package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface TimeTaskRecordDao {

    @Select("select * from time_task_record where running_no = #{runningNo} ")
    Map<String,Object> timeTaskRecordByRunningNo(@Param("runningNo") String runningNo);


    @Insert(" INSERT INTO time_task_record(running_no, running_status, create_time, source_system, interface_name)" +
            " VALUES(#{taskRecord.runningNo}, #{taskRecord.runningStatus}, NOW(), #{taskRecord.sourceSystem}," +
            " #{taskRecord.interfaceName})")
    int insertTimeTaskRecord(@Param("taskRecord") Map<String, Object> timeTaksRecord);


    @Update(" UPDATE time_task_record SET running_status = #{newRunningStatus} WHERE running_no = #{runningNo} ")
    int updateTimeTaskRecordStatus(@Param("runningNo") String runningNo, @Param("newRunningStatus") String newRunningStatus);

    @Select("SELECT * FROM time_task_record WHERE interface_name = #{interfaceName} ORDER BY id DESC LIMIT 1")
    @ResultType(Map.class)
    Map<String, Object> getCurrentTaskRecordByInterfaceName(@Param("interfaceName") String interfaceName);
}
