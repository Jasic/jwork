package org.jasic.modue.annotation;

import java.lang.annotation.*;

/**
 * 用于标注模块或子模块的一个实现方法,需要实现的内容请在标注的方法内实现
 * 标注的方法必须是一个没有参数的方法
 * <p/>
 * Jasic
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Service {

}
