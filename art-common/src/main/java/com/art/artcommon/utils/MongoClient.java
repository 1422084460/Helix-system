package com.art.artcommon.utils;

import com.art.artcommon.constant.CustomException;
import com.art.artcommon.constant.R;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import dev.morphia.query.internal.MorphiaCursor;

import java.util.List;
import java.util.Map;

/**
 * description
 * mongo 通用工具类
 * @author lou
 * @create 2022/3/25
 */
public class MongoClient<T> {

    /**
     * 数据源
     */
    private Datastore datastore = SpringContextHolder.getBean("mongoDataStore");

    /**
     * 目标实体
     */
    public Class<T> clazz;

    /**
     * 构造方法
     * @param clazz 目标实体
     */
    public MongoClient(Class<T> clazz){
        this.clazz = clazz;
    }

    /**
     * 获取mongo集合所有文档
     * @return List<T>
     */
    public List<T> queryAll() {
        Query<T> query = datastore.createQuery(clazz);
        MorphiaCursor<T> cursor = query.find();
        return cursor.toList();
    }

    /**
     * 根据单个条件筛选获取部分mongo集合文档
     * @param condition 筛选条件
     * @param value 对应值
     * @param order 排序字段
     * @return List<T>
     */
    public List<T> queryByFilter(String condition,Object value,String order,boolean isAsc) {
        Query<T> query = datastore.createQuery(clazz).filter(condition, value);
        if (!order.equals("")){
            if (isAsc){
                query.order(order);
            }else {
                query.order("-"+order);
            }
        }
        MorphiaCursor<T> cursor = query.find();
        return cursor.toList();
    }

    /**
     * 根据多个条件筛选获取部分mongo集合文档
     * @param filter 筛选条件map
     * @return List<T>
     */
    public List<T> queryByFilter(Map<String,Object> filter,String order,boolean isAsc) {
        Query<T> query = datastore.createQuery(clazz);
        for (Map.Entry<String,Object> entry : filter.entrySet()){
            query.filter(entry.getKey(),entry.getValue());
        }
        if (!order.equals("")){
            if (isAsc){
                query.order(order);
            }else {
                query.order("-"+order);
            }
        }
        MorphiaCursor<T> cursor = query.find();
        return cursor.toList();
    }

    /**
     * 根据单个条件筛选获取单个mongo集合文档
     * @param condition 筛选条件
     * @param value 对应值
     * @return Object
     */
    public Object queryOne(String condition,Object value) {
        Query<T> query = datastore.createQuery(clazz).filter(condition,value);
        MorphiaCursor<T> cursor = query.find();
        return cursor.toList().get(0);
    }

    /**
     * 插入单条mongo集合文档
     * @param entity 实体
     */
    public void saveOne(Object entity){
        try{
            datastore.save(entity);
        }catch (Exception e){
            throw new CustomException(R.CODE_FAIL,"添加数据失败");
        }
    }

    /**
     * 插入多条mongo集合文档
     * @param list 数据集合
     */
    public void saveBatch(List<Object> list){
        try{
            datastore.save(list);
        }catch (Exception e){
            throw new CustomException(R.CODE_FAIL,"添加数据失败");
        }
    }

    /**
     * 更新单个mongo集合文档
     * @param targetField 目标字段
     * @param updateField 预更新字段
     * @param queryValue 目标字段值
     * @param newValue 更新值
     */
    public void updateOne(String targetField,String updateField,Object queryValue,Object newValue){
        try {
            Query<T> query = datastore.createQuery(clazz).field(targetField).equal(queryValue);
            UpdateOperations<T> operations = datastore.createUpdateOperations(clazz).set(updateField,newValue);
            datastore.update(query,operations);
        }catch (Exception e){
            throw new CustomException(R.CODE_FAIL,"更新数据失败");
        }
    }

    /**
     * 批量删除一条或多条mongo集合文档
     * @param filter 筛选条件map
     */
    public void deleteMany(Map<String,Object> filter){
        try {
            Query<T> query = datastore.createQuery(clazz);
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                query.filter(entry.getKey(), entry.getValue());
            }
            datastore.delete(query);
        }catch (Exception e){
            throw new CustomException(R.CODE_FAIL,"删除数据失败");
        }
    }
}
