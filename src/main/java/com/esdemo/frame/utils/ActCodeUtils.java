package com.esdemo.frame.utils;

import com.esdemo.frame.config.SpringHolder;
import com.esdemo.frame.enums.RepayEnum;
import com.esdemo.frame.utils.redis.RedisUtils;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.ProviderBean;
import com.esdemo.modules.service.AccessService;
import com.esdemo.modules.service.ActivationCodeService;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.ProviderService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 激活码工具类
 */
@Slf4j
public class ActCodeUtils {
    private static AgentInfoService agentInfoService = SpringHolder.getBean(AgentInfoService.class);
    private static AccessService accessService = SpringHolder.getBean(AccessService.class);
    private static ActivationCodeService activationCodeService = SpringHolder.getBean(ActivationCodeService.class);
    private static ProviderService providerService = SpringHolder.getBean(ProviderService.class);

    /**
     * 根据超级还商户号获取V2商户信息（商户名称、商户编号）
     *
     * @param repayMerNo 超级还商户号
     * @return
     */
    public static Map<String, Object> getV2MerInfoByRepayMerNo(String repayMerNo) {
        return activationCodeService.getV2MerInfoByRepayMerNo(repayMerNo);
    }

    /**
     * 根据V2商户号/商户名称模糊查询超级还商户号
     * 当前登录代理商节点下
     *
     * @param v2MerKey V2商户key
     * @return
     */
    public static List<String> getRepayMerNoByV2MerKey(String v2MerKey, String currAgentNode, boolean isOwn) {
        return accessService.getRepayMerNoByV2MerKey(v2MerKey, currAgentNode, isOwn);
    }

    /**
     * 只有未使用才返回海报拼接图片
     *
     * @return
     */
    public static String getPosteraliImgBase64(String agentNo, String qrCodeContent) {
        String base64Image = "";
        AgentInfo agentInfo = agentInfoService.queryAgentInfo(agentNo);
        if (agentInfo == null || StringUtils.isBlank(agentInfo.getOneLevelId())) {
            return base64Image;
        }
        /*通过登录代理商的一级代理商和oem_type = 'nfc'去取背景海报,
          取不到的话则取oem_type = 'nfc' and agent_no =default 的背景图*/
        ProviderBean providerBean = providerService.queryOemServiceCost(agentInfo.getOneLevelId(), RepayEnum.NFC.getType());
        if (providerBean == null || StringUtils.isBlank(providerBean.getCommonCodeUrl())) {
            providerBean = providerService.queryOemServiceCost("default", RepayEnum.NFC.getType());
            if (providerBean == null || StringUtils.isBlank(providerBean.getCommonCodeUrl())) {
                return base64Image;
            }
        }
        String commonCodeUrl = providerBean.getCommonCodeUrl();
        if (StringUtils.isBlank(commonCodeUrl)) {
            return base64Image;
        }
        //根据系统参数判断是否走缓存处理，由于base64太大，上线后看是否影响redis性能，方便切换
        String posteraliRedisSwitch = WebUtils.getSysConfigValueByKey("POSTERALI_IMG_BASE64_SWITCH");
        String redisKey = "POSTERALI_IMG_BASE64_" + agentNo + "_" + commonCodeUrl + "_" + qrCodeContent;
        if ("1".equals(posteraliRedisSwitch)) {
            //如果缓存异常，走原逻辑获取，经查阅资料，可以直接将base64放入redis缓存
            try {
                base64Image = RedisUtils.get(redisKey);
            } catch (Exception e) {
                base64Image = "";
            }
            if (StringUtils.isNotBlank(base64Image)) {
                log.info("代理商{}获取海报拼接图片走缓存......", agentNo);
                return base64Image;
            }
        }
        log.info("代理商{}获取海报拼接图片不走缓存......", agentNo);
        //获取阿里云海报图片url
        String imgUrl = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, commonCodeUrl);
        log.info("代理商编号{}对应一级代理商nfc海报背景图片地址为：{}", agentNo, imgUrl);
        File tempFile = null;
        try {
            //获取海报背景图片
            tempFile = ActCodeUtils.getPosteraliBackgroundImg(commonCodeUrl);
            if (null == tempFile) {
                log.info("代理商{}获取阿里云海报背景图片为空", agentNo);
                return base64Image;
            }
            String fileAbsolutePath = tempFile.getAbsolutePath();
            if (StringUtils.isNotBlank(qrCodeContent)) {
                //二维码宽度
                int qrWidth = 432;
                //二维码高度
                int qrHeight = 429;
                //二维码在海报的x位置
                int qrPosX = 1201;
                //二维码在海报的y位置
                int qrPosY = 291;
                ImageUtils.zxingCodeCreateImage(qrCodeContent, qrWidth, qrHeight, fileAbsolutePath, qrPosX + "", qrPosY + "");
            }
            base64Image = Base64Utils.encodeFile(tempFile.getCanonicalPath());
            if ("1".equals(posteraliRedisSwitch)) {
                try {
                    RedisUtils.set(redisKey, base64Image, 1, TimeUnit.DAYS);
                } catch (Exception e) {
                    log.info("代理商{}获取海报拼接图片Base64存入redis异常:{}", agentNo, e);
                }
            }
            return base64Image;

        } catch (Exception e) {
            log.info("代理商{}获取海报拼接图片异常:{}", agentNo, e);
        } finally {
            if (null != tempFile) {
                tempFile.delete();
            }
        }
        return base64Image;
    }

    /**
     * 获取阿里云海报背景图片
     *
     * @param commonCodeUrl 海报背景图阿里云图片名称
     * @return 海报背景图
     */
    public static File getPosteraliBackgroundImg(String commonCodeUrl) throws Exception {
        //下载图片（临时图片要删除）
        File file = File.createTempFile("posteraliBackgroundImg", ".jpg");
        FileOutputStream fos = new FileOutputStream(file);
        ALiYunOssUtil.download(Constants.ALIYUN_OSS_ATTCH_TUCKET, commonCodeUrl, fos);
        fos.flush();
        fos.close();
        return file;
    }

    /**
     * 返回激活码校验地址，生成二维码
     *
     * @param actCode
     * @param isNfc
     * @return
     */
    public static String getActQrCodeContent(String actCode, boolean isNfc) {
        if (StringUtils.isBlank(actCode)) {
            return "";
        }
        if (isNfc) {
            String nfcCodeQRprefix = WebUtils.getDictValue("NFC_PARENT_CODE_JHCODE");
            return nfcCodeQRprefix + "?activationCode=" + actCode;
        }
        String codeQRprefix = WebUtils.getDictValue("REPAY_JHCODE");
        return codeQRprefix + "?activationCode=" + actCode;
    }
}
