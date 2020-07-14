package com.esdemo.frame.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 给手机号打码
     *
     * @param mobilePhone 手机号
     */
    public static String mask4MobilePhone(String mobilePhone) {
        return Optional.ofNullable(mobilePhone)
                .orElse("")
                .replaceAll("(.{3})(.*)(.{4})", "$1****$3");
    }

    /**
     * 字符串左补0
     */
    public static String stringFillLeftZero(String str, int len) {
        if (str.length() < len) {
            StringBuffer sb = new StringBuffer(len);
            for (int i = 0; i < len - str.length(); i++)
                sb.append('0');
            sb.append(str);
            return new String(sb);
        } else
            return str;
    }

    /**
     * 功能：不定长参数,其中一个参数为null或空则返回true,负责返回false
     *
     * @param str
     * @return boolean
     */
    public static boolean isEmpty(String... str) {
        for (String s : str) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(s) || "null".equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param str
     * @return boolean
     */
    public static boolean isNotEmpty(String... str) {
        return !isEmpty(str);
    }


    /**
     * 功能：不定长参数,其中一个参数为null或空或为空格字符串则返回true,负责返回false
     *
     * @param str
     * @return boolean
     */
    public static boolean isBlank(String... str) {
        for (String s : str) {
            if (org.apache.commons.lang3.StringUtils.isBlank(s) || "null".equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    /**
     * @param str
     * @return boolean
     */
    public static boolean isNotBlank(String... str) {
        return !isBlank(str);
    }

    /**
     * 功能：判断字符串是否是数值. 默认允许有正负号,默认允许有小数点
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        boolean sign = true;
        int point_bef = Integer.MAX_VALUE;// 小数点前有几位
        int point_aft = Integer.MAX_VALUE;// 小数点后有几位
        return isNumeric(str, sign, point_bef, point_aft);
    }

    /**
     * 功能：判断字符串是否是数值
     *
     * @param str
     * @param sign  是否允许有正负号
     * @param point 是否允许有小数点
     * @return
     */
    public static boolean isNumeric(String str, boolean sign, boolean point) {
        int point_bef = Integer.MAX_VALUE;// 小数点前有几位
        int point_aft = Integer.MAX_VALUE;// 小数点后有几位
        if (!point)
            point_aft = 0;

        return isNumeric(str, sign, point_bef, point_aft);
    }

    /**
     * 功能：判断字符串是否是数值
     *
     * @param str
     * @param sign      是否允许有正负号
     * @param point_bef 精度,小数点前有几位
     * @param point_aft 精度,小数点后有几位,如果为0,则为整数
     * @return
     */
    public static boolean isNumeric(String str, boolean sign, int point_bef,
                                    int point_aft) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        boolean point = true;// 是否允许小数点
        if (point_aft == 0) {
            point = false;// 不允许有小数点
        } else {
            point = true;
        }
        StringBuffer pat = new StringBuffer();
        if (sign) {
            pat.append("[+|-]?");
        }
        if (point_bef == 0) {
            pat.append("[0]");
        } else {
            pat.append("[0-9]{1,");
            pat.append(point_bef);
            pat.append("}");
        }
        if (point && str.indexOf(".") != -1) {// 允许小数点,并且有小数点
            pat.append("[.]");
            pat.append("[0-9]{1,");// 小数点后必须有一位
            pat.append(point_aft);
            pat.append("}");
        }
        Pattern pattern = Pattern.compile(pat.toString());
        if (!pattern.matcher(str).matches()) {
            return false;
        } else {// 排除如00.1,返回false
            if (str.indexOf(".") != -1
                    && str.substring(0, str.indexOf(".")).length() > 1
                    && Integer.valueOf(str.substring(0, str.indexOf("."))) == 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 功能：查看字符串是否有这个子字符串
     *
     * @param str    主字符串
     * @param substr 字字符串
     * @return
     */
    public static boolean hasSubstring(String str, String substr) {
        if (str == null || substr == null)
            return false;
        int strLen = str.length();
        int substrLen = substr.length();
        for (int i = 0; (i + substrLen) <= strLen; i++) {
            if (str.substring(i, i + substrLen).equalsIgnoreCase(substr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 功能：验证是否是正确的手机号
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (isBlank(mobile) || mobile.length() != 11) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        if (!pattern.matcher(mobile).matches()) {
            return false;
        }
        return true;
    }

    /**
     * 验证邮件是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String regEx = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 功能：字符串不以"/"结尾，则在串尾加"/"
     *
     * @param s
     * @return
     */
    public static String addSlashInEnd(String s) {
        if (s != null) {
            s = s.trim();
            if (!s.endsWith("/")) {
                s = s + "/";
            }
        } else {
            s = "";
        }
        return s;
    }

    /**
     * 功能：字符串不以"/"结尾，则在串尾加"/";字符串如果以/开头,则去掉第一个/
     *
     * @return
     */
    public static String dealSlash(String s) {
        if (s != null) {
            s = s.trim();
            if (!s.endsWith("/")) {
                s = s + "/";
            }
            if (s.startsWith("/")) {
                s = s.substring(1, s.length());
            }
        } else {
            s = "";
        }
        return s;

    }

    /**
     * 功能：传入一个数字类型的参数，返回一个小数点后两位的小数
     *
     * @param parm
     */
    public static String ConverDouble(String parm) {
        if (isNumeric(parm, false, true)) {
            if (parm.indexOf(".") >= 0) {
                String value = parm.substring(parm.indexOf(".") + 1);
                if (value.length() == 1) {
                    return parm + "0";
                } else if (value.length() > 2) {
                    return parm.substring(0, parm.indexOf(".") + 1)
                            + value.substring(0, 2);
                } else {
                    return parm;
                }
            } else {
                return parm + ".00";
            }
        }
        return null;
    }


    /**
     * @param obj
     * @return String
     * @obj==null,或obj是空字符串，就返回参数ifEmptyThen，否则返回obj.toString。
     */

    public static String ifEmptyThen(Object obj, String ifEmptyThen) {
        String ret = "";
        if (obj == null || String.valueOf(obj) == "") {
            ret = ifEmptyThen;
        } else {
            ret = obj.toString();
        }
        return ret;
    }

    /**
     * 功能：传入一个对象，如果为null，则输出为"",如果不为null,就调用toString()方法
     *
     * @param s
     */
    public static String filterNull(Object s) {
        if (s == null) {
            return "";
        } else {
            return s.toString();
        }
    }


    /**
     * 字符串左补空格
     *
     * @throws UnsupportedEncodingException
     */
    public static String stringFillLeftSpace(String str, int len) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer(len);
        if (!isEmpty(str)) {
            int strLen = str.getBytes("GBK").length;
            if (strLen < len) {
                for (int i = 0; i < len - strLen; i++)
                    sb.append(" ");
                sb.append(str);
                return new String(sb);
            } else
                return str;
        } else {
            for (int i = 0; i < len; i++)
                sb.append(" ");
            return new String(sb);
        }
    }

    /**
     * 字符串右补空格
     *
     * @throws UnsupportedEncodingException
     */
    public static String stringFillRightSpace(String str, int len) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer(len);
        if (!isEmpty(str)) {
            int strLen = str.getBytes("GBK").length;
            if (strLen < len) {
                sb.append(str);
                for (int i = 0; i < len - strLen; i++)
                    sb.append(" ");
                return new String(sb);
            } else
                return str;
        } else {
            for (int i = 0; i < len; i++)
                sb.append(" ");
            return new String(sb);
        }
    }

    /**
     * <h1>校验参数长度</h1>
     *
     * @param minLength 最小长度
     * @param value     需要校验的值
     * @param maxLength 最大参数
     * @return boolean 校验值于最小以及最大之间则成立 否则不成立
     * @see : 参数顺序 minLength 最小长度、value 需要校验的值、maxLength 最大参数
     * <br><h1>校验公式： value.length() > minLength && value.length() < maxLength</h1>
     */
    public static boolean checkLength(int minLength, String value, int maxLength) {
        return value.length() > minLength && value.length() < maxLength ? true : false;
    }

    /**
     * trim全角空格
     *
     * @param str
     * @return
     */
    public static String trimChinese(String str) {
        return str.replace("　", "").trim();
    }

    /**
     * 功能：判断一个字符串是否包含特殊字符
     *
     * @param string 要判断的字符串
     * @return false 提供的参数string包含特殊字符
     */
    public static boolean isConSpeCharacters(String string) {
        // TODO Auto-generated method stub
        if (string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
            //如果不包含特殊字符
            return true;
        }
        return false;
    }


    // 求两个字符串数组的并集，利用set的元素唯一性
    public static String[] union(String[] arr1, String[] arr2) {
        Set<String> set = new HashSet<String>();
        for (String str : arr1) {
            set.add(str);
        }
        for (String str : arr2) {
            set.add(str);
        }
        String[] result = {};
        return set.toArray(result);
    }

    /**
     * 判断是否含有特殊字符
     * 代理商名称是否包含 +/?\\$&';#=:
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        if(Pattern.matches(".*([+]|[/]|[?]|[\\$]|[&]|[']|[;]|[#]|[=]|[:]).*", str)) {
            return true;
        }
        return false;
    }
}
