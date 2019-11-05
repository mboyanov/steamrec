import java.nio.file.{Files, Paths}

import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset}

case class Experiment(rank: Int,
                      iterations: Int,
                      lambda: Double,
                      alpha: Double) {
  def getOutputDir: String = {
    s"rank_${rank}_it_${iterations}_lambda_${lambda}_alpha_${alpha}"
  }
}

object ExperimentRunner {

  def runExperiment(experiment: Experiment, cachedRatings: Dataset[Rating[Int]], data: GameData): Unit = {
    val als = new ALS().setImplicitPrefs(true).setRank(experiment.rank).setRegParam(experiment.lambda)
        .setAlpha(experiment.alpha).setMaxIter(experiment.iterations)
      .setUserCol("user")
      .setItemCol("item")
      .setRatingCol("rating")
    val model = als.fit(cachedRatings)
    model.write.overwrite().save(Paths.get("data_dir", experiment.getOutputDir, "model").toFile.toString)
    //writeFeatures(experiment, model.userFactors, "user_features.txt")
    //writeFeatures(experiment, model.itemFactors,"game_features.txt")
    val ratings = ModelUtils.predict(data, model)
    val mrr = Metrics.mrr(data, ratings)
    Files.write(Paths.get("data_dir", experiment.getOutputDir, "mrr.txt"), mrr.toString.getBytes)
    print(s"MEAN RECIPROCAL RANK: $mrr")
  }
//
//  private def writeFeatures(experiment: Experiment, features: DataFrame, destination: String): Unit = {
//    features.repartition(1).map(x => (x.get(0), x._2.mkString(","))).saveAsTextFile(s"data_dir/${experiment.getOutputDir}/${destination}")
//  }
}
