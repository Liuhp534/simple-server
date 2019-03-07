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
 * @description: http服务器
 * @author: liuhp534
 * @create: 2019-01-19 13:46
 */
public class HttpServerCustome {

    public static void main(String[] args) {
        createHttpServer();
    }

    /**
    * @Description:
    *
    * http服务器
    *
    * @param
    * @return
    * @throws
    * @author Liuhp534
    * @date 2019/1/19 14:58
    */
    private static void createHttpServer() {
        try {
            //创建channel
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(8080));
            ssc.configureBlocking(Boolean.FALSE);
            //注册selector
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select(3000) == 0) {
                    System.out.println("等待请求超时......");
                    continue;
                }
                System.out.println("处理请求中......");
                //获取selectionkey
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                SelectionKey tempKey = null;
                while (it.hasNext()) {
                    tempKey = it.next();
                    Thread thread = new Thread(new HttpHandler(tempKey));
                    thread.run();
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class HttpHandler implements  Runnable {
        private int bufferSize = 1024;
        private String localCharset = "UTF-8";
        private SelectionKey key;

        public HttpHandler (SelectionKey key) {
            this.key = key;
        }

        public HttpHandler (int bufferSize, String localCharset) {
            this.bufferSize = bufferSize;
            this.localCharset = localCharset;
        }

        @Override
        public void run() {
            try {
                if (key.isAcceptable()) {
                    this.handlerAccept();
                } else if (key.isReadable()) {
                    this.handlerRead();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void handlerRead() throws IOException {
            //从selector中获取channel
            SocketChannel channel = (SocketChannel) this.key.channel();
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
                //System.out.println("received string : " + receivedStr);
                //打印请求信息
                String[] requestMessages = receivedStr.split(System.lineSeparator());
                for (String message :requestMessages) {
                    System.out.println(message);
                }
                //打印首行信息
                String[] firstLine = requestMessages[0].split(" ");
                String method = firstLine[1];
                System.out.println("--------------------");
                System.out.println("Method : " + firstLine[0]);
                System.out.println("url : " + firstLine[1]);
                System.out.println("Http version : " + firstLine[2]);
                System.out.println("--------------------");
                //返回数据给客户端
                StringBuilder sb = new StringBuilder();
                sb.append("HTTP/1.1 200 OK" + System.lineSeparator())
                        .append("Content-Type:text/html;charset=UTF-8" + System.lineSeparator())
                        .append(System.lineSeparator())
                        .append("<html><head><title>显示报文</title></head><body>")
                        .append("显示的报文 : <br>");
                for (String message : requestMessages) {
                    sb.append(message + "<br>");
                }
                sb.append("</body></html>");
                if ("/".equals(method)) {
                    buffer = ByteBuffer.wrap(sb.toString().getBytes(localCharset));
                } else {
                    buffer = ByteBuffer.wrap(" ".getBytes(localCharset));
                }
                channel.write(buffer);
                channel.close();
            }
        }

        public void handlerAccept() throws IOException {
            //从selector中获取channel
            SocketChannel channel = ((ServerSocketChannel) this.key.channel()).accept();
            channel.configureBlocking(Boolean.FALSE);
            channel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
        }

    }















}
