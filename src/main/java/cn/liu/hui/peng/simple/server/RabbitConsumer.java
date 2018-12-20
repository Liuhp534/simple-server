package cn.liu.hui.peng.simple.server; 

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
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
 * @date	2018年9月14日 下午3:22:20
 * @version      
 */
public class RabbitConsumer {

    
    public static void main(String[] args) {
	consumerMessage();
    }
    
    /**
     * 消费消息
     * */
    public static void consumerMessage() {
	Connection conn = RabbitConnection.createConnection();
	try {
	    String queneName = "liuhp_quene";
	    final Channel channel = conn.createChannel();
	    channel.queueDeclare(queneName, false, false, false, null);
	    channel.queueBind(queneName, "liuhp_exchange", "rabbit_test_routing_key");//一定需要绑定
	    
	    //消费消息，推模式
	    channel.basicQos(10);
	    channel.basicConsume(queneName, true, new DefaultConsumer(channel) {

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
		    System.out.println(sb.toString());
		    //channel.basicAck(envelope.getDeliveryTag(), false);//这个可以确认是否处理消息
		}
		
	    });
	    Thread.sleep(30000L);
	    /*try {
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
	} catch (IOException | InterruptedException e) {
	    e.printStackTrace();
	} finally {
	    
	}
    }
}
 