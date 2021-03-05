package com.nikitatomilov.example

class ServiceA : GenericService {
  override fun doAnotherStuff() {
    println("do stuff in ${this.javaClass.simpleName}")
  }

  override fun doAnotherStuff(arg: Int) {
    println("do stuff $arg times in ${this.javaClass.simpleName}")
  }
}