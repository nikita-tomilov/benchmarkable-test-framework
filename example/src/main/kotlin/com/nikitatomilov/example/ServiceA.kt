package com.nikitatomilov.example

import com.nikitatomilov.api.TestableSubject

class ServiceA : GenericService, TestableSubject {
  override fun doAnotherStuff() {
    println("do stuff in ${this.javaClass.simpleName}")
  }

  override fun doAnotherStuff(arg: Int) {
    println("do stuff $arg times in ${this.javaClass.simpleName}")
  }

  override fun beforeAll() {
    println("before all in ${this.javaClass.simpleName}")
  }

  override fun afterAll() {
    println("after all in ${this.javaClass.simpleName}")
  }

  override fun beforeEach() {
    println("before each in ${this.javaClass.simpleName}")
  }

  override fun afterEach() {
    println("after each in in ${this.javaClass.simpleName}")
  }

}