package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.ProblemEnum;
import com.esdemo.modules.dao.ProblemDao;
import com.esdemo.modules.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：问题建议Service实现
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Service
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    @Resource
    private ProblemDao problemDao;

    /**
     * 查询所有问题类型
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getAllProblemType() {

        List<Map<String, Object>> list = ProblemEnum.getAllProblemToMap();

        //由于数据库表的问题类型数据也适配与盛钱包APP，与盛代宝的有冲突，此业务无需改动，暂时先写死
        //list = problemDao.getAllProblemType();
        return list;
    }

    /**
     * 提交问题信息
     *
     * @param params
     * @return
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int insertProblemInfo(Map<String, String> params) {
        return problemDao.insertProblem(params);
    }
}
