package dev.macrohq.meth.feature

import dev.macrohq.meth.util.EaseUtil
import dev.macrohq.meth.util.RotationUtil
import dev.macrohq.meth.util.gameSettings
import dev.macrohq.meth.util.player
import net.minecraft.util.MathHelper
import kotlin.math.ceil

class AutoRotation {
  private var easeFunction: ((Float) -> Float)? = null
  private var targetAngle: Angle? = null
  private var angleChange: Angle? = null
  fun enable(angle: Angle, time: Int = 500, dynamicUpdate: Boolean = false, easeFunction: (Float) -> Float = EaseUtil.easingFunctions.random()){
    this.targetAngle = angle
    this.easeFunction = easeFunction
    this.angleChange = this.getNeededChange(targetAngle!!)
  }

  fun moveMouse(deltaX: Int, deltaY: Int) {
    val sensitivity: Float = gameSettings.mouseSensitivity * 0.6f + 0.2f
    val adjustedSensitivity = sensitivity * sensitivity * sensitivity * 8.0f
    val yawChange = deltaX * adjustedSensitivity
    val pitchChange = deltaY * adjustedSensitivity
    player.setAngles(yawChange, pitchChange)
  }

  fun pixelsForDegree(yawChange: Float, pitchChange: Float): Pair<Int, Int> {
    val mouseSensitivity: Float = gameSettings.mouseSensitivity * 0.6f + 0.2f
    val mouseSensitivityCubed = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f
    val pixelsPerDegreeHorizontal = yawChange / (3f / 20) * mouseSensitivityCubed
    val pixelsPerDegreeVertical = pitchChange / (3f / 20) * mouseSensitivityCubed
    return Pair(ceil(pixelsPerDegreeHorizontal).toInt(), ceil(pixelsPerDegreeVertical).toInt())
  }

  fun getNeededChange(endRot: Angle): Angle {
    var yawChange = MathHelper.wrapAngleTo180_float(endRot.yaw) - MathHelper.wrapAngleTo180_float(player.rotationYaw)
    if (yawChange <= -180.0f) yawChange += 360.0f else if (yawChange > 180.0f) yawChange += -360.0f
    return Angle(yawChange, endRot.pitch - player.rotationPitch)
  }
}

data class Angle(val yaw: Float, val pitch: Float)