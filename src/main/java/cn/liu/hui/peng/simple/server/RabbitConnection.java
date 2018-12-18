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
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * <p>
 * 
 *
 *
 * </p>
 * @author	hz16092620 
 * @date	2018年9月14日 上午10:57:23
 * @version      
 */
public class RabbitConnection {
    
    private final static String HOST = "192.168.10.59";//192.168.10.59
    
    private final static String VIRTUALHOST = "/";
    
    private final static int PORT = 5672;
    
    private final static String USERNAME = "it";
    
    private final static String PASSWORD = "its123";
    
    
    /**
     * 获取connection
     * */
    public static Connection createConnection() {
	Connection conn = null;
	//创建connection工厂
	ConnectionFactory factory = new ConnectionFactory();
	factory.setHost(HOST);
	factory.setPort(PORT);
	factory.setVirtualHost(VIRTUALHOST);
	factory.setUsername(USERNAME);
	factory.setPassword(PASSWORD);
	//通过uri的方式,有错误搞不定
	/*try {
	    //URI uri = new URI("amqp", USERNAME + ":" + PASSWORD, HOST, PORT, VIRTUALHOST, "", "");
	    URI uri = new URI("amqp://" + USERNAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + VIRTUALHOST);
	    factory.setUri(uri);
	} catch (URISyntaxException e1) {
	    e1.printStackTrace();
	} catch (KeyManagementException e) {
	    e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}*/
	try {
	    conn = factory.newConnection();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (TimeoutException e) {
	    e.printStackTrace();
	}
	return conn;
    }

}
 