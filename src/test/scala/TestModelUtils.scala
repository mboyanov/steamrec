import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.sql.Dataset
import org.scalatest.{FlatSpec, Matchers}

class TestModelUtils extends FlatSpec with Matchers {

  "Model" should "predict" in {

    val game = Game(BigInt(100), "CS", BigInt(100))
    val users = List(User("123", List(game), List(game),List.empty))
    val spark = SparkSessionFactory.get()
    import spark.implicits._

    val usersDs: Dataset[User] = spark.createDataset(users).as[User]
    val gd = new GameData(usersDs)

    val userFeatures = spark.createDataset(Array((0, Array.ofDim[Double](50))))
    val model = new MatrixFactorizationModel(50, userFeatures.rdd, userFeatures.rdd)
    val preds = ModelUtils.predict(gd, model)
    preds should not be (null);
    val count = preds.count().toInt
    count should equal (1)
  }

}
