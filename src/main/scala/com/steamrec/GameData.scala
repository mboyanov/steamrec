package com.steamrec

import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.sql.{Dataset, SparkSession}

@SerialVersionUID(100L)
class GameData(data: Dataset[User]) extends Serializable {
  val spark: SparkSession = SparkSessionFactory.get()

  import spark.implicits._

  val dataSet: Dataset[User] = data.cache();


  val id2User = spark.sparkContext.broadcast(dataSet.map(u => (u.user_id.toInt, u)).collect().toMap)

  def getTrainRatings(log_smoothing:Boolean = false): Dataset[Rating[Int]] = {
    dataSet.flatMap(u => {
      u.train_games.map(g => toRating(u, g, log_smoothing))
    })
  }

  def getValidRatings: Dataset[Rating[Int]] = {
    dataSet.flatMap(u => {
      u.valid_games.map(g => toRating(u, g))
    })
  }

  private def toRating(u: User, g: Game, log_smoothing:Boolean=false): Rating[Int] = {
    if (log_smoothing) {
      Rating(u.user_id.toInt, g.game_id.toInt, (g.playtime_forever.toFloat + 1) / 60 )

    } else {
      Rating(u.user_id.toInt, g.game_id.toInt, Math.log((g.playtime_forever.toFloat + 1) / 60 ).toFloat)

    }
    Rating(u.user_id.toInt, g.game_id.toInt, (g.playtime_forever.toFloat + 1) / 60 )
  }
}
