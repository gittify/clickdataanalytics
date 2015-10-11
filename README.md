##Click Data Analytics

  A platform for generating insights on millions of user clicks


 * Provide a platform for analyzing the most popular links on the web
 * View trends related to location, device, user-agent, and referrals


#### Technologies Used

- Kafka
- Camus
- HDFS
- Spark
- Redis
- Cassandra
- Flask
- Python
- Java

##### Directories
-clickproducer - Simulate stream of bookmarks as input to Kafka
-clickrealtime - Stream processing using Spark Stream and Redis
- sparksql - batch processing using SparkSql and Cassandra

##### Cassandra table Schema

CREATE TABLE playground.recommender (
    user text,
    url text,
    PRIMARY KEY (user, url)
) WITH CLUSTERING ORDER BY (url ASC)


###### Usage
-Run Realtime Job:
spark-submit --class com.insight.batch.SparkBookmark --master spark://ip-172-xx-xx-xxx:7077  /pathto/spark-batch-0.0.1-SNAPSHOT.jar

-Run Collaborative filtering algorithm:
spark-submit --class CollaborativeFiltering --master spark://ip-172-xx-xx-xxx:7077  /pathto/spark-batch-0.0.1-SNAPSHOT.jar








  
  
