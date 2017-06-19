package org.jasic.util;

import org.apache.commons.lang.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @Author weixiong.zwx.
 * @Date 14-6-6
 */
public class CommonUtil {

    private CommonUtil() {
    }

    /**
     * 将map转成字典格式 K1:V1;K2:V2
     *
     * @param map        实体map
     * @param kvSep      key 与value之前的分隔符
     * @param sectionSep kv对之间的分隔符
     * @return
     */
    public static String mapToDict(Map<?,?> map, String kvSep, String sectionSep) {

        StringBuilder sb = new StringBuilder("");
        if (map != null) {
            int index = 1;
            for (Map.Entry entry : map.entrySet()){
                sb.append(entry.getKey()).append(kvSep).append(entry.getValue());
                if (index < map.size()) {
                    sb.append(sectionSep);
                }
                index++;
            }
        }
        return sb.toString();
    }

    public static Map<String, String> dictToMap(String target, String kvSep, String sectionSep) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(target)) {
            return map;
        }

        String[] strList = target.split(sectionSep);
        for (String line : strList) {
            if (StringUtils.isBlank(line))
                continue;
            StringBuilder sb = new StringBuilder(line);
            int index = sb.indexOf(kvSep);
            if (index == -1)
                continue;
            String key = sb.substring(0, index);
            String value = sb.substring(index + 1);
            map.put(key, value);
        }
        return map;
    }


    /**
     * @param object
     * @return
     */
    public static Map<String, Object> fieldval2Map(Object object, int priority) {
        if (object == null) {
            return fieldval2Map(object, null, priority);
        }
        return fieldval2Map(object, object.getClass(), priority);
    }

    /**
     * @param object
     * @param current
     * @return
     */
    public static Map<String, Object> fieldval2Map(Object object, Class<?> current) {
        return fieldval2Map(object, current, 1);
    }

    /**
     * @param object
     * @return
     */
    public static Map<String, Object> fieldval2Map(Object object) {
        if (object == null) {
            return fieldval2Map(object, 1);
        }
        return fieldval2Map(object, 1);
    }


    @SuppressWarnings("unchecked")
    public static boolean fieldStatus(Field field, Class<?> clazz, int priority) {
        if (field == null || clazz == null) return false;
        boolean status;
        switch (priority) {
            case 1: {
                String fieldName = field.getName();
                String firstLetter = fieldName.charAt(0) + "";
                String getMethod = "get" + fieldName.replaceFirst(firstLetter, firstLetter.toUpperCase());
                Method method = null;
                try {
                    method = clazz.getMethod(getMethod, null);//第二个参数为方法的参数类型
                } catch (NoSuchMethodException e) {
                }
                if (method == null) {
                    status = false;
                    break;
                }
                status = true;
                break;
            }

            case 2: {
                status = Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
                break;
            }
            case 3: {
                status = Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
                break;
            }
            default: {
                status = true;
                break;
            }
        }

        return status;
    }

    /**
     * Return the plain object [field--value]
     *
     * @param object
     * @param current
     * @param priority 1=bean模式，即是只有当字段含有正规的public getXxx()方法
     *                 2=private，选择private字段的
     *                 3=public，选择public字段的
     *                 0=all，所有字段包括静态
     * @return
     */
    public static Map<String, Object> fieldval2Map(Object object, Class<?> current, int priority) {

        Map<String, Object> resultMap = new HashMap<String, Object>(); /** Add all the classes below the specify class in the inheritance class tree */
        if (object == null) {
            resultMap.put("Null", null);
            return resultMap;
        }
        boolean isBranch = false;
        Class<?> cl = object.getClass();
        List<Class<?>> classes = new ArrayList<Class<?>>(10);
        do {
            classes.add(cl);
            if (current.equals(cl)) {
                isBranch = true;
                break;
            }
            cl = cl.getSuperclass();
        } while (cl != null);
        if (!isBranch)
            throw new IllegalArgumentException("The specify class is not in the inheritance class tree!");
        try {
            for (Class<?> c : classes) {
                for (Field field : c.getDeclaredFields()) {
                    if (fieldStatus(field, c, priority)) {
                        field.setAccessible(true);
                        Object reObj = field.get(object);
                        if (null == reObj)
                            continue;
                        else if (reObj instanceof Object[] && !(reObj instanceof String[])) {
                            Map<String, Object> rMap = new LinkedHashMap<String, Object>();
                            for (int i = 0; i < ((Object[]) reObj).length; i++) {
                                rMap.put("Array[" + i + "]", ((Object[]) reObj)[i]);
                                Object o = ((Object[]) reObj)[i];
                                if (o != null)
                                    rMap.put(i + "", fieldval2Map(o, o.getClass(), priority));
                            }
                            resultMap.put(field.getName(), rMap);
                        } else if (!(reObj instanceof Integer) && !(reObj instanceof Byte) && !(reObj instanceof Boolean) && !(reObj instanceof Float) && !(reObj instanceof Character) && !(reObj instanceof Long) && !(reObj instanceof Double) && !(reObj instanceof Short) && !(reObj instanceof String) && !(reObj instanceof Enum))
                            resultMap.put(field.getName(), fieldval2Map(reObj, reObj.getClass(), priority));
                        else resultMap.put(field.getName(), field.get(object));
                        field.setAccessible(false);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        /*
        * 对基本类型的数组处理
        */
        StringBuilder stringBuilder = new StringBuilder();
        if (object == null) stringBuilder.append("");
        else if (object instanceof byte[]) for (byte o : (byte[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof short[]) for (short o : (short[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof char[]) for (char o : (char[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof boolean[]) for (boolean o : (boolean[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof int[]) for (int o : (int[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof float[]) for (float o : (float[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof double[]) for (double o : (double[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof long[]) for (long o : (long[]) object) stringBuilder.append(o).append(",");
        else if (object instanceof String[]) for (String o : (String[]) object) stringBuilder.append(o).append(",");
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            resultMap.put(object.getClass().getSimpleName(), stringBuilder.toString());
        }
        return resultMap;
    }

    // Map --> Bean 2: 利用org.apache.commons.beanutils 工具类实现 Map --> Bean
//    public static void map2Bean(Map<String, ?> map, Object obj) {
//        if (map == null || obj == null) {
//            return;
//        }
//        try {
//            BeanUtils.populate(obj, map);
//        } catch (Exception e) {
//            log.error("transMap2Bean2 Error " + e);
//        }
//    }

    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static void map2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            System.out.println("transMap2Bean Error " + e);
        }
        return;
    }

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> bean2Map(Object obj) {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }


    public static void main(String[] args) {

    }

}
