package com.nikitatomilov

import com.nikitatomilov.api.*
import com.nikitatomilov.measurements.Measurements
import java.lang.reflect.Method

class BenchmarkInstanceRunner(
  private val target: BenchmarkableBase,
  private val benchmarkStopwatch: BenchmarkStopwatch =
      GuavaStopwatchStrategy(),
  private val iterationsStrategy: IterationStrategy =
      UntilDoesntChangeIterationStrategy(0.01, 10, 10000)
) {

  fun runAll(): Map<String, Measurements> {
    val measurements = HashMap<String, ArrayList<Long>>()
    val methods = target.getBenchmarkableMethods()
    target.beforeAll()
    methods.forEach { method ->
      when (method.parameterCount) {
        0 -> {
          var time: Long
          iterationsStrategy.clear()
          do {
            time = runSingle(method)
            measurements.getOrPut(name(method)) { arrayListOf() }.add(time)
          } while(!iterationsStrategy.shouldStopIterating(time))
        }
        1 -> {
          val args = target.getParametersFor(method.name)
          args.forEach { arg ->
            var time: Long
            iterationsStrategy.clear()
            do {
              time = runSingle(method, arg)
              measurements.getOrPut(name(method, arg)) { arrayListOf() }.add(time)
            } while(!iterationsStrategy.shouldStopIterating(time))
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