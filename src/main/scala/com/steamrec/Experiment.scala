package com.steamrec

case class Experiment(rank: Int,
                      iterations: Int,
                      lambda: Double,
                      alpha: Double,
                      log_smoothing:Boolean) {
  def getOutputDir: String = {
    val dir = s"rank_${rank}_it_${iterations}_lambda_${lambda}_alpha_${alpha}"
    if (log_smoothing) {
      dir.concat(s"_log_smoothing")
    } else {
      dir
    }
  }
}
