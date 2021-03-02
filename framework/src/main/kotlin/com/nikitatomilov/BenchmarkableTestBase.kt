package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.api.TestTargets
import com.nikitatomilov.api.TestableSubject
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

abstract class BenchmarkableTestBase(
  private val testInstance: Class<*>
) {

  @TestFactory
  fun benchmarkableTests(): Iterable<DynamicContainer> {
    val requiredTests = getTestMethods()
    val testTargets = getCastedTestTargets()
    return testTargets.map { target ->
      DynamicContainer.dynamicContainer(target::class.java.simpleName,
          listOf(buildTestFunc("setUp") { target.beforeAll() } ) +
          requiredTests.map { testMethod ->
            DynamicTest.dynamicTest(testMethod.name) {
              target.beforeEach()
              testMethod.invoke(this, target)
              target.afterEach()
            }
          }.toList() + listOf(buildTestFunc("tearDown") { target.afterAll() } ))
    }
  }

  abstract fun buildTestTargets(): TestTargets<*>

  internal fun getCastedTestTargets(): List<TestableSubject> =
      buildTestTargets().targets.map {
        if (it !is TestableSubject) {
          error("$it should implement TestableSubject")
        }
        it as TestableSubject
      }

  internal fun getTestMethods() = testInstance.methods.filter {
    it.isAnnotationPresent(BenchmarkableTest::class.java)
  }.sortedBy { it.name }

  private fun buildTestFunc(name: String, callback: () -> Unit) = DynamicTest.dynamicTest(name, callback)
}