/**
 * Copyright (c) 2006-2015 Hzins Ltd. All Rights Reserved. 
 *  
 * This code is the confidential and proprietary information of   
 * Hzins. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Hzins,http://www.hzins.com.
 *  
 */   
package cn.liu.hui.peng.simple.server.mq; 

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import cn.liu.hui.peng.simple.server.RabbitConnection;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * <p>
 * 
 * rpc服务器，1、开启队列，2、消费消息，3、把response发送到回调队列。
 *
 * </p>
 * @author	hz16092620 
 * @date	2018年9月16日 上午10:08:23
 * @version      
 */
public class DefaultExchangeQueue {
    
    public static void main(String[] args) {
	consumerMessage();
    }
    
    /**
     * 服务端消费消息
     * */
    public static void consumerMessage() {
	Connection conn = RabbitConnection.createConnection();
	try {
	    String queneName = "rpc_liuhp_quene";
	    final Channel channel = conn.createChannel();
	    channel.queueDeclare(queneName, false, true, false, null);//这边不能是 排外的队列???
	    channel.queueBind(queneName, "", "rabbit_test_routing_key");//绑定默认的exchange
	    
	    //消费消息，推模式
	    channel.basicQos(100);//最多消费消息个数
	    Consumer consumer = new DefaultConsumer(channel) {
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		    /*System.out.println(consumerTag);
		    System.out.println(envelope.getDeliveryTag());
		    System.out.println(envelope.getExchange());
		    System.out.println(envelope.getRoutingKey());*/
		    StringBuilder sb = new StringBuilder();
		    for (byte b : body) {
			sb.append((char) b);
		    }
		    System.out.println("消费者消费消息，收到数据: " + sb.toString());
		    BasicProperties props = new BasicProperties().builder().correlationId(properties.getCorrelationId()).build();
		    //channel.basicAck(envelope.getDeliveryTag(), false);//这个可以确认是否处理消息
		    channel.basicPublish("", properties.getReplyTo(), props, "result".getBytes());
		}
	    };
	    channel.basicConsume(queneName, true, consumer);
	    /*Thread.sleep(30000L);
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
	    }*/
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    
	}
    }

}
 