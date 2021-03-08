package com.nikitatomilov

import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceBTest : GenericServiceBenchmark(buildServiceB()) {
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
}