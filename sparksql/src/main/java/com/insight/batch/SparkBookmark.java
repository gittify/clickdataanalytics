package com.insight.batch;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import scala.Tuple2;

/*
 * Spark SQL to process the raw JSON data file 
 * Extract users and links for the recommendation algorithm 
 */

public class SparkBookmark {
 
	String inputFile = null;
	SparkConf conf = null;
	JavaSparkContext sc = null;
	SQLContext sqlCtx = null;
	
	String distinctUsersFile = null;
	String distinctLinksFile = null;
	
	/* 
	 * initialize spark config and sql context
	 * Inputs are the input file to process
	 * outLinks and outUsers are files to output distinct links and distinct users
	 * 
	 */
	public void initialize(String inFile, String outLinks, String outUsers){
		inputFile = inFile;   //"/user/personin.txt";
	    conf = new SparkConf();
	    conf.set("spark.local.dir","/mnt/my-data/");
	    conf.setMaster("spark://ec2-52-88-228-174.us-west-2.compute.amazonaws.com:7077");
	    conf.setAppName("Simple Application");
	    sc = new JavaSparkContext(conf);
	    sqlCtx = new SQLContext(sc);
	    distinctUsersFile = outUsers;
	    distinctLinksFile = outLinks;
	}
	
	
	/*
	 * Read raw JSON data file and create bookmarks temp table 
	 * 
	 */
	public void createBookmarks(){
		DataFrame input = sqlCtx.jsonFile(inputFile);
	    // Print the schema
	    input.printSchema();
	    // Register the input schema RDD
	    input.registerTempTable("bookmarks");
	}
	
	
	/*
	 * group data by urls
	 * 
	 */
	public DataFrame groupData() {
		DataFrame topUrls = sqlCtx.sql("SELECT h, u, count(u) ,l  FROM bookmarks GROUP BY h,u,l");
		 
	    String[] cols = topUrls.columns();
	   
	    System.out.println(Arrays.asList(cols));
	    topUrls.registerTempTable("urlCount");
	    
	    return topUrls;
	}
	
	/*
	 * get distinct links and assign each link with an id
	 * 
	 * */
	
	public void processlinks(DataFrame topUrls){
		  JavaRDD<String> topUrlText = topUrls.toJavaRDD().map(new Function<Row, String>() {
		        public String call(Row row) {
		          return row.getString(1);
		        }});
		    System.out.println(topUrlText.collect());
		    JavaPairRDD<String, Long> distinctUrls = topUrlText.distinct().zipWithUniqueId();
		    JavaRDD<Row> urls = distinctUrls.map(new Function<Tuple2<String, Long>,Row>() {
		        public Row call(Tuple2<String,Long>val) throws Exception {
			          
		        	//Url top = new Url(val._1(),val._2());               	           
		            return  RowFactory.create(val._1(),val._2());
		        }
		    });
		   urls.coalesce(1,true).saveAsTextFile(distinctLinksFile);
		    List<StructField> fields = new ArrayList<StructField>();
		      fields.add(DataTypes.createStructField("url", DataTypes.StringType, true));
		      fields.add(DataTypes.createStructField("lid", DataTypes.LongType, false));
		      
		    
		    StructType schema = DataTypes.createStructType(fields);
			DataFrame urlDataFrame = sqlCtx.createDataFrame(urls, schema);
			urlDataFrame.registerTempTable("links");
	}
	
	/* 
	 * get distinct users and assign each with a unique id 
	 *
	 */
	
	public void processUsers(DataFrame topUrls){
		JavaRDD<String> userText = topUrls.toJavaRDD().map(new Function<Row, String>() {
	        public String call(Row row) {
	          return row.getString(3);
	        }});
	   
	    JavaPairRDD<String, Long> distinctUsers = userText.distinct().zipWithUniqueId();
	    JavaRDD<Row> users = distinctUsers.map(new Function<Tuple2<String, Long>,Row>() {
	        public Row call(Tuple2<String,Long>val) throws Exception {
		                     	
	            return RowFactory.create(val._1(),val._2());
	        }
	    });
	 //   System.out.println(users.collect());
	    users.coalesce(1,true).saveAsTextFile(distinctUsersFile);  
	    List<StructField> fieldsUser = new ArrayList<StructField>();
	    fieldsUser.add(DataTypes.createStructField("user", DataTypes.StringType, true));
	    fieldsUser.add(DataTypes.createStructField("uid", DataTypes.LongType, false));
	    
	    
	    StructType schemaUser = DataTypes.createStructType(fieldsUser);
		DataFrame userDataFrame = sqlCtx.createDataFrame(users, schemaUser);
	    userDataFrame.registerTempTable("users");
	    DataFrame filterQuery = sqlCtx.sql(
			    "SELECT uid, lid, c2 from users u, links l, urlCount b where u.user = b.l and l.url=b.u ");
	  // filterQuery.collect();
	    filterQuery.toJavaRDD().coalesce(1, true).saveAsTextFile("/user/filterquery.text");
	}
	
	/*
	 * stop spark context
	 *
	 */
	public void shutdown(){
		sc.stop();
	}
	public static void main(String[] args) {
	
	SparkBookmark bkmark = new SparkBookmark();
    bkmark.initialize(args[0],args[1],args[2]);
    DataFrame urlData = bkmark.groupData();
    bkmark.processlinks(urlData);
    bkmark.processUsers(urlData);
    bkmark.shutdown();
    
  }
}
