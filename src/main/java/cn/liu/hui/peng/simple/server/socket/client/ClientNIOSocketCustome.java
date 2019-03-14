package cn.liu.hui.peng.simple.server.socket.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @description: nio客户端
 * @author: liuhp534
 * @create: 2019-01-19 15:02
 */
public class ClientNIOSocketCustome {

    public static void main(String[] args) {
        //for (int i = 0; i < 2; i ++) {
            createNIOSocket();
        //}
    }

    private static void createNIOSocket() {
        SocketChannel channel = null;
        Selector selector = null;
        boolean blockFlag = Boolean.FALSE;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(blockFlag);
            channel.connect(new InetSocketAddress("192.168.11.76", 8080));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);
            if (blockFlag) {
                System.out.println("阻塞模式下...");
            } else {
                System.out.println("非阻塞模式下...");
            }
            while (true) {
                if (selector.select(3000) == 0) {
                    System.out.println("等待请求超时......");
                    continue;
                }
                System.out.println("处理请求中......");
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isConnectable()) {
                        handleConnection(key);
                    } if (key.isReadable()) {
                        handleReadable(key);
                    }
                    it.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void handleConnection(SelectionKey key) {
        System.out.println("handler connection !");
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.finishConnect()) {
                Selector selector = key.selector();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                String info = "I'm information from client";
                buffer.clear();
                buffer.put(info.getBytes());
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.println(info);
                    channel.write(buffer);
                }
                channel.register(selector, SelectionKey.OP_READ);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleReadable(SelectionKey key) {
        System.out.println("handler read !");
        SocketChannel client = null;
        try {
            while (Boolean.TRUE) {//非阻塞的可能返回0，所以需要循环，异常则推出。
                Selector selector = key.selector();
                client = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(10);
                int temp = client.read(buffer); // 从channel读到buffer，服务端一直写入，那么每次从buffer中拿的数据不一定
                if (temp > 0) {// 代表读完毕了
                    buffer.flip(); // 为write()准备
                    byte[] bytes = new byte[buffer.remaining()]; // 创建字节数组
                    buffer.get(bytes);// 将数据取出放到字节数组里
                    buffer.clear();
                    System.out.println("客户端收到 : " + new String(bytes));
                } else if (temp == -1) {
                    System.out.println("退出读取服务端数据...");
                    break;
                }
                //client.register(selector, SelectionKey.OP_READ, buffer);//不能重复注册
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
