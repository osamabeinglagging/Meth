package dev.macrohq.meth.pathfinding.npf.blockpos

import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import kotlin.math.sqrt

class BetterBlockPos(val x: Int, val y: Int, val z: Int){
  constructor(x: Double, y: Double, z: Double) : this(
    MathHelper.floor_double(x),
    MathHelper.floor_double(y),
    MathHelper.floor_double(z)
  )
  constructor(x: Float, y: Float, z: Float) : this(
    MathHelper.floor_float(x),
    MathHelper.floor_float(y),
    MathHelper.floor_float(z)
  )
  constructor(blockPos: BlockPos) : this(blockPos.x, blockPos.y, blockPos.z)
  companion object{
    fun longHash(betterBlockPos: BetterBlockPos): Long{
      return longHash(betterBlockPos.x, betterBlockPos.y, betterBlockPos.z)
    }

    fun longHash(x: Int, y: Int, z: Int): Long{
      var hash = 8693L;
      hash = 9523609 * hash + x
      hash = 6988907 * hash + y
      hash = 3898207 * hash + z
      return hash
    }
  }

  override fun equals(other: Any?): Boolean {
    if(other == null) return false
    if(other is BlockPos){
      return this.x == other.x && this.y == other.y && this.z == other.z
    }
    val betterBlockPos = other as BetterBlockPos
    return this.x == betterBlockPos.x && this.y == betterBlockPos.y && this.z == betterBlockPos.z
  }

  override fun hashCode(): Int {
    return longHash(x,y,z).toInt()
  }

  fun up(amount: Int = 1): BetterBlockPos{
    return if(amount == 0) this else BetterBlockPos(this.x, this.y+amount, this.z)
  }

  fun down(amount: Int = 1): BetterBlockPos{
    return BetterBlockPos(this.x, this.y-amount, this.z)
  }

  fun north(amount: Int = 1): BetterBlockPos{
    return BetterBlockPos(this.x, this.y, this.z - amount)
  }

  fun south(amount: Int = 1): BetterBlockPos{
    return BetterBlockPos(this.x, this.y, this.z + amount)
  }

  fun east(amount: Int = 1): BetterBlockPos{
    return BetterBlockPos(this.x + amount, this.y, this.z)
  }

  fun west(amount: Int = 1): BetterBlockPos{
    return BetterBlockPos(this.x - amount, this.y, this.z)
  }

  fun distanceSq(betterBlockPos: BetterBlockPos): Double{
    val dx = (this.x - betterBlockPos.x).toDouble()
    val dy = (this.y - betterBlockPos.y).toDouble()
    val dz = (this.z - betterBlockPos.z).toDouble()

    return dx * dx + dy * dy + dz * dz
  }

  fun distanceTo(betterBlockPos: BetterBlockPos): Double{
    val dx = (this.x - betterBlockPos.x).toDouble()
    val dy = (this.y - betterBlockPos.y).toDouble()
    val dz = (this.z - betterBlockPos.z).toDouble()

    return sqrt(dx * dx + dy * dy + dz * dz)
  }
}