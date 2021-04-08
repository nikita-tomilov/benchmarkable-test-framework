package com.nikitatomilov

import com.google.common.collect.HashBasedTable
import com.nikitatomilov.api.*
import com.nikitatomilov.measurements.Measurements
import org.reflections.Reflections
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.util.concurrent.TimeUnit

object BenchmarkRunner {

  @JvmStatic
  @JvmOverloads
  fun run(
    testSource: Class<*>,
    stopwatch: BenchmarkStopwatch = GuavaStopwatchStrategy(TimeUnit.NANOSECONDS),
    iterationsStrategy: IterationStrategy =
        UntilDoesntChangeIterationStrategy(0.35, 10, 10000)
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

    val measurements = HashBasedTable.create<BenchmarkableBase, NamedResult, Measurements>()
    testSourceInstances.forEach { instance ->
      val runner = BenchmarkInstanceRunner(instance, stopwatch, iterationsStrategy)
      val measurementsForTarget = runner.runAll()
      measurementsForTarget.forEach { (name, values) ->
        measurements.put(instance, name, values)
      }
    }

    val dir = File("./benchmark-results/results-${Instant.now().toString().replace(":", "-")}")
    dir.mkdirs()

    val namedResultsFull = measurements.columnKeySet().toList()
    val methodNames = namedResultsFull.map { it.name() }.toSet().toList().sorted()
    methodNames.forEach { name ->
      val resultsForMethodName = namedResultsFull.filter { it.name() == name }.toSet()
      if (resultsForMethodName.size == 1) {
        val method = resultsForMethodName.single()
        printResultsFor(method.name(), stopwatch, measurements.column(method))
      } else {
        val sorted = resultsForMethodName.sortedBy { it.paramForSorting() }
        printResultsFor(name, sorted, stopwatch, measurements, dir.absolutePath)
      }
    }
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
    stopwatch: BenchmarkStopwatch,
    measurements: Map<BenchmarkableBase, Measurements>
  ) {
    println("\nBenchmark results in ${stopwatch.unitOfMeasurements()} for '$methodName': ${Measurements.m4stringDescription()}")
    measurements.forEach { (target, values) ->
      val name = "${target.javaClass.simpleName};".padEnd(20, ' ')
      val results = values.toM4String()
      println("$name $results")
    }
    println("\n")
  }

  private fun printResultsFor(
    methodName: String,
    namedResults: List<NamedResult>,
    stopwatch: BenchmarkStopwatch,
    table: HashBasedTable<BenchmarkableBase, NamedResult, Measurements>,
    targetDir: String
  ) {
    val sb = StringBuilder()
    val implementations = table.rowKeySet().sortedBy { it.javaClass.simpleName }
    val tableLegend = implementations.joinToString(" ") { "${it.javaClass.simpleName}_AVG" }
    println("\nBenchmark results in in ${stopwatch.unitOfMeasurements()} for '$methodName': \nparam $tableLegend ")
    sb.append("param $tableLegend\n")
    namedResults.forEach { nr ->
      val param = nr.param()
      val rowString =
          implementations.map { table.get(it, nr).avg }.joinToString("") { "$it ".padEnd(20, ' ') }
      val rowName = "$param ".padEnd(20, ' ')
      println("$rowName $rowString")
      sb.append("$rowName $rowString\n")
    }
    println("\n")
    val file = File(targetDir + "/results-$methodName.csv")
    val csvContents = sb.toString()
    Files.write(file.toPath(), csvContents.toByteArray())
  }
}