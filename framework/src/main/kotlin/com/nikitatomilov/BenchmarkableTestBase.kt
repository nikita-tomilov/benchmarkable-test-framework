package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.annotations.ParameterProvider
import com.nikitatomilov.api.TestTargets
import com.nikitatomilov.api.TestableSubject
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.lang.reflect.Method

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
            when (testMethod.parameterCount) {
              1 -> {
                listOf(DynamicTest.dynamicTest(name(testMethod)) {
                  invokeTestMethod(target, testMethod) }
                )
              }
              2 -> {
                getParametersFor(testMethod.name).map { param ->
                  DynamicTest.dynamicTest(name(testMethod, param)) {
                    invokeTestMethod(target, testMethod, param)
                  }
                }
              }
              else -> error("Method '${testMethod.name}' should have either 1 or 2 parameters")
            }
          }.flatten().toList() + listOf(buildTestFunc("tearDown") { target.afterAll() } ))
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

  @Suppress("UNCHECKED_CAST")
  internal fun getParametersFor(methodName: String): List<Any> {
    val providerMethod = testInstance.methods.filter {
      it.isAnnotationPresent(ParameterProvider::class.java)
    }.singleOrNull {
      val annotation = it.getAnnotation(ParameterProvider::class.java)
      val expectedName = annotation.methodName
      val returnType = it.returnType
      (methodName == expectedName) && (returnType == List::class.java)
    } ?: error("Cannot find suitable @ParameterProvider for method '$methodName'")
    return providerMethod.invoke(this) as List<Any>
  }

  private fun invokeTestMethod(target: TestableSubject, testMethod: Method) {
    target.beforeEach()
    testMethod.invoke(this, target)
    target.afterEach()
  }

  private fun invokeTestMethod(target: TestableSubject, testMethod: Method, arg: Any) {
    target.beforeEach()
    testMethod.invoke(this, target, arg)
    target.afterEach()
  }

  private fun buildTestFunc(name: String, callback: () -> Unit) = DynamicTest.dynamicTest(name, callback)
}