package com.nikitatomilov

import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@Suppress("unused")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceATest : GenericServiceBenchmark(buildServiceA()) {
  @Test
  fun `do stuff works 1`() {
    `generic do stuff works 1`()
  }

  @Test
  fun `do stuff works 2`() {
    `generic do stuff works 2`()
  }

  @ParameterizedTest
  @MethodSource("paramSource")
  fun `do stuff works 3`(input: Int, expected: Boolean) {
    `generic do stuff works 3`(input)
  }

  private fun paramSource(): Stream<Arguments> =
      providerForTest3().map { Arguments.of(it, true) }.stream()

  @BeforeAll
  override fun beforeAll() {
    println("custom before all in ${this.javaClass.simpleName}")
  }

  @AfterAll
  override fun afterAll() {
    println("custom after all in ${this.javaClass.simpleName}")
  }

  @BeforeEach
  override fun beforeEach() {
    println("custom before each in ${this.javaClass.simpleName}")
  }

  @AfterEach
  override fun afterEach() {
    println("custom after each in in ${this.javaClass.simpleName}")
  }
}