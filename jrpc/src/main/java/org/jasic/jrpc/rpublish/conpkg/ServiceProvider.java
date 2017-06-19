package org.jasic.jrpc.rpublish.conpkg;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.jasic.jrpc.rpublish.serial.SerialRequest;
import org.jasic.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 * <p/>
 * 序列化服务提供方
 */
public class ServiceProvider implements IoHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IoAcceptor acceptor = null;

    private int port = -1;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    /**
     * 提供service的对象
     */
    public static final ConcurrentHashMap<String, Object> CachedMap = new ConcurrentHashMap<String, Object>();

    /**
     * 构造函数
     */
    public ServiceProvider() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        acceptor = new NioSocketAcceptor();

        /**
         * 定义自定义协议解析器
         */
        acceptor.getFilterChain().addLast("serialize_codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        /**
         * 设置数据将被读取的缓冲区大小
         */
        acceptor.getSessionConfig().setReadBufferSize(5120);

        /**
         * 默认10秒内没有读写就设置为空闲通道
         */
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

        /**
         * 设置消息处理器
         */
        acceptor.setHandler(this);
    }

    public void start() {
        try {
            acceptor.bind(new InetSocketAddress(getPort()));
            logger.info("Publish server listen on port[" + getPort() + "]");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
    }


    // -----------------------------------------
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        logger.info(" >> Recv remote invoke from [" + session + "]");

        if (!(message instanceof SerialRequest)) {
            logger.warn("Request[" + message + "] is not SerialRequest ");
            session.close(true);
            return;
        }

        // TODO 在此使用策略模式进行扩展
        SerialRequest request = (SerialRequest) message;

        Object service =  CachedMap.get(request.getInterfaceName());
        Method method = service.getClass().getMethod(request.getMethodName(), request.getParaTypes());
        Object result = method.invoke(service, request.getArguments());

        session.write(result);

        logger.info(" >> Handle remote invoke from [" + session + "],request[" + StringUtil.mapToString(StringUtil.fieldval2Map(request)) + "]"
                + "],result[" + request + "]");
        session.close(true);

    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        session.close(true);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

    }


    @Override
    public void messageSent(IoSession session, Object message) throws Exception {

    }

    @Override
    public void inputClosed(IoSession session) throws Exception {

    }
}
