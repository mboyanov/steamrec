package com.steamrec

import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.sql.{Dataset, SparkSession}

@SerialVersionUID(100L)
class GameData(data: Dataset[User]) extends Serializable {
  val spark: SparkSession = SparkSessionFactory.get()

  import spark.implicits._

  val dataSet: Dataset[User] = data.cache();


  val id2User = spark.sparkContext.broadcast(dataSet.map(u => (u.user_id.toInt, u)).collect().toMap)

  def getTrainRatings: Dataset[Rating[Int]] = {
    dataSet.flatMap(u => {
      u.train_games.map(g => toRating(u, g))
    })
  }

  def getValidRatings: Dataset[Rating[Int]] = {
    dataSet.flatMap(u => {
      u.valid_games.map(g => toRating(u, g))
    })
  }

  private def toRating(u: User, g: Game): Rating[Int] = {
    Rating(u.user_id.toInt, g.game_id.toInt, (g.playtime_forever.toFloat + 1) / 60 )
  }
}
