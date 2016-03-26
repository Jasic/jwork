package org.jasic.jrpc.rserver;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.jasic.modue.annotation.Module;
import org.jasic.modue.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executors;

/**
 * @Author 菜鹰.
 * @Date 2014/12/16
 * <p/>
 * 服务中介者类似catserver
 */
@Module(name = "CatServer", priority = 0, status = true)
public class ConfigServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private IoAcceptor acceptor = null;

    /**
     * 构造函数
     */
    public ConfigServer() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        acceptor = new NioSocketAcceptor();
        /**
         * 定义线程池过滤器（如果不设置则直接使用IoProcess处理handler的内容)
         */
        acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        /**
         * 定义自定义协议解析器
         */
        acceptor.getFilterChain().addLast("cat_codec", new ProtocolCodecFilter(new TextLineCodecFactory()));

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
        acceptor.setHandler(new RServerHandler());
    }

    @Service
    public void service() {
        try {
            int port = 8000;
            acceptor.bind(new InetSocketAddress(port));
            logger.info("Server bind in port [" + port + "] success~");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
    }
}
