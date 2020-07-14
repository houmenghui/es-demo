package com.esdemo.frame.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.esdemo.frame.bean.AppDeviceInfo;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.bean.ResponseType;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.frame.utils.redis.RedisUtils;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.dao.SysDictDao;
import com.esdemo.modules.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 10:23
 */
@Slf4j
@Component
public class WebUtils {
    private static SysConfigService sysConfigService;

    @Resource
    public void setSysConfigService(SysConfigService sysConfigService) {
        WebUtils.sysConfigService = sysConfigService;
    }

    private static SysDictDao sysDictDao;

    @Resource
    public void setSysDictDao(SysDictDao sysDictDao) {
        WebUtils.sysDictDao = sysDictDao;
    }

    /**
     * 获取设备公共参数
     */
    public static AppDeviceInfo getAppDeviceInfo(HttpServletRequest request) {
        String header = request.getHeader("app-info");
        try {
            return GsonUtils.fromJson2Bean(URLDecoder.decode(header, "utf-8"), AppDeviceInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取请求body中的参数信息
     */
    public static Map<String, String> getReqBodyInfo(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        try (
                BufferedReader reader = request.getReader()
        ) {
            if (null != reader) {
                String str = "";
                StringBuilder wholeStr = new StringBuilder();
                while ((str = reader.readLine()) != null) {
                    //逐行读取body体里面的内容；
                    wholeStr.append(str);
                }
                if (StringUtils.isNotBlank(wholeStr.toString())) {
                    JSONObject paramJson = JSONUtil.parseObj(wholeStr.toString());
                    if (null != paramJson) {
                        params = JSONUtil.toBean(paramJson, Map.class);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取请求{}body信息异常{}", request, e);
        }
        return params;
    }

    /**
     * 根据系统参数名获取对应的值
     *
     * @param paramKey
     * @return
     */
    public static String getSysConfigValueByKey(String paramKey) {
        SysConfigService sysConfigService = SpringHolder.getBean(SysConfigService.class);
        return sysConfigService.getSysConfigValueByKey(paramKey);
    }

    /**
     * 按参数名首字母ascii升序，组装QueryString格式的字符串,空值不参与
     *
     * @param is      空参数是否参与
     * @param params  参数map
     * @param outKeys 不参与签名参数
     * @return String
     */
    public static String buildSignSrc(boolean is, Map params, String... outKeys) {
        //不参与签名参数
        Map<String, String> outKeyMap = new HashMap<>();
        if (null != outKeys && outKeys.length > 0) {
            for (String key : outKeys) {
                outKeyMap.put(key, key);
            }
        }

        TreeMap<String, String> tempMap = new TreeMap<>();
        for (Object key1 : params.keySet()) {
            String key = (String) key1;
            //不参与签名
            if (outKeyMap.get(key) != null) {
                continue;
            }
            // 空参数不参与签名的参数
            Object value = params.get(key);
            String valueStr = StringUtils.filterNull(value);
            if (!is && StringUtils.isBlank(valueStr)) {
                continue;
            }
            tempMap.put(key, valueStr);
        }

        StringBuilder buf = new StringBuilder();
        for (String key : tempMap.keySet()) {
            buf.append(key).append("=").append(tempMap.get(key)).append("&");
        }
        String src = "";
        if (!StringUtils.isBlank(buf.toString())) {
            src = buf.substring(0, buf.length() - 1);
        }
        return src;
    }


    /**
     * 获取请求的真实ip地址
     */
    public static String getRealIp(HttpServletRequest request) {
        try {
            String xforwardedFor = request.getHeader("x-forwarded-for");
            String xRealIp = request.getHeader("X-Real-IP");
            log.info("请求头数据: key = x-forwarded-for, value = {}", xforwardedFor);
            log.info("请求头数据: key = X-Real-IP, value = {}", xRealIp);
            if (StringUtils.isNotBlank(xforwardedFor)) {
                return xforwardedFor.split(",")[0];
            }
            if (StringUtils.isNotBlank(xRealIp)) {
                return xRealIp;
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            log.error("getRemoteAddr ==> " + e);
            return "";
        }
    }

    /**
     * 获取登陆代理商编号
     */
    public static String getLoginAgentNo(HttpServletRequest request) {
        UserInfoBean loginUserInfo = getLoginUserInfoFromRedis(request);
        if (loginUserInfo == null) {
            return null;
        }
        return loginUserInfo.getAgentNo();
    }

    /**
     * 从redis获取登陆信息
     *
     * @param request
     * @return
     */
    public static UserInfoBean getLoginUserInfoFromRedis(HttpServletRequest request) {
        //todo4lvsw 本地测试用
        /*UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setAgentNode("0-108-");
        userInfoBean.setAgentNo("108");
        userInfoBean.setAgentLevel(1L);
        userInfoBean.setAgentOem("110010");
        userInfoBean.setOneLevelId("108");
        userInfoBean.setTeamId(Constants.TEAM_ID_999);
        return userInfoBean;*/
        String loginToken = getLoginToken(request);
        if (StringUtils.isBlank(loginToken)) {
            return null;
        }
        String loginTokenRedisKey = String.format(Constants.REDIS_LOGIN_TOKEN_KEY, loginToken);
        return GsonUtils.fromJson2Bean(RedisUtils.get(loginTokenRedisKey), UserInfoBean.class);
    }


    /**
     * 通过request获取登陆token
     */
    public static String getLoginToken(HttpServletRequest request) {
        // 1. 通过请求参数获取loginToken
        String loginToken = request.getParameter(Constants.LOGIN_TOKEN);
        if (StringUtils.isNotBlank(loginToken)) {
            return loginToken;
        }
        // 2. 通过请求头获取loginToken
        loginToken = request.getHeader(Constants.LOGIN_TOKEN);
        if (StringUtils.isNotBlank(loginToken)) {
            return loginToken;
        }
        // 3. 通过公共参数获取loginToken
        AppDeviceInfo appDeviceInfo = getAppDeviceInfo(request);
        if (appDeviceInfo != null && StringUtils.isNotBlank(appDeviceInfo.getLoginToken())) {
            return appDeviceInfo.getLoginToken();
        }
        // 4. 通过cookie获取loginToken
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> StringUtils.equals(cookie.getName(), Constants.LOGIN_TOKEN))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * 设置响应体返回json数据
     */
    public static void setJsonDataResponse(HttpServletResponse response, ResponseBean responseBean, int stauts) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(stauts);
            IOUtils.write(GsonUtils.toJson(responseBean), response.getOutputStream(), "utf-8");
        } catch (Exception e) {
            log.error(ExceptionUtils.collectExceptionStackMsg(e));
        }
    }

    public static void setJsonDataResponse(HttpServletResponse response, ResponseType type, int stauts) {
        setJsonDataResponse(response, type.getResponseBean(), stauts);
    }

    /**
     * 保存用户到redis
     *
     * @param loginUser 登陆信息
     */
    public static void saveLoginUserInfo2Redis(UserInfoBean loginUser) {
        if (loginUser == null) {
            return;
        }
        // 生成新的登陆token,并将信息保存到redis中
        String newLoginToken = UUID.randomUUID().toString();
        loginUser.setLoginToken(newLoginToken);
        String loginTokenRedisKey = String.format(Constants.REDIS_LOGIN_TOKEN_KEY, newLoginToken);
        Integer loginTokenTtl = sysConfigService.getSysConfigValueByKey(Constants.SYS_CONFIG_LOGIN_TOKEN_TTL, 24 * 3600, Integer::valueOf);
        RedisUtils.set(loginTokenRedisKey, GsonUtils.toJson(loginUser), loginTokenTtl, TimeUnit.SECONDS);
    }

    public static void expireLoginUserInfo(HttpServletRequest request) {
        String loginToken = getLoginToken(request);
        String loginTokenRedisKey = String.format(Constants.REDIS_LOGIN_TOKEN_KEY, loginToken);
        Integer loginTokenTtl = sysConfigService.getSysConfigValueByKey(Constants.SYS_CONFIG_LOGIN_TOKEN_TTL, 24 * 3600, Integer::valueOf);
        RedisUtils.expire(loginTokenRedisKey, loginTokenTtl);
    }

    public static void deleteLoginUserInfoFromRedis(HttpServletRequest request) {
        String loginToken = getLoginToken(request);
        if (StringUtils.isNotBlank(loginToken)) {
            String loginTokenKey = String.format(Constants.REDIS_LOGIN_TOKEN_KEY, loginToken);
            RedisUtils.del(loginTokenKey);
        }
    }

    /**
     * 获取数据字典值
     *
     * @param key 键
     * @return
     */
    public static String getDictValue(String key) {
        if (StringUtils.isNotBlank(key)) {
            Map<String, Object> result = sysDictDao.getDictValue(key);
            return StringUtils.filterNull(result.get("sysValue"));
        }
        return null;
    }

    /**
     * 获取数据字典值集合
     *
     * @param key 键
     * @return
     */
    public static List<Map<String, Object>> getDictValues(String key) {
        if (StringUtils.isNotBlank(key)) {
            return sysDictDao.getDictValues(key);
        }
        return null;
    }

    /**
     * 获取字典值对应的名称
     *
     * @param key       键
     * @param key_value 键值
     * @return
     */
    public static String getDictSysName(String key, String key_value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(key_value)) {
            return sysDictDao.getDictSysName(key, key_value);
        }
        return null;
    }

    /**
     * 获取当前线程里面的request对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    /**
     * 获取当前线程里面的response对象
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

    /**
     * 文件上传
     *
     * @param request
     * @return
     */
    public static Map<String, Object> uplodFiles(HttpServletRequest request) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("status", false);
        try {
            if (request instanceof MultipartRequest) {
                List<String> str = new ArrayList<>();
                MultipartRequest qq = (MultipartRequest) request;

                //区分安卓和IOS，传递方式不一样
                MultiValueMap<String, MultipartFile> multiFileMap = qq.getMultiFileMap();
                List<MultipartFile> fileList = multiFileMap.get("file");
                //安卓
                if (!CollectionUtils.isEmpty(fileList)) {
                    for (MultipartFile file : fileList) {
                        String fileOriginalName = file.getOriginalFilename();
                        if (fileOriginalName != null && !fileOriginalName.equals("")) {
                            Date date = new Date();
                            int random = new Random().nextInt(100000);
                            String fileName = RandomUtil.randomString(4) + "_" + date.getTime() + "_" + random + ".jpg";//时间戳+文件后缀名
                            ALiYunOssUtil.saveFile(Constants.ALIYUN_OSS_ATTCH_TUCKET, fileName, file.getInputStream());
                            System.out.println(ALiYunOssUtil.genUrl("agent-attch", fileName, new Date(date.getTime() + 100000)));
                            str.add(fileName);
                        }
                    }

                } else {
                    //IOS
                    Map<String, MultipartFile> maps = qq.getFileMap();
                    for (String key : maps.keySet()) {
                        String fileOriginalName = maps.get(key).getOriginalFilename();
                        if (fileOriginalName != null && !fileOriginalName.equals("")) {
                            Date date = new Date();
                            int random = new Random().nextInt(100000);
                            String fileName = key.substring(0, key.length() - 4) + "_" + date.getTime() + "_" + random + ".jpg";//时间戳+文件后缀名
                            ALiYunOssUtil.saveFile(Constants.ALIYUN_OSS_ATTCH_TUCKET, fileName, maps.get(key).getInputStream());
                            System.out.println(ALiYunOssUtil.genUrl("agent-attch", fileName, new Date(date.getTime() + 100000)));
                            str.add(fileName);
                        }
                    }
                }
                msg.put("str", str);
                msg.put("status", true);
            }
        } catch (Exception e) {
            msg.put("msg", "图片上传失败");
            log.error("上传图片失败!", e);
        }
        return msg;
    }
}
