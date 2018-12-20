package cn.liu.hui.peng.simple.server.mq; 

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import cn.liu.hui.peng.simple.server.RabbitConnection;

import com.rabbitmq.client.AMQP.Exchange;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * <p>
 * 
 *
 *
 * </p>
 * @author	hz16092620 
 * @date	2018年11月8日 下午2:57:18
 * @version      
 */
public class DirectExchange {

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
	    String directExchange = "direct_exchange_liuhp";
	    String routingKey = "rabbit_test_routing_key";
	    channel = conn.createChannel();
	    final String queneName = channel.queueDeclare().getQueue();

	    final String uuid = UUID.randomUUID().toString();
	    BasicProperties props = new BasicProperties().builder().correlationId(uuid).replyTo(queneName).build();
	    channel.exchangeDeclare(directExchange, "direct", false);//非持久化的direct类型的exchange
	    channel.basicPublish(directExchange, routingKey, props, "我来自自定义direct exchange".getBytes("utf-8"));// 发送消息
	    System.out.println(directExchange + " exchange, 按照routingKey= " + routingKey + " 发送消息。。。。");
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
 