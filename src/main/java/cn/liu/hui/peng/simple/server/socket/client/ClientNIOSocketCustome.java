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
                    System.out.println(buffer);
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
        SocketChannel sc = null;
        try {
            sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int temp = sc.read(buffer); // 从channel读到buffer
            String content = "";
            if (temp > 0) {// 代表读完毕了,准备写(即打印出来)
                buffer.flip(); // 为write()准备
                // =====取出buffer里的数据
                byte[] bytes = new byte[buffer.remaining()]; // 创建字节数组
                buffer.get(bytes);// 将数据取出放到字节数组里
                content += new String(bytes);
                System.out.println("客户端收到 : " + content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
