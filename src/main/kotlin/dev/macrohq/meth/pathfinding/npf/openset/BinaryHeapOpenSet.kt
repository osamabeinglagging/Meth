package dev.macrohq.meth.pathfinding.npf.openset

import dev.macrohq.meth.pathfinding.npf.path.PathNode

class BinaryHeapOpenSet(private var capacity: Int = 1024) {
  private var elements: Array<PathNode?> = arrayOfNulls(capacity)
  var size = 0

  private fun getLeftChildIndex(parentIndex: Int) = 2 * parentIndex + 1
  private fun getRightChildIndex(parentIndex: Int) = 2 * parentIndex + 2
  private fun getParentIndex(childIndex: Int) = (childIndex - 1) / 2

  private fun hasLeftChild(parentIndex: Int) = this.getLeftChildIndex(parentIndex) < this.size
  private fun hasRightChild(parentIndex: Int) = this.getRightChildIndex(parentIndex) < this.size
  private fun hasParent(childIndex: Int) = this.getParentIndex(childIndex) >= 0

  private fun getLeftChild(parentIndex: Int) = this.elements[this.getLeftChildIndex(parentIndex)]
  private fun getRightChild(parentIndex: Int) = this.elements[this.getRightChildIndex(parentIndex)]
  private fun getParent(childIndex: Int) = this.elements[this.getParentIndex(childIndex)]

  private fun swap(itemFirst: Int, itemSecond: Int){
    val temp = this.elements[itemFirst]
    this.elements[itemSecond] = this.elements[itemFirst]
    this.elements[itemSecond]!!.heapPosition = itemFirst

    this.elements[itemFirst] = temp
    this.elements[itemFirst]!!.heapPosition = itemSecond
  }

  private fun ensureCapacity(){
    if(size == capacity){
      this.capacity *= 2
      this.elements = this.elements.copyOf(this.capacity)
    }
  }

  fun poll(): PathNode{
    val itemToPoll = this.elements[0]
    this.elements[0] = this.elements[this.size - 1]
    this.size--
    this.heapifyDown()
    itemToPoll!!.heapPosition = -1
    return itemToPoll
  }

  fun add(node: PathNode){
    this.ensureCapacity()

    this.elements[this.size] = node
    this.size++
    this.heapifyUp()
  }

  private fun heapifyDown(){
    var index = 0
    while (this.hasLeftChild(index)){
      var smallChildIndex = this.getLeftChildIndex(index)
      if(this.hasRightChild(index) && this.getLeftChild(index)!!.totalCost > this.getRightChild(index)!!.totalCost){
        smallChildIndex = this.getRightChildIndex(index)
      }
      if(this.elements[index]!!.totalCost > this.elements[smallChildIndex]!!.totalCost) {
        this.swap(index, smallChildIndex)
        index = smallChildIndex
      }
      else{
        break
      }
    }
  }
  private fun heapifyUp(){
    var index = this.size - 1
    while (this.hasParent(index) && this.getParent(index)!!.totalCost > this.elements[index]!!.totalCost){
      val parentIndex = this.getParentIndex(index)
      this.swap(parentIndex, index)
      index = parentIndex
    }
  }
}
