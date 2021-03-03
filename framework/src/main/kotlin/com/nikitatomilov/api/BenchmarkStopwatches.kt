package com.nikitatomilov.api

import com.google.common.base.Stopwatch
import com.nikitatomilov.measurements.CPUUtils
import java.util.concurrent.TimeUnit

interface BenchmarkStopwatch {
  fun init()

  fun elapsed(): Long
}

class CPUTimeStopwatch: BenchmarkStopwatch {
  private var start = 0L

  override fun init() {
    start = CPUUtils.getCPUTime()
  }

  override fun elapsed(): Long {
    return CPUUtils.getCPUTime() - start
  }
}

class GuavaStopwatchStrategy: BenchmarkStopwatch {

  private lateinit var sw: Stopwatch

  override fun init() {
    sw = Stopwatch.createStarted()
  }

  override fun elapsed(): Long {
    sw.stop()
    return sw.elapsed(TimeUnit.NANOSECONDS)
  }
}