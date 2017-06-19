package org.jasic.jrpc.rserver;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.jasic.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 */
public class RServerHandler extends IoHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RServerHandler.class);

    @Override
    public void sessionClosed(IoSession session) throws Exception {

        logger.info("connection from " + session.getRemoteAddress() + " has been closed~~");
        logger.info("------------------------------ connection end -----------------------------\n");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
        if (message.equals("quit")) {
            session.close(true);
            logger.warn("receive quit common, session close~");
            return;
        }

        String request = (String) message;
        InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
        logger.info(request);
        handle(request, address, session);

    }


    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        return;
    }

    //--------------- 临时decode（其实应代替成XXXCodecFactory）--------------------
    ConcurrentHashMap<String, InetSocketAddress> serviceMap = new ConcurrentHashMap<String, InetSocketAddress>();

    void handle(final String line, final InetSocketAddress address, final IoSession session) {
        String className = null;
        String ip = null;
        int port = -1;
        if (StringUtil.isEmpty(line)) return;

        // register
        if (line.contains("cmd:regist")) {
            className = line.split(",")[1].split(":")[1];
            ip = line.split(",")[2].split(":")[1];
            String portStr = line.split(",")[3].split(":")[1];
            if (StringUtil.isNumeric(portStr)) port = Integer.parseInt(portStr);

            if (StringUtil.isEmpty(className) || StringUtil.isEmpty(ip) || port == -1) {
                logger.error(">> handle register request from " + address + " fail~ please check it~ ");
                return;
            }
            InetSocketAddress addressTemp = new InetSocketAddress(ip, port);

            serviceMap.put(className, addressTemp);
            logger.info(">> register " + className + " from " + addressTemp + " success~");
            session.close(false);
            return;
        }

        // subscribe
        else if (line.contains("cmd:sub")) {
            className = line.split(",")[1].split(":")[1];
            InetSocketAddress addressTemp = serviceMap.get(className);
            if (StringUtil.isEmpty(className) || addressTemp == null) {
                logger.error(">> handle sub request from " + address + " fail~ please check it~ ");
                return;
            }

            StringBuffer respLine = new StringBuffer("cmd:subResp")
                    .append(",class:")
                    .append(className)
                    .append(",ip:")
                    .append(addressTemp.getHostName())
                    .append(",port:")
                    .append(addressTemp.getPort());

            write(session, respLine.toString());
            logger.info(">> handle subscribe request " + className + " from " + addressTemp + " success~");
            session.close(false);
            return;
        }
    }

    private void write(IoSession session, String s) {
        session.write(s);
    }

}
