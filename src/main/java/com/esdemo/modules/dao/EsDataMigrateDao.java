package com.esdemo.modules.dao;

import com.esdemo.modules.bean.EsNpospDataBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-08-02 16:12
 */
@Mapper
public interface EsDataMigrateDao {
    /**
     * 根据商户编号查询商户数据(需要跟es增量脚本的sql保持一致)
     * @param merchantNo 商户编号
     * @return
     */
    EsNpospDataBean queryMerchantInfo(@Param("merchantNo") String merchantNo);

    /**
     * 根据商户编号查询商户进件信息(需要跟es增量脚本的sql保持一致)
     * @param merchantNo 商户编号
     * @return
     */
    List<EsNpospDataBean> listMbpInfo(@Param("merchantNo") String merchantNo);

    List<EsNpospDataBean> listMbpInfoByMerchantNoAndBpId(@Param("merchantNo") String merchantNo, @Param("bpId")String bpId);

    /**
     * 根据商户号查询相应的交易信息(需要跟es增量脚本的sql保持一致)
     * @param merchantNo 商户编号
     * @param pageSize   每页条数
     * @param offset     偏移量
     */
    List<EsNpospDataBean> listOrderInfo(@Param("merchantNo") String merchantNo,
                                        @Param("pageSize") int pageSize,
                                        @Param("offset") int offset);
}
