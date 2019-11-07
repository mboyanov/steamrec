import org.slf4j.{Logger, LoggerFactory}




object GameRecommender extends App{
  private lazy val logger: Logger = LoggerFactory.getLogger(GameRecommender.getClass)
  val ratingsFile = "data_dir/train-test-split-v2.json"
  val data = DataLoader.loadData(ratingsFile,8)
  val ex = Experiment(100, 5, 0.01, 10)
  val cachedRatings = data.getTrainRatings.cache()
  logger.info("Ratings loaded, cached")
  ExperimentRunner.runExperiment(ex, cachedRatings, data)

}
