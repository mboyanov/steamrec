package com.steamrec

import java.nio.file.{Files, Paths}

import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.sql.Dataset

object ExperimentRunner {

  def runExperiment(experiment: Experiment, cachedRatings: Dataset[Rating[Int]], data: GameData): Unit = {
    val als = new ALS()
        .setImplicitPrefs(true)
        .setRank(experiment.rank)
        .setRegParam(experiment.lambda)
        .setAlpha(experiment.alpha)
        .setMaxIter(experiment.iterations)
        .setUserCol("user")
        .setItemCol("item")
        .setRatingCol("rating")
    val model = als.fit(cachedRatings)
    model.write.overwrite().save(Paths.get("data_dir", experiment.getOutputDir, "model").toFile.toString)
    val ratings = model.recommendForAllUsers(100)

    val mrr = Metrics.mrr(data, ratings)
    Files.write(Paths.get("data_dir", experiment.getOutputDir, "mrr.txt"), mrr.toString.getBytes)
    print(s"MEAN RECIPROCAL RANK: $mrr")
  }
//
//  private def writeFeatures(experiment: Experiment, features: DataFrame, destination: String): Unit = {
//    features.repartition(1).map(x => (x.get(0), x._2.mkString(","))).saveAsTextFile(s"data_dir/${experiment.getOutputDir}/${destination}")
//  }
}
