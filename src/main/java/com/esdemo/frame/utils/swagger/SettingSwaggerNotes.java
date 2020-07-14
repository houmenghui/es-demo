package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：设置接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class SettingSwaggerNotes {

    public static final String SEEK_LOGIN_PWD_FIRST = "找回登录密码第一步-验证短信验证码\n" +
            "- 请求参数\n" +
            "    - mobileNo: 手机号，必传，位于请求body中\n" +
            "    - verifyCode: 短信验证码，必传，位于请求body中\n\n" +
            "- 返回参数\n" +
            "   - mobileNo：在下一步需要上传\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"mobileNo\": \"18603049008\"\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String SEEK_LOGIN_PWD_SECOND = "找回登录密码第二步-设置登录密码\n" +
            "- 请求参数\n" +
            "    - mobileNo: 手机号，必传，位于请求body中\n" +
            "    - newLoginPwd: 新登录密码，密文，必传，位于请求body中\n" +
            "    - newConfirmLoginPwd: 确认新登录密码，密文，必传，位于请求body中\n\n";

    public static final String UPDATE_LOGIN_PWD = "修改登录密码\n" +
            "- 请求参数\n" +
            "    - oldLoginPwd: 旧登录密码，密文，必传，位于请求body中\n" +
            "    - newLoginPwd: 新登录密码，密文，必传，位于请求body中\n" +
            "    - newConfirmLoginPwd: 确认新登录密码，密文，必传，位于请求body中\n\n";

    public static final String LOAD_CAPTCHA = "获取图片验证码\n\n" +
            "- 请求参数\n" +
            "    - uuid: 生成一个不唯一字符串传给后台，必传，位于请求body中\n" +
            "- 返回信息：以流的方式输入验证码，客户端直接显示即可，不需要解析";

    public static final String SEND_SMS_VALIDATE_CODE = "发送短信验证码\n" +
            "- 请求参数\n" +
            "    - needCaptcha: 是否需要验证图形验证码，”字符串true/false“，非必传，默认”false“，位于请求body中\n" +
            "    - captcha: needCaptcha=”true“时的图形验证码，非必传，位于请求body中\n" +
            "    - mobileNo: 接收短信验证码的手机号，必传，位于请求body中\n" +
            "    - templateCode: 发送手机短信的业务类型，必传，位于请求body中\n" +
            "           - SEEK_LOGIN_PWD: 找回登录密码，\n" +
            "           - SET_SAFE_PHONE: 设置安全手机，\n" +
            "           - SET_SAFE_PASSWORD: 设置资金密码，\n" +
            "           - COMPLETE_SAFE_PHONE: 安全手机设置成功后系统自动发送的短信，\n" +
            "           - ......\n\n";

    public static final String OPERATE_SAFE_PHONE = "操作安全手机\n" +
            "- 请求参数\n" +
            "    - operateType: 操作类型，必传，位于请求body中\n" +
            "           - SET：第一次设置安全手机\n" +
            "           - UPDATE_FIRST：修改安全手机第一步\n" +
            "           - UPDATE_SECOND：修改安全手机第二步\n" +
            "    - mobileNo: 接收短信验证码的安全手机号，必传，位于请求body中\n" +
            "    - verifyCode: 短信验证码，必传，位于请求body中\n\n\n" +
            "- 操作完成后请求一下公共数据下发接口，判断最新的是否设置安全手机状态";

    public static final String OPERATE_SAFE_PASSWORD = "操作资金密码\n" +
            "- 请求参数\n" +
            "    - operateType: 操作类型，必传，位于请求body中\n" +
            "           - SET：第一次资金密码\n" +
            "           - UPDATE：修改资金密码\n" +
            "    - confirmed: SET的时候，confirmed=“confirmed”表示是一连串的动作，设置好安全手机后，再设置资金密码就不需要短信验证码的校验了，如果不等于“confirmed”，且是SET的时候，则必须校验手机验证码，非必传，位于请求body中\n" +
            "    - newSafePassword: 新资金密码，必传，位于请求body中\n" +
            "    - oldSafePassword: 如果是修改，则必传，原资金密码，位于请求body中\n" +
            "    - mobileNo: 如果是第一次设置，且根据confirm的逻辑判断，非必传，安全手机号，位于请求body中\n" +
            "    - verifyCode: 如果是第一次设置，且根据confirm的逻辑判断，非必传，安全手机号收到的短信验证码，位于请求body中\n\n\n" +
            "- 操作完成后请求一下公共数据下发接口，判断最新的是否设置资金密码状态";

    public static final String GET_PROBLEM_TYPE_LIST = "获取问题类型\n" +
            "- 返回参数\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"problemTypeList\": [\n" +
            "            {\n" +
            "                \"type\": \"4\",\n" +
            "                \"name\": \"功能异常\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"type\": \"5\",\n" +
            "                \"name\": \"分润结算\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"type\": \"6\",\n" +
            "                \"name\": \"产品建议\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"type\": \"7\",\n" +
            "                \"name\": \"投诉\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"complainterList\": [\n" +
            "            {\n" +
            "                \"type\": \"1\",\n" +
            "                \"name\": \"对客户服务不满意\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"type\": \"2\",\n" +
            "                \"name\": \"对上级代理不满意\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"type\": \"3\",\n" +
            "                \"name\": \"其他\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String SUBMIT_FEEDBACK = "提交意见反馈\n" +
            "- 不需要签名\n\n" +
            "- 请求参数\n" +
            "    - problemType: 问题类型，必传，位于请求body中\n" +
            "    - complainterType: 投诉类型，非必传，位于请求body中\n" +
            "    - content: 输入内容，必传，位于请求body中\n" +
            "    - 上传图片的字节流，非必传\n" +
            "    - mobileNo: 手机号，必传，位于请求body中\n\n";

    public static final String FORGET_MONEY_PWD = "忘记资金密码\n" +
            "- 请求参数\n" +
            "    - newSafePassword: 新资金密码，密文，加密规则同登录密码，必传，位于请求body中\n\n" +
            "    - mobileNo: 必填，安全手机号，位于请求body中\n" +
            "    - verifyCode: 必填，安全手机号收到的短信验证码，位于请求body中\n\n\n";

}
