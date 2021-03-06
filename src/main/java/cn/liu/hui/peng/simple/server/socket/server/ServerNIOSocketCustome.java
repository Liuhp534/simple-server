package cn.liu.hui.peng.simple.server.socket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @description: nio服务端
 * @author: liuhp534
 * @create: 2019-01-19 13:46
 */
public class ServerNIOSocketCustome {

    public static void main(String[] args) {
        createNIOServer();
    }

    /**
    * @Description:
    *
    * nio服务端的socket
    *
    * @param
    * @return
    * @throws
    * @author Liuhp534
    * @date 2019/1/19 14:58
    */
    private static void createNIOServer() {
        try {
            //创建channel
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(8080));
            ssc.configureBlocking(Boolean.FALSE);
            //注册selector
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            Handler handler = new Handler();
            while (true) {
                if (selector.select(3000) == 0) {
                    //System.out.println("等待请求超时......");
                    continue;
                }
                System.out.println("处理请求中......");
                //获取selectionkey
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                SelectionKey tempKey = null;
                while (it.hasNext()) {
                    tempKey = it.next();
                    if (tempKey.isAcceptable()) {
                        handler.handlerAccept(tempKey);
                    } else if (tempKey.isReadable()) {
                        handler.handlerRead(tempKey);
                    } else if (tempKey.isWritable()) {
                        handler.handlerWrite(tempKey);
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler {
        private int bufferSize = 1024;
        private String localCharset = "UTF-8";
        private int count = 1;

        public Handler () {

        }

        public Handler (int bufferSize, String localCharset) {
            this.bufferSize = bufferSize;
            this.localCharset = localCharset;
        }

        public void handlerWrite(SelectionKey key) {
            System.out.println("handlerWrite " + count);
            SocketChannel channel = null;
            try {
                Thread.sleep(1L);
                //从selector中获取channel
                channel = (SocketChannel) key.channel();
                //返回数据给客户端
                ByteBuffer buffer = (ByteBuffer) key.attachment();
                //ByteBuffer buffer = ByteBuffer.wrap(("come from server !" + count).getBytes());
                buffer.put(("come from server !" + count).getBytes());
                count ++;
                buffer.flip();
                System.out.println("limit : " + buffer.limit());
                channel.write(buffer);
                //channel.close();//不关闭可以一直执行
                buffer.clear();
                if (count > 100) {
                    count = 1;
                    channel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                key.cancel();
                try {
                    channel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        public void handlerRead(SelectionKey key) throws IOException {
            System.out.println("handlerRead");
            /*try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //从selector中获取channel
            SocketChannel channel = (SocketChannel) key.channel();
            Selector selector = key.selector();
            //获取buffer
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            buffer.clear();
            //没有内容则关闭
            if (channel.read(buffer) == -1) {
                channel.close();
            } else {
                //将buffer转化为可读状态
                buffer.flip();
                //将buffer接收中接收的值按照localCharset编码
                String receivedStr = Charset.forName(localCharset).decode(buffer).toString();
                buffer.clear();
                System.out.println("received string : " + receivedStr);

                SelectionKey writeKey = channel.register(selector, SelectionKey.OP_WRITE, ByteBuffer.allocate(bufferSize));
            }
        }

        public void handlerAccept(SelectionKey key) throws IOException {
            System.out.println("handlerAccept");
            //从selector中获取channel
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(Boolean.FALSE);
            channel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
        }

    }















}
