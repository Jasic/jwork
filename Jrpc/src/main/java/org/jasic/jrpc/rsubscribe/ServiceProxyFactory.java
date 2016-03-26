package org.jasic.jrpc.rsubscribe;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jasic.jrpc.rpublish.serial.SerialRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author 菜鹰.
 * @Date 2014/12/17
 * <p/>
 * 工厂类
 */
public class ServiceProxyFactory {

    private final static ServiceProxyFactory proxyFactory = new ServiceProxyFactory();
    private long waitTimeout = 5000;

    private ServiceProxyFactory() {
    }

    public static ServiceProxyFactory getInstance() {
        return proxyFactory;
    }

    public <T> T create(final Class<T> interfaceClass, final SocketAddress socketAddress) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        if (!method.getName().startsWith("play")) {
                            return "[err: filter by proxy]";
                        }else if(method.getName().equals("toString")){
                            return interfaceClass.getName();
                        }
                        Object object = null;
                        NioSocketConnector connector = new NioSocketConnector();
                        connector.setConnectTimeoutMillis(3000L);
                        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
                        SocketSessionConfig cfg = connector.getSessionConfig();
                        cfg.setUseReadOperation(true);
                        IoSession session = connector.connect(socketAddress).awaitUninterruptibly().getSession();

                        try {
                            // 组装请求
                            SerialRequest serialRequest = new SerialRequest();
                            serialRequest.setMethodName(method.getName());
                            serialRequest.setParaTypes(method.getParameterTypes());
                            serialRequest.setArguments(args);
                            serialRequest.setInterfaceName(interfaceClass.getName());
                            // 发送
                            session.write(serialRequest).awaitUninterruptibly();
                            // 接收
                            ReadFuture readFuture = session.read();
                            if (readFuture.awaitUninterruptibly(waitTimeout, TimeUnit.SECONDS)) {
                                object = readFuture.getMessage();
                                // TODO 处理消息
                            } else {
                                // 读超时
                            }
                        } finally {
                            // 断开
                            session.close(true);
                            session.getService().dispose();
                        }
                        return object;
                    }
                }
        );
    }
}
