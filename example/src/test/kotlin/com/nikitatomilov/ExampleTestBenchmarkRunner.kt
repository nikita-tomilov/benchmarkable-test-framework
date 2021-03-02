package com.nikitatomilov

object ExampleTestBenchmarkRunner {
  @JvmStatic
  fun main(args: Array<String>) {
    BenchmarkRunner.run(ExampleTest())
  }
}