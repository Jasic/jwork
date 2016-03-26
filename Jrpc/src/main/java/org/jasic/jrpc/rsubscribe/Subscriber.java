package org.jasic.jrpc.rsubscribe;

import org.jasic.modue.annotation.Module;
import org.jasic.modue.annotation.Service;
import org.jasic.util.DateTimeUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author Jasic
 * Date 14-12-16.
 * 订单client
 */
@Module(name = "Subscriber", priority = 2, status = true)
public class Subscriber {

    public static final ConcurrentHashMap<String,InetSocketAddress> ServiceCacheMap = new ConcurrentHashMap<String, InetSocketAddress>();

    private String catServerIp;

    private int catServerPort;

    public Subscriber(String catServerIp, int catServerPort) {
        this.catServerIp = catServerIp;
        this.catServerPort = catServerPort;
    }



    @Service
    public void subscribe(final String serverName) {
        final SubClient subClient = new SubClient(catServerIp, catServerPort);
        subClient.connect();
        subClient.subscribe(serverName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                try {
                    for (; ; ) {
                        if (ServiceCacheMap.get(serverName) != null || count > 20) {
                            subClient.disconnect();
                            break;
                        }
                        DateTimeUtil.sleep(1);
                        count++;
                    }
                } finally {
                    // do release
                }
            }
        }).start();

    }

    public <T> T getService(final Class<T> interfaceClass) {
        final int getServiceAddressTimeout = 5;
        final SocketAddress socketAddress = getAddressFromCatServer(interfaceClass, getServiceAddressTimeout);
        return ServiceProxyFactory.getInstance().create(interfaceClass, socketAddress);
    }

    /**
     * @param interfaceClass
     * @param timeout
     * @return
     */
    private InetSocketAddress getAddressFromCatServer(Class interfaceClass, long timeout) {
        InetSocketAddress address = null;
        long start = System.currentTimeMillis();
        for (; address == null && (System.currentTimeMillis() - start) / 1000 < timeout; ) {
            // 1 get from cache
            address = ServiceCacheMap.get(interfaceClass.getName());
            DateTimeUtil.sleep(1);
        }
        // 2 get from server （算了不实时）
//        if (null == address /** || !address.getHostName().equals(ip) || !(address.getPort()==port) */) {
//            // TODO 捉异常
//            subscribe(interfaceClass);
//            long tempTimeout = timeout / 2;
//            if (tempTimeout > 0)
//                return getAddressFromCatServer(interfaceClass, tempTimeout);
//        }
        if (address == null) throw new RuntimeException("Get service provider address timeout~");
        return address;
    }


    private <T> void check0(Class<T> interfaceClass, String ip, int port) {
        if (interfaceClass == null)
            throw new IllegalArgumentException(">> Interface class == null");
        if (!interfaceClass.isInterface())
            throw new IllegalArgumentException(">> The " + interfaceClass.getName() + " must be interface class!");
        if (ip == null || ip.length() == 0)
            throw new IllegalArgumentException(">> Host == null!");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException(">> Invalid port " + port);
    }

}
