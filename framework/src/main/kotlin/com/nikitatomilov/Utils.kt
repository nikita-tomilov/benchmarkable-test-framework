package com.nikitatomilov

import java.lang.reflect.Method

fun name(method: Method) = method.name

fun name(method: Method, arg: Any) = "${method.name} - $arg"