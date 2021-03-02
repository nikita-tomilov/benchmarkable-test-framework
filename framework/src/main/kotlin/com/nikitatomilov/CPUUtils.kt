package com.nikitatomilov

import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean

object CPUUtils {

  //thanks to https://stackoverflow.com/questions/180158/how-do-i-time-a-methods-execution-in-java

  /** Get CPU time in nanoseconds.  */
  fun getCPUTime(): Long {
    val bean: ThreadMXBean = ManagementFactory.getThreadMXBean()
    return if (bean.isCurrentThreadCpuTimeSupported) bean.currentThreadCpuTime else 0L
  }

  /** Get user time in nanoseconds.  */
  fun getUserTime(): Long {
    val bean: ThreadMXBean = ManagementFactory.getThreadMXBean()
    return if (bean.isCurrentThreadCpuTimeSupported) bean.currentThreadUserTime else 0L
  }

  /** Get system time in nanoseconds.  */
  fun getSystemTime(): Long {
    val bean: ThreadMXBean = ManagementFactory.getThreadMXBean()
    return if (bean.isCurrentThreadCpuTimeSupported) bean.currentThreadCpuTime - bean.currentThreadUserTime else 0L
  }

  /** Get "wall" time in nanoseconds.  */
  fun getWallClockTime(): Long {
    return System.nanoTime()
  }
}