package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.api.TestTargets
import com.nikitatomilov.example.GenericService
import com.nikitatomilov.example.ServiceA
import com.nikitatomilov.example.ServiceB

@Suppress("unused")
class ExampleTest : BenchmarkableTestBase(
    ExampleTest::class.java,
    TestTargets<GenericService>(
        listOf(
            buildServiceA(),
            buildServiceB()))) {

  @BenchmarkableTest
  fun `do stuff works 1`(target: GenericService) {
    target.doStuff()
    println("works 1")
  }

  @BenchmarkableTest
  fun `do stuff works 2`(target: GenericService) {
    target.doStuff()
    println("works 2")
  }

  companion object {
    fun buildServiceA(): ServiceA = ServiceA()
    fun buildServiceB(): ServiceB = ServiceB()
  }
}
