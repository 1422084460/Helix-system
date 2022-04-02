package com.art.artcommon.utils;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import dev.morphia.query.internal.MorphiaCursor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * description
 * mongo 通用工具类
 * @author lou
 * @create 2022/3/25
 */
public class MongoUtils {

    private static Datastore datastore = SpringContextHolder.getBean("mongoDataStore");

    public static List<?> queryAll(String fullClassName){
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Query<?> query = datastore.createQuery(clazz);
            MorphiaCursor<?> cursor = query.find();
            return cursor.toList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<?> queryByFilterOne(String fullClassName,String condition,Object value){
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Query<?> query = datastore.createQuery(clazz).filter(condition,value);
            MorphiaCursor<?> cursor = query.find();
            return cursor.toList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<?> queryByFields(String fullClassName,String[] fields,Object[] fieldsValue){
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Query<?> query = datastore.createQuery(clazz);
            int index = 0;
            for(String f : fields){
                query.field(f).equal(fieldsValue[index]);
                index++;
            }
            MorphiaCursor<?> cursor = query.find();
            return cursor.toList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void saveOne(String fullClassName,String[] fields,Class<?>[] paramType,Object[] fieldsValue){
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Object o = clazz.newInstance();
            int index = 0;
            for(String f : fields){
                Method method = clazz.getDeclaredMethod("set"+f.substring(0,1).toUpperCase()+f.substring(1),paramType[index]);
                method.invoke(o,fieldsValue[index]);
                index++;
            }
            datastore.save(o);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateOne(String fullClassName,String queryField,String updateField,Object queryValue,Object newValue){
        try {
            Class<?> clazz = Class.forName(fullClassName);
            Query query = datastore.createQuery(clazz).field(queryField).equal(queryValue);
            UpdateOperations<?> operations = datastore.createUpdateOperations(clazz).set(updateField,newValue);
            datastore.update(query,operations);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
