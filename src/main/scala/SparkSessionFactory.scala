import org.apache.spark.sql.SparkSession

object SparkSessionFactory {
  def get():SparkSession = {
    SparkSession.builder.appName("GameRec").master("local[*]").getOrCreate()
  }
}
