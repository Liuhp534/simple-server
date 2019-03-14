package cn.liu.hui.peng.simple.server.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @description: nio客户端
 * @author: hz16092620
 * @create: 2019-03-14 12:48
 */
public class SocketChannelClient {

    public static void main(String[] args) {
        client();
    }

    /**
    * @Description: socketChannel客户端，非selector版本
    * @param
    * @return
    * @throws
    * @author hz16092620
    * @date 2019/3/14 13:52
    */
    private static void client() {
        try {
            SocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
            SocketChannel client = SocketChannel.open();
            client.configureBlocking(Boolean.FALSE);//可以阻塞和非阻塞的
            client.connect(address);
            if (client.finishConnect()) {
                writeToServer(client);
                readFromServer(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * @Description: 请求数据到服务器
    * @param
    * @return
    * @throws
    * @author hz16092620
    * @date 2019/3/14 13:51
    */
    private static void writeToServer(SocketChannel client) {
        try {
            String sendInfo = "come from client !";
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.clear();//切到写入状态
            buffer.put(sendInfo.getBytes());
            buffer.flip();//切换到读状态
            client.write(buffer);//传数据给服务端
            System.out.println("发送数据 : " + sendInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * @Description: 接收服务端的数据
    * @param
    * @return
    * @throws
    * @author hz16092620
    * @date 2019/3/14 13:52
    */
    private static void readFromServer(SocketChannel client) {
        try {
            boolean stop = Boolean.FALSE;
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (!stop) {//非阻塞状态下需要循环处理
                buffer.clear();//切到写入状态
                int byteCount = client.read(buffer);
                if (byteCount > 0) {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    buffer.clear();
                    System.out.println("客户端收到 : " + new String(bytes));
                } else if (byteCount == -1) {
                    System.out.println("退出读取服务端数据...");
                    stop = Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
