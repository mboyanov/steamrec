package com.steamrec

case class Experiment(rank: Int,
                      iterations: Int,
                      lambda: Double,
                      alpha: Double) {
  def getOutputDir: String = {
    s"rank_${rank}_it_${iterations}_lambda_${lambda}_alpha_${alpha}"
  }
}
