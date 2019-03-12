package cn.liu.hui.peng.simple.server.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @description: 客户端socket
 * @author: hz16092620
 * @create: 2019-01-15 17:57
 */
public class ClientSocketCustome {

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            clientCreate();
            System.out.println("client send");
        }

    }

    /**
    * @Description: socket客户端
    * @param
    * @return
    * @throws
    * @author hz16092620
    * @date 2019/1/15 22:23
    */
    private static void clientCreate() {
        String data = "client data";
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            //写数据
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(data);
            writer.flush();

            //读数据
            //BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //System.out.println("server response data : " + reader.readLine());

            writer.close();
            //reader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
