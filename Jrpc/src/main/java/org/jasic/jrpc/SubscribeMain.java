package org.jasic.jrpc;

import org.jasic.jrpc.rpublish.intf.ServiceInterface;
import org.jasic.jrpc.rsubscribe.Subscriber;
import org.jasic.jrpc.rsubscribe.comsumer.HxfConsumerBean;
import org.jasic.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ommon.IVspService;

/**
 * @Author 菜鹰.
 * @Date 2014/12/17
 */
public class SubscribeMain {
    private static Logger logger = LoggerFactory.getLogger(SubscribeMain.class);

    private static String ConfigIp = "10.72.50.133";//临时
    private static int CoonfigPort = 8000;//临时

    public static void main(String[] args) {
        logger.info(">> start subscriber going on````");

        try {
            // 1、启动tomcxt
            startTomcxt(args);

            // 2、配置HxfConsumerBean
            HxfConsumerBean bean1 = new HxfConsumerBean().setName0(ServiceInterface.class.getName()).setVersion0("1.0.0").setSubscriber0(subscriber);
            HxfConsumerBean bean2 = new HxfConsumerBean().setName0(IVspService.class.getName()).setVersion0("1.0.0").setSubscriber0(subscriber);
            bean1.init();
            bean2.init();

            // 3、获取(sprxng)服务对象
            ServiceInterface serviceInstance = getBeanByInterface(ServiceInterface.class);
            IVspService vspService = getBeanByInterface(IVspService.class);
            // 4、调用对象方法

            //------ 测试调用
            logger.info("----------------------------------- 测试开始 ------------------------------------");
            logger.info("表弟叫表哥们玩LOL，得到答复：" + serviceInstance.playLol());
            logger.info("表弟叫表哥们玩DOTA，得到答复：" + serviceInstance.playDota());

            logger.info("调用vsp接口服务,result:" + vspService.play_自动发人币服务());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // ----------------------------- 启动伪容器 -------------------------------------
    private static Subscriber subscriber;

    private static void startTomcxt(String[] args) {
        try {
            if (args.length == 2) {
                ConfigIp = args[0];
                CoonfigPort = Integer.parseInt(args[1]);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        subscriber = new Subscriber(ConfigIp, CoonfigPort);
    }

    private static <T> T getBeanByInterface(Class<T> className) {
        return subscriber.getService(className);
    }

    // ----------------------------- 启动伪Hsf消费服务 -------------------------------------

}
