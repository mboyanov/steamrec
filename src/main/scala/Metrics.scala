import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._

object Metrics {

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

  def mrr(gameData: GameData, ratings: Dataset[Rating[Int]]): Double = {
    val spark = SparkSessionFactory.get()
    import spark.implicits._
    val rrs: Dataset[Double] = ratings.groupByKey(r => r.user).mapGroups((uid, ratings) => rr(gameData.id2User(uid), ratings))
    rrs.select(avg(rrs.columns(0))).head().getDouble(0)
  }

}
