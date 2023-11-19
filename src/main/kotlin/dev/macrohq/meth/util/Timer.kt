package dev.macrohq.meth.util

class Timer(millis: Int) {
  private var endTime: Long

  init {
    endTime = System.currentTimeMillis() + millis
  }

  val isDone: Boolean
    get() = System.currentTimeMillis() >= endTime
}