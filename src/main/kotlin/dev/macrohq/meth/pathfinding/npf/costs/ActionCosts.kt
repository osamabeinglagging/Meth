package dev.macrohq.meth.pathfinding.npf.costs

import dev.macrohq.meth.util.BlockUtil
import dev.macrohq.meth.util.world
import net.minecraft.util.BlockPos
import kotlin.math.pow
import kotlin.math.sqrt

object ActionCosts {
  val SQRT_2 = sqrt(2.0)
  const val COST_INF = 1000000.0
  const val WALK_ONE_BLOCK_COST = 20 / 4.317 // 4.633
  const val WALK_ONE_IN_WATER_COST = 20 / 2.2 // 9.091
  const val LADDER_UP_ONE_COST = 20 / 2.35 // 8.511
  const val LADDER_DOWN_ONE_COST = 20 / 3.0 // 6.667
  const val SNEAK_ONE_BLOCK_COST = 20 / 1.3 // 15.385
  const val SPRINT_ONE_BLOCK_COST = 20 / 5.612 // 3.564
  val FALL_1_25_BLOCKS_COST: Double = distanceToTicks(1.25)
  val FALL_0_25_BLOCKS_COST: Double = distanceToTicks(0.25)

  // 0.4 in BlockSoulSand but effectively about half
  const val WALK_ONE_OVER_SOUL_SAND_COST: Double = WALK_ONE_BLOCK_COST * 2
  const val WALK_OFF_BLOCK_COST: Double = WALK_ONE_BLOCK_COST * 0.8 // 3.706
  const val CENTER_AFTER_FALL_COST: Double = WALK_ONE_BLOCK_COST - WALK_OFF_BLOCK_COST // 0.927
  val JUMP_ONE_BLOCK_COST: Double = FALL_1_25_BLOCKS_COST - FALL_0_25_BLOCKS_COST
  val FALL_N_BLOCKS_COST: DoubleArray = generateFallNBlocksCost()

  private fun generateFallNBlocksCost(): DoubleArray {
    val costs = DoubleArray(4097)
    for (i in 0 until 4097) {
      costs[i] = distanceToTicks(i.toDouble())
    }
    return costs
  }

  private fun velocity(ticks: Int): Double {
    return (0.98.pow(ticks.toDouble()) - 1) * -3.92
  }

  private fun distanceToTicks(distance: Double): Double {
    if (distance == 0.0) {
      return 0.0 // Avoid 0/0 NaN
    }
    var tmpDistance = distance
    var tickCount = 0
    while (true) {
      val fallDistance = velocity(tickCount)
      if (tmpDistance <= fallDistance) {
        return tickCount + tmpDistance / fallDistance
      }
      tmpDistance -= fallDistance
      tickCount++
    }
  }

  fun getWalkCost(parent: BlockPos, child: BlockPos): Double {
    val childState = world.getBlockState(child)

    val parentUp3State = world.getBlockState(parent.add(0, 3, 0))

    val childUpState = world.getBlockState(child.add(0, 1, 0))
    val childUp2State = world.getBlockState(child.add(0, 2, 0))
    val childUp3State = world.getBlockState(child.add(0, 3, 0))

    var cost = 0.0
    val isDiagonal = parent.x != child.x && parent.z != child.z

    if (!childState.block.material.isSolid) return COST_INF
    if (childUpState.block.material.isSolid) return COST_INF
    if (childUp2State.block.material.isSolid) return COST_INF

    if (child.y > parent.y) {
      if (BlockUtil.canWalkOn(parent, child)) {
        cost += if (isDiagonal) (WALK_ONE_BLOCK_COST * SQRT_2) / 4.0 else WALK_ONE_BLOCK_COST / 4.0
      } else {
        if (parentUp3State.block.material.isSolid) return COST_INF // head bonk
        cost += JUMP_ONE_BLOCK_COST
      }
    }

    if (child.y == parent.y) {
      if (BlockUtil.canWalkOn(parent, child)) {
        cost += if (isDiagonal) (WALK_ONE_BLOCK_COST * SQRT_2) / 4.0 else WALK_ONE_BLOCK_COST / 4.0
      } else return COST_INF // No clue
    }

    if (child.y < parent.y) {
      if (childUp3State.block.material.isSolid) return COST_INF
      cost += WALK_OFF_BLOCK_COST / 2.25 // random number
    }

    return cost
  }
}