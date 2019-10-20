import java.util.stream.Collectors


import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.collection.mutable


case class Game(appid: BigInt, name: String, playtime_forever: BigInt)

case class User(steam_id: String, games: List[Game], train_games: List[Game], valid_games: List[Game])


object DataLoader {
  val spark = SparkSessionFactory.get()

  import spark.implicits._

  def loadData(path: String, numPartitions: Int = 8): GameData = {
    new GameData(spark.read.json(path).repartition(numPartitions).as[User])
  }

  def getMappings(values: Dataset[String]): (Array[String], mutable.HashMap[String, Int]) = {
    val i2s = values.sort().collect()
    val s2i = new mutable.HashMap[String, Int]()
    for ((userid, i) <- i2s.zipWithIndex) {
      s2i(userid) = i
    }
    (i2s, s2i)
  }

  def getUserMappings(data: Dataset[User]): (Array[String], mutable.HashMap[String, Int]) = {
    DataLoader.getMappings(data.map(u => u.steam_id))
  }

  def getGameMappings(data: Dataset[User]): (Array[String], mutable.HashMap[String, Int]) = {
    DataLoader.getMappings(data.flatMap(u => u.games.map(g => g.name)))
  }
}

@SerialVersionUID(100L)
class GameData(data: Dataset[User]) extends Serializable {
  val spark: SparkSession = SparkSessionFactory.get()

  import spark.implicits._

  val dataSet: Dataset[User] = data;
  val (id2user: Array[String], user2id: mutable.HashMap[String, Int]) = DataLoader.getUserMappings(data)

  val (id2game: Array[String], game2id: mutable.HashMap[String, Int]) = DataLoader.getGameMappings(data)

  val id2User: Map[Int, User] = dataSet.groupByKey(u => user2id(u.steam_id)).reduceGroups((u1, u2) => u1).collect().toMap

  def getTrainRatings: Dataset[Rating] = {
    data.flatMap(u => {
      u.train_games.map(g => toRating(u, g))
    })
  }

  def getValidRatings: Dataset[Rating] = {
    data.flatMap(u => {
      u.valid_games.map(g => toRating(u, g))
    })
  }

  private def toRating(u: User, g: Game): Rating = {
    Rating(user2id(u.steam_id), game2id(g.name), g.playtime_forever.toDouble / 60.0)
  }
}