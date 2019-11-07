import java.util.stream.Collectors

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.ml.recommendation.ALS.Rating
import org.apache.spark.sql.{Dataset, SparkSession}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable


case class Game(game_id:BigInt, name: String, playtime_forever: BigInt)

case class User(user_id: BigInt, games: List[Game], train_games: List[Game], valid_games: List[Game])


object DataLoader {
  val Logger: Logger = LoggerFactory.getLogger(DataLoader.getClass)
  val spark = SparkSessionFactory.get()

  import spark.implicits._

  def loadData(path: String, numPartitions: Int = 8): GameData = {
    new GameData(spark.read.json(path).as[User])
  }

}

@SerialVersionUID(100L)
class GameData(data: Dataset[User]) extends Serializable {
  val spark: SparkSession = SparkSessionFactory.get()

  import spark.implicits._

  val dataSet: Dataset[User] = data.cache();


  val id2User = dataSet.map(u => (u.user_id.toInt, u)).collect().toMap

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