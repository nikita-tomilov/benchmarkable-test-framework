package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.annotations.ParameterProvider
import com.nikitatomilov.api.TestTargets
import com.nikitatomilov.example.GenericService
import com.nikitatomilov.example.ServiceA
import com.nikitatomilov.example.ServiceB

@Suppress("unused")
class ExampleTest : BenchmarkableTestBase(ExampleTest::class.java) {

  @BenchmarkableTest
  fun `do stuff works 1`(target: GenericService) {
    target.doAnotherStuff()
    println("works 1")
  }

  @BenchmarkableTest
  fun `do stuff works 2`(target: GenericService) {
    target.doAnotherStuff()
    println("works 2")
  }

  @ParameterProvider("do stuff works 3")
  fun buildParams() = listOf(1, 10, 100)

  @BenchmarkableTest
  fun `do stuff works 3`(target: GenericService, iterations: Int) {
    target.doAnotherStuff(iterations)
    println("works 3")
  }

  companion object {
    fun buildServiceA(): ServiceA = ServiceA()
    fun buildServiceB(): ServiceB = ServiceB()
  }

  override fun buildTestTargets(): TestTargets<*> {
    return TestTargets<GenericService>(
        listOf(
            buildServiceA(),
            buildServiceB()))
  }
}