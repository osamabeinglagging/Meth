package dev.macrohq.meth.newpathfinding.movement

import kotlin.math.pow

object ActionCosts {
    const val WALK_ONE_BLOCK_COST = 20 / 4.317 // 4.633
    const val WALK_ONE_IN_WATER_COST = 20 / 2.2 // 9.091
    const val WALK_ONE_OVER_SOUL_SAND_COST = WALK_ONE_BLOCK_COST * 2 // 0.4 in BlockSoulSand but effectively about half
    const val LADDER_UP_ONE_COST = 20 / 2.35 // 8.511
    const val LADDER_DOWN_ONE_COST = 20 / 3.0 // 6.667
    const val SNEAK_ONE_BLOCK_COST = 20 / 1.3 // 15.385
    const val SPRINT_ONE_BLOCK_COST = 20 / 5.612 // 3.564
    const val SPRINT_MULTIPLIER = SPRINT_ONE_BLOCK_COST / WALK_ONE_BLOCK_COST // 0.769
    const val WALK_OFF_BLOCK_COST = WALK_ONE_BLOCK_COST * 0.8 // 3.706
    const val CENTER_AFTER_FALL_COST = WALK_ONE_BLOCK_COST - WALK_OFF_BLOCK_COST // 0.927
    const val COST_INF = 1000000.0
    val FALL_1_25_BLOCKS_COST = 6.234399666206506
    val FALL_0_25_BLOCKS_COST = 3.071002418107766
    val JUMP_ONE_BLOCK_COST = FALL_1_25_BLOCKS_COST - FALL_0_25_BLOCKS_COST
}