package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.annotations.ParameterProvider
import com.nikitatomilov.example.GenericService
import com.nikitatomilov.example.ServiceA
import com.nikitatomilov.example.ServiceB

open class GenericServiceTest(
  private val target: GenericService
) : BenchmarkableBase() {

  @BenchmarkableTest
  fun `generic do stuff works 1`() {
    target.doAnotherStuff()
    println("works 1")
  }

  @BenchmarkableTest
  fun `generic do stuff works 2`() {
    target.doAnotherStuff()
    println("works 2")
  }

  @BenchmarkableTest
  fun `generic do stuff works 3`(i: Int) {
    target.doAnotherStuff(i)
    println("works 3")
  }

  @ParameterProvider("generic do stuff works 3")
  fun providerForTest3(): List<Int> = listOf(1, 10, 100)

  override fun beforeAll() {}

  override fun afterAll() {}

  override fun beforeEach() {}

  override fun afterEach() {}

  companion object {
    fun buildServiceA(): ServiceA = ServiceA()
    fun buildServiceB(): ServiceB = ServiceB()
  }
}