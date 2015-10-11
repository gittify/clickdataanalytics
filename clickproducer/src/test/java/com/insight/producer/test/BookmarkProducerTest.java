package com.insight.producer.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.mockito.Mockito;

import com.insight.producer.BookmarkProducer;

public class BookmarkProducerTest {

	@Test
	public void test() throws InterruptedException, ExecutionException {
		
		KafkaProducer prodMock = mock(KafkaProducer.class);
		

		prodMock.send(any(ProducerRecord.class),any(Callback.class));
		
		BookmarkProducer simulator = new BookmarkProducer();
		BookmarkProducer simulSpy = Mockito.spy(simulator);
		simulator.simulateBookmarks(prodMock,"src/test/resources/test.txt");
		
		verify(prodMock,times(1)).send(any(ProducerRecord.class),any(Callback.class));
		
	
		
	}
	
	
	

}
