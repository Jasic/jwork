package tcpstatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author 菜鹰.
 * @Date 2014/12/18
 */
class IOServer {

    private ServerSocket serverSocket;

    public static int MAX_INPUT = 10;

    private int port;

    public IOServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket();
        this.serverSocket.bind(new InetSocketAddress(port));
        System.out.println("绑定端口[" + this.port + "]成功...");
        for (; ; ) {
            final Socket socket = this.serverSocket.accept();
            System.out.println("接收到一个新请求[" + socket + "]");
            new Thread() {
                public void run() {
                    try {
                        byte[] input = new byte[MAX_INPUT];
                        socket.getInputStream().read(input);
                        process(input);
                        setTestTcpCloseStatus(socket);
//                        setTestBrokePipe(socket);
                        if (socket.isClosed())
                            System.out.println("关闭链接[" + socket + "]");

                    } catch (IOException ex) {
                        ex.printStackTrace(); /* ... */ }
                }

                private byte[] process(byte[] input) {
                    System.out.println("接收到客户端的数据:" + new String(input));
                    return new byte[1];
                }
            }.start();
        }
    }


    /**
     * 协助 testBrokenPipe
     *
     * @param socket
     * @throws IOException
     */
    public void setTestBrokePipe(Socket socket) throws IOException {
        socket.setSoLinger(true, 0);
        socket.close();
    }

    /**
     * socket.close(); //Connect reset write 需要配合并闭
     *
     * @param socket
     * @throws IOException
     */
    public void setTestTcpCloseStatus(Socket socket) throws IOException {
    }

    public static void main(String[] args) throws IOException {
        new IOServer(8000);

    }



}
