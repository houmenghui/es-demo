package com.esdemo.frame.utils;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.modules.dao.SysConfigDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.io.*;
import java.util.Date;

@Slf4j
public class ALiYunOssUtil {
    private static final SysConfigDao sysConfigDao = SpringHolder.getBean(SysConfigDao.class);
    public static final String ACCESS_KEY = sysConfigDao.getStringValueByKey("Ali_OSS_ACCESS_KEY");
    public static final String ACCESS_KEY_SECRET = sysConfigDao.getStringValueByKey("Ali_OSS_ACCESS_SECRET");
    public static final String ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";

    private static GenericObjectPool<OSSClient> pool;

    static {
        pool = new GenericObjectPool<OSSClient>(new PoolableALiYunOssFactory());
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxActive(50);
        pool.setMaxWait(20 * 1000);
        pool.setTestOnBorrow(false);
        // 当 timeBetweenEvictionRunsMillis 大于0时，每过timeBetweenEvictionRunsMillis
        // 时间，
        // 就会启动一个线程，校验连接池中闲置时间超过minEvictableIdleTimeMillis的连接对象。
        pool.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000);
        pool.setMinEvictableIdleTimeMillis(5 * 60 * 1000);
    }

    /**
     * 保存文件到阿里云
     *
     * @param bucketName bucket的名称
     * @param fileName   保存在阿里云的文件名
     * @param file       要保存的文件对象
     * @throws IOException
     */
    public static void saveFile(String bucketName, String fileName, File file)
            throws IOException {
        FileInputStream fis = new FileInputStream(file);
        saveFile(bucketName, fileName, fis);
    }

    public static void saveFileInputStream(String bucketName, String fileName,
                                           InputStream fis) throws IOException {
        saveFile(bucketName, fileName, fis);
    }

    public static void download(String bucketName, String fileName, OutputStream outputStream) {
        OSSClient client = null;
        InputStream inputStream = null;
        try {
            client = pool.borrowObject();
            OSSObject object = client.getObject(bucketName, fileName);

            inputStream = object.getObjectContent();
            org.apache.commons.io.IOUtils.copy(inputStream, outputStream);
            pool.returnObject(client);

        } catch (Exception e) {
            try {
                pool.invalidateObject(client);
            } catch (Exception e1) {
                log.error("", e1);
            }
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }

    public static void main(String[] args) {
        File f = new File("D:\\111111111.jpg");
        try {
            saveFile("agent-attch", "111111111.jpg", f);
            System.out.println(genUrl("agent-attch", "111111111.jpg", new Date(new Date().getTime() + 100000)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("异常{}", e);
        }
//		try {
//			System.out.println(exists("eeepaybag", "activity/one144118219156861249.png"));
//			String pattern = "yyyy-MM-dd";
//			String dateStr = "2015-10-13";
//			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//			Date now = sdf.parse(dateStr);
//			System.out.println(genUrl("eeepaybag","15919979589/144101343430414320.png",now));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			log.error("异常{}", e);
//		}
    }

    /**
     * 保存文件到阿里云
     *
     * @param bucketName bucket的名称
     * @param fileName   保存在阿里云的文件名
     * @param is         数据输入流 输入流会在方法执行完毕 关闭
     * @throws IOException
     */
    public static void saveFile(String bucketName, String fileName,
                                InputStream is) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(is.available());

        System.out.println(ACCESS_KEY);
        System.out.println(ACCESS_KEY_SECRET);
        OSSClient client = new OSSClient(ACCESS_KEY, ACCESS_KEY_SECRET);
        client.putObject(bucketName, fileName, is, metadata);
        is.close();
    }

    /**
     * 复制阿里云上的文件
     *
     * @param srcBucketName 源 bucket名称
     * @param srcFileName   源文件名
     * @param bucketName    目的地bucket名称
     * @param fileName      目的文件名
     * @throws IOException
     */
    public static void cp(String srcBucketName, String srcFileName,
                          String bucketName, String fileName) throws IOException {
        OSSClient client = new OSSClient(ACCESS_KEY, ACCESS_KEY_SECRET);
        client.copyObject(srcBucketName, srcFileName, bucketName, fileName);

    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param fileName   文件名称
     */
    public static void rm(String bucketName, String fileName) {
        OSSClient client = new OSSClient(ACCESS_KEY, ACCESS_KEY_SECRET);
        client.deleteObject(bucketName, fileName);
    }

    /**
     * 生成url地址
     *
     * @param bucketName
     * @param fileName
     * @param expiresDate 过期时间 (此参数无效)
     * @return
     * @see #genUrl(String, String)
     * @deprecated 过期时间将从数据库获取,
     */
    public static String genUrl(String bucketName, String fileName, Date expiresDate) {
        OSSClient client = new OSSClient(ACCESS_KEY, ACCESS_KEY_SECRET);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName);
        generatePresignedUrlRequest.setExpiration(expiresDate);
        return client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    public static String genUrl(String bucketName, String fileName) {
        OSSClient client = new OSSClient(ENDPOINT, ACCESS_KEY, ACCESS_KEY_SECRET);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName);
        generatePresignedUrlRequest.setExpiration(DateUtils.addMinute(new Date(), getAliyunOssTTL()));
        return client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    /**
     * 获取阿里云文件过期时间
     *
     * @return
     */
    private static int getAliyunOssTTL() {
        String ttl = sysConfigDao.getStringValueByKey("aliyun_oss_time_to_live");
        try {
            int ttlValue = Integer.valueOf(ttl);
            if (ttlValue <= 0) {
                ttlValue = 30;
            }
            return ttlValue;
        } catch (Exception e) {
            return 30;
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public static boolean exists(String bucketName, String fileName) {
        OSSClient client = new OSSClient(ACCESS_KEY, ACCESS_KEY_SECRET);
        try {
            client.getObject(bucketName, fileName);
        } catch (OSSException e) {
            if (e.getErrorCode().equals("NoSuchKey")) {
                return false;
            } else {
                throw e;
            }
        }
        return true;
    }
}
