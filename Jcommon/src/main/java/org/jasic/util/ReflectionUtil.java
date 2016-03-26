package org.jasic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具类.
 * <p/>
 * 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class等Util函数.
 */
public abstract class ReflectionUtil {

    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static Logger logger = LoggerFactory
            .getLogger(ReflectionUtil.class);

    /**
     * 调用Getter方法.
     */
    public static Object invokeGetterMethod(Object obj, String propertyName) {
        String getterMethodName = "get" + StringUtil.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[]{},
                new Object[]{});
    }

    /**
     * 调用Setter方法.使用value的Class来查找Setter方法.
     */
    public static void invokeSetterMethod(Object obj, String propertyName,
                                          Object value) {
        invokeSetterMethod(obj, propertyName, value, null);
    }

    /**
     * 调用Setter方法.
     *
     * @param propertyType 用于查找Setter方法,为空时使用value的Class替代.
     */
    public static void invokeSetterMethod(Object obj, String propertyName,
                                          Object value, Class<?> propertyType) {
        Class<?> type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtil.capitalize(propertyName);
        invokeMethod(obj, setterMethodName, new Class[]{type},
                new Object[]{value});
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field ["
                    + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object obj, final String fieldName,
                                     final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field ["
                    + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p/>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getAccessibleField(final Object obj,
                                           final String fieldName) {
        Asserter.notNull(obj, "object不能为空");
        Asserter.hasText(fieldName, "fieldName");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {// NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 对于被cglib AOP过的对象, 取得真实的Class类型.
     */
    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符. 用于一次性调用的情况.
     */
    public static Object invokeMethod(final Object obj,
                                      final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method ["
                    + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问. 如向上转型到Object仍无法找到, 返回null.
     * <p/>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object...
     * args)
     */
    public static Method getAccessibleMethod(final Object obj,
                                             final String methodName, final Class<?>... parameterTypes) {
        Asserter.notNull(obj, "object不能为空");

        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                Method method = superClass.getDeclaredMethod(methodName,
                        parameterTypes);

                method.setAccessible(true);

                return method;

            } catch (NoSuchMethodException e) {// NOSONAR
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class. eg. public UserDao
     * extends HibernateDao<User>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be
     * determined
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     * <p/>
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     * determined
     */
    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenricType(final Class clazz,
                                                final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName()
                    + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of "
                    + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName()
                    + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(
            Exception e) {
        if (e instanceof IllegalAccessException
                || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException("Reflection Exception.", e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException("Reflection Exception.",
                    ((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

    /**
     * 获取创建表时的字段 (暂只支持int,string类型)
     *
     * @param clazz
     * @param ignoreFields
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getTableColumns(final Class clazz,
                                         final String... ignoreFields) {
        String tableColumns = "";
        List<String> ignores = Arrays.asList(ignoreFields);
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                if (!ignores.contains(field.getName())) {
                    if (Integer.class.isAssignableFrom(field.getType())) {
                        tableColumns += field.getName() + " int,\n";
                    } else if (String.class.isAssignableFrom(field.getType())) {
                        tableColumns += field.getName() + " varchar(50),\n";
                    }
                }
            }
        }
        /**
         * 将最后的逗号去掉
         */
        if (tableColumns.length() > 2
                && tableColumns.substring(tableColumns.length() - 2).indexOf(
                ",\n") != -1) {
            tableColumns = tableColumns.substring(0, tableColumns.length() - 2);
        }
        return tableColumns;
    }

    /**
     * 获取插入表时的字段
     *
     * @param clazz
     * @param ignoreFields
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getTableFields(final Class clazz,
                                        final String... ignoreFields) {
        String tableFields = "";
        List<String> ignores = Arrays.asList(ignoreFields);
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                if (!ignores.contains(field.getName())) {
                    tableFields += field.getName() + ",";
                }
            }
        }
        /**
         * 将最后的逗号去掉
         */
        if (tableFields.length() > 1
                && tableFields.substring(tableFields.length() - 1).indexOf(",") != -1) {
            tableFields = tableFields.substring(0, tableFields.length() - 1);
        }
        return tableFields;
    }

    /**
     * 获取插入表时的字段的值(暂只支持int,string类型)
     *
     * @param obj
     * @param ignoreFields
     * @return
     */
    public static String getTableValues(final Object obj,
                                        final String... ignoreFields) {
        String tableValues = "";
        List<String> ignores = Arrays.asList(ignoreFields);
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!ignores.contains(field.getName())) {
                    try {
                        Object val = field.get(obj);
                        if (val == null) {
                            tableValues += "null,";
                        } else {
                            if (Integer.class.isAssignableFrom(field.getType())) {
                                tableValues += field.get(obj) + ",";
                            } else if (String.class.isAssignableFrom(field
                                    .getType())) {
                                tableValues += "'" + field.get(obj) + "',";
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        /**
         * 将最后的逗号去掉
         */
        if (tableValues.length() > 1
                && tableValues.substring(tableValues.length() - 1).indexOf(",") != -1) {
            tableValues = tableValues.substring(0, tableValues.length() - 1);
        }
        return tableValues;
    }
}
