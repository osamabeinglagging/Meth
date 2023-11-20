package dev.macrohq.meth.feature.routebuilder

import net.minecraft.util.BlockPos

class Vein(val veinColor: Int, val veinX: Int, val veinY: Int, val veinZ: Int) {
  var encodedValue: Int

  constructor(veinColor: Int, centerBlock: BlockPos) : this(veinColor, centerBlock.x, centerBlock.y, centerBlock.z)
  constructor(encodedValue: Int) : this(
    (encodedValue shr 29) and 0b111,
    (encodedValue shr 19) and 0b1111111111,
    (encodedValue shr 10) and 0b111111111,
    encodedValue and 0b1111111111
  )

  init {
    this.encodedValue = this.encodeVein(this.veinColor, this.veinX, this.veinY, this.veinZ)
  }

  fun getCenterBlock(): BlockPos = BlockPos(this.veinX, this.veinY, this.veinZ)

  private fun encodeVein(veinColor: Int, veinX: Int, veinY: Int, veinZ: Int): Int =
    (veinColor shl 29) or (veinX shl 19) or (veinY shl 10) or veinZ

  private fun decodeVein(encodedValue: Int): Vein = Vein(
    (encodedValue shr 29) and 0b111,
    (encodedValue shr 19) and 0b1111111111,
    (encodedValue shr 10) and 0b111111111,
    encodedValue and 0b1111111111
  )
}