import org.apache.spark.mllib.recommendation.ALS


object GameRecommender extends App{
  val ratingsFile = "data_dir/train-test-split.json"
  val data = DataLoader.loadData(ratingsFile)

  val cachedRatings = data.getTrainRatings.rdd.cache()
  ALS.trainImplicit(cachedRatings, 50, 10)

}
