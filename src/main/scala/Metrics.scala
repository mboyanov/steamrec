import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.sql.Dataset

object Metrics {

  def rr(user: User, ratings: Iterator[Rating], id2game: Array[String]): Double = {
    val trainGames = user.train_games.map(g => g.name).toSet
    val validGames = user.valid_games.map(g => g.name).toSet
    val sortedRatings = ratings.toList
      .filter(x => !trainGames.contains(id2game(x.product)))
      .sortBy(r => -r.rating);
    sortedRatings.zipWithIndex.find((r)=>  validGames contains id2game(r._1.product))
      .map(r => 1.0/ (r._2 + 1))
      .getOrElse(0.0)


  }

  def mrr(gameData: GameData, ratings: Dataset[Rating]): Double = {
    val spark = SparkSessionFactory.get()
    import spark.implicits._
    val rrs = ratings.groupByKey(r => r.user).mapGroups((uid, ratings) => rr(gameData.id2User(uid), ratings, gameData.id2game))
    rrs.createOrReplaceTempView("df")
    rrs.sqlContext.sql("select avg(value) as avg from df").head().getAs[Double]("avg")
  }

}
