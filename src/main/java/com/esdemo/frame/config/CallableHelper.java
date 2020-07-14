package com.esdemo.frame.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @Title：doBatch
 * @Description：跑批多线程辅助处理类
 * @Author：zhangly
 * @Date：2019/4/10 17:44
 * @Version：1.0
 */
@Slf4j
public class CallableHelper {

    /**
     * 多线程处理批量数据
     *
     * @param callable       实现Callable的类
     * @param totalList      批量处理的总数据集合
     * @param paramClasses   实现Callable的类的参数类型集合，第一个参数类型必须是子集合
     * @param otherParamList 实现Callable的类的参数集合，排除第一个子集合参数之外的参数集合
     * @param resList        返回数据
     * @param <T>
     * @throws Exception
     */
    public static <T> void executeCallable(Callable callable, List<T> totalList, Class[] paramClasses, Object[] otherParamList, List resList) throws Exception {
        int num = totalList.size();
        int threadNum = 1;
        if (num >= 10 && num < 100) threadNum = 3;
        if (num >= 100 && num < 1000) threadNum = 5;
        if (num >= 1000 && num < 5000) threadNum = 10;
        if (num >= 5000) threadNum = 20;

        resList = CollectionUtil.isEmpty(resList) ? new ArrayList<>() : resList;

        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(100,
                new BasicThreadFactory.Builder().namingPattern("custom-schedule-pool-%d").daemon(true).build());

        CompletionService<List> cs = ThreadUtil.newCompletionService(executor);
        //获取带参的构造函数
        Class<? extends Callable> callableClass = callable.getClass();
        Constructor<? extends Callable> declaredConstructor = callableClass.getDeclaredConstructor(paramClasses);
        //多线程执行任务
        int eachThreadNum = num / threadNum;
        List<T> childrenList;
        List<Object> paramList = new ArrayList<>();
        for (int threadIndex = 0; threadIndex < threadNum; threadIndex++) {
            if (threadIndex == threadNum - 1) {
                childrenList = totalList.subList(threadIndex * eachThreadNum, num);
            } else {
                childrenList = totalList.subList(threadIndex * eachThreadNum, threadIndex * eachThreadNum + eachThreadNum);
            }
            paramList = new ArrayList<>();
            paramList.add(childrenList);
            if (null != otherParamList && otherParamList.length > 0) {
                paramList.addAll(Arrays.asList(otherParamList));
            }
            cs.submit(declaredConstructor.newInstance(paramList.toArray()));
        }
        //获得线程处理结果
        for (int threadIndex = 0; threadIndex < threadNum; threadIndex++) {
            try {
                resList.addAll(cs.take().get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 所有任务已经完成,关闭线程池
        executor.shutdown();
    }
}
