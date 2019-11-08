package com.steamrec

import org.apache.spark.sql.SparkSession

object SparkSessionFactory {
  def get():SparkSession = {
    val sess = SparkSession.builder.appName("GameRec").master("local[*]").getOrCreate()
    sess.conf.set("spark.executor.memory", "3g")
    sess
  }
}
