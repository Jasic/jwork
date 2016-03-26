package org.jasic.modue.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注系统的一个模块
 *
 * @author Jasic
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    /**
     * 模块名,多个模块之间的模块名不能相同
     *
     * @return
     */
    String name();

    /**
     * 启动优先级别，1为最大级别，数值越大级别越低，默认为0
     */
    int priority() default 0;

    /**
     * 模块状态,true启动,false不启动,默认启动
     *
     * @return
     */
    boolean status() default true;
}
