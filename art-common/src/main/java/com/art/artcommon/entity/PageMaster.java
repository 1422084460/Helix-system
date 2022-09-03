package com.art.artcommon.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * description
 * 分页辅助类
 * @author lou
 * @create 2022/9/2
 */
public class PageMaster extends JSONObject {

    /**
     * 总页数
     */
    private int count;

    /**
     * 每页计数
     */
    private int pageSize;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 物理页面组
     */
    public JSONObject pages = new JSONObject();

    /**
     * 获取总页数
     * @return int
     */
    public int size(){
        return count;
    }

    /**
     * 获取每页计数
     * @return int
     */
    public int getPageSize(){
        return pageSize;
    }

    /**
     * 获取当前页
     * @return int
     */
    public int getCurrentPage(){
        return currentPage;
    }

    /**
     * 构造方法
     * @param targetList 目标集合
     * @param pageSize 每页计数
     */
    private PageMaster(List<JSONObject> targetList, int pageSize){
        this.pageSize = pageSize;
        int targetCount = targetList.size();
        this.count = computePages(targetCount,pageSize);
        this.currentPage = 1;
        if (count==1){
            pages.put("1",targetList);
        }else {
            cutPages(targetList);
        }
        put("pages",pages);
    }

    /**
     * 计算总页数
     * @param targetCount 目标数量
     * @param pageSize 每页计数
     * @return int
     */
    private int computePages(int targetCount,int pageSize){
        if (targetCount<=pageSize){
            return 1;
        }else {
            return targetCount/pageSize + 1;
        }
    }

    /**
     * 拆分页面
     * @param targetList 目标集合
     */
    private void cutPages(List<JSONObject> targetList){
        int sum = count;
        int start = 0;
        for (int i=1;i<=sum;i++){
            List<JSONObject> sub;
            if ((targetList.size()-(i-1)*pageSize)>pageSize){
                sub = targetList.subList(start, start + pageSize);
            }else {
                sub = targetList.subList(start, targetList.size());
            }
            pages.put(""+i,sub);
            start += pageSize;
        }
    }

    /**
     * 自定义每页计数创建分页
     * @param targetList 目标集合
     * @param pageSize 每页计数
     * @return PageMaster
     */
    public static PageMaster create(List<JSONObject> targetList,int pageSize){
        return new PageMaster(targetList,pageSize);
    }

    /**
     * 以默认每页计数创建分页
     * @param targetList 目标集合
     * @return PageMaster
     */
    public static PageMaster create(List<JSONObject> targetList){
        return new PageMaster(targetList,10);
    }
}
