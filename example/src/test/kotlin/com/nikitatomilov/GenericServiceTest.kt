package com.nikitatomilov

import com.nikitatomilov.GenericServiceBenchmark.Companion.buildServiceA
import com.nikitatomilov.GenericServiceBenchmark.Companion.buildServiceB
import com.nikitatomilov.example.GenericService

class GenericServiceTest : AllTestsRunner<GenericService>(
    GenericServiceBenchmark::class.java, listOf(buildServiceA(), buildServiceB()))