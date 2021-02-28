package com.nikitatomilov.api

interface TestableSubject {

  fun beforeAll()

  fun afterAll()

  fun beforeEach()

  fun afterEach()
}