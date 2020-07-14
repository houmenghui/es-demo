package com.esdemo.frame.utils;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 09:44
 */
public class Constants {
    /**
     * 登陆密码解密时需要的密钥
     */
    public static final String LOGIN_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPWZ5xYEswHWydbcnTz08Yr/IgLJZHNeeF5S4gcPvnpL2D1dPRCp5HaXyTwv3jpWdNYubdCa2mRLfZzZ6N9qu9WfOS4mthnIZmK83aGzDP49HHCt6xHyf/w1A4n2G71dVREvbijBEogJCDmkPeWpFUvQwXFb4EkH8Fmf65JQVAwjAgMBAAECgYAW56OFiiqnoUBxqWGArddY/zJM0DtuBwFyyogJ4I4DGc+w6WEojK+h38YEtvIivq1mzC2xpr93WxL77dap/2pE8y1ss2OVN2aPHbSdkGMy/BDQn2USJbtr8CC1DIL1a7NPWWD8dN8yDf3lS0EILin38ZzLkepEyVQS27GigQREAQJBAP9Julqbmba5M4M0YAtsa0l0DCTszEijnPg3A4nychsKWPROovkZlNaksX9/W2rcE+3JmxDBIZI1TvlUCholZNMCQQD2SUIr7JG0CA2J7Hhl632JnSOFZ2wUhILxNjFt1h0TA+PuCoDYPQQRjZ00kCfDfqiod0jxvwp+ElJeBtqHqGlxAkAFDGAzCoCvrFnoblC36Rz2BuV2lXg0t4eTIQNg5vp6rmmz6xot8uOOmxMngk08f72lJid63VbcnVFCfPb2LWchAkEAzr20ZHbT4JKZ+tucPcIuwaQ9OzEUEy0hViat24vPIDU10o7SlbKyhaGhA4y3NG5QWgq4GubJggcTSYbrTtFaoQJBAOZNJRcTrm8AcTv0SBoo8REYXI+CjiwXwVTEJrAxx2Sc4t7zxsYBJTSTrRH9F1bQPEgwtDUnFRrhizZsNrBflOc=";
    public static final String LOGIN_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD1mecWBLMB1snW3J089PGK/yICyWRzXnheUuIHD756S9g9XT0QqeR2l8k8L946VnTWLm3QmtpkS32c2ejfarvVnzkuJrYZyGZivN2hswz+PRxwresR8n/8NQOJ9hu9XVURL24owRKICQg5pD3lqRVL0MFxW+BJB/BZn+uSUFQMIwIDAQAB";
    /**
     * 用于存放loginToken的参数名
     */
    public static final String LOGIN_TOKEN = "LOGIN_TOKEN";
    /**
     * redis key 的前缀
     */
    public static final String REDIS_KEY_PREFIX = "kqAgentApi2";

    public static final String CAPTCHA_REDIS_KEY_PREFIX = "CAPTCHA_REDIS_KEY_";

    public static final String TEAM_ID_999 = "999";

    /*阿里云存储boss附件bucket*/
    public static final String ALIYUN_OSS_ATTCH_TUCKET = "agent-attch";

    /*阿里云存储boss附件临时bucket*/
    public static final String ALIYUN_OSS_TEMP_TUCKET = "boss-temp";

    public static final String ACCOUNT_API_SECURITY = "zouruijin";

    public static final String USER_NO_SEQ = "user_no_seq";

    public static final String USER_VALUE = "1000000000000000000";
    /**
     * 用户登陆相关的redis key
     * agentApi2:userInfo:loginToken = 用户信息
     */
    public static final String REDIS_LOGIN_TOKEN_KEY = REDIS_KEY_PREFIX + ":loginToken:%s";

    /**
     * es 索引
     */
    public static final String NPOSP_ES_INDEX = "kq_nposp_es";
    public static final String NPOSP_ES_TYPE= "_doc";

    /**
     * 登陆loginToken的存活时间
     */
    public static final String SYS_CONFIG_LOGIN_TOKEN_TTL = "agentApi2_login_token_ttl";
    /**
     * 优质商户 本月交易金额>=x元
     * 默认值: 本月交易金额≥50000元
     */
    public static final String SYS_CONFIG_QUALITY_SEARCH_CUR_MONTH_TRANS_MONEY = "agentApi2_merchant_quality_search_cur_month_trans_money";
    /**
     * 活跃商户 近x天交易笔数>=x笔,且交易金额>=x元
     * 默认值: 近30天交易笔数≥2笔并交易金额≥10元
     */
    public static final String SYS_CONFIG_ACTIVE_SEARCH_TRANS_DAY = "agentApi2_merchant_active_search_trans_day";
    public static final String SYS_CONFIG_ACTIVE_SEARCH_TRANS_ORDER_NUM = "agentApi2_merchant_active_search_trans_order_num";
    public static final String SYS_CONFIG_ACTIVE_SEARCH_TRANS_MONEY = "agentApi2_merchant_active_search_trans_money";
    /**
     * 休眠商户 入网≥X天,连续无交易大于X天
     * 默认值:入网≥60天,连续无交易大于60天
     */
    public static final String SYS_CONFIG_SLEEP_SEARCH_MERCHANT_CREATE = "agentApi2_merchant_sleep_search_merchant_create";
    public static final String SYS_CONFIG_SLEEP_SEARCH_TRANS_TIME = "agentApi2_merchant_sleep_search_trans_time";
    /**
     * 商户交易下滑汇总商户的最大数量
     */
    public static final String SYS_CONFIG_MAX_TRANS_SLIDE = "agentApi2_merchant_max_trans_slide";

    public static final String TASK_HMAC_KEY = "YFBTASKMGR20180422" ;
}
