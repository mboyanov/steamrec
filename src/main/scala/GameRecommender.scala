import org.apache.spark.mllib.recommendation.ALS


object GameRecommender extends App{
  val ratingsFile = "data_dir/train-test-split.json"
  val data = DataLoader.loadData(ratingsFile)

  val cachedRatings = data.getTrainRatings.rdd.cache()
  val model = ALS.trainImplicit(cachedRatings, 50, 10)
  val ratings = ModelUtils.predict(data, model)
  val mrr = Metrics.mrr(data, ratings)
  print(s"MEAN RECIPROCAL RANK: $mrr")

}
