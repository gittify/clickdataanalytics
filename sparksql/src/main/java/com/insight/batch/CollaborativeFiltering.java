import scala.Tuple2;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.SparkConf;

/*
 * This class runs the Spark MLLib's ALS algorithm to generate recommendations
 * Recommendations are generated using implicit feedback dataset
 * 
 */
public class CollaborativeFiltering {
	
	SparkConf conf =null;
	JavaSparkContext sc = null;
	
	public void initialize(){
		 conf = new SparkConf().setAppName("Collaborative Filtering Example");
	     sc = new JavaSparkContext(conf);
	}
	
	public JavaRDD createRatingsModel(){

	    // Load and parse the data
	 
	    String path = "/user/filterquery.text/part-00000";
	    JavaRDD<String> data = sc.textFile(path);
	    JavaRDD<Rating> ratings = data.map(
	      new Function<String, Rating>() {
	        public Rating call(String s) {
	        	s = s.replace("[", "");
	        	s = s.replace("]","");
	          String[] sarray = s.split(",");
	          return new Rating(Integer.parseInt(sarray[0]), Integer.parseInt(sarray[1]), 
	                            Double.parseDouble(sarray[2]));
	        }
	      }
	    );
	    
	    return ratings;
	}
	
	public void executeALS(JavaRDD ratings){
		
		// Build the recommendation model using ALS
	    int rank = 10;
	    int numIterations = 10;
	    MatrixFactorizationModel model = ALS.trainImplicit(JavaRDD.toRDD(ratings), rank, numIterations, 0.01,1.0); 

	    // Evaluate the model on rating data
	    JavaRDD<Tuple2<Object, Object>> userProducts = ratings.map(
	      new Function<Rating, Tuple2<Object, Object>>() {
	        public Tuple2<Object, Object> call(Rating r) {
	          return new Tuple2<Object, Object>(r.user(), r.product());
	        }
	      }
	    );
	    JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD.fromJavaRDD(
	      model.predict(JavaRDD.toRDD(userProducts)).toJavaRDD().map(
	        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
	          public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
	            return new Tuple2<Tuple2<Integer, Integer>, Double>(
	              new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
	          }
	        }
	    ));
	    JavaRDD<Tuple2<Double, Double>> ratesAndPreds = 
	      JavaPairRDD.fromJavaRDD(ratings.map(
	        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
	          public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
	            return new Tuple2<Tuple2<Integer, Integer>, Double>(
	              new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
	          }
	        }
	    )).join(predictions).values();
	    double MSE = JavaDoubleRDD.fromRDD(ratesAndPreds.map(
	      new Function<Tuple2<Double, Double>, Object>() {
	        public Object call(Tuple2<Double, Double> pair) {
	          Double err = pair._1() - pair._2();
	          return err * err;
	        }
	      }
	    ).rdd()).mean();
	    System.out.println("Mean Squared Error = " + MSE);

	    // Save and load model
	    model.save(sc.sc(), "myModelPath");
		
	}
	
	public void generateRecommendations(){
		MatrixFactorizationModel sameModel = MatrixFactorizationModel.load(sc.sc(), "myModelPath");
	    
	    Rating[] recommendations = sameModel.recommendProducts(58,10);
	    for (int i = 0; i < recommendations.length; i++){
	        System.out.println(recommendations[i]);
	  }
	}
  public static void main(String[] args) {
    
	  CollaborativeFiltering filter = new CollaborativeFiltering();
	  filter.initialize();
	  JavaRDD ratings = filter.createRatingsModel();
	  filter.executeALS(ratings);
	  filter.generateRecommendations();

    
    
  }
  }