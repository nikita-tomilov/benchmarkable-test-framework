package com.nikitatomilov.api

import java.lang.reflect.Method

data class NamedResult(
  private val method: Method,
  private val param: Any? = null
) {

  fun nameWithParam(): String = if (param == null) method.name else "${method.name} - $param"

  fun name(): String = method.name

  @Suppress("UNCHECKED_CAST")
  fun param(): Comparable<Any> {
    return param as Comparable<Any>
  }
}