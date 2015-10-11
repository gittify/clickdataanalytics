package com.insight.producer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;

/*
 * Kafka publisher to simulate real time event
 * This will read a file of raw json data and play the bookmarks
 */
public class BookmarkProducer {
	
	final static Logger logger = Logger.getLogger(BookmarkProducer.class);
	
	public void simulateBookmarks(KafkaProducer<String,String> producer,String fileName){
		
		boolean sync = false;
		//same topic as consumer
		String topic="my-topic";
		//key could be any string
		String key = "mykey";
	
		if(fileName != null){
				BufferedReader br=null;
				try {
				 br = new BufferedReader(new FileReader(fileName));
			    
			      
			        String line = br.readLine();
		
			        while (line != null) {
				
			        	String value = line;
			        	logger.debug(value);
				
			        	ProducerRecord<String,String> producerRecord = new ProducerRecord<String,String>(topic, key, value);
								if (sync) {
									producer.send(producerRecord).get();
								} else {
									producer.send(producerRecord);
								}
							
								
						        line = br.readLine();
					   }
				}      
	    	 catch (FileNotFoundException e) {
	    		 logger.error("File not found exception :"+e);						
			} catch (IOException e) {
				logger.error("IOException  :"+e);
			} catch (InterruptedException e) {
				logger.error("IOException  :"+e);
			} catch (ExecutionException e) {
				logger.error("IOException  :"+e);
			}
		  
	        finally {
		        try {
					br.close();
					producer.close();
				} catch (IOException e) {
					
					logger.error("IOException  :"+e);
				}
		    }
		}
		
	
	}
	 
	public static void main(String args[]) throws InterruptedException, ExecutionException {
		
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"ec2-52-88-228-174.us-west-2.compute.amazonaws.com:9092,ec2-52-10-129-97.us-west-2.compute.amazonaws.com:9092,ec2-52-88-231-95.us-west-2.compute.amazonaws.com:9092,ec2-52-89-3-2.us-west-2.compute.amazonaws.com:9092");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

		KafkaProducer<String,String> producer = new KafkaProducer<String,String>(props);
		
		BookmarkProducer simulator = new BookmarkProducer();
		simulator.simulateBookmarks(producer,args[0]);
		
	}
}