package com.esdemo.modules.bean;

import com.esdemo.frame.enums.QueryScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：ES查询条件
 * @Author：zhangly
 * @Date：2019/5/22 11:06
 * @Version：1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EsSearchBean {

    //查询过滤字段
    private String[] includeFields;
    //分页信息
    private PageRequest pageRequest;
    //查询ES类型
    private String typeName;
    //分组字段
    private String[] groupFields;
    //求和字段（ES stats方法可以直接获取sum、max、min、avg，但一般用的就是求和，所以暂时就只用求和），key为求和字段，value为求和别名，为空默认为"sum_"+key
    private Map<String, String> sumFields;
    //排序字段
    private Map<String, SortOrder> sortFields;
    //not字段
    private Map<String, List<String>> notFields;
    //in字段（或者查询）
    private Map<String, List<String>> inFields;
    private List<AgentInfo> inAgentInfo;
    //查询范围，ALL：全部，OFFICAL：直营，CHILDREN：下级，默认全部
    private QueryScope queryScope;
    //代理商编号
    private String agentNo;
    //代理商oem
    private String agentOem;
    //代理商节点
    private String agentNode;
    //代理商等级
    private String agentLevel;
    //开始创建时间
    private String startCreateTime;
    //结束创建时间
    private String endCreateTime;
    //开始交易时间
    private String startTransTime;
    //结束交易时间
    private String endTransTime;
    //最小交易金额
    private String minTransAmount;
    //最大交易金额
    private String maxTransAmount;
    //所属组织
    private String teamId;
    //所属子组织
    private String teamEntryId;
    //业务产品
    private String bpId;
    //交易卡片种类
    private String cardType;
    //交易方式
    private String payMethod;
    //交易方式
    private String orderType;
    //订单号
    private String orderNo;
    //交易卡号
    private String accountNo;
    //交易状态
    private String transStatus;
    //结算状态
    private String settleStatus;
    //机具SN号
    private String deviceSn;
    //商户手机号
    private String mobileNo;
    //商户名称/编号
    private String merchantKey;
    //商户编号
    private String merchantNo;
    //商户激活状态
    private String hlfActive;

    //关联子对象
    private List<HasChildQueryBuilder> childQueryBuilderList;
    //关联父对象
    private List<HasParentQueryBuilder> parentQueryBuilderList;
}