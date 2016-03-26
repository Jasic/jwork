package org.jasic.jrpc;

import org.jasic.jrpc.rpublish.PublishServer;
import org.jasic.jrpc.rpublish.intf.ServiceInterface;
import org.jasic.jrpc.rpublish.intf.ServiceInterfaceImpl;
import org.jasic.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ommon.IVspService;
import test.ommon.VspService;

/**
 * @Author 菜鹰.
 * @Date 2014/12/17
 */
public class PublishMain {
    private static Logger logger = LoggerFactory.getLogger(PublishMain.class);
    private PublishServer publishServer;

    private static String IP = "10.72.50.133";//临时
    private static int PORT = 8000;//临时


    public static void main(String[] args) {
        logger.info(">> start publisher````");

        PublishMain publishMain = new PublishMain();
        // 1、启动tomcxt容器
        publishMain.startTomcxt();

        // 2、hxf服务提供方配置
        publishMain.configHxfService(args);
    }
    // ------------------------------- 容器设置 -------------------------------

    // 启动发布服务
    public void startTomcxt() {
        publishServer = new PublishServer();
        publishServer.service();
    }

    // 山寨配置
    public void configHxfService(String[] args) {
        try {
            if (args.length == 2) {
                IP = args[0];
                PORT = Integer.parseInt(args[1]);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        configHxfService(IP, PORT);

    }

    public void configHxfService(String ip, int port) {
        ServiceInterface instance = new ServiceInterfaceImpl();
        IVspService vspService = new VspService();
        publishServer.simulateRPCPublish(ServiceInterface.class, ip, port);
        publishServer.simulateRPCPublish(IVspService.class, ip, port);
        publishServer.regService(instance);
        publishServer.regService(vspService);
        logger.info(">> publish hxf services over~ .......");

    }
}
