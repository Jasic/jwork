package org.jasic.modue;

import org.jasic.modue.annotation.Module;
import org.jasic.modue.annotation.Service;
import org.jasic.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 用于启动每个模块的线程
 *
 * @author Jasic
 */
public class ModuleThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ModuleThread.class);

    private Object moduleBean = null;

    private String defaultServiceMethod = "service";

    /**
     * 构造函数
     */
    public ModuleThread(Object moduleBean) {
        this.moduleBean = moduleBean;
    }

    public void run() {

        /**
         * 通过反射，查找在模块类中是否有service的方法
         */
        Method method = ReflectionUtil.getAccessibleMethod(moduleBean, defaultServiceMethod);

        if (method == null) {
            /**
             * 判断带有注解@Service的方法
             */
            for (Method serviceMethod : moduleBean.getClass().getMethods()) {
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
            String moduleName = moduleBean.getClass().getAnnotation(Module.class).name();

            logger.error("Server can't load " + moduleName + " Moudle!");
            logger.error("caused by Module " + moduleName
                    + " can't find the service method or the @Service annotation method!");
            throw new RuntimeException("Module " + moduleName + " is configure error!!");
        }

        /**
         * 启动服务内容
         */
        ReflectionUtil.invokeMethod(moduleBean, method.getName(), new Class[]{}, new Object[]{});
    }
}
