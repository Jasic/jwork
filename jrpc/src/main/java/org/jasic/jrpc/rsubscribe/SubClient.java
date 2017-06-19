package org.jasic.jrpc.rsubscribe;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.jasic.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static org.jasic.jrpc.rsubscribe.Subscriber.ServiceCacheMap;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 */
public class SubClient extends IoHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SubClient.class);
    public static final int CONNECT_TIMEOUT = 3000;

    private String host;
    private int port;
    private SocketConnector connector;
    private IoSession session;

    private static String COMMON = "subscribe";

    public SubClient(String host, int port) {
        this.host = host;
        this.port = port;
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("serial_codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        connector.setHandler(this);
    }

    public boolean isConnected() {
        return (session != null && session.isConnected());
    }

    public void connect() {
        ConnectFuture connectFuture = connector.connect(new InetSocketAddress(host, port));
        connectFuture.awaitUninterruptibly(CONNECT_TIMEOUT);
        try {
            session = connectFuture.getSession();
        } catch (RuntimeIoException e) {
        }
    }

    public void disconnect() {
        if (session != null) {
            session.close(false).awaitUninterruptibly(CONNECT_TIMEOUT);
            session = null;
        }
    }

    public void subscribe(String className) {
        if (session != null && session.isConnected()) {
            String line = "cmd:" + COMMON + ",class:" + className;
            session.write(line);
        }
    }


    //--------------------
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
    }


    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);

        if (!(message instanceof String)) return;

        // 处理响应
        handleResp((String) message, session);


    }

    private boolean handleResp(String request, IoSession session) {
        // 中介服务器响应（随意啦）
        String interfaceName = null;
        String ip = null;
        int port = -1;
        if (request.contains("cmd:subResp")) {
            interfaceName = request.split(",")[1].split(":")[1];
            ip = request.split(",")[2].split(":")[1];
            String portStr = request.split(",")[3].split(":")[1];
            if (StringUtil.isNumeric(portStr)) {
                port = Integer.parseInt(portStr);
            }

            if (StringUtil.isEmpty(interfaceName) || StringUtil.isEmpty(ip) || port == -1) {
                logger.error(">> Subscribe client handle response from catserver error~ Jasic check it ");
                return false;
            }

            InetSocketAddress address = new InetSocketAddress(ip, port);
            ServiceCacheMap.put(interfaceName, address);
            logger.info(">>  Subscribe client handle response from catserver success and cached it~");

            session.close(false);
            return true;
        }

        // 其它先不鸟啦·
        else return false;
    }
}
