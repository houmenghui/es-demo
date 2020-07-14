package com.esdemo.frame.bean;

import lombok.Data;

import java.util.List;

/**
 * @Title：agentApi2
 * @Description：分页对象
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Data
public class PageBean {

    //当前页
    private int pageNo = 0;
    //每页显示多少条
    private int pageSize = 10;
    //总记录数
    private long totalCount;
    //当前页数据列表
    private List pageContent;
    //总页数
    private long pageCount;

    /**
     * 无参的构造函数
     */
    public PageBean() {

    }

    /**
     * 带参数构造函数
     *
     * @param pageNo
     * @param pageSize
     * @param totalCount
     * @param pageContent
     */
    public PageBean(int pageNo, int pageSize, long totalCount, List pageContent) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.pageContent = pageContent;
        //计算总页数
        pageCount = ((totalCount + pageSize - 1) / pageSize);
    }
}
