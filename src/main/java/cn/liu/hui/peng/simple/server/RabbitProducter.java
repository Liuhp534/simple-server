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
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * <p>
 * 
  *生产消息
 *
 * </p>
 * @author	hz16092620 
 * @date	2018年9月14日 下午3:20:57
 * @version      
 */
public class RabbitProducter {
    
    public static void main(String[] args) {
	createExchange();
    }
    
    /**
     * 交换器发送数据
     * */
    public static void createExchange() {
	Connection conn = RabbitConnection.createConnection();
	Channel channel = null;
	try {
	    channel = conn.createChannel();
	    channel.exchangeDeclare("liuhp_exchange", "direct", true);//true表示持久化的
	    //channel.queueBind("liuhp_quene", "liuhp_exchange", "rabbit_test_routing_key");
	    //发送消息
	    for (int i = 0; i < 20; i++) {
		channel.basicPublish("liuhp_exchange", "rabbit_test_routing_key", null, String.valueOf(new Random().nextInt(100)).getBytes());
		//channel.basicPublish("liuhp_exchange", "liuhp_quene", null, String.valueOf(new Random().nextInt(100)).getBytes());
	    }
	} catch (IOException e) {
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
 