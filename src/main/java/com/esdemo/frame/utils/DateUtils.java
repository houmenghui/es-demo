/**
 * 版权 (c) 2010 公司名称
 * 保留所有权利。
 */

package com.esdemo.frame.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;


/**
 * 描述：日期处理常用类
 * 
 * @author guosl 
 * 创建时间：Jan 13, 2010
 */
@Slf4j
public class DateUtils {
	public final static String DATEFORMAT = "yyyy-MM-dd";
	public final static String DATATIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	
//  public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//  public static SimpleDateFormat dataTimeFormat = new SimpleDateFormat(
//      "yyyy-MM-dd HH:mm:ss");

  /**
   * 
   * 功能：解析数据库中的日期字符串 格式:yyyy-MM-dd
   * 
   * @param dateStr
   * @return
   */
  public static Date parseDate(String dateStr) {
    Date date = null;
    try {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
      date = dateFormat.parse(dateStr);
    } catch (ParseException e) {
      log.error("异常{}", e);
    }
    return date;
  }
  /**
   * 
   * 功能：格式化日期字符串
   * 例如：2008-8-8  转换为2008-08-08
   *
   * @param dateStr
   * @return
   */
  public static String getDateStrFormat(String dateStr){
    try {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
      Date date = dateFormat.parse(dateStr);
      return dateFormat.format(date);
    } catch (ParseException e) {
      log.error("异常{}", e);
    }
    return null;
  }

  /**
   * 
   * 功能：解析数据库中的时间字符串 格式:yyyy-MM-dd HH:mm:ss
   * 
   * @param
   * @return
   */
  public static Date parseDateTime(String dateTimeStr) {
    Date date = null;
    try {
    	SimpleDateFormat dataTimeFormat = new SimpleDateFormat(DATATIMEFORMAT);
      date = dataTimeFormat.parse(dateTimeStr);
    } catch (ParseException e) {
      log.error("异常{}", e);
    }
    return date;
  }

  /**
   * 计算两个日期之间的天数
   * 
   * @param startDate
   *          开始时间
   * @param endDate
   *          结束时间
   * @return
   */
  public static int calcDays(String startDate, String endDate) {
    int days = 1;
    try {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
      long start = dateFormat.parse(startDate).getTime();
      long end = dateFormat.parse(endDate).getTime();
      days = (int) ((end - start) / (24 * 60 * 60 * 1000));
    } catch (ParseException e) {
      log.error("异常{}", e);
      return -1;
    }
    return days;
  }

