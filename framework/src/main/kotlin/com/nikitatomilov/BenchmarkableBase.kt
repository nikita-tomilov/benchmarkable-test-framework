package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.annotations.ParameterProvider
import java.lang.reflect.Method

abstract class BenchmarkableBase {

  abstract fun beforeAll()

  abstract fun afterAll()

  abstract fun beforeEach()

  abstract fun afterEach()

  fun getBenchmarkableMethods(): List<Method> {
    return this.javaClass.methods.filter {
      it.isAnnotationPresent(BenchmarkableTest::class.java)
    }.sortedBy { it.name }
  }

  @Suppress("UNCHECKED_CAST")
  fun getParametersFor(methodName: String): List<Any> {
    val providerMethod = this.javaClass.methods.filter {
      it.isAnnotationPresent(ParameterProvider::class.java)
    }.singleOrNull {
      val annotation = it.getAnnotation(ParameterProvider::class.java)
      val expectedName = annotation.methodName
      val returnType = it.returnType
      (methodName == expectedName) && (returnType == List::class.java)
    } ?: error("Cannot find suitable @ParameterProvider for method '$methodName'")
    return providerMethod.invoke(this) as List<Any>
  }

}