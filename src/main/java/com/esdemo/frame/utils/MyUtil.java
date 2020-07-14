package com.esdemo.frame.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工具类，编写一些常用的方法
 * @author ZengJA
 */
@Slf4j
public class MyUtil {

	/**
	 * 将一个(GRPC)简易Bean转成一个键值均为String类型的Map，值会去掉前后空格
	 * @param obj 需要转的Bean对象
	 * @return Map
	 */
	public static Map<String,String> beanToMap(Object obj) {
		if(obj==null){
			return null;
		}
		Map<String,String> map = new Hashtable<>();
		StringBuilder result = new StringBuilder();
		Field[] fields = obj.getClass().getDeclaredFields();
		String key = null;
		Object value = null;
		for (int j = 0,length = fields.length; j < length; j++) {
			fields[j].setAccessible(true);
			key = fields[j].getName();
			if(!key.endsWith("_")){
				continue;
			}
			try {
				value = fields[j].get(obj);
				if(value!=null && !"".equals(value)){
					result.setLength(0);
					for (int i = 0; i < key.length(); i++) {
						char ch = key.charAt(i);
						if(i>0 && Character.isUpperCase(ch)){
							result.append("_").append(ch);
						}else{
							result.append(ch);
						}
					}
					if(key.endsWith("_")){
						result.setLength(result.length()-1);
					}
					key = result.toString().toLowerCase();
					map.put(key, String.valueOf(value).trim());
				}
			} catch (IllegalArgumentException e) {
				log.error("异常{}", e);
			} catch (IllegalAccessException e) {
				log.error("异常{}", e);
			}
		}
		return map;
	}
	/**
	 * 将Bean类型集合对象转成Map类型集合
	 * @param list Bean类型集合
	 * @return
	 */
	public static List<Map<String,String>> beansToList(List list) {
		if(list!=null && list.size()>0){
			List<Map<String,String>> mapList = new ArrayList<>();
			for (Object obj : list) {
				mapList.add(beanToMap(obj));
			}
			return mapList;
		}
		return null;
	}

	/**
	 * GRPC专用版，将数据类型为Map的List集合转换成数据类型为GRPC实体类的List集合
	 * @param type GRPC的Bean类型
	 * @param list 源数据
	 * @return 返回目标类型的集合
	 */
	public static <T>T listToGrpcBeans(Class type,List<Map<String, Object>> list){
		List objs = new ArrayList<>();
		for (Map<String, Object> map : list) {
			objs.add(baseConvertMap(type,map,true));
		}
		return (T)objs;
	}
	/**
	 * 非GRPC版，将数据类型为Map的List集合转换成数据类型为实体类的List集合
	 * @param type
	 * @param list
	 * @return
	 */
	public static <T>T listToBeans(Class type,List<Map<String, Object>> list){
		List objs = new ArrayList<>();
		for (Map<String, Object> map : list) {
			objs.add(baseConvertMap(type,map,false));
		}
		return (T)objs;
	}
	
	/**
	 * 转换List&lt;Map&lt;String, String&gt;&gt;为List&lt;Map&lt;String,Object&gt;&gt;
	 * @param list
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 * @date 2017年3月16日下午5:11:29
	 * @author ZengJA
	 */
	public static List<Map<String,Object>> strToObj(List<Map<String, String>> list){
		List<Map<String,Object>> tmpList =  new ArrayList<Map<String,Object>>();
		Map<String,Object> tmp = null;
		Set<String> keys = null;
		for (Map<String, String> map : list) {
			keys = map.keySet();
			tmp = new HashMap<String,Object>();
			for (String key : keys) {
				tmp.put(key, map.get(key));				
			}
			tmpList.add(tmp);
		}
		return tmpList;
	}

	/**
	 * 将Map转换成指定的GRPC下的Bean
	 * @param type
	 * @param map
	 * @return
	 */
	public static <T>T mapToGrpcBean(Class<T> type,Map<String, Object> map){
		return baseConvertMap(type, map, true);
	}
	
	/**
	 * 将Map转换成指定的常规Bean
	 * @param type
	 * @param map
	 * @return
	 */
	public static <T>T mapToBean(Class<T> type,Map<String, Object> map){
		return baseConvertMap(type, map, false);
	}

