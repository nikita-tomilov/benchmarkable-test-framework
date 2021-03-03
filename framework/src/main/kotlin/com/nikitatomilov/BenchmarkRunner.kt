package com.nikitatomilov

import com.google.common.collect.HashBasedTable
import com.nikitatomilov.api.*
import com.nikitatomilov.measurements.Measurements
import java.lang.reflect.Method

class BenchmarkRunner(
  private val target: TestableSubject,
  private val methods: List<Method>,
  private val baseClass: BenchmarkableTestBase,
  private val benchmarkStopwatch: BenchmarkStopwatch =
      GuavaStopwatchStrategy(),
  private val iterationsStrategy: IterationStrategy =
      UntilDoesntChangeIterationStrategy(0.01, 10, 10000)
) {

  fun runAll(): Map<String, Measurements> {
    val measurements = HashMap<String, ArrayList<Long>>()
    target.beforeAll()
    methods.forEach { method ->
      when (method.parameterCount) {
        1 -> {
          var time: Long
          iterationsStrategy.clear()
          do {
            time = runSingle(method)
            measurements.getOrPut(name(method)) { arrayListOf() }.add(time)
          } while(!iterationsStrategy.shouldStopIterating(time))
        }
        2 -> {
          val args = baseClass.getParametersFor(method.name)
          args.forEach { arg ->
            var time: Long
            iterationsStrategy.clear()
            do {
              time = runSingle(method, arg)
              measurements.getOrPut(name(method, arg)) { arrayListOf() }.add(time)
            } while(!iterationsStrategy.shouldStopIterating(time))
          }
        }
        else -> error("Method '${method.name}' should have either 1 or 2 parameters")
      }
    }
    target.afterAll()
    return measurements.map { it.key to Measurements(it.value) }.toMap()
  }

  private fun runSingle(method: Method): Long {
    target.beforeEach()
    val time = runWithTiming { method.invoke(baseClass, target) }
    target.afterEach()
    return time
  }

  private fun runSingle(method: Method, arg: Any): Long {
    target.beforeEach()
    val time = runWithTiming { method.invoke(baseClass, target, arg) }
    target.afterEach()
    return time
  }

  private fun runWithTiming(lambda: () -> Unit): Long {
    benchmarkStopwatch.init()
    lambda.invoke()
    return benchmarkStopwatch.elapsed()
  }

  companion object {
    fun run(testBase: BenchmarkableTestBase) {
      val methods = testBase.getTestMethods()
      val measurements = HashBasedTable.create<TestableSubject, String, Measurements>()
      testBase.getCastedTestTargets().forEach { target ->
        val measurementsForTarget = BenchmarkRunner(target, methods, testBase).runAll()
        measurementsForTarget.forEach { (name, values) ->
          measurements.put(target, name, values)
        }
      }
      val methodNames = measurements.columnKeySet().toList().sorted()
      methodNames.forEach { printResultsFor(it, measurements.column(it)) }
    }

    private fun printResultsFor(methodName: String, measurements: Map<TestableSubject, Measurements>) {
      println("\nBenchmark results in ns for '$methodName': ${Measurements.m4stringDescription()}")
      measurements.forEach { (target, values) ->
        val name = "${target.javaClass.simpleName};".padEnd(20, ' ')
        val results = values.toM4String()
        println("$name $results")
      }
      println("\n")
    }
  }
}