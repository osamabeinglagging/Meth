package dev.macrohq.meth.pathfinding

import dev.macrohq.meth.util.player
import dev.macrohq.meth.util.world
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.pow
import kotlin.math.sqrt

object MovementHelper {
  private val AVAILABLE_MOVEMENTS = arrayOf(
    booleanArrayOf(true, false, false, false, false),
    booleanArrayOf(false, true, false, false, false),
    booleanArrayOf(false, false, true, false, false),
    booleanArrayOf(false, false, false, true, false),
    booleanArrayOf(true, false, true, false, false),
    booleanArrayOf(false, true, true, false, false),
    booleanArrayOf(false, true, false, true, false),
    booleanArrayOf(true, false, false, true, false)
  )

  private fun getStrafeForward(
    front: Boolean, back: Boolean, left: Boolean, right: Boolean, sneak: Boolean
  ): Pair<Float, Float> {
    var strafe = 0f
    var forward = 0f
    if (front) forward += 1
    if (back) forward -= 1
    if (left) strafe += 1
    if (right) strafe -= 1
    if (sneak) {
      strafe = (strafe.toDouble() * .3f).toFloat(); forward = (forward.toDouble() * .3f).toFloat()
    }
    return Pair(strafe, forward)
  }

  private fun getMotion(inputStrafe: Float, inputForward: Float, friction: Float): Pair<Double, Double> {
    var motionX = player.motionX
    var motionZ = player.motionZ
    var inputMagnitude = inputStrafe * inputStrafe + inputForward * inputForward

    if (inputMagnitude >= 1.0E-4F) {
      inputMagnitude = MathHelper.sqrt_float(inputMagnitude)

      if (inputMagnitude < 1.0F) {
        inputMagnitude = 1.0F
      }

      val normalizedFriction = friction / inputMagnitude
      val strafeSpeed = inputStrafe * normalizedFriction * normalizedFriction
      val forwardSpeed = inputForward * normalizedFriction * normalizedFriction

      val yawRadians = player.rotationYaw * 3.1415927F / 180.0F
      val sinYaw = MathHelper.sin(yawRadians)
      val cosYaw = MathHelper.cos(yawRadians)

      motionX += (strafeSpeed * cosYaw - forwardSpeed * sinYaw).toDouble()
      motionZ += (forwardSpeed * cosYaw + strafeSpeed * sinYaw).toDouble()
    }
    return Pair(motionX, motionZ)
  }

  fun allPoints(): MutableList<Vec3> {
    val points = mutableListOf<Vec3>()
    for (i in AVAILABLE_MOVEMENTS) {
      val strafeForward = getStrafeForward(i[0], i[1], i[2], i[3], i[4])
      val motion = getMotion(strafeForward.first, strafeForward.second, calculateFriction())
      val nextPos = player.positionVector.addVector(motion.first, 0.0, motion.second)
      points.add(nextPos)
    }
    return points
  }

  fun closestKeysetsToBlock(block: BlockPos): BooleanArray {
    val points = allPoints()
    val blk = Vec3(block).addVector(.5, 0.0, .5)
    return AVAILABLE_MOVEMENTS[points.indexOf(points.minBy {
      sqrt((it.xCoord - blk!!.xCoord).pow(2.0) + (it.zCoord - blk.zCoord).pow(2.0))
    })]
  }

  private fun calculateFriction(): Float {
    var slipperiness = 0.91F
    if (player.onGround) {
      val blockPos = BlockPos(player.posX.toInt(), (player.entityBoundingBox.minY - 1).toInt(), player.posZ.toInt())
      slipperiness = world.getBlockState(blockPos).block.slipperiness * 0.91F
    }
    return 0.16277136F / (slipperiness * slipperiness * slipperiness)
  }
}