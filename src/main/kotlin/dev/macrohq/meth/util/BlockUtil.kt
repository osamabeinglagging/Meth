package dev.macrohq.meth.util

import dev.macrohq.meth.pathfinding.AStarPathfinder
import dev.macrohq.meth.util.algorithm.Sort
import net.minecraft.block.BlockStairs
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

object BlockUtil {
  val BLOCK_SIDES = mapOf(
    EnumFacing.DOWN to floatArrayOf(0.5f, 0.01f, 0.5f),
    EnumFacing.UP to floatArrayOf(0.5f, 0.99f, 0.5f),
    EnumFacing.WEST to floatArrayOf(0.01f, 0.5f, 0.5f),
    EnumFacing.EAST to floatArrayOf(0.99f, 0.5f, 0.5f),
    EnumFacing.NORTH to floatArrayOf(0.5f, 0.5f, 0.01f),
    EnumFacing.SOUTH to floatArrayOf(0.5f, 0.5f, 0.99f),
    null to floatArrayOf(0.5f, 0.5f, 0.5f)
  )

  private val ALLOWED_MITHRIL = mapOf(
    Blocks.prismarine to intArrayOf(0, 1, 2),
    Blocks.wool to intArrayOf(3, 7),
    Blocks.stained_hardened_clay to intArrayOf(9),
    Blocks.stone to intArrayOf(4)
  )

  fun validMithril(titanium: Boolean = false): MutableList<BlockPos> {
    val blocks = mutableListOf<BlockPos>()
    val tita = mutableListOf<BlockPos>()
    neighbourGenerator(player.getStandingOnFloor(), -5, 5, -2, 4, -5, 5).forEach {
      val isAllowed = isBlockAllowedMithril(it)
      val distance = player.headPosition().distanceTo(it.toVec3())
      if (isAllowed && distance < 4.5 && RaytracingUtil.getValidSide(it) != null) {
        if (titanium && world.getBlockState(it).block == Blocks.stone) tita.add(it)
        blocks.add(it)
      }
    }
    blocks.removeAll(tita)
    blocks.remove(player.getStandingOnFloor())
    Sort.quickSort(blocks, 0, blocks.size-1, ::mithrilCost)
    blocks.addAll(0, tita)
    return blocks.toMutableSet().toMutableList()
  }

  private fun mithrilCost(block: BlockPos): Float {
    val hardness = hardnessCost(block)
    val bestPoint = bestPointOnBlock(block)
    val rot = abs(AngleUtil.getYawChange(bestPoint)) + abs(AngleUtil.getPitchChange(bestPoint))
    val distance = bestPoint.distanceTo(player.headPosition())
    return (rot * .15 + hardness * .5 + distance * .35).toFloat()
  }

  private fun hardnessCost(blockPos: BlockPos): Int {
    val state = mc.theWorld.getBlockState(blockPos)
    val block = state.block
    val color = block.getMetaFromState(state)

    return when (block) {
      Blocks.prismarine -> 3
      Blocks.stained_hardened_clay -> 1
      Blocks.wool -> if (color == 7) 5 else 7
      Blocks.stone -> 7
      else -> 10
    }
  }

  fun getClosestSidePos(block: BlockPos): Vec3 {
    return getSidePos(block, RaytracingUtil.getValidSide(block))
  }

  fun getSidePos(block: BlockPos, side: EnumFacing?): Vec3 {
    val i = BLOCK_SIDES[side]!!
    return Vec3((block.x + i[0]).toDouble(), (block.y + i[1]).toDouble(), (block.z + i[2]).toDouble())
  }

  fun isBlockAllowedMithril(blockPos: BlockPos): Boolean {
    val state = world.getBlockState(blockPos)
    val block = state.block
    val color = block.getMetaFromState(state)
    if (ALLOWED_MITHRIL.containsKey(block)) {
      return ALLOWED_MITHRIL[block]!!.contains(color)
    }
    return false
  }

  fun neighbourGenerator(mainBlock: BlockPos, size: Int): List<BlockPos> {
    return neighbourGenerator(mainBlock, size, size, size)
  }

  private fun neighbourGenerator(mainBlock: BlockPos, xD: Int, yD: Int, zD: Int): List<BlockPos> {
    return neighbourGenerator(mainBlock, -xD, xD, -yD, yD, -zD, zD)
  }

