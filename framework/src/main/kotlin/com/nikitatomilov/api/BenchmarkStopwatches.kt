package com.nikitatomilov.api

import com.google.common.base.Stopwatch
import com.nikitatomilov.measurements.CPUUtils
import java.util.concurrent.TimeUnit

interface BenchmarkStopwatch {
  fun init()

  fun elapsed(): Long

  fun unitOfMeasurements(): String
}

class CPUTimeStopwatch(
  val timeUnit: TimeUnit
): BenchmarkStopwatch {
  private var start = 0L

  override fun init() {
    start = CPUUtils.getCPUTime()
  }

  override fun elapsed(): Long {
    return timeUnit.convert(CPUUtils.getCPUTime() - start, TimeUnit.NANOSECONDS)
  }

  override fun unitOfMeasurements(): String {
    return timeUnit.toString()
  }
}

class GuavaStopwatchStrategy(
  val timeUnit: TimeUnit
): BenchmarkStopwatch {

  private lateinit var sw: Stopwatch

  override fun init() {
    sw = Stopwatch.createStarted()
  }

  override fun elapsed(): Long {
    sw.stop()
    return sw.elapsed(timeUnit)
  }

  override fun unitOfMeasurements(): String {
    return timeUnit.toString()
  }
}