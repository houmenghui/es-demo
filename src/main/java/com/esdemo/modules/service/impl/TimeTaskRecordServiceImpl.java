package com.esdemo.modules.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.TaskRecordStatusEnum;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.dao.TimeTaskRecordDao;
import com.esdemo.modules.service.SysConfigService;
import com.esdemo.modules.service.TaskRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TimeTaskRecordServiceImpl implements TaskRecordService {

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private TimeTaskRecordDao timeTaskRecordDao;

    @Override
    public Map<String, Object> timeTaskRecordByRunningNo(String runningNo) {

        return timeTaskRecordDao.timeTaskRecordByRunningNo(runningNo);
    }

    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public int insertTimeTaskRecord(Map<String, Object> timeTaksRecord) {
        return timeTaskRecordDao.insertTimeTaskRecord(timeTaksRecord);
    }

    @Override
    public int updateTimeTaskRecordStatus(String runningNo, String status) {
        return timeTaskRecordDao.updateTimeTaskRecordStatus(runningNo,status);
    }

    /**
     * 任务回调处理
     *
     * @param interfaceName
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public void taskRunningCallback(String interfaceName) {

        Map<String,Object> taskRecord = getCurrentTaskRecordByInterfaceName(interfaceName);
        log.info("任务运行记录接口名【{}】对应的记录对象【{}】", interfaceName, JSONUtil.toJsonStr(taskRecord));
        if (null == taskRecord) {
            return;
        }
        String runningNo = StringUtils.filterNull(taskRecord.get("running_no"));
        String oldRunningStatus = StringUtils.filterNull(taskRecord.get("running_status"));
        //修改状态为完成
        int count = updateTimeTaskRecordStatus(runningNo, TaskRecordStatusEnum.COMPLETE.getCode());
        log.info("任务运行记录接口名【{}】对应的记录编号【{}】状态改为完成影响的行数【{}】", interfaceName, runningNo, count);
        if (count < 1) {
            return;
        }
        //发起回调

        String backUrl = sysConfigService.getSysConfigValueByKey("TASK_SYSTEM_URL");
        backUrl = StringUtils.isBlank(backUrl) ? "http://taskmgr.pay-world.cn/task/taskCalllBack" : backUrl;

        String hmac = SecureUtil.md5(runningNo + TaskRecordStatusEnum.COMPLETE.getCode() + Constants.TASK_HMAC_KEY);
        Map<String, Object> params = new HashMap<>();
        params.put("runningNo", runningNo);
        params.put("runningStatus", TaskRecordStatusEnum.COMPLETE.getCode());
        params.put("hmac", hmac);
        HttpUtil.post(backUrl,params);
    }

    public Map<String,Object> getCurrentTaskRecordByInterfaceName(String interfaceName) {
        return  timeTaskRecordDao.getCurrentTaskRecordByInterfaceName(interfaceName);
    }
}
