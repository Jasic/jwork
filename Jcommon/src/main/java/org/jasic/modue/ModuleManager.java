package org.jasic.modue;

import org.jasic.modue.annotation.Module;
import org.jasic.modue.annotation.SubModule;
import org.jasic.modue.cache.ThreadGroups;
import org.jasic.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * 模块的管理器
 *
 * @author Jasic
 */
public class ModuleManager {
    private static Logger logger = LoggerFactory.getLogger(ModuleManager.class);

    // no priority config没有配置启动级别的（为默认值0)
    private List<Class<?>> dpModuleClassList = null;

    // 有启动级别的，并将级别相同的进行分类
    private Map<Integer, Class<?>> pModuleClassMap = null;

    private Map<String, List<Class<?>>> dpSubModuleClassMap = null;
    private Map<String, Map<Integer, Class<?>>> pSubModuleClassMap = null;

    //已经排好序列的模块
    private List<Class<?>> moduleClassList = null;
    private List<String> moduleNameList = null;

    private Map<String, List<Class<?>>> subModuleClassMap = null;

    private Map<String, Thread> moduleMap = null;
    private Map<String, Map<String, Thread>> subModuleMap = null;

    /**
     * 构造函数
     */
    public ModuleManager() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        dpModuleClassList = new ArrayList<Class<?>>();
        pModuleClassMap = new HashMap<Integer, Class<?>>();

        dpSubModuleClassMap = new HashMap<String, List<Class<?>>>();
        pSubModuleClassMap = new HashMap<String, Map<Integer, Class<?>>>();

        moduleClassList = new LinkedList<Class<?>>();
        moduleNameList = new ArrayList<String>();

        subModuleClassMap = new HashMap<String, List<Class<?>>>();

