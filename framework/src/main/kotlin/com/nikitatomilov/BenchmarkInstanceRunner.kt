package com.nikitatomilov

import com.nikitatomilov.annotations.RunJustOnce
import com.nikitatomilov.api.*
import com.nikitatomilov.measurements.Measurements
import java.lang.reflect.Method

class BenchmarkInstanceRunner(
  private val target: BenchmarkableBase,
  private val benchmarkStopwatch: BenchmarkStopwatch,
  private val iterationsStrategy: IterationStrategy
) {

  fun runAll(): Map<NamedResult, Measurements> {
    val measurements = HashMap<NamedResult, ArrayList<Long>>()
    val methods = target.getBenchmarkableMethods()
    target.beforeAll()
    methods.forEach { method ->
      val strategyToUse = if (method.isAnnotationPresent(RunJustOnce::class.java)) {
        FixedCountIterationStrategy(1)
      } else {
        iterationsStrategy
      }
      println("Beginning to benchmark '${target.javaClass.simpleName}', method '${method.name}'")
      when (method.parameterCount) {
        0 -> {
          var time: Long
          strategyToUse.clear()
          do {
            time = runSingle(method)
            measurements.getOrPut(NamedResult(method)) { arrayListOf() }.add(time)
          } while(!strategyToUse.shouldStopIterating(time))
        }
        1 -> {
          val args = target.getParametersFor(method.name)
          args.forEach { arg ->
            var time: Long
            strategyToUse.clear()
            do {
              time = runSingle(method, arg)
              measurements.getOrPut(NamedResult(method, arg)) { arrayListOf() }.add(time)
            } while(!strategyToUse.shouldStopIterating(time))
          }
        }
        else -> error("Method '${method.name}' should have either 0 or 1 parameter")
      }
    }
    target.afterAll()
    return measurements.map { it.key to Measurements(it.value) }.toMap()
  }

  //TODO: handle exceptions somehow
  private fun runSingle(method: Method): Long {
    target.beforeEach()
    val time = runWithTiming { method.invoke(target) }
    target.afterEach()
    return time
  }

  private fun runSingle(method: Method, arg: Any): Long {
    target.beforeEach()
    val time = runWithTiming { method.invoke(target, arg) }
    target.afterEach()
    return time
  }

  private fun runWithTiming(lambda: () -> Unit): Long {
    benchmarkStopwatch.init()
    lambda.invoke()
    return benchmarkStopwatch.elapsed()
  }
}