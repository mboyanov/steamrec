import org.apache.spark.mllib.recommendation.Rating
import org.scalatest.{FlatSpec, Matchers}

class TestMetrics extends FlatSpec with Matchers {

  "mrr" should "be defined" in {
    val spark = SparkSessionFactory.get()
    import spark.implicits._

    val game = Game(BigInt(100), "CS", BigInt(100))
    val users = List(User("123", List(game), List(game),List.empty))

    val gameData = new GameData( spark.createDataset(users))
    val ratings = spark.createDataset(List(Rating(0,0, 2)))
    val res = Metrics.mrr(gameData, ratings)
    res should be (0)
  }

}
