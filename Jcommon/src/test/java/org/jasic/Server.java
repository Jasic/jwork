package org.jasic;

import org.jasic.modue.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 后台服务器主类，用于启动所有的模块
 *
 * @author Jasic
 * @date Sep 7, 2012
 */
public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private String serverName = null;

    private ModuleManager moduleManager = null;

    /**
     * 构造函数
     */
    public Server(String serverName) {

        this.serverName = serverName;

        moduleManager = new ModuleManager();

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        logger.info("---------------------- " + serverName + " Start ----------------------");
        /**
         * 进行模块的加载
         */
        moduleManager.loadModule(this.getClass());
    }

    /**
     * 开启服务器
     */
    public void start() {
        moduleManager.startModule();
    }

    /**
     * main方法
     *
     * @param args
     */
    public static void main(String[] args) {

        String serverName = "JRPC";
        if (args == null || args.length < 1) {
            logger.warn("Server module using the default name,expects a typing name!\n");
        } else
            serverName = args[0];

        Server server = new Server(serverName);
        server.start();
    }
}
