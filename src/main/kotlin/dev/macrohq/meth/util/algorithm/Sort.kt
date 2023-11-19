package dev.macrohq.meth.util.algorithm

object Sort {

  // Quick Sort

  fun <T> quickSort(list: MutableList<T>, start: Int, end: Int, cost: (item: T) -> Float) {
    if (end <= start) return

    val pivotPoint = partition(list, start, end, cost)
    quickSort(list, start, pivotPoint-1, cost)
    quickSort(list, pivotPoint+1, end, cost)
  }

  private fun <T> partition(list: MutableList<T>, start: Int, end: Int, cost: (item: T) -> Float): Int {
    val pivot = cost(list[end])
    var i = start - 1

    for (j in start until end) {
      if (cost(list[j]) < pivot) {
        i++
        val temp = list[j]
        list[j] = list[i]
        list[i] = temp
      }
    }
    i++
    val temp = list[end]
    list[end] = list[i]
    list[i] = temp

    return i
  }

}