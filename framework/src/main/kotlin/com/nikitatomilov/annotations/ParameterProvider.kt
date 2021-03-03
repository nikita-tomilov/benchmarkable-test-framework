package com.nikitatomilov.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ParameterProvider(val methodName: String)