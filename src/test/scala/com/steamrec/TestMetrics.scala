package com.steamrec

import com.steamrec.Metrics.{GroupedRating, RatingItem}
import org.scalatest.{FlatSpec, Matchers}




class TestMetrics extends FlatSpec with Matchers {

  "mrr" should "be defined" in {
    val spark = SparkSessionFactory.get()
    import spark.implicits._

    val game = Game(0, "CS", BigInt(100))
    val users = List(User(0, List(game), List(game),List.empty))

    val gameData = new GameData( spark.createDataset(users))
    val ratings = spark.createDataset(List(
      GroupedRating(0, Array(RatingItem(0, 2))))
    ).toDF()

    val res = Metrics.mrr(gameData, ratings)
    res should be (0)
  }

  "mrr" should "be 1" in {
    val spark = SparkSessionFactory.get()
    import spark.implicits._

    val game = Game(0, "CS", BigInt(100))
    val users = List(User(0, List(game), List.empty,List(game)))

    val gameData = new GameData( spark.createDataset(users))
    val ratings = spark.createDataset(List(
      GroupedRating(0, Array(RatingItem(0, 2)))
    )).toDF()
    val res = Metrics.mrr(gameData, ratings)
    res should be (1)
  }

  "mrr" should "be 0.5" in {
    val spark = SparkSessionFactory.get()
    import spark.implicits._

    val game = Game(0, "CS", BigInt(100))
    val users = List(User(0, List(game), List.empty,List(game)))

    val gameData = new GameData( spark.createDataset(users))
    val ratings = spark.createDataset(List(
      GroupedRating(0, Array(RatingItem(0, 2))),
      GroupedRating(0, Array(RatingItem(12, 4))))).toDF()
    val res = Metrics.mrr(gameData, ratings)
    res should be (0.5)
  }

}
