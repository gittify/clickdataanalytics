package com.insight.realtime;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
//import java.util.logging.Logger;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import redis.clients.jedis.Jedis;
import scala.Tuple2;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
 * Spark Stream Real time processing - using Redis as the store
 */
public class StreamData {
	
	final static Logger logger = Logger.getLogger(StreamData.class);
	static Properties properties = new Properties();
	
	 /*
	 * Wrap the connection as Spark streaming can only accept serializable objs
	 */
	 public static class MyRedisConnection implements Serializable{
		
			private static final long serialVersionUID = 1L;
			 public static Jedis jedis = new Jedis(properties.getProperty("redisServer"));
    }
	 
	 /*
	  * Setup spark config and listen to the incoming stream on my-topic
	  * Process the streams and store urls in Redis
	  */
    public static void processRealTimeStream(){
    	try {
  		  properties.load(new FileInputStream("src/main/resources/dev.properties"));
  		} catch (IOException e) {
  		  
  		}
      
      String zkQuorum = properties.getProperty("zookeeper");
      String group = properties.getProperty("group");
      SparkConf conf = new SparkConf().setAppName(properties.getProperty("sparkApp"));
      // Create a StreamingContext with a 1 second batch size
      JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(5000));
      jssc.checkpoint("checkpoint");
      Map<String, Integer> topics = new HashMap<String, Integer>();
      topics.put(properties.getProperty("kafkaTopic"), 1);
      JavaPairDStream<String, String> input = KafkaUtils.createStream(jssc, zkQuorum, group, topics);
      input.print();
      
      
      insertIntoRedis(input);
   
     
      // start our streaming context and wait for it to "finish"
      jssc.start();
      // Wait for 10 seconds then exit. To run forever call without a timeout
      jssc.awaitTermination();
      // Stop the streaming context
      jssc.stop();
  	}

	protected static void insertIntoRedis(JavaPairDStream<String, String> input) {
		final String redisKey =properties.getProperty("redisKey");
		  final ObjectMapper mapper = new ObjectMapper();
		  JavaDStream<Bookmark> data = input.map(new Function<Tuple2<String, String>, Bookmark>() 
		          {
		              public Bookmark call(Tuple2<String, String> message) throws JsonParseException, JsonMappingException, IOException
		              {
		              	Bookmark bkmark = mapper.readValue(message._2(), Bookmark.class);
		              	MyRedisConnection.jedis.zincrby(redisKey, 1d,bkmark.toCustomString());
		                  return bkmark;
		              }
		          }
		          );          
		  data.print();
	}
    
  @SuppressWarnings("deprecation")
public static void main(String[] args) throws Exception {
  
	
	processRealTimeStream();
	
  }
  }
