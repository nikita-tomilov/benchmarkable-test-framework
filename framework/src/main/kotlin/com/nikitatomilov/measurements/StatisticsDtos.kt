package com.nikitatomilov.measurements

import kotlin.math.ceil

data class Measurements(
  val array: List<Long> = arrayListOf()
) {

  val min = array.min() ?: 0L

  val max = array.max() ?: 0L

  val avg = array.average()

  val med = array.median()

  val p90 = array.percentile(90)

  val p95 = array.percentile(95)

  val p99 = array.percentile(99)

  fun toM4String(): String {
    return array.size.pretty() + min.pretty() + max.pretty() + avg.pretty() +
        med.pretty() + p90.pretty() + p95.pretty() + p99.pretty()
  }

  companion object {
    fun m4stringDescription(): String = "iterations/MIN/MAX/AVG/MED/P90/P95/P99"
  }
}

private fun List<Long>.median(): Double {
  return if (this.size % 2 == 0)
    (this[this.size / 2] * 1.0 + this[this.size / 2 - 1] * 1.0)/2;
  else
    this[this.size / 2] * 1.0
}

private const val PADDING = 15
private fun Int.pretty() = "$this;".padEnd(PADDING, ' ')
private fun Long.pretty() = "$this;".padEnd(PADDING, ' ')
private fun Double.pretty() = "%.${3}f;".format(this).padEnd(PADDING, ' ')

private fun List<Long>.percentile(percentile: Long): Long {
  val latencies = this.sorted()
  val index = ceil(percentile * 1.0 / 100.0 * latencies.size).toInt()
  return latencies[index - 1]
}