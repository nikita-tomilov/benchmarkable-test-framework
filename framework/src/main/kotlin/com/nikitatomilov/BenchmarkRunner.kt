package com.nikitatomilov

import com.nikitatomilov.api.TestableSubject
import java.lang.reflect.Method

class BenchmarkRunner(
  private val target: TestableSubject,
  private val methods: List<Method>,
  private val baseClass: BenchmarkableTestBase,
  private val clockFunc: () -> Long = { CPUUtils.getWallClockTime() }
) {

  fun runAll() {
    target.beforeAll()
    methods.forEach { method ->
      target.beforeEach()
      val time = runWithTiming { method.invoke(baseClass, target) }
      println("took $time nanoseconds")
      target.afterEach()
    }
    target.afterAll()
  }

  private fun runWithTiming(lambda: () -> Unit): Long {
    val startTime = clockFunc.invoke()
    lambda.invoke()
    val endTime = clockFunc.invoke()
    return endTime - startTime
  }

  companion object {
    fun run(testBase: BenchmarkableTestBase) {
      val methods = testBase.getTestMethods()
      testBase.getCastedTestTargets().forEach {
        BenchmarkRunner(it, methods, testBase).runAll()
      }
    }
  }
}