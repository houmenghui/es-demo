package com.esdemo.frame;

import com.esdemo.frame.utils.ALiYunOssUtil;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.DateUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * <p>Description: <／p>
 * <p>Company: www.eeepay.cn<／p> 
 * @author liusha
 * @date 2016年6月1日
 */
public class TestAliyun {
	private static final String READ_PATH=TestAliyun.class.getResource("TestAliyun.class").getPath();
	private static final String WRITE_PATH="";
	private static final String FILE_NAME="TestAliyun.class";
	
	@Test
	public void testSave() throws IOException {
		File file=new File(READ_PATH);
		//判断文件是否存
		if(!ALiYunOssUtil.exists(Constants.ALIYUN_OSS_TEMP_TUCKET, FILE_NAME)){
			//保存文件名
			ALiYunOssUtil.saveFile(Constants.ALIYUN_OSS_TEMP_TUCKET, FILE_NAME, file);
		}
		//获取文件URL
		System.out.println(ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_TEMP_TUCKET, FILE_NAME, new Date(2016,5,2)));
		
		//判断文件是否存
		if(ALiYunOssUtil.exists(Constants.ALIYUN_OSS_TEMP_TUCKET, FILE_NAME)){
			//删除文件
			ALiYunOssUtil.rm(Constants.ALIYUN_OSS_TEMP_TUCKET, FILE_NAME);
		}
	}

	@Test
	public void test1() {
		String s = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, "abcd_1536904195251_16633.jpg", DateUtils.addSecond(new Date(), 3600));
		System.out.println(s);
	}
}
