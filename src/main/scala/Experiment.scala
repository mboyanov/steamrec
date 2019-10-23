import java.nio.file.{Files, Paths}

import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD

case class Experiment(rank: Int,
                      iterations: Int,
                      lambda: Double,
                      alpha: Double) {
  def getOutputDir: String = {
    s"rank_${rank}_it_${iterations}_lambda_${lambda}_alpha_${alpha}"
  }
}

object ExperimentRunner {

  def runExperiment(experiment: Experiment, cachedRatings: RDD[Rating], data: GameData): Unit = {
    val model = ALS.trainImplicit(cachedRatings, experiment.rank, experiment.iterations, lambda = experiment.lambda, alpha = experiment.alpha)
    writeFeatures(experiment, model.userFeatures, "user_features.txt")
    writeFeatures(experiment, model.productFeatures,"game_features.txt")
    val ratings = ModelUtils.predict(data, model)
    val mrr = Metrics.mrr(data, ratings)
    Files.write(Paths.get("data_dir", experiment.getOutputDir, "mrr.txt"), mrr.toString.getBytes)
    print(s"MEAN RECIPROCAL RANK: $mrr")
  }

  private def writeFeatures(experiment: Experiment, features: RDD[(Int, Array[Double])], destination: String): Unit = {
    features.repartition(1).map(x => (x._1, x._2.mkString(","))).saveAsTextFile(s"data_dir/${experiment.getOutputDir}/${destination}")
  }
}
