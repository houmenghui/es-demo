package com.esdemo.frame.utils;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.modules.bean.AcqMerBusInfo;
import com.esdemo.modules.bean.AcqMerFileInfo;
import com.esdemo.modules.bean.AcqMerInfo;
import com.esdemo.modules.bean.MerchantBpBean;
import com.esdemo.modules.service.AcqMerchantService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @program: agentApi2
 * @description:
 * @author: zhangly
 * @create: 2020/04/06
 */
public class SposUtils {

    /**
     * 获取商户进件资料照片
     *
     * @param merchantNo
     * @param mriIds
     * @return
     */
    public static List<AcqMerFileInfo> getMerItemFile(String merchantNo, String[] mriIds) {
        List<AcqMerFileInfo> acqMerFileInfoList = new ArrayList<>();
        if(StringUtils.isBlank(merchantNo) || mriIds == null || mriIds.length == 0){
            return acqMerFileInfoList;
        }
        AcqMerchantService acqMerchantService = SpringHolder.getBean(AcqMerchantService.class);
        AcqMerFileInfo file = null;
        String file_url = "", file_name = "";
        Date expiresDate = new Date(Calendar.getInstance().getTime().getTime() * 3600 * 1000);

        for(String mriId : mriIds){
            file_name = acqMerchantService.getMerItemByNoAndMriId(merchantNo, mriId);
            if(StringUtils.isNotBlank(file_name)){
                file_url = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, file_name, expiresDate);
                if(StringUtils.isNotBlank(file_url)){
                    file = new AcqMerFileInfo();
                    file.setFile_type(mriId);
                    file.setFile_url(file_url);
                    acqMerFileInfoList.add(file);
                }
            }
        }
        return acqMerFileInfoList;
    }

    /**
     * 保存收单商户进件记录时校验业务产品下拉列表
     *
     * @param acqMerInfo
     * @return
     */
    public static String valiAcqMerBus(AcqMerInfo acqMerInfo) {
        String merchant_no = acqMerInfo.getMerchant_no();
        String change_mer_business_info = acqMerInfo.getChange_mer_business_info();
        if (StringUtils.isNotBlank(merchant_no, change_mer_business_info)) {
            AcqMerBusInfo acqMerBusInfo = JSONUtil.toBean(change_mer_business_info, AcqMerBusInfo.class);
            List<AcqMerBusInfo.MerBusId> acqMerBusIdList = acqMerBusInfo.getChangeBusinessInfo();
            if (!CollectionUtils.isEmpty(acqMerBusIdList)) {
                for (AcqMerBusInfo.MerBusId mbi : acqMerBusIdList) {
                    if (StringUtils.isBlank(mbi.getNewBpId())) {
                        return "请选择更改业务产品";
                    }
                }
            }
        }
        return null;
    }

    /**
     * 收单商户进件记录下发商户业务产品及业务产品组
     *
     * @param productList
     * @param changeMerBusinessInfo
     * @return
     */
    public static void loadAcqChangeMerBusinessInfo(List<MerchantBpBean> productList, String changeMerBusinessInfo) {
        if (StringUtils.isNotBlank(changeMerBusinessInfo) && !CollectionUtils.isEmpty(productList)) {
            AcqMerBusInfo acqMerBusInfo = JSONUtil.toBean(changeMerBusinessInfo, AcqMerBusInfo.class);
            List<AcqMerBusInfo.MerBusId> acqMerBusIdList = acqMerBusInfo.getChangeBusinessInfo();
            if (!CollectionUtils.isEmpty(acqMerBusIdList)) {
                for (MerchantBpBean mbb : productList) {
                    for (AcqMerBusInfo.MerBusId mbi : acqMerBusIdList) {
                        if (mbb.getBpId().equals(mbi.getOldBpId())) {
                            List<MerchantBpBean> canReplaceBpList = mbb.getCanReplaceBpList();
                            //这一步是为了获取当前值的业务产品名称，其实可以让前端自己确认，但是非要后台下发，搞的这里3层for循环
                            for (MerchantBpBean child : canReplaceBpList) {
                                if (child.getBpId().equals(mbi.getNewBpId())) {
                                    mbb.setCurrBpName(child.getBpName());
                                    mbb.setCurrBpId(child.getBpId());
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 修改收单商户进件时判断是否改身份证号
     *
     * @param acqMerInfo
     * @return
     */
    public static boolean isUpdateIdCard(AcqMerInfo acqMerInfo) {
        //兼容老版本，没有普通商户号的，直接保存即可，有商户号的根据Md5校验是否有修改
        String merchantNo = acqMerInfo.getMerchant_no();
        if (StringUtils.isNotBlank(merchantNo)) {
            String new_legal_person_id_md5 = acqMerInfo.getUp_legal_person_id_md5();
            String old_legal_person_id_md5 = acqMerInfo.getLegal_person_id_md5();
            if (!StringUtils.isBlank(new_legal_person_id_md5) && !new_legal_person_id_md5.equals(old_legal_person_id_md5)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 修改收单商户进件时判断是否有修改银行卡号
     *
     * @param acqMerInfo
     * @return
     */
    public static boolean isUpdateBankNo(AcqMerInfo acqMerInfo) {
        //兼容老版本，没有普通商户号的，直接保存即可，有商户号的根据Md5校验是否有修改
        String merchantNo = acqMerInfo.getMerchant_no();
        if (StringUtils.isNotBlank(merchantNo)) {
            String new_bank_no_md5 = acqMerInfo.getUp_bank_no_md5();
            String old_bank_no_md5 = acqMerInfo.getBank_no_md5();
            if (!StringUtils.isBlank(new_bank_no_md5) && !new_bank_no_md5.equals(old_bank_no_md5)) {
                return true;
            }
        }
        return false;
    }
}
