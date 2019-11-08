package com.steamrec

import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.sql.functions.avg
import org.apache.spark.sql.{DataFrame, Dataset, Row}

import scala.collection.mutable

object Metrics {


  case class RatingItem(item: Int, rating: Float)
  case class GroupedRating(user: Int, recommendations: Array[RatingItem])

  def rr(user: User, ratings: Iterator[Rating[Int]]): Double = {
    val trainGames = user.train_games.map(g => g.game_id).toSet
    val validGames = user.valid_games.map(g => g.game_id).toSet
    val sortedRatings = ratings.toList
      .filter(x => !trainGames.contains(x.item))
      .sortBy(r => -r.rating);
    sortedRatings.zipWithIndex.find((r)=>  validGames contains r._1.item)
      .map(r => 1.0/ (r._2 + 1))
      .getOrElse(0.0)


  }

  def mapItem(uid:Int, ratingItem:Row): Rating[Int] = {
    Rating(uid, ratingItem.getAs[Int]("item"), ratingItem.getAs[Float]("rating"))
  }

  def row2ratings(row: Row): Iterator[Rating[Int]] = {
    val uid = row.getAs[Int](0)
    row.getAs[mutable.WrappedArray[Row]]("recommendations")
      .map(x => mapItem(uid, x)).toIterator
  }

  def mrr(gameData: GameData, ratings: DataFrame): Double = {
    val spark = SparkSessionFactory.get()
    import spark.implicits._
    val rrs: Dataset[Double] = ratings.map(r => rr(gameData.id2User.value(r.getAs("user")), row2ratings(r)))
    rrs.select(avg(rrs.columns(0))).head().getDouble(0)
  }

}