	/**
	 * 将属性名称转换成KEY
	 * @param field
	 * @return
	 */
	public static String fieldToKey(String field) {
		if(!field.endsWith("_")){
			return null;
		}
		if(field.endsWith("_")){
			field = field.substring(0, field.length()-1);
		}
		StringBuilder result = new StringBuilder();
		try {
			char ch;
			for (int i = 0; i < field.length(); i++) {
				ch = field.charAt(i);
				if(i>0 && Character.isUpperCase(ch)){
					result.append("_").append(ch);
				}else{
					result.append(ch);
				}
			}
			field = result.toString().toLowerCase();
		} catch (IllegalArgumentException e) {
			log.error("异常{}", e);
		}
		return result.toString();
	}
	/**
	 * 将KEY转换成属性名称
	 * @param key
	 * @return
	 */
	public static String keyToField(String key) {
		StringBuilder result = new StringBuilder();
		try {
			int _index = -1;
			char ch;
			for (int i = 0; i < key.length(); i++) {
				ch = key.charAt(i);
				if('_' == ch){
					_index = i+1;
					continue;
				}else if(_index==i){
					result.append((ch+"").toUpperCase());
				}else{
					result.append(ch);
				}
			}
			key = result.append("_").toString();
		} catch (IllegalArgumentException e) {
			log.error("异常{}", e);
		}
		return result.toString();
	}

	/**
	 * 比较属性名称与KEY是否一致
	 * @param field
	 * @param key
	 * @return
	 */
	public static boolean fieldEqualsKey(String field,String key) {
		String tmp = fieldToKey(field);
		return key.equalsIgnoreCase(tmp);
	}
	
	/**
	 * 将Map转换成任意Bean
	 * @param type 目标Bean类型
	 * @param map 源数据
	 * @param bl 是否是GRPC类型的Bean
	 * @return 返回目标Bean对象
	 */
	private static <T>T baseConvertMap(Class<T> type,Map<String, Object> map,boolean bl){
		Object obj = null;
		try {
			//设置私有的构造方法为可访问
			Constructor<T> con = type.getDeclaredConstructor();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			con.setAccessible(true);
			obj = con.newInstance(); //创建 JavaBean 对象

			Set<String> set = map.keySet();
			Object tmp = null;
			Field field = null;
			for (String key : set) {
				tmp = map.get(key);
				tmp = tmp instanceof Date ? sdf.format(tmp) : tmp;
				if(tmp!=null){
					try {
						field = type.getDeclaredField(bl ? keyToField(key) : key);
						field.setAccessible(true);
						field.set(obj,String.valueOf(tmp));//如果当前属性有值，则将所有的值转换成字符串
					} catch (NoSuchFieldException e) {
						//logger.info("mapToBean > 根据MAP的KEY找属性异常，KEY："+key+" >by> "+type);
					}
				}
			}
		} catch (Exception e) {
			log.error("异常 {}", e);
		}
		return (T) obj;//返回
	}
	
	 /**
     * 	根据附件名称，生成附件下载地址
     *  @param attachments
     *  @return
     *  @author zengja-
     *  @date 2016年1月8日 下午4:52:15
     */
    public static Map<String,String> getAttachmentsUrl(String attachments) {
    	if(StringUtils.isNotEmpty(attachments)){
    		String[] atts = attachments.split(",");
    		if(atts.length>0){
    			Map<String,String> map = new HashMap<String, String>();
    			Date expiresDate = new Date(Calendar.getInstance().getTime().getTime() * 3600 * 1000);
    			for (String att : atts) {
    				if(StringUtils.isNotEmpty(att)){
    					String urlStr = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, att, expiresDate);
    					map.put(att, urlStr);
    				}
    			}
    			return map;
    		}
    	}
		return null;
	}
    public static Map<String,String> getAttachmentsUrl1(String attachments) {
    	if(StringUtils.isNotEmpty(attachments)){
    		String[] atts = attachments.split(",");
    		if(atts.length>0){
    			Map<String,String> map = new HashMap<String, String>();
    			Date expiresDate = new Date(Calendar.getInstance().getTime().getTime() * 3600 * 1000);
    			for (String att : atts) {
    				if(StringUtils.isNotEmpty(att)){
    					String urlStr = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_TEMP_TUCKET, att, expiresDate);
    					map.put(att, urlStr);
    				}
    			}
    			return map;
    		}
    	}
		return null;
	}
	
}