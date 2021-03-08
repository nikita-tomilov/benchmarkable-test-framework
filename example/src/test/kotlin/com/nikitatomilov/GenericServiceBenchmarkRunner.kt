package com.nikitatomilov

object GenericServiceBenchmarkRunner {
  @JvmStatic
  fun main(args: Array<String>) {
    BenchmarkRunner.run(GenericServiceBenchmark::class.java)
  }
}