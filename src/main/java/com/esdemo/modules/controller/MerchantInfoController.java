package com.esdemo.modules.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.AcqMerAuditStatus;
import com.esdemo.frame.enums.AcqMerStatus;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.*;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.AccessService;
import com.esdemo.modules.service.AcqMerchantService;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.MerchantInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author tgh
 * @description 商户, 进件, 查询
 * @date 2019/5/21
 */
@Api(description = "商户模块")
@RestController
@Slf4j
@RequestMapping("/merchantInfo")
public class MerchantInfoController {
    public final static Map specialIndustryMap = new HashMap();

    static {
        specialIndustryMap.put("5812", "食品卫生许可证");
        specialIndustryMap.put("7911", "文化经营许可证");
        specialIndustryMap.put("5813", "文化经营许可证");
        specialIndustryMap.put("7011", "特种行业许可证");
        specialIndustryMap.put("5072", "加工或生产合同");
        specialIndustryMap.put("5111", "加工或生产合同");
        specialIndustryMap.put("5137", "加工或生产合同");
        specialIndustryMap.put("5998", "加工或生产合同");
        specialIndustryMap.put("5541", "成品油零售经营许可证");
    }

    @Resource
    private MerchantInfoService merchantInfoService;

    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private AccessService accessService;
    @Resource
    private AcqMerchantService acqMerchantService;

