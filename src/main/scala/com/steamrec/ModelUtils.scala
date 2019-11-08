package com.steamrec

import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.ml.recommendation.ALSModel
import org.apache.spark.sql.Dataset

object ModelUtils {
  def predict(gameData: GameData, model: ALSModel): Dataset[Rating[Int]] = {
    val numGames = model.itemFactors.count().toInt
    val numUsers = model.userFactors.count().toInt
    val spark = SparkSessionFactory.get()
    import spark.implicits._
    val gamesDS: Dataset[Int] = spark createDataset ((0 until numGames).toList)
    val usersDs: Dataset[Int] = spark createDataset ((0 until numUsers).toList)
    val denseDS: Dataset[Rating[Int]] = usersDs.crossJoin(gamesDS).map(x=> Rating(x.getAs[Int](0), x.getAs[Int](1), 0.0f))
    model.transform(denseDS).as
  }
}
