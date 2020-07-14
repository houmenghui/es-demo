package com.esdemo.modules.controller;

import cn.hutool.core.bean.BeanUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.UserSwaggerNotes;
import com.esdemo.modules.bean.UserEntityInfo;
import com.esdemo.modules.bean.UserInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Title：agentApi2
 * @Description：人员管理
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/user")
@Api(description = "人员管理模块")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "人员查询", notes = UserSwaggerNotes.QUERY_USER_LIST)
    @PostMapping("/queryUserList/{pageNo}/{pageSize}")
    @OldSwaggerDeveloped
    public ResponseBean queryUserList(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                      @PathVariable(required = false) int pageNo,
                                      @PathVariable(required = false) int pageSize,
                                      @RequestBody(required = false) Map<String, String> bodyParams) {
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageNo = pageNo - 1;
        pageSize = pageSize < 1 ? 1 : pageSize;
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        String loginAgentNo = userInfoBean.getAgentNo();
        try {
            bodyParams.put("teamId", Constants.TEAM_ID_999);
            bodyParams.put("agentNo", loginAgentNo);

            List<Map<String, Object>> dbUserInfoList = userService.getUserInfoByParams(bodyParams);
            List<Map<String, Object>> pageUserInfoList = new ArrayList<>();
            int totalCount = null == dbUserInfoList ? 0 : dbUserInfoList.size();
            int normalCount = 0;
            int invalidCount = 0;

            if (!CollectionUtils.isEmpty(dbUserInfoList)) {
                //偷了个懒，由于本系统没有集成mybatis的分页插件，并且是后来才要求加分页功能，实际情况来说代理商人员信息也不会特别多，所以就手动分页了，后续有要求再改
                int fromIndex = pageNo * pageSize;
                if (fromIndex >= totalCount) {
                    return ResponseBean.success();
                }
                int toIndex = fromIndex + pageSize;
                toIndex = toIndex >= totalCount ? totalCount : toIndex;
                pageUserInfoList = dbUserInfoList.subList(fromIndex, toIndex);

                for (Map<String, Object> pageUserInfo : pageUserInfoList) {
                    String dbManage = StringUtils.filterNull(pageUserInfo.get("manage"));
                    String dbStatus = StringUtils.filterNull(pageUserInfo.get("status"));
                    String manageZh = "1".equals(dbManage) ? "管理员" : ("0".equals(dbManage) ? "销售员" : "店员");
                    String statusZh = "1".equals(dbStatus) ? "正常" : "失效";
                    pageUserInfo.put("manage_zh", manageZh);
                    pageUserInfo.put("status_zh", statusZh);

                    int loop = "1".equals(dbStatus) ? normalCount++ : invalidCount++;
                }
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", totalCount);
            resultMap.put("normalCount", normalCount);
            resultMap.put("invalidCount", invalidCount);
            resultMap.put("userList", pageUserInfoList);
            return ResponseBean.success(resultMap);

        } catch (Exception e) {
            log.error("当前登录代理商{}人员查询异常{}", loginAgentNo, e);
            return ResponseBean.error("人员查询失败，请稍候再试");
        }
    }

    @ApiOperation(value = "新增人员信息", notes = UserSwaggerNotes.SAVE_AGENT_USER_INFO)
    @PostMapping("/saveAgentUserInfo")
    @OldSwaggerDeveloped
    public ResponseBean saveAgentUserInfo(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                          @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        String loginAgentNo = userInfoBean.getAgentNo();
        try {
            String userName = StringUtils.filterNull(bodyParams.get("userName"));
            String mobilephone = StringUtils.filterNull(bodyParams.get("mobilephone"));
            String email = StringUtils.filterNull(bodyParams.get("email"));
            String manage = StringUtils.filterNull(bodyParams.get("manage"));
            if (StringUtils.isBlank(userName, mobilephone, email, manage)) {
                return ResponseBean.error("必要信息不能有空");
            }
            if (!StringUtils.isMobile(mobilephone)) {
                return ResponseBean.error("输入手机号格式不正确");
            }
            if (!StringUtils.isEmail(email)) {
                return ResponseBean.error("输入邮箱格式不正确");
            }
            Map<String, Object> dbUserMap = userService.getAgentMobilephone(mobilephone, Constants.TEAM_ID_999);
            if (!CollectionUtils.isEmpty(dbUserMap)) {
                return ResponseBean.error("此手机号已存在");
            }
            dbUserMap = userService.getAgentEmail(email, Constants.TEAM_ID_999);
            if (!CollectionUtils.isEmpty(dbUserMap)) {
                return ResponseBean.error("此邮箱已存在");
            }
            UserInfo userInfo = BeanUtil.mapToBean(bodyParams, UserInfo.class, true);
            userInfo.setTeamId(Constants.TEAM_ID_999);

            UserEntityInfo userEntityInfo = new UserEntityInfo();
            userEntityInfo.setStatus("1");
            userEntityInfo.setApply("1");
            userEntityInfo.setUserType("1");
            userEntityInfo.setManage(manage);
            userEntityInfo.setEntityId(loginAgentNo);

            int saveCount = userService.insertAgentUserInfo(userInfo, userEntityInfo);
            if (saveCount < 1) {
                return ResponseBean.error("新增人员信息失败");
            }
            return ResponseBean.success();

        } catch (Exception e) {
            log.error("当前登录代理商{}新增人员信息异常{}", loginAgentNo, e);
            return ResponseBean.error("新增人员信息失败，请稍候再试");
        }
    }

    @ApiOperation(value = "修改人员信息", notes = UserSwaggerNotes.UPDATE_AGENT_USER_INFO)
    @PostMapping("/updateAgentUserInfo")
    @OldSwaggerDeveloped
    public ResponseBean updateAgentUserInfo(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                            @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        String loginAgentNo = userInfoBean.getAgentNo();
        try {
            String userId = StringUtils.filterNull(bodyParams.get("userId"));
            String userName = StringUtils.filterNull(bodyParams.get("userName"));
            String mobilephone = StringUtils.filterNull(bodyParams.get("mobilephone"));
            String email = StringUtils.filterNull(bodyParams.get("email"));
            String manage = StringUtils.filterNull(bodyParams.get("manage"));
            String status = StringUtils.filterNull(bodyParams.get("status"));
            if (StringUtils.isBlank(userId, userName, mobilephone, email, manage, status)) {
                return ResponseBean.error("必要信息不能有空");
            }
            if (!StringUtils.isMobile(mobilephone)) {
                return ResponseBean.error("输入手机号格式不正确");
            }
            if (!StringUtils.isEmail(email)) {
                return ResponseBean.error("输入邮箱格式不正确");
            }
            Map<String, Object> dbUserMap = userService.getAgentByUserId(userId);
            if (CollectionUtils.isEmpty(dbUserMap)) {
                return ResponseBean.error("此人员信息不存在");
            }
            //必须是当前登录代理商
            String dbEntityId = StringUtils.filterNull(dbUserMap.get("entity_id"));
            if (!loginAgentNo.equals(dbEntityId)) {
                return ResponseBean.error("修改操作不合法");
            }
            List<Map<String, Object>> manageList = userService.queryManagePerson(dbEntityId);
            if (CollectionUtils.isEmpty(manageList)) {
                return ResponseBean.error("必须存在一个有效的管理员");
            }
            if (manageList.size() == 1) {
                String manageUserId = StringUtils.filterNull(manageList.get(0).get("user_id"));
                if (userId.equals(manageUserId)) {
                    if ("0".equals(manage)) {
                        return ResponseBean.error("只存在一个有效的管理员不能将管理员改为其它角色");
                    }
                    if ("0".equals(status)) {
                        return ResponseBean.error("只存在一个有效的管理员不能将管理员状态设为失效");
                    }
                }
            }

            UserInfo userInfo = BeanUtil.mapToBean(bodyParams, UserInfo.class, true);
            UserEntityInfo userEntityInfo = BeanUtil.mapToBean(bodyParams, UserEntityInfo.class, true);
            userEntityInfo.setApply("1");

            userService.updateUserInfoWithApp(userInfo, userEntityInfo);

            return ResponseBean.success();

        } catch (Exception e) {
            log.error("当前登录代理商{}修改人员信息异常{}", loginAgentNo, e);
            return ResponseBean.error("修改人员信息失败，请稍候再试");
        }
    }
}