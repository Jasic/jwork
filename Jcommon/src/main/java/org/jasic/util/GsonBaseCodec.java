package org.jasic.util;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Jasic
 * Date: 13-6-13
 */
public abstract class GsonBaseCodec<T> {
    private static Logger logger = LoggerFactory
            .getLogger(GsonBaseCodec.class);

    private Class<T> entityClass;
    private Gson gson;

    public GsonBaseCodec() {
        /**
         * 通过子类的泛型定义取得对象类型
         */
        this.entityClass = ReflectionUtil.getSuperClassGenricType(getClass());

        gson = new Gson();
    }

    /**
     * 获取消息对应的实体
     *
     * @param msgStr
     * @return
     */
    public T getMsgEntity(String msgStr) {

        if (msgStr != null && !msgStr.equals("")) {
            Object o = gson.fromJson(msgStr, entityClass);
            return (T) o;
        } else {
            try {
                T value = entityClass.newInstance();
                return value;
            } catch (InstantiationException e) {
                logger.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return null;
    }

    /**
     * 获取消息对应的字符串
     *
     * @param msgEntity
     * @return
     */
    public String getMsgStr(T msgEntity) {

        String value = gson.toJson(msgEntity, entityClass);

        if (value.equals("{}"))
            value = "";

        return value;
    }
}

