package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.api.TestableSubject
import org.junit.jupiter.api.*

open class BenchmarkableTestBase(
  private val testInstance: Class<*>,
  private val targets: Iterable<TestableSubject>
) {

  @BeforeEach
  fun runBeforeAlls() {
    targets.forEach { it.beforeAll() }
  }

  @AfterEach
  fun runAfterAlls() {
    targets.forEach { it.afterAll() }
  }

  @TestFactory
  fun benchmarkableTests(): Iterable<DynamicContainer> {
    val requiredTests = testInstance.methods.filter {
      it.isAnnotationPresent(BenchmarkableTest::class.java)
    }
    return targets.map { target ->
      DynamicContainer.dynamicContainer(target::class.java.simpleName, requiredTests.map { testMethod ->
        DynamicTest.dynamicTest(testMethod.name) {
          target.beforeEach()
          testMethod.invoke(this, target)
          target.afterEach()
        }
      })
    }
  }

}