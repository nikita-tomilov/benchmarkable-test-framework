package com.nikitatomilov.api

import java.lang.reflect.Method
import kotlin.reflect.KClass

data class NamedResult (
  private val method: Method,
  private val param: Any? = null
) {

  fun nameWithParam(): String = if (param == null) method.name else "${method.name} - $param"

  fun name(): String = method.name

  fun param(): Any {
    return param ?: error("param is null")
  }

  @Suppress("UNCHECKED_CAST")
  fun paramForSorting(): ComparableWrapper {
    return ComparableWrapper(param())
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as NamedResult

    if (method != other.method) return false
    if (param != other.param) return false

    return true
  }

  override fun hashCode(): Int {
    var result = method.hashCode()
    result = 31 * result + (param?.hashCode() ?: 0)
    return result
  }
}

data class ComparableWrapper(
  val x: Any
) : Comparable<ComparableWrapper> {
  override fun compareTo(other: ComparableWrapper): Int {
    val a = x.javaClass
    val b = other.x.javaClass
    val x = this.x
    val y = other.x
    if ((a == b) && (x is Comparable<*>) && (y is Comparable<*>)) {
      val xc = cast(x, a)
      val yc = cast(y, a)
      val method = a.methods.filter { it.name == "compareTo" }
          .filter { it.parameterCount == 1 }.singleOrNull { it.parameterTypes[0] == a }
      if (method != null) {
        val result = method.invoke(xc, yc)
        return result as Int
      }
    }
    return x.toString().compareTo(y.toString())
  }
}

fun <T: Any> cast(any: Any, clazz: Class<out T>): T = clazz.cast(any)