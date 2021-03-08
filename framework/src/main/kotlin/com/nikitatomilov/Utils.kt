package com.nikitatomilov

import com.nikitatomilov.annotations.BenchmarkableTest
import com.nikitatomilov.annotations.ParameterProvider
import java.lang.reflect.Method

fun name(method: Method) = method.name

fun name(method: Method, arg: Any) = "${method.name} - $arg"

fun getTestMethods(testSource: Class<*>) = testSource.methods.filter {
  it.isAnnotationPresent(BenchmarkableTest::class.java)
}.sortedBy { it.name }

@Suppress("UNCHECKED_CAST")
fun getParametersFor(caller: Any, testSource: Class<*>, methodName: String): List<Any> {
  val providerMethod = testSource.methods.filter {
    it.isAnnotationPresent(ParameterProvider::class.java)
  }.singleOrNull {
    val annotation = it.getAnnotation(ParameterProvider::class.java)
    val expectedName = annotation.methodName
    val returnType = it.returnType
    (methodName == expectedName) && (returnType == List::class.java)
  } ?: error("Cannot find suitable @ParameterProvider for method '$methodName'")
  return providerMethod.invoke(caller) as List<Any>
}