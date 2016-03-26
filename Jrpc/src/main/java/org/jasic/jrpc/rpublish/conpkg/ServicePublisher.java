package org.jasic.jrpc.rpublish.conpkg;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 */
public class ServicePublisher extends IoHandlerAdapter {
    public static final int CONNECT_TIMEOUT = 3000;

    private String host;
    private int port;
    private SocketConnector connector;
    private IoSession session;

    private int priPort;

    public int getPriPort() {
        return priPort;
    }

    public void setPriPort(int priPort) {
        this.priPort = priPort;
    }

    private static String COMMON = "regist";

    public ServicePublisher(String host, int port) {
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
            session.close(true).awaitUninterruptibly(CONNECT_TIMEOUT);
            session = null;
        }
    }

    public void publish(String className) {
        if (session != null && session.isConnected()) {
            InetSocketAddress address = ((InetSocketAddress) session.getLocalAddress());
            String line = "cmd:" + COMMON + ",class:" + className + ",ip:" + address.getHostName() + ",port:" + getPriPort();
            session.write(line);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
    }
}
