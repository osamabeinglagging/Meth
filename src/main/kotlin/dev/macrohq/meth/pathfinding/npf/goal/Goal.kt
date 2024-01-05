package dev.macrohq.meth.pathfinding.npf.goal

import dev.macrohq.meth.pathfinding.npf.blockpos.BetterBlockPos
import dev.macrohq.meth.pathfinding.npf.costs.ActionCosts
import dev.macrohq.meth.pathfinding.npf.path.PathNode
import net.minecraft.util.BlockPos
import kotlin.math.abs

class Goal(val x: Int, val y: Int, val z: Int) {

  constructor(betterBlockPos: BetterBlockPos) : this(betterBlockPos.x, betterBlockPos.y, betterBlockPos.z)
  constructor(blockPos: BlockPos) : this(blockPos.x, blockPos.y, blockPos.z)

  fun reached(pathNode: PathNode): Boolean{
    return this.x == pathNode.x && this.y == pathNode.y && this.z == pathNode.z
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Goal) return false
    return this.x == other.x && this.y == other.y && this.z == other.z
  }

  override fun hashCode(): Int {
    return BetterBlockPos.longHash(this.x, this.y, this.z).toInt()
  }

  fun getGoalBlock(): BetterBlockPos {
    return BetterBlockPos(this.x, this.y, this.z)
  }

  fun heuristic(x: Int, y: Int, z: Int): Double {
    val dx = abs(this.x - x).toDouble()
    val dy = (this.y - y).toDouble()
    val dz = abs(this.z - z).toDouble()
    var cost = 0.0;

    if (dx > dz) {
      cost += ((dx - dz) * ActionCosts.WALK_ONE_BLOCK_COST) / 4
      cost += (dz * ActionCosts.WALK_ONE_BLOCK_COST * ActionCosts.SQRT_2) / 4
    } else {
      cost += ((dz - dx) * ActionCosts.WALK_ONE_BLOCK_COST) / 4
      cost += (dx * ActionCosts.WALK_ONE_BLOCK_COST * ActionCosts.SQRT_2) / 4
    }

    cost += if (dy > 0) {
      ActionCosts.JUMP_ONE_BLOCK_COST * dy
    } else {
      ActionCosts.FALL_N_BLOCKS_COST[abs(dy).toInt()]
    }
    return cost
  }

  fun estimatedDistanceFrom(betterBlockPos: BetterBlockPos): Double {
    return heuristic(betterBlockPos.x, betterBlockPos.y, betterBlockPos.z)
  }

  fun estimatedDistanceFrom(blockPos: BlockPos): Double {
    return heuristic(blockPos.x, blockPos.y, blockPos.z)
  }
}