  fun neighbourGenerator(
    mainBlock: BlockPos,
    xD1: Int,
    xD2: Int,
    yD1: Int,
    yD2: Int,
    zD1: Int,
    zD2: Int
  ): List<BlockPos> {
    val neighbours: MutableList<BlockPos> = ArrayList()
    for (x in xD1..xD2) {
      for (y in yD1..yD2) {
        for (z in zD1..zD2) {
          neighbours.add(BlockPos(mainBlock.x + x, mainBlock.y + y, mainBlock.z + z))
        }
      }
    }
    return neighbours
  }

  fun isStairSlab(block: BlockPos): Boolean {
    return world.getBlockState(block).block is BlockStairs ||
            world.getBlockState(block).block is BlockStairs
  }

  fun blocksBetweenValid(startPoss: BlockPos, endPoss: BlockPos): Boolean {
    var startPos = startPoss
    var endPos = endPoss
    if (startPos.x > endPos.x) {
      startPos = endPoss
      endPos = startPoss
    }
    val blocks =
      bresenham(startPos.toVec3().addVector(0.0, 0.4, 0.0), endPos.toVec3().addVector(0.0, 0.4, 0.0)).toMutableList()
    var blockFail = 0
    var lastBlockY = blocks[0].y
    var lastFullBlock = world.isBlockFullCube(blocks[0])
    var isLastBlockSlab = isStairSlab(blocks[0])
    var isLastBlockAir = world.isAirBlock(blocks[0])
    blocks.remove(blocks[0])
    blocks.forEach {
      if (!AStarPathfinder.Node(it, null).isWalkable() && !world.isAirBlock(it)) {
        return false
      }
//            if(!(isLastBlockSlab && world.isBlockFullCube(it))) return false
      if (isLastBlockAir && world.isBlockFullCube(it) && !isStairSlab(it)) return false
//            if(!(isLastBlockAir && isStairSlab(it))) return false
      if (lastFullBlock && world.isBlockFullCube(it) && it.y > lastBlockY) return false
      if (world.isAirBlock(it)) blockFail++
      else blockFail = 0
      if (blockFail > 3) return false

      lastBlockY = it.y
      lastFullBlock = world.isBlockFullCube(it)
      isLastBlockSlab = isStairSlab(it)
      isLastBlockAir = world.isAirBlock(it)
    }
    return true
  }

  private fun bresenham(start: Vec3, end: Vec3): List<BlockPos> {
    var start0 = start
    val blocks = mutableListOf(start0.toBlockPos())
    val x1 = MathHelper.floor_double(end.xCoord)
    val y1 = MathHelper.floor_double(end.yCoord)
    val z1 = MathHelper.floor_double(end.zCoord)
    var x0 = MathHelper.floor_double(start0.xCoord)
    var y0 = MathHelper.floor_double(start0.yCoord)
    var z0 = MathHelper.floor_double(start0.zCoord)

    var iterations = 200
    while (iterations-- >= 0) {
      if (x0 == x1 && y0 == y1 && z0 == z1) {
        blocks.add(end.toBlockPos())
        return blocks
      }
      var hasNewX = true
      var hasNewY = true
      var hasNewZ = true
      var newX = 999.0
      var newY = 999.0
      var newZ = 999.0
      if (x1 > x0) {
        newX = x0.toDouble() + 1.0
      } else if (x1 < x0) {
        newX = x0.toDouble() + 0.0
      } else {
        hasNewX = false
      }
      if (y1 > y0) {
        newY = y0.toDouble() + 1.0
      } else if (y1 < y0) {
        newY = y0.toDouble() + 0.0
      } else {
        hasNewY = false
      }
      if (z1 > z0) {
        newZ = z0.toDouble() + 1.0
      } else if (z1 < z0) {
        newZ = z0.toDouble() + 0.0
      } else {
        hasNewZ = false
      }
      var stepX = 999.0
      var stepY = 999.0
      var stepZ = 999.0
      val dx = end.xCoord - start0.xCoord
      val dy = end.yCoord - start0.yCoord
      val dz = end.zCoord - start0.zCoord
      if (hasNewX) stepX = (newX - start0.xCoord) / dx
      if (hasNewY) stepY = (newY - start0.yCoord) / dy
      if (hasNewZ) stepZ = (newZ - start0.zCoord) / dz
      if (stepX == -0.0) stepX = -1.0E-4
      if (stepY == -0.0) stepY = -1.0E-4
      if (stepZ == -0.0) stepZ = -1.0E-4
      var enumfacing: EnumFacing
      if (stepX < stepY && stepX < stepZ) {
        enumfacing = if (x1 > x0) EnumFacing.WEST else EnumFacing.EAST
        start0 = Vec3(newX, start0.yCoord + dy * stepX, start0.zCoord + dz * stepX)
      } else if (stepY < stepZ) {
        enumfacing = if (y1 > y0) EnumFacing.DOWN else EnumFacing.UP
        start0 = Vec3(start0.xCoord + dx * stepY, newY, start0.zCoord + dz * stepY)
      } else {
        enumfacing = if (z1 > z0) EnumFacing.NORTH else EnumFacing.SOUTH
        start0 = Vec3(start0.xCoord + dx * stepZ, start0.yCoord + dy * stepZ, newZ)
      }
      x0 = MathHelper.floor_double(start0.xCoord) - if (enumfacing == EnumFacing.EAST) 1 else 0
      y0 = MathHelper.floor_double(start0.yCoord) - if (enumfacing == EnumFacing.UP) 1 else 0
      z0 = MathHelper.floor_double(start0.zCoord) - if (enumfacing == EnumFacing.SOUTH) 1 else 0
      blocks.add(BlockPos(x0, y0, z0))
    }
    return blocks
  }

