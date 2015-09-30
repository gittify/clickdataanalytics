package com.insight.producer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/*
 * Kafka publisher to simulate real time events
 */
public class BookmarkProducer {
	
	 
	public static void main(String args[]) throws InterruptedException, ExecutionException {
		
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"ec2-52-88-228-174.us-west-2.compute.amazonaws.com:9092,ec2-52-10-129-97.us-west-2.compute.amazonaws.com:9092,ec2-52-88-231-95.us-west-2.compute.amazonaws.com:9092,ec2-52-89-3-2.us-west-2.compute.amazonaws.com:9092");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

		KafkaProducer<String,String> producer = new KafkaProducer<String,String>(props);
		
		boolean sync = false;
		String topic="my-topic";
		String key = "mykey";
	//	String value = "myvalue";
		BufferedReader br=null;
		try {
		 br = new BufferedReader(new FileReader(args[0]));
	    
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
		
	        	String value = line;
	        	System.out.println(value);
		
	        	ProducerRecord<String,String> producerRecord = new ProducerRecord<String,String>(topic, key, value);
						if (sync) {
							producer.send(producerRecord).get();
						} else {
							producer.send(producerRecord);
						}
					
						
				        line = br.readLine();
			   }
	        
	    	} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		  
	        finally {
		        try {
					br.close();
					producer.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
		    }
	
		
	}
}