/**
 * Copyright (c) 2006-2015 Hzins Ltd. All Rights Reserved. 
 *  
 * This code is the confidential and proprietary information of   
 * Hzins. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Hzins,http://www.hzins.com.
 *  
 */   
package cn.liu.hui.peng.simple.server; 

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * <p>
 * 
 * 客户端发送消息，然后接收消息，
 *
 * </p>
 * @author	hz16092620 
 * @date	2018年9月16日 上午10:27:08
 * @version      
 */
public class RpcClient {

    public static void main(String[] args) {
	createClient();
    }

    /**
     * 交换器发送数据
     * */
    public static void createClient() {
	Connection conn = RabbitConnection.createConnection();
	Channel channel = null;
	try {
	    channel = conn.createChannel();
	    final String queneName = channel.queueDeclare().getQueue();

	    final String uuid = UUID.randomUUID().toString();
	    BasicProperties props = new BasicProperties().builder().correlationId(uuid).replyTo(queneName).build();
	    channel.basicPublish("", "rpc_liuhp_quene", props, String.valueOf(new Random().nextInt(100)).getBytes());// 发送消息
	    System.out.println("default exchange \"\" 发送消息。。。。");
	    // 接受返回的结果
	    // 方式一 queneingConsumer 很多的问题，这个暂时不研究
	    // QueueingConsumer consume = new QueueingConsumer(channel);
	    /*
	     * while (true) { QueueingConsumer.Delivery delivery =
	     * consume.nextDelivery(); if
	     * (delivery.getProperties().getCorrelationId().equals(uuid)) {
	     * System.out.println(new String(delivery.getBody())); } break; }
	     */
	    // 方式二 defaultConsumer
	    Consumer consumer = new DefaultConsumer(channel) {

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		    while (true) {
			if (properties.getCorrelationId().equals(uuid)) {
			    StringBuilder sb = new StringBuilder();
			    for (byte b : body) {
				sb.append((char) b);
			    }
			    System.out.println(queneName + " 回调队列接收返回的消息: " + sb.toString());
			}
			break;
		    }
		}
	    };
	    channel.basicConsume(queneName, true, consumer);

	    Thread.sleep(30000L);
	} catch (IOException | InterruptedException e) {
	    e.printStackTrace();
	} finally {
	    try {
		channel.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (TimeoutException e) {
		e.printStackTrace();
	    }
	    try {
		conn.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
 