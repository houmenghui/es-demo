package com.esdemo.frame.aop;

import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.md5.Md5;
import com.esdemo.modules.dao.SysDictDao;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-09-03 08:51
 */
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@Component
@Slf4j
public class CacheAspect {

    @Resource
    SysDictDao sysDictDao;

    @Pointcut("@annotation(com.esdemo.frame.annotation.CacheData)")
    public void cache() {
    }

    @Resource
    private RedisTemplate redisTemplate;

    @Around("cache() && @annotation(cacheData)")
    public Object methodAroud(ProceedingJoinPoint point, CacheData cacheData) {
        String key = generateKey(point);

        Object redisResult = redisTemplate.opsForValue().get(key);
        if (redisResult != null) {
            log.info("缓存 key = {} 命中,直接返回缓存数据", key);
            return redisResult;
        }
        try {
            log.info("缓存 key = {} 不命中,调用接口查询", key);
            Object result = point.proceed();
            if (result != null) {
                long ttl = getTTL(cacheData);
                log.info("缓存 key = {} 缓存数据, 缓存时间 {} s", key, ttl);
                redisTemplate.opsForValue().set(key, result, ttl, TimeUnit.SECONDS);
            }
            return result;
        } catch (Throwable throwable) {
            if (redisResult != null) {
                log.info("缓存 key = {} 异常,清除缓存数据", key);
                redisTemplate.delete(key);
            }
            log.error("缓存 key = {} 异常, 异常信息 e : {}", key, throwable);
        }
        return null;
    }

    private long getTTL(CacheData cacheData) {
        int ttl = 120;
        switch (cacheData.type()) {
            case TTL:
                int configTTL = getConfigTTL();
                ttl = configTTL > 0 ? configTTL : cacheData.ttl();
                break;
            case ALL_DAY:
                ttl = getRemainSecondsOneDay(new Date());
                break;
            default:
                break;
        }
        return ttl;
    }

    public int getConfigTTL() {
        String sysTtl = sysDictDao.getDictSysValue("AGENT2_REDIS_TTL");
        if (StringUtils.isBlank(sysTtl)) {
            return -1;
        }
        try {
            return Integer.parseInt(sysTtl);
        }catch (Exception e){
            return -1;
        }
    }

    public static Integer getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }

    private String generateKey(ProceedingJoinPoint point) {
        String declaringTypeName = point.getTarget().getClass().getSimpleName();
        declaringTypeName = declaringTypeName.replaceAll("\\.", "_");
        String name = point.getSignature().getName();
        String argsMd5 = Md5.md5Str(GsonUtils.toJson(((MethodInvocationProceedingJoinPoint) point).getArgs()));
        return String.format("agentApi2:%s:%s:%s", declaringTypeName, name, argsMd5);
    }

}
