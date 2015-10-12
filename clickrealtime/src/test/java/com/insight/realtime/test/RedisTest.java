package com.insight.realtime.test;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
public class RedisTest {
 
public static void main(String[] args) {
 String key = "mostUsedLanguages";
 Jedis jedis = new Jedis("ec2-52-88-228-174.us-west-2.compute.amazonaws.com");
 //Adding a value with score to the set
 jedis.zadd(key,100,"Java");//ZADD
 
 //We could add more than one value in one calling
 Map<String, Double> scoreMembers = new HashMap< String,Double>();
 scoreMembers.put( "Python",90d);
 scoreMembers.put( "Javascript",80d);
 jedis.zadd(key, scoreMembers);
 
 //We could get the score for a member
 System.out.println("Number of Java users:" + jedis.zscore(key, "Java"));
 
 //We could get the number of elements on the set
 System.out.println("Number of elements:" + jedis.zcard(key));//ZCARD
 
 
//get all the elements sorted from bottom to top
System.out.println(jedis.zrange(key, 0, -1));
 
/*if(jedis.zcard(key)< 3)
//get all the elements sorted from top to bottom
System.out.println(jedis.zrevrange(key, 0, -1));
else
	*/
System.out.println("****");
	System.out.println(jedis.zrevrange("realtime", 0, 10));
	
	
//We could get the elements with the associated score
Set<Tuple> elements = jedis.zrevrangeWithScores("realtime", 0, -1);
for(Tuple tuple: elements){
System.out.println(tuple.getElement() + "-" + tuple.getScore());
}

//We can increment a score for a element using ZINCRBY
System.out.println("Score before zincrby:" + jedis.zscore(key, "Python"));
//Incrementing the element score
jedis.zincrby(key, 1, "Python1");
System.out.println("Score after zincrby:" + jedis.zscore(key, "Python"));
jedis.zincrby(key, 1, "Python1");
System.out.println("Score after zincrby:" + jedis.zscore(key, "Python1"));
System.out.println(jedis.keys("*"));
System.out.println(jedis.zrange("realtime",0,-1));

}
 
}