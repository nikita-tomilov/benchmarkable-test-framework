package com.nikitatomilov.api

import com.nikitatomilov.measurements.median
import com.nikitatomilov.measurements.stddev
import kotlin.math.abs

interface IterationStrategy {
  fun clear()
  fun shouldStopIterating(measuredTime: Long): Boolean
}

class FixedCountIterationStrategy(
  private val numberOfIterations: Int
) : IterationStrategy {
  private var counter = 0

  override fun clear() {
    counter = 0
  }

  override fun shouldStopIterating(measuredTime: Long): Boolean {
    counter++
    return (counter >= numberOfIterations)
  }
}

class UntilDoesntChangeIterationStrategy(
  private val relativePercentage: Double,
  private val minIterations: Int,
  private val maxIterations: Int
): IterationStrategy {
  private val measurements = ArrayList<Long>()

  override fun clear() {
    measurements.clear()
  }

  override fun shouldStopIterating(measuredTime: Long): Boolean {
    measurements.add(measuredTime)
    val med = measurements.median()
    val stddev = measurements.stddev()
    val relDiff = abs(med - stddev) / med
    val closeEnough = relDiff < relativePercentage
    return (measurements.size >= minIterations) && (closeEnough || (measurements.size > maxIterations))
  }
}