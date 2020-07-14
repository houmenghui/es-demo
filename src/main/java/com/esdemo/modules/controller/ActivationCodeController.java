package com.esdemo.modules.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.LoginValid;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.ActCodeStatus;
import com.esdemo.frame.enums.PosteraliSource;
import com.esdemo.frame.enums.RepayEnum;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.ActCodeUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.ActivationCodeSwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.AccessService;
import com.esdemo.modules.service.ActivationCodeService;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.ProviderService;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2020/03/18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/activationCode")
@Api(description = "激活码模块")
@RestController
public class ActivationCodeController {
    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private ActivationCodeService activationCodeService;
    @Resource
    private AccessService accessService;
    @Resource
    private ProviderService providerService;

    @ApiOperation(value = "NFC激活码列表查询", notes = ActivationCodeSwaggerNotes.LIST_NFC_ACTIVATION_CODE)
    @PostMapping("/listNfcActivationCode/{pageNo}/{pageSize}")
    @OldSwaggerDeveloped
    public ResponseBean listNfcActivationCode(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                              @PathVariable(required = false) int pageNo,
                                              @PathVariable(required = false) int pageSize,
                                              @RequestBody ActCodeQueryBean queryBean) {
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 10 ? 10 : pageSize;
        try {
            if (StringUtils.isBlank(queryBean.getQueryType())) {
                return ResponseBean.error("参数不合法");
            }
            String queryAgentNo = queryBean.getAgentNo();
            String queryMerchantNo = queryBean.getMerchantNo();
            String currAgentNode = userInfoBean.getAgentNode();
            //数据权限
            if (StringUtils.isNotBlank(queryAgentNo)) {
                if (!accessService.canAccessTheAgent(currAgentNode, queryAgentNo)) {
                    return ResponseBean.error("无权操作");
                }
            }
            AgentInfo loginAgentInfo = agentInfoService.queryAgentInfo(userInfoBean.getAgentNo());
            Map<String, Object> resMap = activationCodeService.listNfcActivationCode(queryBean, loginAgentInfo, pageNo, pageSize);
            Page<ActivationCodeBean> page = (Page<ActivationCodeBean>) resMap.get("page");
            //查询商户名称，由于需要关联V2商户信息，需要关联好几张表，且只有已使用的才需要商户名称，这里单独查询效率会更好
            List<ActivationCodeBean> pageData = page.getResult();
            if (!CollectionUtil.isEmpty(pageData)) {
                String unifiedMerchantNo = "", unifiedMerchantName = "";
                Map<String, Object> v2MerInfo = new HashMap<>();
                for (ActivationCodeBean activationCodeBean : pageData) {
                    unifiedMerchantNo = activationCodeBean.getUnifiedMerchantNo();
                    //只能显示V2商户信息
                    activationCodeBean.setUnifiedMerchantName("");
                    activationCodeBean.setUnifiedMerchantNo("");
                    if (StringUtils.isNotBlank(unifiedMerchantNo)) {
                        v2MerInfo = activationCodeService.getV2MerInfoByRepayMerNo(unifiedMerchantNo);
                        if (CollectionUtil.isNotEmpty(v2MerInfo)) {
                            unifiedMerchantName = StringUtils.filterNull(v2MerInfo.get("merchant_name"));
                            unifiedMerchantNo = StringUtils.filterNull(v2MerInfo.get("merchant_no"));
                            activationCodeBean.setUnifiedMerchantName(unifiedMerchantName);
                            activationCodeBean.setUnifiedMerchantNo(unifiedMerchantNo);
                        }
                    }
                }
            }
            resMap.put("pageData", page.getResult());
            resMap.remove("page");
            return ResponseBean.success(resMap, page.getTotal());
        } catch (AppException e) {
            log.error("{}NFC激活码列表查询异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }

    @ApiOperation(value = "下发激活码", notes = ActivationCodeSwaggerNotes.DIVIDE_NFC_ACTIVATION_CODE)
    @PostMapping("/divideNfcActivationCodeTo/{operateAgentNo}")
    @OldSwaggerDeveloped
    public ResponseBean divideNfcActivationCode(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                                @PathVariable(required = true) String operateAgentNo,
                                                @RequestBody ActCodeQueryBean queryBean) {
        try {
            AgentInfo loginAgentInfo = agentInfoService.queryAgentInfo(userInfoBean.getAgentNo());
            long succCount = activationCodeService.divideNfcActivationCode(operateAgentNo, queryBean, loginAgentInfo);
            Map<String, Long> countMap = new HashMap<>();
            countMap.put("succCount", succCount);
            return ResponseBean.success(countMap);
        } catch (AppException e) {
            log.error("{}下发激活码异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }

    @ApiOperation(value = "回收激活码", notes = ActivationCodeSwaggerNotes.RECOVERY_NFC_ACTIVATION)
    @PostMapping("/recoveryNfcActivation")
    @OldSwaggerDeveloped
    public ResponseBean recoveryNfcActivation(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                              @RequestBody ActCodeQueryBean queryBean) {
        try {
            String queryAgentNo = queryBean.getAgentNo();
            String currAgentNode = userInfoBean.getAgentNode();
            //按代理商查询，如果没选择代理商，默认查当前登录的代理商交易
            if (StringUtils.isNotBlank(queryAgentNo)) {
                if (!accessService.canAccessTheAgent(currAgentNode, queryAgentNo)) {
                    return ResponseBean.error("无权操作");
                }
            }
            AgentInfo loginAgentInfo = agentInfoService.queryAgentInfo(userInfoBean.getAgentNo());
            long succCount = activationCodeService.recoveryNfcActivation(queryBean, loginAgentInfo);
            Map<String, Long> countMap = new HashMap<>();
            countMap.put("succCount", succCount);
            return ResponseBean.success(countMap);
        } catch (AppException e) {
            log.error("{}回收激活码异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }

    @ApiOperation(value = "分配母码", notes = ActivationCodeSwaggerNotes.ASSIGN_PARENTCODE)
    @PostMapping("/assignParentCode")
    @OldSwaggerDeveloped
    public ResponseBean assignParentCode(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                         @RequestBody ActCodeQueryBean queryBean) {
        try {
            AgentInfo loginAgentInfo = agentInfoService.queryAgentInfo(userInfoBean.getAgentNo());
            long succCount = activationCodeService.assignParentCode(queryBean, loginAgentInfo);
            Map<String, Long> countMap = new HashMap<>();
            countMap.put("succCount", succCount);
            return ResponseBean.success(countMap);
        } catch (AppException e) {
            log.error("{}分配母码异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }

    @ApiOperation(value = "回收母码", notes = ActivationCodeSwaggerNotes.RECOVERY_PARENTCODE)
    @PostMapping("/recoveryParentCode")
    @OldSwaggerDeveloped
    public ResponseBean recoveryParentCode(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                           @RequestBody ActCodeQueryBean queryBean) {
        try {
            AgentInfo loginAgentInfo = agentInfoService.queryAgentInfo(userInfoBean.getAgentNo());
            long succCount = activationCodeService.recoveryParentCode(queryBean, loginAgentInfo);
            Map<String, Long> countMap = new HashMap<>();
            countMap.put("succCount", succCount);
            return ResponseBean.success(countMap);
        } catch (AppException e) {
            log.error("{}回收母码异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }

    @ApiOperation(value = "汇总母码信息", notes = ActivationCodeSwaggerNotes.SUMMARY_PARENTCODE)
    @PostMapping("/summaryParentCode")
    @OldSwaggerDeveloped
    public ResponseBean summaryParentCode(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            AgentInfo loginAgentInfo = agentInfoService.queryAgentInfo(userInfoBean.getAgentNo());
            Map<String, Object> result = activationCodeService.summaryParentCode(loginAgentInfo);
            return ResponseBean.success(result);
        } catch (AppException e) {
            log.error("{}汇总母码信息异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取激活码海报图片Base64", notes = ActivationCodeSwaggerNotes.GET_ACT_CODE_POSTERALI_IMG_BASE64)
    @PostMapping("/getActCodePosteraliImgBase64")
    @OldSwaggerDeveloped
    public ResponseBean getActCodePosteraliImgBase64(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                                     @RequestBody(required = false) Map<String, String> bodyParams) {
        String base64Image = "";
        try {
            String source = bodyParams.get("source");
            if (StringUtils.isBlank(source)) {
                return ResponseBean.error("必要参数不能为空");
            }
            String qrCodeContent = "";
            String nfcOrigCode = "";
            //从母码汇总页面请求过来
            if (PosteraliSource.SUMMARY_PARENT_CODE.getSource().equals(source)) {
                String loginAgentNo = userInfoBean.getAgentNo();
                AgentInfo loginAgentInfo = agentInfoService.queryAgentInfo(loginAgentNo);
                ProviderBean providerBean = providerService.queryServiceCost(loginAgentNo, RepayEnum.NFC.getType());
                if (providerBean != null && StringUtils.isNotBlank(providerBean.getNfcOrigCode())) {
                    nfcOrigCode = providerBean.getNfcOrigCode();
                }
                qrCodeContent = ActCodeUtils.getActQrCodeContent(nfcOrigCode, true);
                base64Image = ActCodeUtils.getPosteraliImgBase64(loginAgentNo, qrCodeContent);
            }
            //从激活码详情页面请求过来
            if (PosteraliSource.ACT_CODE_DETAIL.getSource().equals(source)) {
                String codeId = bodyParams.get("codeId");
                if (StringUtils.isBlank(codeId)) {
                    return ResponseBean.error("必要参数不能为空");
                }
                ActivationCodeBean activationCodeBean = activationCodeService.getActivationCodeById(codeId);
                if (null == activationCodeBean) {
                    return ResponseBean.error("激活码不存在");
                }
                String agentNo = activationCodeBean.getAgentNo();
                String status = activationCodeBean.getStatus();
                if (StringUtils.isBlank(agentNo) || !ActCodeStatus.ALLOCATED.getStatus().equals(status)) {
                    return ResponseBean.error("激活码状态不合法");
                }
                nfcOrigCode = activationCodeBean.getNfcOrigCode();
                String uuidCode = activationCodeBean.getUuidCode();
                //返回普通二维码
                if (StringUtils.isBlank(nfcOrigCode) && StringUtils.isNotBlank(uuidCode)) {
                    qrCodeContent = ActCodeUtils.getActQrCodeContent(uuidCode, false);
                }
                //返回nfc二维码内容
                if (StringUtils.isNotBlank(nfcOrigCode)) {
                    qrCodeContent = ActCodeUtils.getActQrCodeContent(nfcOrigCode, true);
                }
                base64Image = ActCodeUtils.getPosteraliImgBase64(agentNo, qrCodeContent);
            }
            return ResponseBean.success(base64Image);
        } catch (AppException e) {
            log.error("{}获取激活码海报图片Base64异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取激活码海报图片Base64，自己测试使用", notes = ActivationCodeSwaggerNotes.GET_ACT_CODE_POSTERALI_IMG_BASE64)
    @PostMapping("/getPosteraliImgBase64Test")
    @OldSwaggerDeveloped
    @SignValidate(needSign = false)
    @LoginValid(needLogin = false)
    public ResponseBean getPosteraliImgBase64Test(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                                  @RequestBody(required = false) Map<String, Integer> bodyParams) {
        String base64Image = "";
        try {
            int qrWidth = bodyParams.get("qrWidth");
            int qrHeight = bodyParams.get("qrHeight");
            int qrPosX = bodyParams.get("qrPosX");
            int qrPosY = bodyParams.get("qrPosY");
            String qrCodeUrl = ActCodeUtils.getActQrCodeContent("aaaaaaaaaab", true);
            String agentNo = "1446";

            base64Image = ActCodeUtils.getPosteraliImgBase64(agentNo, qrCodeUrl);
            return ResponseBean.success(base64Image);
        } catch (AppException e) {
            log.error("{}获取激活码海报图片Base64异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error(e.getMessage());
        }
    }
}