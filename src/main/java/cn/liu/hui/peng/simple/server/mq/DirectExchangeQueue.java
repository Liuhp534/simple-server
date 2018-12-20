package cn.liu.hui.peng.simple.server.mq; 

import java.io.IOException;

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
 *
 *
 * </p>
 * @author	hz16092620 
 * @date	2018年11月8日 下午3:02:11
 * @version      
 */
public class DirectExchangeQueue {
    public static void main(String[] args) {
	consumerMessage();
    }
    
    /**
     * 服务端消费消息
     * */
    public static void consumerMessage() {
	Connection conn = RabbitConnection.createConnection();
	try {
	    String directExchange = "direct_exchange_liuhp";
	    String queneName = "direct_liuhp_quene";
	    String bindingKey = "rabbit_test_routing_key";
	    final Channel channel = conn.createChannel();
	    channel.queueDeclare(queneName, false, true, false, null);//这边不能是 排外的队列???
	    channel.queueBind(queneName, directExchange, bindingKey);//绑定默认的exchange
	    
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
		    channel.basicPublish("", properties.getReplyTo(), props, "direct queue 返回的数据。".getBytes("utf-8"));
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
 