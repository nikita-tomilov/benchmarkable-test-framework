package com.nikitatomilov

import com.nikitatomilov.GenericServiceBenchmark.Companion.buildServiceA
import com.nikitatomilov.GenericServiceBenchmark.Companion.buildServiceB
import com.nikitatomilov.example.GenericService
import org.junit.jupiter.api.Disabled

@Disabled
class GenericServiceTest : AllTestsRunner<GenericService>(
    GenericServiceBenchmark::class.java, listOf(buildServiceA(), buildServiceB()))