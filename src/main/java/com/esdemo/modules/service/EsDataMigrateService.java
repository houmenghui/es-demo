package com.esdemo.modules.service;

/**
 * es 数据迁移
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-08-02 16:09
 */
public interface EsDataMigrateService {

    /**
     * 商户迁移
     *
     * @param merchantNo 迁移的商户
     * @param oldAgentNo 迁移之前商户所属代理商编号
     */
    void merchantMigrate(String merchantNo, String oldAgentNo);

    /**
     * 取消业务产品
     *
     * @param merchantNo 商户编号
     */
    void changeMerchantProducts(String merchantNo);

    /**
     * 代理商迁移
     *
     * @param migrateAgentNode 需要迁移的代理商节点
     * @param newParentId      迁移后后的父级代理商id
     */
    void agentMigrate(String migrateAgentNode, String newParentId);

//    /**
//     * 根据商户子组织修改进件和交易子组织
//     *
//     * @param merchantNo     需要修改的商户号
//     * @param newEntryTeamId 新的商户子组织
//     */
//    void updateMbpAndOrderEntryTeamByMer(String merchantNo, String newEntryTeamId);

    /**
     * 商户欢乐返激活时，商户进件同步激活
     *
     * @param merchantNo
     */
    void activeMerchantBusinessProduct(String merchantNo);

    /**
     * 修复商户迁移数据
     * @param merchantNo
     */
    void repairMerchantMigrate(String merchantNo);

    /**
//     * 普通商户绑定特约商户，同步更新ES商户数据
//     *
//     * @param merchantNo
//     * @param acqMerchantNo
//     */
//    void bindAcqMerchantNoToEs(String merchantNo, String acqMerchantNo);
}
