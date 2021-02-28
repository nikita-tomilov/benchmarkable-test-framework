package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.api.TestableSubject
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

open class BenchmarkableTestBase(
  private val testInstance: Class<*>,
  private val targets: Iterable<TestableSubject>
) {

  @TestFactory
  fun benchmarkableTests(): Iterable<DynamicContainer> {
    val requiredTests = testInstance.methods.filter {
      it.isAnnotationPresent(BenchmarkableTest::class.java)
    }
    return targets.map { target ->
      DynamicContainer.dynamicContainer(target::class.java.simpleName,
          listOf(buildFunc("setUp") { target.beforeAll() } ) +
          requiredTests.map { testMethod ->
            DynamicTest.dynamicTest(testMethod.name) {
              target.beforeEach()
              testMethod.invoke(this, target)
              target.afterEach()
            }
          }.toList() + listOf(buildFunc("tearDown") { target.afterAll() } ))
    }
  }

  private fun buildFunc(name: String, callback: () -> Unit) = DynamicTest.dynamicTest(name, callback)
}