  fun getRelativeBlock(x: Int, y: Int, z: Int, block: BlockPos): BlockPos {
    return when (mc.thePlayer.horizontalFacing) {
      EnumFacing.SOUTH -> block.add(-x, y, z)
      EnumFacing.NORTH -> block.add(x, y, -z)
      EnumFacing.EAST -> block.add(z, y, x)
      else -> block.add(-z, y, -x)
    }
  }

  fun getBreakTicks(block: BlockPos, miningSpeed: Int): Int {
    val speed =
      (ceil(((getMithrilStrength(block) * 30) / miningSpeed) + (ceil(ping.serverPing * 0.1 / 50.0) + config.mithrilMinerTickGlideOffset)) * 2).toInt()
    return maxOf(speed, 4)
  }

  private fun getMithrilStrength(blockPos: BlockPos): Int {
    val state = world.getBlockState(blockPos)
    val block = state.block
    val color = block.getMetaFromState(state)

    return when {
      block == Blocks.prismarine -> 800
      block == Blocks.stained_hardened_clay -> 500
      block == Blocks.wool -> {
        if (color == 7) 500
        else 1500
      }

      block == Blocks.stone && color == 4 -> 2000
      else -> 2000
    }
  }

  fun bestPointOnBlock(block: BlockPos): Vec3 {
    return pointsOnBlockVisible(block).filter {
      RaytracingUtil.canSeePoint(it)
    }.minByOrNull {
      abs(AngleUtil.getYawChange(it)) + abs(AngleUtil.getPitchChange(it))
    } ?: Vec3(block).addVector(.5, .5, .5)
  }

  fun pointsOnBlockVisible(block: BlockPos): MutableList<Vec3> {
    val points = mutableListOf<Vec3>()
    RaytracingUtil.validSides(block).forEach {
      points.addAll(pointsOnBlockSide(block, it))
    }
    return points
  }

  private fun pointsOnBlockSide(block: BlockPos, side: EnumFacing?): MutableList<Vec3> {
    val points = mutableListOf<Vec3>()
    val it = BLOCK_SIDES[side]!!
    fun randomVal(): Float = (Random.nextInt(3, 7)) / 10f

    if (side != null) {
      for (i in 0 until 20) {
        var x = it[0]
        var y = it[1]
        var z = it[2]
        if (x == .5f) x = randomVal()
        if (y == .5f) y = randomVal()
        if (z == .5f) z = randomVal()
        val point = Vec3(block).addVector(x.toDouble(), y.toDouble(), z.toDouble())
        if (!points.contains(point)) points.add(point)
      }
    } else {
      for (bside in BLOCK_SIDES.values) {
        for (i in 0 until 20) {
          var x = bside[0]
          var y = bside[1]
          var z = bside[2]

          if (x == .5f) x = randomVal()
          if (y == .5f) y = randomVal()
          if (z == .5f) z = randomVal()

          val point = Vec3(block).addVector(x.toDouble(), y.toDouble(), z.toDouble())
          if (!points.contains(point)) points.add(point)
        }
      }
    }
    return points
  }
}
