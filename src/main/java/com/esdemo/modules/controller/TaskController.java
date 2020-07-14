package com.esdemo.modules.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import com.esdemo.frame.annotation.LoginValid;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.TaskRecordStatusEnum;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.service.TaskRecordService;
import com.esdemo.modules.task.CollectHpbDayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 外部定时任务处理
 *
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/7/11 10:42
 * @Version：1.0
 */
@RestController
@Slf4j
@RequestMapping(value = "/timeTask")
public class TaskController {


    @Autowired
    private TaskRecordService taskRecordService;

    /**
     * 定时任务系统调度批处理系统任务业务处理
     *
     * @param className
     * @param methodName
     * @param params
     * @return
     */
    @SignValidate(needSign = false)
    @LoginValid(needLogin = false)
    @RequestMapping(value = "/{className}/{methodName}")
    public JSONObject handleTaskDispatch(@PathVariable("className") String className,
                                         @PathVariable("methodName") String methodName,
                                         @RequestParam Map<String, String> params) {
        JSONObject resJson = new JSONObject();

        String runningNo = params.get("runningNo");
        String hmac = params.get("hmac");
        if (StringUtils.isBlank(className, methodName, runningNo, hmac)) {
            throw new RuntimeException("定时任务系统调度批处理定时业务处理必要参数有空");
        }
        //验签
        String newHmac = SecureUtil.md5(runningNo + Constants.TASK_HMAC_KEY);
        if (!hmac.equals(newHmac)) {
            throw new RuntimeException("定时任务系统调度批处理定时业务处理签名异常");
        }
        //交易运行编号是否已存在，已存在直接返回状态和编号，否则新建
        Map<String ,Object> taskRecord = taskRecordService.timeTaskRecordByRunningNo(runningNo);
        if (null != taskRecord) {
            resJson.put("runningNo", runningNo);
            resJson.put("runningStatus", taskRecord.get("running_status"));
            return resJson;
        }

        String collectDayTimeStr = params.get("collectDayTime");
        String collectDayNumStr = params.get("collectDayNum");
        Integer collectDayNum = null;
        if (StringUtils.isNotBlank(collectDayNumStr)) {
            try {
                collectDayNum = Integer.parseInt(collectDayNumStr);
            } catch (Exception e) {
                collectDayNumStr = null;
            }
        }

        if (StringUtils.isNotBlank(collectDayTimeStr)) {
            try {
                Date collectDayTime = DateUtil.parse(collectDayTimeStr, "yyyy-MM-dd");
                if (collectDayTime.after(DateUtil.beginOfDay(new Date()))) {
                    collectDayNumStr = null;
                }
            } catch (Exception e) {
                collectDayTimeStr = null;
            }
        }

        //反射校验类名和方法
        Object clazzInstance = null;
        Class clazz = null;
        //获取定时任务包名称
        String packageName = CollectHpbDayTask.class.getPackage().getName() + ".";
        try {
            clazz = Class.forName(packageName + className);
            Constructor declaredConstructor = clazz.getDeclaredConstructor(Object.class, Object.class);
            clazzInstance = declaredConstructor.newInstance(collectDayTimeStr, collectDayNum);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("定时任务系统调度批处理定时业务处理" + className + "类名异常");
        }
        String interfaceName = packageName + className + "." + methodName;
        clazz = clazzInstance.getClass();
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("定时任务系统调度批处理定时业务处理" + interfaceName + "方法名异常");
        }
        if (method == null) {
            throw new RuntimeException("定时任务系统调度批处理定时业务处理" + interfaceName + "方法名不存在");
        }
        //新增运行记录
        taskRecord =  new HashMap<>();
        taskRecord.put("runningNo",runningNo);
        taskRecord.put("runningStatus",TaskRecordStatusEnum.INIT.getCode());
        taskRecord.put("sourceSystem","kqagentapi2");
        taskRecord.put("interfaceName",interfaceName);
        int saveCount = taskRecordService.insertTimeTaskRecord(taskRecord);
        log.info("定时任务系统调度批处理定时业务处理【{}】新增运行记录影响的行数【{}】", interfaceName, saveCount);
        if (saveCount < 1) {
            throw new RuntimeException("定时任务系统调度批处理定时业务处理" + interfaceName + "新增运行记录失败");
        }
        final Method finalMethod = method;
        final Object finalClazzInstance = clazzInstance;

        //新开线程进行调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    finalMethod.invoke(finalClazzInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("定时任务系统调度批处理定时业务处理反射调用" + packageName + className + "." + methodName + "异常");
                }
            }
        }).start();

        resJson.put("runningNo", runningNo);
        resJson.put("runningStatus", TaskRecordStatusEnum.RUNNING.getCode());

        return resJson;
    }

    /**
     * 手动触发
     *
     * @param className
     * @param methodName
     * @param params
     * @return
     */
    @RequestMapping(value = "/manual/{className}/{methodName}")
    @LoginValid(needLogin = false)
    @SignValidate(needSign = false)
    public ResponseBean manualTrigger(@PathVariable("className") String className,
                                      @PathVariable("methodName") String methodName,
                                      @RequestParam Map<String, String> params) {

        String collectDayTimeStr = params.get("collectDayTime");
        String collectDayNumStr = params.get("collectDayNum");
        Integer collectDayNum = null;
        if (StringUtils.isNotBlank(collectDayNumStr)) {
            try {
                collectDayNum = Integer.parseInt(collectDayNumStr);
            } catch (Exception e) {
                collectDayNumStr = null;
            }

        }
        if (StringUtils.isNotBlank(collectDayTimeStr)) {
            try {
                Date collectDayTime = DateUtil.parse(collectDayTimeStr, "yyyy-MM-dd");
                if (collectDayTime.after(DateUtil.beginOfDay(new Date()))) {
                    collectDayNumStr = null;
                }
            } catch (Exception e) {
                collectDayTimeStr = null;
            }
        }
        //反射校验类名和方法
        Object clazzInstance = null;
        Class clazz = null;
        //获取定时任务包名称
        String packageName = CollectHpbDayTask.class.getPackage().getName() + ".";
        try {
            clazz = Class.forName(packageName + className);
            Constructor declaredConstructor = clazz.getDeclaredConstructor(Object.class, Object.class);
            clazzInstance = declaredConstructor.newInstance(collectDayTimeStr, collectDayNum);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("定时任务系统调度批处理定时业务处理" + className + "类名异常");
        }
        String interfaceName = packageName + className + "." + methodName;
        clazz = clazzInstance.getClass();
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("定时任务系统调度批处理定时业务处理" + interfaceName + "方法名异常");
        }
        if (method == null) {
            throw new RuntimeException("定时任务系统调度批处理定时业务处理" + interfaceName + "方法名不存在");
        }

        final Method finalMethod = method;
        final Object finalClazzInstance = clazzInstance;

        //新开线程进行调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    finalMethod.invoke(finalClazzInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("定时任务系统调度批处理定时业务处理反射调用" + packageName + className + "." + methodName + "异常");
                }
            }
        }).start();

        return ResponseBean.success();
    }

}
