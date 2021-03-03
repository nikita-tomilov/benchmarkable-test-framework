package com.nikitatomilov.api

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
  private var lastState = 0L
  private var iterations = 0

  override fun clear() {
    lastState = 0L
    iterations = 0
  }

  override fun shouldStopIterating(measuredTime: Long): Boolean {
    iterations++
    val diff = abs(measuredTime - lastState)
    val relativeDiff = diff * 1.0 / measuredTime
    val closeEnough = relativeDiff < relativePercentage
    lastState = measuredTime
    return (iterations >= minIterations) && (closeEnough || (iterations > maxIterations))
  }
}