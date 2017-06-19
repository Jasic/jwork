package org.jasic.ioAndnio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author 菜鹰.
 * @Date 2014/12/18
 */
public class IOServer {

    private ServerSocket serverSocket;

    public static int MAX_INPUT;

    private int port;

    public IOServer() throws IOException {
        this.port = 8000;
        this.serverSocket = new ServerSocket();
        this.serverSocket.bind(new InetSocketAddress(port));
        for (; ; ) {
            final Socket socket = this.serverSocket.accept();
            new Thread() {
                public void run() {
                    try {
                        byte[] input = new byte[MAX_INPUT];
                        socket.getInputStream().read(input);
                        byte[] output = process(input);
                        socket.getOutputStream().write(output);
                    } catch (IOException ex) { /* ... */ }
                }

                private byte[] process(byte[] input) {
                    return new byte[0];
                }
            }.start();
        }
    }




    public static void main(String[] args) throws Exception {
        new Thread() {
            @Override
            public void run() {
                try {
                    new IOServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        new Thread() {
            @Override
            public void run() {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("127.0.0.1", 8000));
                    Thread.sleep(100000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
