package com.nikitatomilov.api

import java.lang.reflect.Method

data class NamedResult (
  private val method: Method,
  private val param: Any? = null
) {

  fun nameWithParam(): String = if (param == null) method.name else "${method.name} - $param"

  fun name(): String = method.name

  @Suppress("UNCHECKED_CAST")
  fun param(): Comparable<Any> {
    return param as Comparable<Any>
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