        moduleMap = new HashMap<String, Thread>();
        subModuleMap = new HashMap<String, Map<String, Thread>>();
    }

    /**
     * 进行模块的加载
     */
    public void loadModule(Class<?> serverClass) {
        logger.info(">initializing all modules in this server .........");

        /**
         * 将标注了@Module的模块class加到map中去
         */
        List<Class<?>> allClassList = getClasses(serverClass);
        if (allClassList != null) {
            for (Class<?> beanClass : allClassList) {
                if (beanClass.isAnnotationPresent(Module.class)) {
                    Module module = beanClass.getAnnotation(Module.class);

                    if (module.status()) {
                        if (module.priority() < 0) {
                            logger.error("Server can't load " + module.name() + " Moudle!");
                            logger.error("caused by Module " + module.name() + " priority "
                                    + module.priority() + " is error! the priority value must > 0.");
                            throw new RuntimeException("Module " + module.name() + " is configure error!!");
                        } else {
                            /**
                             * 没有配置优先级别的module,先保留起来
                             */
                            if (module.priority() == 0) {
                                dpModuleClassList.add(beanClass);

                                moduleNameList.add(module.name());
                            } else {
                                /**
                                 * 如果有配置优先级别的，就put到map中
                                 */
                                pModuleClassMap.put(module.priority(), beanClass);

                                moduleNameList.add(module.name());
                            }
                        }
                    }
                }
            }
        }

        /**
         * 对有优先级别的模块进行排序
         */
        List<Integer> priorityList = new ArrayList<Integer>(pModuleClassMap.keySet());

        /**
         * 按优先级别排序排序
         */
        Collections.sort(priorityList);

        for (int priority : priorityList) {
            moduleClassList.add(pModuleClassMap.get(priority));
        }

        /**
         * 再将默认没优先级的放进去
         */
        moduleClassList.addAll(dpModuleClassList);

        /**
         * 加载子模块
         */
        loadSubModule(serverClass);
    }

    /**
     * 加载子模块
     */
    private void loadSubModule(Class<?> serverClass) {
        /**
         * 获取所有class
         */
        List<Class<?>> allClassList = getClasses(serverClass);

        /**
         * 将标注了@SubModule的模块class加到map中去
         */
        for (Class<?> beanClass : allClassList) {
            if (beanClass.isAnnotationPresent(SubModule.class)) {
                SubModule subModule = beanClass.getAnnotation(SubModule.class);

                if (subModule.status()) {
                    /**
                     * 判断所属的模块是否存在
                     */
                    if (!moduleNameList.contains(subModule.module())) {
                        logger.error("Server can't load " + subModule.name() + " SubMoudle!");
                        logger.error("caused by SubModule " + subModule.name() + " configure the Module "
                                + subModule.module() + " is not exist!");
                        throw new RuntimeException("SubModule " + subModule.name() + " is configure error!!");
                    }
                    /**
                     * 判断优先级别不能小于0
                     */
                    else if (subModule.priority() < 0) {
                        logger.error("Server can't load " + subModule.name() + " SubMoudle!");
                        logger.error("caused by SubModule " + subModule.name() + " priority "
                                + subModule.priority() + " is error! the priority value must > 0.");
                        throw new RuntimeException("SubModule " + subModule.name() + " is configure error!!");
                    } else {
                        /**
                         * 没有配置优先级别的subModule,先保留起来
                         */
                        if (subModule.priority() == 0) {
                            /**
                             * 判断在没有设置优先级别的MAP中，是否有对应的模块，没有就新建一个
                             */
                            if (!dpSubModuleClassMap.containsKey(subModule.module())) {
                                List<Class<?>> dpSubModuleClassList = new LinkedList<Class<?>>();
                                dpSubModuleClassMap.put(subModule.module(), dpSubModuleClassList);
                            }

                            /**
                             * 将子模块加进去没有设置优先级别的MAP中
                             */
                            dpSubModuleClassMap.get(subModule.module()).add(beanClass);
                        } else {
                            /**
                             * 判断在设置了优先级别的MAP中，是否有对应的模块，没有就新建一个
                             */
                            if (!pSubModuleClassMap.containsKey(subModule.module())) {
                                Map<Integer, Class<?>> pSubModuleClassList = new HashMap<Integer, Class<?>>();
                                pSubModuleClassMap.put(subModule.module(), pSubModuleClassList);
                            }

                            /**
                             * 将子模块加进去设置了优先级别的MAP中
                             */
                            pSubModuleClassMap.get(subModule.module()).put(subModule.priority(), beanClass);
                        }
                    }
                }
            }
        }

        /**
         * 对有各个模块的子模块进行排序
         */
        for (String moduleName : pSubModuleClassMap.keySet()) {
            Map<Integer, Class<?>> pSubModuleClassList = pSubModuleClassMap.get(moduleName);

            /**
             * 如果不存在就，新建
             */
            if (!subModuleClassMap.containsKey(moduleName)) {
                List<Class<?>> subModuleClassList = new LinkedList<Class<?>>();
                subModuleClassMap.put(moduleName, subModuleClassList);
            }

            /**
             * 对有优先级别的子模块进行排序
             */
            List<Integer> priorityList = new ArrayList<Integer>(pSubModuleClassList.keySet());

            /**
             * 按优先级别排序排序
             */
            Collections.sort(priorityList);

            for (int priority : priorityList) {
                subModuleClassMap.get(moduleName).add(pSubModuleClassList.get(priority));
            }

        }

        /**
         * 再将默认没优先级的放进去
         */
        for (String moduleName : dpSubModuleClassMap.keySet()) {
            List<Class<?>> dpSubModuleClassList = dpSubModuleClassMap.get(moduleName);

            /**
             * 如果不存在就，新建
             */
            if (!subModuleClassMap.containsKey(moduleName)) {
                List<Class<?>> subModuleClassList = new LinkedList<Class<?>>();
                subModuleClassMap.put(moduleName, subModuleClassList);
            }

            subModuleClassMap.get(moduleName).addAll(dpSubModuleClassList);
        }
    }

    /**
     * 根据sever启动类获取其下所的类名
     *
     * @param serverClass
     * @return
     */
    private List<Class<?>> getClasses(Class<?> serverClass) {
        /**
         * 判断是否已经打成jar包
         */
        String protocol = Thread.currentThread().getContextClassLoader()
                .getResource(serverClass.getName().replace('.', '/') + ".class").getProtocol();

        /**
         * 获取所有class
         */
        List<Class<?>> allClassList = null;
        if ("file".equals(protocol)) {
            allClassList = ClassUtil.getClasses(serverClass.getPackage().getName(), null, true);
        } else if ("jar".equals(protocol)) {
            try {
                String jarPackagePath = URLDecoder.decode(serverClass.getProtectionDomain().getCodeSource()
                        .getLocation().getFile(), "UTF-8");

                allClassList = ClassUtil
                        .getClasses(serverClass.getPackage().getName(), jarPackagePath, true);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return allClassList;
    }

    /**
     * 启动模块
     */
    public void startModule() {
        /**
         * 启动各个模块
         */
        Thread thread = null;

        int index = 0;
        for (Class<?> moduleClass : moduleClassList) {
            String moduleName = moduleClass.getAnnotation(Module.class).name();

            try {
                Object moduleBean = moduleClass.newInstance();
                thread = new Thread(ThreadGroups.getThreadGroup(moduleName), new ModuleThread(moduleBean));
                thread.start();

                logger.info(">starting module [" + index++ + "]:[" + moduleName + "] Module .........");

                moduleMap.put(moduleName, thread);

                /**
                 * 启动各个子模块
                 */
                if (subModuleClassMap.get(moduleName) != null) {
                    for (Class<?> subModuleClass : subModuleClassMap.get(moduleName)) {
                        String subModuleName = subModuleClass.getAnnotation(SubModule.class).name();

                        Object subModuleBean = subModuleClass.newInstance();

                        thread = new Thread(ThreadGroups.getThreadGroup(subModuleName), new SubModuleThread(subModuleBean));
                        thread.start();

                        logger.info(">starting " + subModuleName + " SubModule .........");

                        if (!subModuleMap.containsKey(moduleName)) {
                            Map<String, Thread> subModuleList = new HashMap<String, Thread>();
                            subModuleMap.put(moduleName, subModuleList);
                        }
                        subModuleMap.get(moduleName).put(subModuleName, thread);
                    }
                }
            } catch (InstantiationException e) {
                logger.error("Server can't load " + moduleName + " Moudle!");
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                logger.error("Server can't load " + moduleName + " Moudle!");
                throw new RuntimeException(e);
            }
        }
    }

}
