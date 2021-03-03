package com.nikitatomilov

import com.nikitatomilov.api.TestableSubject
import java.lang.reflect.Method

class BenchmarkRunner(
  private val target: TestableSubject,
  private val methods: List<Method>,
  private val baseClass: BenchmarkableTestBase,
  private val clockFunc: () -> Long = { CPUUtils.getWallClockTime() },
  private val iterationsCount: Int = 1
) {

  fun runAll() {
    target.beforeAll()
    methods.forEach { method ->
      when (method.parameterCount) {
        1 -> {
          (0 until iterationsCount).forEach { _ -> runSingle(method) }
        }
        2 -> {
          val args = baseClass.getParametersFor(method.name)
          args.forEach { arg ->
            (0 until iterationsCount).forEach { _ -> runSingle(method, arg) }
          }
        }
        else -> error("Method '${method.name}' should have either 1 or 2 parameters")
      }

    }
    target.afterAll()
  }

  private fun runSingle(method: Method) {
    target.beforeEach()
    val time = runWithTiming { method.invoke(baseClass, target) }
    println("${name(method)} took $time nanoseconds")
    target.afterEach()
  }

  private fun runSingle(method: Method, arg: Any) {
    target.beforeEach()
    val time = runWithTiming { method.invoke(baseClass, target, arg) }
    println("${name(method, arg)} took $time nanoseconds")
    target.afterEach()
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