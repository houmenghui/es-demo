package com.esdemo.modules.dao;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：问题建议Dao
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
@Mapper
public interface ProblemDao {
    /**
     * 查询问题类型
     *
     * @return
     */
    @Select("SELECT * FROM problem_type")
    List<Map<String, Object>> getAllProblemType();


    /**
     * 添加问题反馈信息
     *
     * @param params
     * @return
     */
    @InsertProvider(type = SqlProvider.class, method = "insertProblem")
    int insertProblem(@Param("params") Map<String, String> params);


    class SqlProvider {
        /**
         * 添加问题反馈信息sql
         *
         * @param params
         * @return
         */
        public String insertProblem(Map<String, Object> params) {
            final Map<String, String> ps = (Map<String, String>) params.get("params");
            return new SQL() {
                {
                    INSERT_INTO("user_feedback_problem");
                    VALUES("user_id", "#{params.userId}");
                    VALUES("user_type", "#{params.userType}");
                    VALUES("title", "#{params.title}");
                    VALUES("problem_type", "#{params.problemType}");
                    VALUES("content", "#{params.content}");
                    VALUES("submit_time", "now()");
                    VALUES("complainter", "#{params.complainterType}");
                    VALUES("mobile", "#{params.mobileNo}");
                    VALUES("app_no", "#{params.appNo}");
                    String tmp = "";
                    tmp = ps.get("attachment");
                    if (StringUtils.isNotEmpty(tmp)) {
                        VALUES("printscreen", "#{params.attachment}");
                    }
                }
            }.toString();
        }
    }
}