    @OldSwaggerDeveloped
    @ApiOperation(value = "校验结算卡获取开户行", notes = "校验结算卡获取开户行\n - 参数: - settleAccountNo结算卡号\n - 返回开户行名称 bankName")
    @GetMapping("/checkSettleAccountNo/{settleAccountNo}")
    public ResponseBean checkSettleAccountNo(@PathVariable String settleAccountNo) {
        try {
            log.info("校验结算卡获取开户行请求参数 settleAccountNo : {}", settleAccountNo);
            return ResponseBean.success(merchantInfoService.checkSettleAccountNo(settleAccountNo));
        } catch (Exception e) {
            log.error("校验结算卡获取开户行异常", e);
            return ResponseBean.error("操作异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "校验身份证", notes = "校验身份证:参数 id_card_no 身份号,返回验证状态")
    @GetMapping("/validateCard/{id_card_no}")
    public ResponseBean validateCard(@PathVariable String id_card_no) {
        log.info("身份证限制产品使用次数<<<身份证号码=" + id_card_no);
        try {
            return ResponseBean.success(merchantInfoService.validateCard(id_card_no));
        } catch (Exception e) {
            log.error("校验身份证异常", e);
            return ResponseBean.error("操作异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "校验四码码(开户名,身份证,结算卡号,手机号)认证", notes = SwaggerNotes.BASE_INFO_CHECK)
    @PostMapping("/baseInfoCheck")
    public ResponseBean baseInfoCheck(@RequestBody String params) {
        try {
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            log.info("校验四码(开户名,身份证,结算卡号,手机号)认证 ====> " + paramsMap);
            return ResponseBean.success(merchantInfoService.checkBaseInfoCheck(paramsMap));
        } catch (Exception e) {
            log.error("校验四码(开户名,身份证,结算卡号,手机号)认证异常", e);
            return ResponseBean.error("操作异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "获取支行信息", notes = SwaggerNotes.GET_BANK_AND_CNAP)
    @PostMapping("/getBankAndCnap")
    public ResponseBean getBankAndCnap(@RequestBody String params) {
        log.info("获取支行信息请求参数==={}======", params);
        try {
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            return ResponseBean.success(merchantInfoService.getBankAndCnap(paramsMap));
        } catch (Exception e) {
            log.error("获取支行信息异常", e);
            return ResponseBean.error("操作异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询商户可以适用的业务产品", notes = SwaggerNotes.GET_MER_PRODUCT_LIST)
    @PostMapping("/getMerProductList")
    public ResponseBean getMerProductList(@RequestBody String params) {
        log.info("查询商户可以适用的业务产品请求参数==={}======", params);
        try {
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            if (paramsMap == null || paramsMap.get("agent_no") == null
                    || paramsMap.get("sn") == null || paramsMap.get("bp_type") == null) {
                return ResponseBean.error("参数有误");
            }
            Map<String, Object> resultMap = merchantInfoService.getMerProductList(paramsMap);
            List list = (List) resultMap.get("objectMap");
            if (list == null || list.size() == 0) {
                return ResponseBean.success(null, "数据为空");
            }
            return ResponseBean.success(list, list.size());
        } catch (Exception e) {
            log.error("查询商户可以适用的业务产品异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "根据业务产品查出服务,根据服务查出业务服率和限额", notes = SwaggerNotes.GET_SERVICE_INFO_BY_PARAMS)
    @PostMapping("/getServiceInfoByParams")
    public ResponseBean getServiceInfoByParams(@RequestBody String params) {
        log.info("==进入===根据业务产品查出服务,根据服务查出业务服率和限额=== 参数 ==> {}" + params);
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap.put("status", false);
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            String agent_no = paramsMap.get("agent_no");
            String bp_id = paramsMap.get("bp_id");
            String one_agent_no = paramsMap.get("one_agent_no");
            String mobilephone = paramsMap.get("mobilephone");
            //查出代理商有多少服务
            List<ServiceInfo> list = merchantInfoService.getServiceInfoByParams(agent_no, bp_id);
            if (list != null && list.size() > 0) {
                resultMap.put("serviceInfo", list);//返回服务
                resultMap.put("status", true);
            } else {
                resultMap.put("msg", "当前业务产品无服务");
            }
            if (StringUtils.isNotBlank(mobilephone)) {
                log.info("商户手机号" + mobilephone);
                if ((Boolean) resultMap.get("status")) {
                    Map<String, String> bpInfoParams = new HashMap<>();
                    bpInfoParams.put("mobilephone", mobilephone);
                    Map<String, Object> bpInfo = merchantInfoService.queryBpInfo(bp_id);
                    String teamId = (String) bpInfo.get("team_id");
                    bpInfoParams.put("team_id", teamId);
                    Map<String, Object> checkRegistRules = merchantInfoService.checkRegistRules(bpInfoParams);
                    if (checkRegistRules != null) {
                        resultMap.put("msg", "已使用相同手机号注册过");
                        log.info(bpInfoParams + "===已使用相同手机号注册过");
                    }
                }
            }
            if ((Boolean) resultMap.get("status")) {
                List<ServiceRate> listRate = merchantInfoService.getServiceRatedByParams(one_agent_no, bp_id);
                if (listRate != null && listRate.size() > 0) {
                    resultMap.put("status", true);
                    resultMap.put("serviceRate", listRate);//返回当前登陆代理商所属一级代理商的费率
                } else {
                    resultMap.put("msg", "当前业务产品无费率");
                }
            }
            if ((Boolean) resultMap.get("status")) {
                List<ServiceQuota> listQuota = merchantInfoService.getServiceQuotaByParams(one_agent_no, bp_id);//查出当前登陆代理商所属一级代理商的限额
                if (listQuota != null && listQuota.size() > 0) {
                    resultMap.put("status", true);
                    resultMap.put("serviceQuota", listQuota);//返回当前登陆代理商所属一级代理商的限额
                } else {
                    resultMap.put("msg", "当前业务产品无限额");
                }
            }
            if ((Boolean) resultMap.get("status")) {
                List<AddRequireItem> listRequireItem = merchantInfoService.getRequireItemByParams(agent_no, bp_id);//查出进件项
                if (listRequireItem != null && listRequireItem.size() > 0) {
                    resultMap.put("status", true);
                    resultMap.put("addRequireItem", listRequireItem);//返回进件项
                }
            }
            log.info("状态==============>" + resultMap.get("status"));
            if ((Boolean) resultMap.get("status")) {
                List<Map<String, Object>> typeList = merchantInfoService.queryMerType("sys_mcc", "-1");
                for (Map<String, Object> map : typeList) {
                    String sysName = (String) map.get("sysName");
                    String sysValue = (String) map.get("sysValue");
                    List<Map<String, Object>> merMccList = merchantInfoService.queryMerType("sys_mcc", sysValue);
                    map.put("merMccType", merMccList);
                    map.put("sysValue", sysValue);
                    map.put("sysName", sysName);
                }
                resultMap.put("status", true);
                resultMap.put("merType", typeList);
            }
            return ResponseBean.success(resultMap);
        } catch (Exception e) {
            log.error("查询异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "商户进件详情", notes = SwaggerNotes.QUERY_MER_ITEM_DETAILS)
    @PostMapping("/queryMerItemDetails")
    public ResponseBean queryMerItemDetails(@RequestBody String params,
                                            @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("商户进件详情请求参数==={}======", params);
        try {
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            if (paramsMap == null || StringUtils.isBlank(paramsMap.get("agent_node")) ||
                    StringUtils.isBlank(paramsMap.get("merchant_no")) || StringUtils.isBlank(paramsMap.get("bp_id")) ||
                    StringUtils.isBlank(paramsMap.get("one_agent_no"))) {
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(merchantInfoService.queryMerItemDetails(paramsMap, userInfoBean));
        } catch (Exception e) {
            log.error("商户进件详情查询异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "商户进件菜单开关", notes = "商户进件菜单开关\n - 参数:\n - one_agent_no 当前所属一级代理商编号,\n - function_number 功能开关编号")
    @GetMapping("/getAcqMerRecSwitch/{one_agent_no}/{function_number}")
    public ResponseBean getAcqMerRecSwitch(@PathVariable String one_agent_no, @PathVariable String function_number) {
        log.info("商户进件菜单开关请求参数===one_agent_no: {}===function_number: {}===", one_agent_no, function_number);
        try {
            return ResponseBean.success(merchantInfoService.getAcqMerRecSwitch(one_agent_no, function_number));
        } catch (Exception e) {
            log.error("商户进件菜单开关异常", e);
            return ResponseBean.error("操作异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "收单商户进件列表查询", notes = SwaggerNotes.GET_ACQ_MERINFO_LIST)
    @PostMapping("/getAcqMerInfoList")
    public ResponseBean getAcqMerInfoList(@RequestBody String params) {
        log.info("收单商户进件列表请求参数==={}======", params);
        try {
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params, Object.class);
            Integer pageNo = Integer.valueOf(paramsMap.get("pageNo").toString());
            Integer pageSize = Integer.valueOf(paramsMap.get("pageSize").toString());
            PageHelper.startPage(pageNo, pageSize, false);
            List<Map<String, Object>> list = merchantInfoService.getAcqMerInfoItemList(paramsMap);
            if (list.size() < 1) {
                return ResponseBean.success(null, "数据为空");
            }
            String audit_status_zh = "", audit_status = "", general_merchant_no = "";
            Map<String, Object> acqMap = new HashMap<>();
            for (Map<String, Object> map : list) {
                audit_status = StringUtils.filterNull(map.get("audit_status"));
                audit_status_zh = AcqMerAuditStatus.getByStatus(audit_status).getStatusZh();
                //审核通过时判断是否失效
                if (AcqMerAuditStatus.AUDIT_SUCC.getStatus().equals(audit_status)) {
                    general_merchant_no = StringUtils.filterNull(map.get("general_merchant_no"));
                    if (StringUtils.isNotBlank(general_merchant_no)) {
                        acqMap = acqMerchantService.queryAcqMerByGeneralMerNo(general_merchant_no, null);
                        if (null != acqMap && !acqMap.isEmpty() && "0".equals(StringUtils.filterNull(acqMap.get("acq_status")))) {
                            audit_status = AcqMerAuditStatus.INVALID.getStatus();
                            audit_status_zh = AcqMerAuditStatus.INVALID.getStatusZh();
                        }
                    }
                }
                map.put("audit_status", audit_status);
                map.put("audit_status_zh", audit_status_zh);
            }
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
            return ResponseBean.success(pageInfo);
        } catch (Exception e) {
            log.error("收单商户进件列表查询异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "收单商户进件详情", notes = SwaggerNotes.GET_ACQ_MERINFO_DETAILS)
    @GetMapping("/getAcqMerInfoDetails/{acq_into_no}/{agent_no}")
    public ResponseBean getAcqMerInfoDetails(@PathVariable String acq_into_no,
                                             @PathVariable String agent_no,
                                             @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("收单商户进件详情查询参数 acq_into_no ==> {}, agent_no ==> {}", acq_into_no, agent_no);
            if (!(userInfoBean.getAgentNo()).equals(agent_no)) {
                log.info("所传代理商编号 {} 跟当前登录代理商编号 {} 不等", agent_no, userInfoBean.getAgentNo());
                return ResponseBean.error("参数有误");
            }
            Map<String, Object> resultMap = new HashMap<>();
            Map<String, Object> acqMerInfoMap = merchantInfoService.getAcqMerInfoByAcqIntoNo(acq_into_no);
            List<Map<String, Object>> fileList = merchantInfoService.getAcqMerInfoIdFile(acq_into_no);
            Date expiresDate = new Date(Calendar.getInstance().getTime().getTime() * 3600 * 1000);
            for (Map<String, Object> map : fileList) {
                String file_url = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, map.get("file_url").toString(), expiresDate);
                map.put("file_url", file_url);
            }
            List<AcqMerFileInfo> acqMerFileInfoList = MyUtil.listToBeans(AcqMerFileInfo.class, fileList);
            //子类需要显示specialIndustry字段（特定行业需要上传特定的证书，需要下发该字段名称）
//                5812	食品卫生许可证
//                7911	文化经营许可证
//                5813	文化经营许可证
//                7011	特种行业许可证
//                5072	加工或生产合同
//                5111	加工或生产合同
//                5137	加工或生产合同
//                5998	加工或生产合同
//                5541	成品油零售经营许可证
            resultMap.put("specialIndustry", "");
            String two_scope = StringUtils.filterNull(acqMerInfoMap.get("two_scope"));
            if (!"".equals(two_scope)) {
                resultMap.put("specialIndustry", specialIndustryMap.get(two_scope));
            }
            /**********智能盛pos二期，脱敏数据处理**********/
            //法人身份证
            String legal_person_id = StringUtils.filterNull(acqMerInfoMap.get("legal_person_id"));
            if (StringUtils.isNotBlank(legal_person_id)) {
                //Md5加密
                String legal_person_id_md5 = SecureUtil.md5(legal_person_id);
                acqMerInfoMap.put("legal_person_id_md5", legal_person_id_md5);
                //前2后2
                legal_person_id = legal_person_id.replaceAll("(?<=\\w{2})\\w(?=\\w{2})", "*");
                acqMerInfoMap.put("legal_person_id", legal_person_id);
            }
            //银行卡号
            String bank_no = StringUtils.filterNull(acqMerInfoMap.get("bank_no"));
            if (StringUtils.isNotBlank(bank_no)) {
                //Md5加密
                String bank_no_md5 = SecureUtil.md5(bank_no);
                acqMerInfoMap.put("bank_no_md5", bank_no_md5);
                //前6后4
                bank_no = bank_no.replaceAll("(?<=\\w{6})\\w(?=\\w{4})", "*");
                acqMerInfoMap.put("bank_no", bank_no);
            }
            /**********智能盛pos二期，脱敏数据处理**********/
            //如果已经绑定了特约商户，需要下发所属代理商的业务产品
            String general_merchant_no = StringUtils.filterNull(acqMerInfoMap.get("general_merchant_no"));
            acqMerInfoMap.put("merchant_no", general_merchant_no);
            if (StringUtils.isNotBlank(general_merchant_no)) {
                MerchantInfo merchantInfo = merchantInfoService.queryMerchantInfo(general_merchant_no);
                if (null == merchantInfo) {
                    return ResponseBean.error("普通商户信息不存在");
                }
                String agentNo = merchantInfo.getAgentNo();
                //获取所属代理商信息
                AgentInfo agentInfo = agentInfoService.queryAgentInfoByNo(agentNo);
                if (null == agentInfo) {
                    return ResponseBean.error("普通商户所属代理商信息不存在");
                }
                //获取业务产品及业务产品组
                List<MerchantBpBean> productList = acqMerchantService.listMerBpInfoWithGroup(general_merchant_no, agentNo);
                String change_mer_business_info = StringUtils.filterNull(acqMerInfoMap.get("change_mer_business_info"));
                SposUtils.loadAcqChangeMerBusinessInfo(productList, change_mer_business_info);
                resultMap.put("productList", productList);
            }
            resultMap.put("acqMerInfo", acqMerInfoMap);
            resultMap.put("acqMerFileInfoList", acqMerFileInfoList);
            return ResponseBean.success(resultMap, "查询收单商户进件详情成功");
        } catch (Exception e) {
            log.error("收单商户进件详情查询异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "申请特约商户初始化数据", notes = SwaggerNotes.GET_ACQ_MERINFO_DETAILS)
    @RequestMapping("/initApplySpecialMer/{merchantNo}")
    public ResponseBean initApplySpecialMer(@PathVariable String merchantNo,
                                            @RequestBody(required = false) Map<String, String> bodyParams,
                                            @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        Map<String, Object> resultMap = new HashMap<>();
        //保持与上一个接口getAcqMerInfoDetails的返回字段一致
        AcqMerInfo acqMerInfo = new AcqMerInfo();
        try {
            String acqMerStatus = StringUtils.filterNull(bodyParams.get("acqMerStatus"));
            log.info("申请特约商户 merchantNo ==> {}，acqMerStatus ==> {}", merchantNo, acqMerStatus);
            MerchantInfo merchantInfo = merchantInfoService.queryMerchantInfo(merchantNo);
            if (null == merchantInfo) {
                return ResponseBean.error("商户信息不存在");
            }
            String agentNo = merchantInfo.getAgentNo();
            //获取所属代理商信息
            AgentInfo agentInfo = agentInfoService.queryAgentInfoByNo(agentNo);
            if (null == agentInfo) {
                return ResponseBean.error("所属代理商信息不存在");
            }
            acqMerInfo.setMerchant_no(merchantNo);
            if (!AcqMerStatus.ACQ_INVALID.getStatus().equals(acqMerStatus)) {
                String merchantType = merchantInfo.getMerchantType();
                //设置经营地区
                String province = StringUtils.filterNull(merchantInfo.getProvince());
                String city = StringUtils.filterNull(merchantInfo.getCity());
                String district = StringUtils.filterNull(merchantInfo.getDistrict());
                String address = StringUtils.filterNull(merchantInfo.getAddress());

                acqMerInfo.setProvince(province);
                acqMerInfo.setCity(city);
                acqMerInfo.setDistrict(district);
                if (StringUtils.isNotBlank(address)) {
                    String detailAddress = province + city + district;
                    if (address.contains(detailAddress)) {
                        detailAddress = address.substring(detailAddress.length());
                        acqMerInfo.setAddress(detailAddress);
                    }
                }
                //法人姓名
                acqMerInfo.setLegal_person(merchantInfo.getLawyer());
                //身份证号
                String legal_person_id = acqMerchantService.getMerItemByNoAndMriId(merchantNo, "6");
                if (StringUtils.isNotBlank(legal_person_id)) {
                    //Md5加密
                    String legal_person_id_md5 = SecureUtil.md5(legal_person_id);
                    //前2后2
                    legal_person_id = legal_person_id.replaceAll("(?<=\\w{2})\\w(?=\\w{2})", "*");
                    acqMerInfo.setLegal_person_id(legal_person_id);
                    acqMerInfo.setLegal_person_id_md5(legal_person_id_md5);
                }
                //身份证号有效期，系统暂未保存
                /*acqMerInfo.setId_valid_start(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "45"));
                acqMerInfo.setId_valid_end(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "45"));*/
                //账户类型
                String accountTypeZh = acqMerchantService.getMerItemByNoAndMriId(merchantNo, "1");
                String accountType = "对私".equals(accountTypeZh) ? "1" : "2";
                acqMerInfo.setAccount_type(accountType);
                //银行卡号
                String bank_no = acqMerchantService.getMerItemByNoAndMriId(merchantNo, "3");
                if (StringUtils.isNotBlank(bank_no)) {
                    //获取开户行名称
                    Map<String, Object> bankMap = acqMerchantService.querySettleAccountNo(bank_no);
                    if (null != bankMap && !bankMap.isEmpty()) {
                        String bankName = StringUtils.filterNull(bankMap.get("bank_name"));
                        acqMerInfo.setAccount_bank(bankName);
                    }
                    //开户地区
                    Map<String, Object> merCardInfo = acqMerchantService.findMerAccountNo(merchantNo, bank_no);
                    if (!CollectionUtil.isEmpty(merCardInfo)) {
                        String bankCity = StringUtils.filterNull(merCardInfo.get("account_city"));
                        String bankProvince = StringUtils.filterNull(merCardInfo.get("account_province"));
                        String bankDistrict = StringUtils.filterNull(merCardInfo.get("account_district"));
                        String cnapsNo = StringUtils.filterNull(merCardInfo.get("cnaps_no"));
                        acqMerInfo.setAccount_province(bankProvince);
                        acqMerInfo.setAccount_city(bankCity);
                        acqMerInfo.setAccount_district(bankDistrict);
                        acqMerInfo.setLine_number(cnapsNo);
                    }
                    //Md5加密
                    String bank_no_md5 = SecureUtil.md5(bank_no);
                    //前6后4
                    bank_no = bank_no.replaceAll("(?<=\\w{6})\\w(?=\\w{4})", "*");
                    acqMerInfo.setBank_no(bank_no);
                    acqMerInfo.setBank_no_md5(bank_no_md5);
                }
                //开户名
                acqMerInfo.setAccount_name(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "2"));
                //支行名称
                acqMerInfo.setBank_branch(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "4"));
                //下发照片资料
                /*法人身份证正面",@"法人身份证反面",@"银行卡正面",@"营业执照正面",@"开户许可证(选填)",@"店铺门头照片",@"店铺合影照片",@"店内经营场所照片"
                分别对应：
                9，10，11，12，23，13，30，14*/
                List<AcqMerFileInfo> acqMerFileInfoList = new ArrayList<>();
                acqMerFileInfoList.addAll(SposUtils.getMerItemFile(merchantNo, new String[]{"9", "10", "11"}));

                //如果是个体或企业获取营业执照相关信息
                if ("2".equals(merchantType) || "3".equals(merchantType)) {
                    acqMerFileInfoList.addAll(SposUtils.getMerItemFile(merchantNo, new String[]{"12", "23", "13", "30", "14"}));
                    //营业执照名称
                    acqMerInfo.setCharter_name(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "38"));
                    //营业执照编号
                    acqMerInfo.setCharter_no(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "34"));
                    //营业执照有效期,系统暂未保存
                    /*acqMerInfo.setCharter_valid_start(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "35"));
                    acqMerInfo.setCharter_valid_end(acqMerchantService.getMerItemByNoAndMriId(merchantNo, "35"));*/
                }
                resultMap.put("acqMerFileInfoList", acqMerFileInfoList);
            }
            resultMap.put("acqMerInfo", acqMerInfo);
            //获取业务产品及业务产品组
            List<MerchantBpBean> productList = acqMerchantService.listMerBpInfoWithGroup(merchantNo, agentNo);
            resultMap.put("productList", productList);
            return ResponseBean.success(resultMap, "申请特约商户初始化数据成功");
        } catch (Exception e) {
            log.error("申请特约商户初始化数据异常", e);
            return ResponseBean.error("申请特约商户初始化数据异常");
        }
    }

    @OldSwaggerDeveloped
    @SignValidate(needSign = false)
    @ApiOperation(value = "收单商户进件", notes = SwaggerNotes.ADD_ACQ_MERINFO)
    @PostMapping("/addAcqMerInfo")
    public ResponseBean addAcqMerInfo(@RequestParam("params") String params,
                                      HttpServletRequest request,
                                      @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("收单商户进件请求参数 ===>{}", params);
        try {
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params, Object.class);
            return merchantInfoService.insertAcqMerInfo(paramsMap, userInfoBean, request);
        } catch (AppException e) {
            log.error("进件异常", e);
            return ResponseBean.error(e.getMessage());
        } catch (Exception e) {
            log.error("收单商户进件异常", e);
            return ResponseBean.error("进件异常");
        }
    }

    @SignValidate(needSign = false)
    @OldSwaggerDeveloped
    @ApiOperation(value = "我要进件,保存数据", notes = SwaggerNotes.INSERT_MERCHANT_INFO)
    @PostMapping("/insertMerchantInfo")
    public ResponseBean insertMerchantInfo(@RequestParam("params") String params,
                                           HttpServletRequest request,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("我要进件,保存数据 请求参数 ===>{}", params);
        try {
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params, Object.class);
            ResponseBean responseBean = merchantInfoService.insertMerchantInfo(paramsMap, userInfoBean, request);
            Object data = responseBean.getData();
            if (data != null) {
                log.info("data=== > {}", data);
                Map<String, String> resultMap = (Map<String, String>) data;
                String merchant_no = resultMap.get("merchantNo");
                String bpId = resultMap.get("bpId");
                String sn = resultMap.get("sn");
                //=== start=== 判断绑定的机具是否是超级推机具，是的话需要调用一次高伟的绑定超级推机具业务处理的接口
                if (merchantInfoService.selectSuperPushTerminal(merchantInfoService.selectTeamIdByBpId(bpId), sn) > 0) {
                    //超级推机具,调用绑定机具接口
                    log.info("<<<<<<<<<<<<<<<<<<<<" + merchant_no + "超级推商户进件接口");
                    String signKey = merchantInfoService.getStringValueByKey("CORE_KEY");
                    String coreUrl = merchantInfoService.getByKey("CORE_URL").getSysValue();
                    String result = ClientInterface.cjtMerToCjtMer(coreUrl, merchant_no, sn, signKey);
                    log.info("调用绑定超级推机具接口返回结果====> {}", result);
                }
                //=== end============
                sensors(merchant_no, sn, userInfoBean.getAgentNo());//调用神策接口
            }
            responseBean.setData(null);
            return responseBean;
        } catch (AppException e) {
            log.error("进件异常", e);
            return ResponseBean.error(e.getMessage());
        } catch (Exception e) {
            log.error("我要进件保存数据异常", e);
            return ResponseBean.error(StringUtils.isBlank(e.getMessage()) ? "操作异常" : e.getMessage());
        }
    }

    private void sensors(String merchant_no, String sn, String agentNo) {
        log.info("调用神策接口传参数  ====> merchant_no {},sn {},agentNo {}", merchant_no, sn, agentNo);
        AgentInfo agentInfo = agentInfoService.queryAgentInfo(agentNo);
        String oneLevelId = agentInfo.getOneLevelId();//一级代理商编号
        if (!agentNo.equals(oneLevelId)) {
            agentInfo = agentInfoService.queryAgentInfo(oneLevelId);
        }
        Map<String, Object> map = agentInfoService.selectActivityBySn(sn);
        log.info("===========根据sn {} 查询到数据 {} =========", sn, map);
        SysDict sysDict = merchantInfoService.getByKey("SENSORS_STATUS");
        if (sysDict != null && "1".equals(sysDict.getSysValue())) {
            Map<String, Object> merMap = merchantInfoService.getUserByMerNo(merchant_no);
            List<Map<String, Object>> list = merchantInfoService.getBpHpByMerNo(merchant_no);
            if (merMap != null) {
                try {
                    //根据身份证计算性别/生日
                    String gender = "未知";//性别
                    String age = "未知";//年龄
                    if (merMap.get("id_card_no") != null) {
                        String id_card_no = StringUtils.filterNull(merMap.get("id_card_no"));//身份证号码
                        if (StringUtils.isNotBlank(id_card_no) && id_card_no.length() == 18) {
                            age = String.format("%s-%s-%s", id_card_no.substring(6, 10), id_card_no.substring(10, 12), id_card_no.substring(12, 14));
                            try {
                                int sex = Integer.valueOf(id_card_no.substring(16, 17));
                                gender = (sex & 1) == 1 ? "男" : "女";
                            } catch (Exception e) {
                                gender = "未知";
                            }
                        }
                    }
                    MerchantInfo merchantInfo = merchantInfoService.queryMerchantInfo(merchant_no);
                    log.info("查询到新进件商户名称,编号 {} {}", merchantInfo.getMerchantName(), merchant_no);
                    merMap.put("source_system", "YS");
                    merMap.put("merchant_type", merchantInfo.getMerchantType());
                    merMap.put("gender", gender);
                    merMap.put("birthday", age);
                    merMap.put("birthyear", age.split("-")[0]);
                    merMap.put("first_level_agent_name", agentInfo.getAgentName());
                    merMap.put("first_level_agent_sales", agentInfo.getSaleName());
                    merMap.put("happy_active_type", map == null ? "" : map.get("activity_type_no"));
                    merMap.put("happy_active_name", map == null ? "" : map.get("activity_type_name"));
                    merMap.put("birthyear", age.split("-")[0]);
                    String token = merchantInfoService.getStringValueByKey("SENSORS_TOKEN");
                    String apiUrl = merchantInfoService.getStringValueByKey("SENSORS_URL");
                    String projectName = merchantInfoService.getStringValueByKey("SENSORS_PROJECT");
                    ClientInterface.getRegisterSource(apiUrl, token, projectName, merMap, list);
                } catch (Exception e) {
                    log.error("调用神策注册接口异常", e);
                }
            }
        }
    }

    @SignValidate(needSign = false)
    @OldSwaggerDeveloped
    @ApiOperation(value = "修改商户进件", notes = SwaggerNotes.UPDATE_MERCHANT_INFO)
    @PostMapping("/updateMerchantInfo/{merchantNo}")
    public ResponseBean updateMerchantInfo(@RequestParam("params") String params,
                                           @PathVariable String merchantNo,
                                           HttpServletRequest request,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("我要修改进件,保存数据 请求参数 ===>{}", params);
        try {
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params, Object.class);
            if (!accessService.canAccessTheMerchant(userInfoBean.getAgentNode(), merchantNo, true)) {
                return ResponseBean.error("无权操作该商户.");
            }
            return merchantInfoService.updateMerchantInfo(merchantNo, paramsMap, userInfoBean, request);
        } catch (AppException e) {
            log.error("修改进件异常", e);
            return ResponseBean.error(e.getMessage());
        } catch (Exception e) {
            log.error("我要修改进件保存数据异常", e);
            return ResponseBean.error(StringUtils.isBlank(e.getMessage()) ? "操作异常" : e.getMessage());
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "收单商户进件大类小类", notes = SwaggerNotes.GET_ACQ_MER_MCC)
    @GetMapping("/getAcqMerMcc")
    public ResponseBean getAcqMerMcc() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            List<Map<String, Object>> list = merchantInfoService.getAcqMerMccList("sys_mcc", "-1");
            for (Map<String, Object> map : list) {
                Map<String, Object> resultMap = new HashMap<>();
                String key = map.get("key").toString();
                String value = map.get("value").toString();
                resultMap.put("key", key);
                resultMap.put("value", value);
                //子类需要显示specialIndustry字段（特定行业需要上传特定的证书，需要下发该字段名称）
//                5812	食品卫生许可证
//                7911	文化经营许可证
//                5813	文化经营许可证
//                7011	特种行业许可证
//                5072	加工或生产合同
//                5111	加工或生产合同
//                5137	加工或生产合同
//                5998	加工或生产合同
//                5541	成品油零售经营许可证
                List<Map<String, Object>> list_temp = merchantInfoService.getAcqMerMccList("sys_mcc", key);
                List<Map<String, Object>> acqMerMccList = new ArrayList<>();
                for (Map<String, Object> map_temp : list_temp) {
                    map_temp.put("specialIndustry", "");
                    String key_value = StringUtils.filterNull(map_temp.get("key"));
                    if (!"".equals(key_value)) {
                        map_temp.put("specialIndustry", specialIndustryMap.get(key_value));
                    }
                    acqMerMccList.add(map_temp);
                }
                resultMap.put("acqMerMccList", acqMerMccList);
                resultList.add(resultMap);
            }
            return ResponseBean.success(resultList);
        } catch (Exception e) {
            log.error("获取收单商户进件大类小类异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "商户进件列表查询", notes = SwaggerNotes.GET_MPAGE_INFO_RPC)
    @PostMapping("/getMerchantInfoList")
    public ResponseBean getMerchantInfoList(@RequestBody String params, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("商户进件列表查询参数 ===> {}", params);
        Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
        if (paramsMap == null || StringUtils.isBlank(paramsMap.get("pageNo")) ||
                StringUtils.isBlank(paramsMap.get("pageSize"))) {
            return ResponseBean.error("参数有误");
        }
        int pageNo = Integer.parseInt(StringUtils.filterNull(paramsMap.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(paramsMap.get("pageSize")));

        List<Map<String, Object>> list=null;
        long count = 0;
        //第一页查询总页数，其它页不查询
        if (pageNo == 1) {
            Page page = PageHelper.startPage(pageNo, pageSize);
            list = merchantInfoService.getMerchantInfoList(paramsMap,userInfoBean);
            count = page.getTotal();
        } else {
            PageHelper.startPage(pageNo, pageSize, false);
            list = merchantInfoService.getMerchantInfoList(paramsMap,userInfoBean);
        }
        return ResponseBean.success(list, count);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询业务产品列表", notes = SwaggerNotes.GET_BP_ID)
    @GetMapping("/getBpId/{agent_no}")
    public ResponseBean getBpId(@PathVariable String agent_no, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("查询业务产品列表 请求参数 ===> agent_no:{}", agent_no);
            if (StringUtils.isBlank(agent_no) || !agent_no.equals(userInfoBean.getAgentNo())) {
                return ResponseBean.error("参数有误");
            }
            List<PbdInfo> list = MyUtil.listToBeans(PbdInfo.class, merchantInfoService.getBpId(agent_no));
            return ResponseBean.success(list);
        } catch (Exception e) {
            log.error("查询业务产品列表异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "机具种类查询", notes = SwaggerNotes.GET_HARD_PRODUCT)
    @GetMapping("/getHardProduct/{agent_no}")
    public ResponseBean getHardProduct(@PathVariable String agent_no, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("机具种类查询 请求参数 ===> agent_no:{}", agent_no);
            if (StringUtils.isBlank(agent_no) || !agent_no.equals(userInfoBean.getAgentNo())) {
                return ResponseBean.error("参数有误");
            }
            List<Map<String, Object>> hardProductList = merchantInfoService.getHardProduct(agent_no);
            for (Map<String, Object> map : hardProductList) {
                String type_name = (String) map.get("type_name");
                String version_nu = (String) map.get("version_nu");
                map.put("type_name", type_name + version_nu);
            }
            //插入全部
            Map<String, Object> mapALL=new HashMap<>();
            mapALL.put("hp_id","");
            mapALL.put("type_name","全部");
            hardProductList.add(0,mapALL);
            return ResponseBean.success(hardProductList);
        } catch (Exception e) {
            log.error("机具种类查询异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "获取代理商业务产品", notes = SwaggerNotes.GET_AGENT_BUSINESS)
    @GetMapping("/getAgentBusiness/{agent_no}")
    public ResponseBean getAgentBusiness(@PathVariable String agent_no, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("获取代理商业务产品 请求参数 ===> agent_no:{}", agent_no);
            if (StringUtils.isBlank(agent_no) || !agent_no.equals(userInfoBean.getAgentNo())) {
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(merchantInfoService.getAgentBusiness(agent_no));
        } catch (Exception e) {
            log.error("获取代理商业务产品异常", e);
            return ResponseBean.error("查询异常");
        }
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "获取代理商所属产品的组织信息", notes = SwaggerNotes.GET_AGENT_TEAMS)
    @GetMapping("/getAgentTeams/{agent_no}")
    public ResponseBean getAgentTeams(@PathVariable String agent_no, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("获取代理商所属产品 请求参数 ===> agent_no:{}", agent_no);
            if (StringUtils.isBlank(agent_no) || !agent_no.equals(userInfoBean.getAgentNo())) {
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(merchantInfoService.getAgentTeams(agent_no, true));
        } catch (Exception e) {
            log.error("获取代理商所属产品异常", e);
            return ResponseBean.error("查询异常");
        }
    }
}
