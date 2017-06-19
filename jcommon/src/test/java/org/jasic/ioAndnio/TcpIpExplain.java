package org.jasic.ioAndnio;

import java.net.Socket;
import java.net.SocketException;

/**
 * @Author 菜鹰.
 * @Date 2014/12/25
 * <p/>
 * 可类似地参考
 * http://hougechuanqi.iteye.com/blog/1243570
 */
public class TcpIpExplain {

    private Socket socket = new Socket();

    /**
     * IP规定了4中服务类型：
     * 1、低成本（0x02）：发送成本低
     * 2、高可靠性（0x04）：保证包数据可靠的发送到目的地
     * 3、高吞吐量（0x08）：一次接受发送大量数据
     * 4、最小延迟（0x10）：传输数据快，最快发送到目的地
     */
    public void ipExplain() throws SocketException {
        socket.setTrafficClass(0x08);
    }

    /**
     * TCP_NODELAY: 表示立即发送数据.禁用nagle算法
     * SO_RESUSEADDR: 表示是否允许重用Socket所绑定的本地地址.
     * SO_TIMEOUT: 表示接收数据时的等待超时数据.
     * SO_LINGER: 表示当执行Socket的close()方法时,是否立即关闭底层的Socket即时底层有数据未发送（使用RST标志）.
     * SO_SNFBUF: 表示发送数据的缓冲区的大小.
     * SO_RCVBUF: 表示接收数据的缓冲区的大小.
     * SO_KEEPALIVE: 表示对于长时间处于空闲状态的Socket , 是否要自动把它关闭.
     * OOBINLINE: 表示是否支持发送一个字节的TCP紧急数据.
     */
    public void tcpExplain() throws SocketException {
        socket.setTcpNoDelay(true);
        socket.setReuseAddress(true);// 为了防止time_wait过多，http1.1规端口可以重用
        socket.setSoTimeout(5000);
        socket.setSoLinger(true, 0);
        socket.setSendBufferSize(1024 * 1024);// 低层不处理的咯
        socket.setReceiveBufferSize(1024 * 1024);
        socket.setKeepAlive(true);
        socket.setOOBInline(true);
    }

//    {
//        boolean on = true;
//        switch (opt) {
//
//            case SO_LINGER:// socket.setTcpNoDelay(true);
//                break;
//            case SO_TIMEOUT:// socket.setSoTimeout(5000);
//                break;
//            case IP_TOS:// socket.setTrafficClass(0x08)（在Java普通socket只有此option）
//                break;
//            case SO_BINDADDR: // 好明显直接抛错
//                throw new SocketException("Cannot re-bind socket");
//            case TCP_NODELAY:// socket.setTcpNoDelay(true);
//                break;
//            case SO_SNDBUF:// 发送缓存底层不作处理
//            case SO_RCVBUF: //socket.setReceiveBufferSize(1024 * 1024);
//                break;
//            case SO_KEEPALIVE:// socket.setKeepAlive(true);
//                break;
//            case SO_OOBINLINE://  socket.setOOBInline(true);
//                break;
//            case SO_REUSEADDR:// socket.setReuseAddress(true);
//                break;
//            default:
//                throw new SocketException("unrecognized TCP option: " + opt);
//        }
//        socketSetOption(opt, on, val);
//    }

}
