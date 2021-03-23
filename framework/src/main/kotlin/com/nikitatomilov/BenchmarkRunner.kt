package com.nikitatomilov

import com.google.common.collect.HashBasedTable
import com.nikitatomilov.api.BenchmarkStopwatch
import com.nikitatomilov.api.GuavaStopwatchStrategy
import com.nikitatomilov.api.IterationStrategy
import com.nikitatomilov.api.UntilDoesntChangeIterationStrategy
import com.nikitatomilov.measurements.Measurements
import org.reflections.Reflections

object BenchmarkRunner {

  @JvmStatic
  @JvmOverloads
  fun run(
    testSource: Class<*>,
    stopwatch: BenchmarkStopwatch = GuavaStopwatchStrategy(),
    iterationsStrategy: IterationStrategy =
        UntilDoesntChangeIterationStrategy(0.01, 10, 10000)
  ) {
    if (!suitableForBenchmarking(testSource)) {
      error("$testSource should extend BenchmarkableBase")
    }
    println("Starting benchmarking for '$testSource'")

    val testInstances = getChildrenOf(testSource)
    println("Found ${testInstances.size} child test bases: " +
        testInstances.joinToString { "'$it'" })

    val testSourceInstances = testInstances.map { testInstance ->
      val constructor = testInstance.declaredConstructors.singleOrNull()
        ?: error("$testInstance should have one constructor")

      if (constructor.parameterCount != 0)
        error("$testInstance should have constructor without arguments")

      constructor.newInstance() as BenchmarkableBase
    }

    val measurements = HashBasedTable.create<BenchmarkableBase, String, Measurements>()
    testSourceInstances.forEach { instance ->
      val runner = BenchmarkInstanceRunner(instance, stopwatch, iterationsStrategy)
      val measurementsForTarget = runner.runAll()
      measurementsForTarget.forEach { (name, values) ->
        measurements.put(instance, name, values)
      }
    }

    val methodNames = measurements.columnKeySet().toList().sorted()
    methodNames.forEach { printResultsFor(it, measurements.column(it)) }
  }

  fun suitableForBenchmarking(x: Class<*>): Boolean {
    var parent = x.superclass
    while (parent != null) {
      if (parent == BenchmarkableBase::class.java) return true
      parent = parent.superclass
    }
    return false
  }

  private fun getChildrenOf(x: Class<*>): List<Class<*>> {
    val r = try {
      Reflections(this::class.java.`package`)
    } catch (e: Exception) {
      //fallback to slower "global" reflections
      Reflections()
    }
    return r.getSubTypesOf(x).toList().sortedBy { "$it" }
  }

  private fun printResultsFor(
    methodName: String,
    measurements: Map<BenchmarkableBase, Measurements>
  ) {
    println("\nBenchmark results in ns for '$methodName': ${Measurements.m4stringDescription()}")
    measurements.forEach { (target, values) ->
      val name = "${target.javaClass.simpleName};".padEnd(20, ' ')
      val results = values.toM4String()
      println("$name $results")
    }
    println("\n")
  }
}