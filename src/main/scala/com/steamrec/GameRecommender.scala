package com.steamrec

import org.slf4j.{Logger, LoggerFactory}

object GameRecommender extends App{
  private lazy val logger: Logger = LoggerFactory.getLogger(GameRecommender.getClass)
  val ratingsFile = "data_dir/train-test-split-v2.json"
  val data = DataLoader.loadData(ratingsFile,8)
  val cachedRatings = data.getTrainRatings().cache()

  val factors = Array(2, 5, 10, 20, 50, 100, 500)
  val reg = Array(0, 1e-5, 1e-3, 1e-2, 1e-1, 1, 10)
  val alphas = Array(1, 2, 5, 10, 20, 50, 100)
  for (f<- factors; r <- reg; a <- alphas) {
    val ex = Experiment(f, 10, r, a, log_smoothing = false)
    logger.info("Running experiment "+ex.toString)
    ExperimentRunner.runExperiment(ex, cachedRatings, data)
  }


}
