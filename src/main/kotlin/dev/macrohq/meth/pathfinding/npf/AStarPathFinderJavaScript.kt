package dev.macrohq.meth.pathfinding.npf

import dev.macrohq.meth.pathfinding.npf.costs.ActionCosts
import dev.macrohq.meth.pathfinding.npf.goal.Goal
import dev.macrohq.meth.pathfinding.npf.openset.BinaryHeapOpenSet
import dev.macrohq.meth.pathfinding.npf.path.PathNode
import dev.macrohq.meth.util.BlockUtil
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.util.BlockPos

class AStarPathFinderJavaScript(val startPos: BlockPos, val targetPos: BlockPos) {
  val openSet = BinaryHeapOpenSet()
  val closedSet = Long2ObjectOpenHashMap<PathNode>()
  val goal = Goal(targetPos)

  fun findPath(maxIters: Int = 2000): MutableList<BlockPos> {
    openSet.add(PathNode(startPos.x, startPos.y, startPos.z, goal))

    for (i in 0..maxIters) {
      val currentPos = openSet.poll()
      println("first: " + currentPos)
//      break
      if (goal.reached(currentPos)) {
        return reconstructPath(currentPos)
      }

      closedSet[currentPos.hash()] = currentPos
      BlockUtil.neighbourGenerator(BlockPos(currentPos.x, currentPos.y, currentPos.z), 1).forEach {
        if (it != BlockPos(currentPos.x, currentPos.y, currentPos.z)) {
          val node = PathNode(it.x, it.y, it.z, goal)
          node.parent = currentPos
          node.costToThisNode += ActionCosts.getWalkCost(BlockPos(currentPos.x, currentPos.y, currentPos.z), it)
          if (closedSet[node.hash()] == null) openSet.add(node)
        }
      }
      
      println("Size: " + openSet.size)
//      break
    }

    return mutableListOf()
  }

  private fun reconstructPath(currentPos: PathNode): MutableList<BlockPos> {
    val path = mutableListOf<BlockPos>()
    var thisNode: PathNode? = currentPos
    while (thisNode != null) {
      path.add(0, BlockPos(thisNode.x, currentPos.y, currentPos.z))
      thisNode = thisNode.parent
    }
    return path
  }
}