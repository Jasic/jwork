package org.jasic.jrpc.rpublish;

import org.jasic.jrpc.rpublish.conpkg.ServiceProvider;
import org.jasic.jrpc.rpublish.conpkg.ServicePublisher;
import org.jasic.modue.annotation.Module;
import org.jasic.modue.annotation.Service;
import org.slf4j.Logger;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 * <p/>
 * 发布服务容器服务
 */
@Module(name = "PublishServer", priority = 1, status = true)
public class PublishServer {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PublishServer.class);

    public static final int ProvidePort = 8001;

    @Service
    public void service() {
        logger.info(">> start service provider.......");
        simulateRPCProvide();
    }

    /**
     * 模拟服务发布
     */
   public void simulateRPCPublish(Class iClass, String ip, int port) {
        ServicePublisher servicePublisher = new ServicePublisher(ip, port);
        servicePublisher.setPriPort(ProvidePort);
        servicePublisher.connect();
        servicePublisher.publish(iClass.getName());
    }

    /**
     * 模拟服务提供
     */
    void simulateRPCProvide() {
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setPort(ProvidePort);
        serviceProvider.start();
    }

    /**
     * 提供服务配置
     */
    public void regService(Object service) {

        Class[] os = service.getClass().getInterfaces();
        String intfName = null;

        if (os == null || os.length < 1) {
            throw new RuntimeException(service.getClass() + " has no interface class");
        }
        // TODO 先写死
        ServiceProvider.CachedMap.put(os[0].getName(), service);
    }
}
