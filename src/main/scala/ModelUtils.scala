import org.apache.spark.mllib.recommendation.{MatrixFactorizationModel, Rating}
import org.apache.spark.sql.{DataFrame, Dataset}

import scala.collection.mutable

object ModelUtils {
  def predict(gameData: GameData, model: MatrixFactorizationModel) : Dataset[Rating] = {
    val numGames = gameData.game2id.size
    val numUsers = gameData.user2id.size
    val spark = SparkSessionFactory.get()
    import spark.implicits._
    val denseMatrix = new mutable.ArrayBuffer[(Int, Int)]
    for (ng <- 0 to numGames;  nu <- 0 to numUsers) {
      denseMatrix.append((ng, nu))
    }
    val gamesDS: Dataset[Int] = spark createDataset ((0 to numGames).toList)
    val usersDs: Dataset[Int] = spark createDataset ((0 to numUsers).toList)
    val denseDS: Dataset[(Int, Int)] = gamesDS.crossJoin(usersDs).as[(Int,Int)]
    model.predict(denseDS.rdd).toDS()
  }
}
