package dev.macrohq.meth.pathfinding.npf.path

import dev.macrohq.meth.pathfinding.npf.goal.Goal
import dev.macrohq.meth.pathfinding.npf.blockpos.BetterBlockPos

class PathNode(val x: Int, val y: Int, val z: Int, val goal: Goal) {
  var parent: PathNode? = null
  var heapPosition: Int = -1
  private var costToEnd: Double = goal.heuristic(this.x, this.y, this.z) // HCost -> Goal.cost
  var costToThisNode: Double = parent?.costToThisNode ?: 0.0 // Cost to here
  val totalCost: Double get() = this.costToEnd + this.costToThisNode// f cost

  fun isInHeap(): Boolean {
    return heapPosition != -1;
  }

  override fun hashCode(): Int {
    return hash().toInt()
  }

  fun hash(): Long {
    return BetterBlockPos.longHash(this.x, this.y, this.z)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    val obj = other as PathNode
    return this.x == obj.x && this.y == obj.y && this.z == obj.z
  }

  override fun toString(): String {
    return "x: $x, y: $y, z: $z"
  }

}