package org.jasic.modue;

import org.jasic.modue.annotation.Service;
import org.jasic.modue.annotation.SubModule;
import org.jasic.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 用于启动每个子模块的线程
 *
 * @author Jasic
 */
public class SubModuleThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(SubModuleThread.class);

    private Object subModuleBean = null;

    private String defaultServiceMethod = "service";

    /**
     * 构造函数
     */
    public SubModuleThread(Object subModuleBean) {
        this.subModuleBean = subModuleBean;
    }

    public void run() {

        /**
         * 通过反射，查找在模块类中是否有service的方法,或带有注解@Service的方法
         */
        Method method = ReflectionUtil.getAccessibleMethod(subModuleBean, defaultServiceMethod);

        if (method == null) {
            /**
             * 判断带有注解@Service的方法
             */
            for (Method serviceMethod : subModuleBean.getClass().getMethods()) {
                if (serviceMethod.isAnnotationPresent(Service.class)) {
                    method = serviceMethod;
                    break;
                }
            }
        }

        /**
         * 如果没找到启动内容的方法，就抛出异常
         */
        if (method == null) {
            String subModuleName = subModuleBean.getClass().getAnnotation(SubModule.class).name();

            logger.error("Server can't load " + subModuleName + " SubMoudle!");
            logger.error("caused by SubModule " + subModuleName
                    + " can't find the service method or the @Service annotation method!");
            throw new RuntimeException("SubModule " + subModuleName + " is configure error!!");
        }

        /**
         * 启动服务内容
         */
        ReflectionUtil.invokeMethod(subModuleBean, method.getName(), new Class[]{}, new Object[]{});
    }
}
