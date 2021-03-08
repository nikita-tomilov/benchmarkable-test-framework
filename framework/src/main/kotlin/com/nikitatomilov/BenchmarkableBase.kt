package com.nikitatomilov

import java.lang.reflect.Method

abstract class BenchmarkableBase {

  abstract fun beforeAll()

  abstract fun afterAll()

  abstract fun beforeEach()

  abstract fun afterEach()

  fun getBenchmarkableMethods(): List<Method> {
    return getTestMethods(this::class.java)
  }

  @Suppress("UNCHECKED_CAST")
  fun getParametersFor(methodName: String): List<Any> {
    return getParametersFor(this, this::class.java, methodName)
  }
}