package com.steamrec

import org.slf4j.{Logger, LoggerFactory}

object DataLoader {
  val Logger: Logger = LoggerFactory.getLogger(DataLoader.getClass)
  val spark = SparkSessionFactory.get()
  import spark.implicits._

  def loadData(path: String, numPartitions: Int = 8): GameData = {
    new GameData(spark.read.json(path).as[User])
  }

}
