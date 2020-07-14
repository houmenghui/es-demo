package com.esdemo.frame.utils;

import com.aliyun.openservices.oss.OSSClient;
import org.apache.commons.pool.PoolableObjectFactory;

public class PoolableALiYunOssFactory implements PoolableObjectFactory<OSSClient>{

	@Override
	public OSSClient makeObject() throws Exception {
		return new OSSClient(ALiYunOssUtil.ACCESS_KEY, ALiYunOssUtil.ACCESS_KEY_SECRET);
	}

	@Override
	public void destroyObject(OSSClient obj) throws Exception {
		
	}

	@Override
	public boolean validateObject(OSSClient obj) {
		return true;
	}

	@Override
	public void activateObject(OSSClient obj) throws Exception {
		
	}

	@Override
	public void passivateObject(OSSClient obj) throws Exception {
		
	}

}
