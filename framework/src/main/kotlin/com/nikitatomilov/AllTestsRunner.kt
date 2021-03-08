package com.nikitatomilov

import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.lang.reflect.Method

abstract class AllTestsRunner<T : Any>(
  private val testSource: Class<*>,
  private val testableImplementations: List<T>
) {

  @TestFactory
  fun benchmarkableTests(): Iterable<DynamicContainer> {
    if (!BenchmarkRunner.suitableForBenchmarking(testSource)) {
      error("$testSource should extend BenchmarkableBase")
    }

    val testTargets = testableImplementations.map { testableImplementation ->
      val constructor = testSource.declaredConstructors.single()
      (testableImplementation::class.java.simpleName) to (constructor.newInstance(
          testableImplementation) as BenchmarkableBase)
    }

    val requiredTests = getTestMethods(testSource)
    return testTargets.map {
      val implName = it.first
      val target = it.second
      DynamicContainer.dynamicContainer(implName,
          listOf(buildTestFunc("setUp") { target.beforeAll() }) +
              requiredTests.map { testMethod ->
                when (testMethod.parameterCount) {
                  0 -> {
                    listOf(DynamicTest.dynamicTest(name(testMethod)) {
                      invokeTestMethod(target, testMethod)
                    }
                    )
                  }
                  1 -> {
                    getParametersFor(target, testSource, testMethod.name).map { param ->
                      DynamicTest.dynamicTest(name(testMethod, param)) {
                        invokeTestMethod(target, testMethod, param)
                      }
                    }
                  }
                  else -> error("Method '${testMethod.name}' should have either 0 or 1 parameters")
                }
              }.flatten().toList() + listOf(buildTestFunc("tearDown") { target.afterAll() }))
    }
  }

  private fun invokeTestMethod(target: BenchmarkableBase, testMethod: Method) {
    target.beforeEach()
    testMethod.invoke(target)
    target.afterEach()
  }

  private fun invokeTestMethod(target: BenchmarkableBase, testMethod: Method, arg: Any) {
    target.beforeEach()
    testMethod.invoke(target, arg)
    target.afterEach()
  }

  private fun buildTestFunc(name: String, callback: () -> Unit) =
      DynamicTest.dynamicTest(name, callback)
}