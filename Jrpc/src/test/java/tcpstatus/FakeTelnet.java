package tcpstatus;

import org.jasic.util.DateTimeUtil;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 菜鹰.
 * @Date 2014/12/22
 * 模拟timewait
 * 在cmd下netstat -ano|findstr 8000 /v "" /c
 * 所有不匹配(/v )空字符串（""）的行（即非空白行）
 */
public class FakeTelnet {

    /**
     * 前置条件，另一端正常，而客户端主动关闭
     * IOServer#setTestTcpCloseStatus(java.net.Socket)
     *
     * 各种关闭握手的状态
     */
    @Test
    public void testTcpCloseStatus() {

        List<Socket> sockets = new ArrayList<Socket>();
        for (int count = 0; count < 10000; count++) {
            try {
                Socket socket = new Socket();
                sockets.add(socket);
                socket.connect(new InetSocketAddress("127.0.0.1", 8000));
                socket.getOutputStream().write("你好帅哟\n".getBytes());
                socket.getOutputStream().flush();
                socket.getOutputStream().close();
                System.out.println(socket.isClosed());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DateTimeUtil.sleep(1);
        for(Socket socket :sockets){
            System.out.println(socket.isClosed());
            try {
                socket.getOutputStream().write(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DateTimeUtil.sleep(100002);
    }

    /**
     * 前置条件
     * 1、原因：server端主动关闭了socket连接。这时候client端的socket就会接收到一个RST包
     * 2、做法：所以server端要设置 socket.setSoLinger(true, 0); 然后socket.close();
     *
     * 3、结果：如果我们继续对一个已经接收了RST包的socket调用写操作，就会产生
     *
     * 4、brokenPipe错误只在linux下面有出现。
     */
    @Test
    public void testBrokenPipe() {
        for (int count = 0; count < 1; count++) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("127.0.0.1", 8000));
                socket.getOutputStream().write("你好帅哟".getBytes());
                DateTimeUtil.sleep(5);// 等待服务端关闭
                System.out.println(socket.getInputStream().read(new byte[1]));// block
                socket.getOutputStream().write(new byte[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testTcpOption() throws IOException {
        Socket socket = new Socket();
        System.out.println(socket.getSoTimeout());
        socket.setTcpNoDelay(true);
        socket.setReuseAddress(true);// 为了防止time_wait过多，http1.1规端口可以重用
        socket.setSoTimeout(5000);
        socket.setSoLinger(true, 1000);
        socket.setSendBufferSize(1024 * 1024);
        socket.setReceiveBufferSize(1024 * 1024);
        socket.setKeepAlive(true);
        socket.setOOBInline(true);
        socket.setTrafficClass(0x08);
        socket.connect(new InetSocketAddress("10.72.50.133", 8080));

        socket.getOutputStream().write("你好帅哟".getBytes());
        socket.getOutputStream().flush();
        System.out.println(socket.getInputStream().read(new byte[1]));// block
        DateTimeUtil.sleep(111111);
    }
}
