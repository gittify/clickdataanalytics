package com.insight.batch;

/**
 * Load some tweets stored as JSON data and explore them.
 */

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


public class SparkBookmark {
  public static void main(String[] args) {
	
    String inputFile = "/user/personin.txt";
    SparkConf conf = new SparkConf();
    conf.set("spark.local.dir","/mnt/my-data/");
    JavaSparkContext sc = new JavaSparkContext(conf);
    SQLContext sqlCtx = new SQLContext(sc);
    DataFrame input = sqlCtx.jsonFile(inputFile);
    // Print the schema
    input.printSchema();
    // Register the input schema RDD
    input.registerTempTable("bookmarks");
 
    DataFrame topUrls = sqlCtx.sql("SELECT h, u, count(u) ,l  FROM bookmarks GROUP BY h,u,l");
 
    
    String[] cols = topUrls.columns();
   
    System.out.println(Arrays.asList(cols));
    topUrls.registerTempTable("urlCount");
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
   urls.coalesce(1,true).saveAsTextFile("/users/distinctlinks.txt");
    List<StructField> fields = new ArrayList<StructField>();
      fields.add(DataTypes.createStructField("url", DataTypes.StringType, true));
      fields.add(DataTypes.createStructField("lid", DataTypes.LongType, false));
      
    
    StructType schema = DataTypes.createStructType(fields);
	DataFrame urlDataFrame = sqlCtx.createDataFrame(urls, schema);
	urlDataFrame.registerTempTable("links");
    
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
    users.coalesce(1,true).saveAsTextFile("/user/distinctusers.txt");
    List<StructField> fieldsUser = new ArrayList<StructField>();
    fieldsUser.add(DataTypes.createStructField("user", DataTypes.StringType, true));
    fieldsUser.add(DataTypes.createStructField("uid", DataTypes.LongType, false));
    
    sc.stop();
  }
}
