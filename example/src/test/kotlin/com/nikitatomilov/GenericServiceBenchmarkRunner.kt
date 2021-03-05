package com.nikitatomilov

object GenericServiceBenchmarkRunner {
  @JvmStatic
  fun main(args: Array<String>) {
    BenchmarkRunner.run(GenericServiceTest::class.java)
  }
}