package com.nikitatomilov

class BenchmarkRunner {
  companion object {
    fun run(testBase: BenchmarkableTestBase) {
      println("I should run tests")
      testBase.getTestMethods().forEach {
        println(" - " + it.name)
      }
      println("over targets")
      testBase.getCastedTestTargets().forEach {
        println(" - " + it.javaClass.simpleName)
      }
    }
  }
}