  /**
   * 功能：指定日期加上指定天数
   * 
   * @param date
   *          日期
   * @param day
   *          天数
   * @return 返回相加后的日期
   */
  public static Date addDate(Date date, int day) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
    return c.getTime();
  }
  /**
   * 功能：指定日期加上指定天数
   * 
   * @param date
   *          日期
   * @param minute
   *          分钟
   * @return 返回相加后的日期
   */
  public static Date addMinute(Date date, int minute) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(getMillis(date) + ((long) minute)*60* 1000);
    return c.getTime();
  }

  public static Date addSecond(Date date, int second) {
	  Calendar c = Calendar.getInstance();
	  c.setTimeInMillis(getMillis(date) + ((long) second)* 1000);
	  return c.getTime();
  }
  /**
   * 功能：指定日期减去指定天数
   * 
   * @param date
   * @param day
   * @return
   */
  public static Date diffDate(Date date, int day) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(getMillis(date) - ((long) day) * 24 * 3600 * 1000);
    return c.getTime();
  }
  /**
   * 功能：指定日期减去指定分钟
   * 
   * @param date
   *          日期
   * @param minute
   *          分钟
   * @return 返回相减后的分钟
   */
  public static Date diffMinute(Date date, int minute) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(getMillis(date) - ((long) minute)*60* 1000);
    return c.getTime();
  }

  /**
   * 
   * 功能：分钟相差 minute的时间值
   * 
   * @param date
   * @param minute
   * @return
   */
  public static Date getDateByMinuteAdd(Date date, int minute) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MINUTE, minute);
    return calendar.getTime();
  }

  /**
   * 功能:两个日期相隔天数
   * 
   * @param startDate
   *          开始日期
   * @param endDate
   *          结束日期
   * @return 返回相减后的日期
   */
  public static int diffDate(Date startDate, Date endDate) {
    return (int) ((getMillis(endDate) - getMillis(startDate)) / (24 * 3600 * 1000));
  }
  /**
   * 功能：两个时间相隔的小时数
   * @param startDate
   * @param endDate
   * @return
   */
  public static int diffHour(Date startDate, Date endDate) {
    return (int) ((getMillis(endDate) - getMillis(startDate)) / (3600 * 1000));
  }
  /**
   * 功能：两个时间相隔的分钟数
   * @param startDate
   * @param endDate
   * @return
   */
  public static int diffMinute(Date startDate, Date endDate) {
    return (int) ((getMillis(endDate) - getMillis(startDate)) / (60 * 1000));
  }

  /**
   * 功能：传入时间按所需格式返回时间字符串
   * 
   * @param date
   *          java.util.Date格式
   * @param format
   *          yyyy-MM-dd HH:mm:ss | yyyy年MM月dd日 HH时mm分ss秒
   * @return
   */
  public static String format(Date date, String format) {
    String result = "";
    try {
      if (date == null) {
        date = new Date();// 如果时间为空,则默认为当前时间
      }
      if (StringUtils.isBlank(format)) {// 默认格式化形式
        format = "yyyy-MM-dd";
      }
      DateFormat df = new SimpleDateFormat(format);
      result = df.format(date);
    } catch (Exception e) {
      log.error("异常{}", e);
    }
    return result;
  }
  
  public static String formatDateTime(Date date){
	  if (date == null) {
		return null;
	}
  	SimpleDateFormat dataTimeFormat = new SimpleDateFormat(DATATIMEFORMAT);
	  return dataTimeFormat.format(date);
  }

  /**
   * 功能：传入时间字符串按所需格式返回时间
   * 
   * @param dateStr
   *          时间字符串
   * @param format
   *          跟传入dateStr时间的格式必须一样 yyyy-MM-dd HH:mm:ss | yyyy年MM月dd日 HH时mm分ss秒
   * @return
   */
  public static Date format(String dateStr, String format) {
    if (StringUtils.isBlank(dateStr)) {
      return new Date();
    }
    if (StringUtils.isBlank(format)) {
      format = "yyyy-MM-dd";
    }
    Date date = null;
    try {
      DateFormat f = new SimpleDateFormat(format);
      date = f.parse(dateStr);
    } catch (ParseException e) {
      log.error("异常{}", e);
    }
    return date;

  }

  /**
   * 功能：时间字符串格式转换
   * 
   * @param dateStr
   *          时间字符串
   * @param format
   *          时间字符串的格式
   * @param toFormat
   *          格式为的格式
   * @return
   */
  public static String format(String dateStr, String format, String toFormat) {
    return format(format(dateStr, format), toFormat);
  }

  /**
   * 功能：格式化rss的时间
   * 输入:
   * @param date
   * @return
   */  
  public static String formatRssDate(Date date) {
		if (date == null) {
			date = new Date();// 如果时间为空,则默认为当前时间
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		SimpleTimeZone zone = new SimpleTimeZone(8, "GMT");
		sdf.setTimeZone(zone);
		return sdf.format(date);
	}
  
  /**
   * 功能：返回年
   * 
   * @param date
   * @return
   */
  public static int getYear(Date date) {
    if (date == null) {
      date = new Date();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.YEAR);

  }

  /**
   * 功能：返回月
   * 
   * @param date
   * @return
   */
  public static int getMonth(Date date) {
    if (date == null) {
      date = new Date();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.MONTH) + 1;
  }

  /**
   * 功能：返回日
   * 
   * @param date
   * @return
   */
  public static int getDay(Date date) {
    if (date == null) {
      date = new Date();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 功能：返回小时
   * 
   * @param date
   * @return
   */
  public static int getHour(Date date) {
    if (date == null) {
      date = new Date();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.HOUR_OF_DAY);
  }

  /**
   * 功能：返回分
   * 
   * @param date
   * @return
   */
  public static int getMinute(Date date) {
    if (date == null) {
      date = new Date();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.MINUTE);
  }

  /**
   * 功能：返回星期 1：星期一，2:星期二 ... 6:星期六 7:星期日
   * 
   * @param date
   * @return
   */
  public static int getChinaWeek(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    int week =  c.get(Calendar.DAY_OF_WEEK)-1;
    if(week==0){
      return 7;
    }else{
      return week;
    }
  }
  
  /**
   * 功能：返回秒
   * 
   * @param date
   * @return
   */
  public static int getSecond(Date date) {
    if (date == null) {
      date = new Date();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.SECOND);
  }

  /**
   * 功能：返回毫秒
   * 
   * @param date
   * @return
   */
  public static long getMillis(Date date) {
    if (date == null) {
      date = new Date();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.getTimeInMillis();
  }

  /**
   * 功能：获取当前月的第一天日期
   * 
   * @return
   */
  public static Date getMonFirstDay() {
    Date date = new Date();
    Calendar c = Calendar.getInstance();
    c.set(getYear(date), getMonth(date) - 1, 1);
    return c.getTime();
  }
  
  /**
   * 功能：获取当前月的最后一天日期
   * 
   * @return
   */
  public static Date getMonLastDay() {
    Date date = new Date();
    Calendar c = Calendar.getInstance();
    c.set(getYear(date), getMonth(date), 1);

    c.setTimeInMillis(c.getTimeInMillis() - (24 * 3600 * 1000));
    return c.getTime();
  }

  /**
   * 功能：获取当前日期 格式:2008-02-02 23:11:10
   * 
   * @return
   */
  public static String getCurrentDateTime() {
    Date date = new Date();
	SimpleDateFormat dataTimeFormat = new SimpleDateFormat(DATATIMEFORMAT);
    return dataTimeFormat.format(date);
  }
  
  /**
   * 功能：获取当前日期 格式:20101010
   * 
   * @return
   */
  public static String getCurrentDate() {
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
    return dateFormat.format(date);
  }
  
  public static String getDateFormatTime(Date date, int num){
	  SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
	  Calendar startDT = Calendar.getInstance();
	  startDT.setTime(date);
	  startDT.add(Calendar.DAY_OF_MONTH, num);
	  return dateFormat.format(startDT.getTime());
  }
  
  /**
   * 得到报文时间格式yyyyMMddHHmmss
   * @return
   */
  public static String getMessageTextTime() {
	    return format(new Date(), "yyyyMMddHHmmss");
  }

  public static void main(String[] args) {
	  // System.out.println(getDateFormatTime(new Date(),-1));
	  
	  
//		int settleCycle = 0;
//
//		Calendar calendar = Calendar.getInstance();
//		int day = calendar.get(Calendar.DATE);
//
//		calendar.set(Calendar.DATE, day + settleCycle);
//		System.out.println(calendar.getTime());
	  
	  
   /* String dateStr = "1899-12-30 16:30:32";
    System.out.println(DateUtils.parseDateTime(dateStr));
    
    System.out.println(DateUtils.format("1109", "MMyy", "MM/yy"));
    System.out.println(format(getMonFirstDay(), "yyyy-MM-dd"));*/
    //System.out.println(getDateStrFormat("1982-8-5"));
    //System.out.println(calcDays("2010-08-01","2010-08-02"));
    
    
   /* Date d = parseDate("2010-10-10");
    int i = getChinaWeek(d);
    System.out.println(i);*/
   /* String period = "2010-10-08,2010-10-31|2010-10-03,2010-10-06|2010-09-01,2010-09-29";
    String[] startEndDateArr  = period.split("\\|");
    
    for(int i=0;i<startEndDateArr.length;i++){
      System.out.println(i+"==="+startEndDateArr[i]);
    }*/
/*    String[] firstSecondDateStr =  "2010-10-08,".split(",");
    
    for(int i=0;i<firstSecondDateStr.length;i++){
      System.out.println(i+"==="+firstSecondDateStr[i]);
    }*/
    
//    System.out.println(getDateStrFormat("1899-12-30"));
  }
}
