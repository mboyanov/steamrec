import org.apache.spark.sql.SparkSession

object SparkSessionFactory {
  def get():SparkSession = {
    val sess = SparkSession.builder.appName("GameRec").master("local[4]").getOrCreate()
    sess.conf.set("spark.executor.memory", "2g")
    sess
  }
}
