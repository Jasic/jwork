package org.jasic.modue.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注系统的一个子模块
 *
 * @author Jasic
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubModule {

    /**
     * 所属模块
     */
    String module();

    /**
     * 子模块名
     *
     * @return
     */
    String name();

    /**
     * 启动优先级别，1为最大级别，数值越大级别越低，默认为0
     */
    int priority() default 0;

    /**
     * 子模块状态,true启动,false不启动,默认启动
     *
     * @return
     */
    boolean status() default true;
}
