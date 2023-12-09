package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.helper.Angle
import dev.macrohq.meth.feature.helper.Target
import dev.macrohq.meth.util.*
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AutoRotation: AbstractFeature() {
  override val featureName: String = "AutoRotation"
  override val isPassiveFeature: Boolean = false

  private var easeFunction: ((Float) -> Float)? = null

  private var target: Target? = null
  private var startAngle: Angle? = null

  private var endTime = 0L
  private var startTime = 0L

  private var lockType: LockType = LockType.NONE
  private var smoothLockTime = 0

  companion object{
    private var instance: AutoRotation? = null
    fun getInstance(): AutoRotation{
      if(instance == null){
        instance = AutoRotation()
      }
      return instance!!
    }
  }

  fun easeTo(target: Target, time: Int, lockType: LockType = LockType.NONE, smoothLockTime: Int = 200, easeFunction: (Float) -> Float = EaseUtil.easingFunctions.random()){
    this.enabled = true
    this.forceEnable = true
    this.lockType = lockType
    this.smoothLockTime = smoothLockTime

    this.easeFunction = easeFunction
    this.startAngle = AngleUtil.PLAYER_ANGLE
    this.target = target

    this.startTime = System.currentTimeMillis()
    this.endTime = this.startTime + time
  }

  private fun changeAngle(yawChange: Float, pitchChange: Float) {
    val newYawChange = yawChange / 0.15f
    val newPitchChange = pitchChange / 0.15f
    player.setAngles(newYawChange, newPitchChange)
  }

  private fun interpolate(startAngle: Angle, endAngle: Angle) {
    val timeProgress = (System.currentTimeMillis() - this.startTime).toFloat() / (this.endTime - this.startTime)
    val totalNeededAngleProgress = this.easeFunction!!(timeProgress)
    val totalChange = AngleUtil.calculateNeededAngleChange(this.startAngle!!, this.target!!.getAngle())

    val currentYawProgress: Float = (player.rotationYaw - startAngle.yaw) / totalChange.yaw
    val currentPitchProgress: Float = (player.rotationPitch - startAngle.pitch) / totalChange.pitch
    val yawProgressThisFrame: Float = totalChange.yaw * (totalNeededAngleProgress - currentYawProgress)
    val pitchProgressThisFrame: Float = totalChange.pitch * (totalNeededAngleProgress - currentPitchProgress)

    this.changeAngle(
      AngleUtil.reduceTrailingPointsTo(yawProgressThisFrame, 2),
      -AngleUtil.reduceTrailingPointsTo(pitchProgressThisFrame, 2)
    )
  }

  override fun disable() {
    enabled = false
    forceEnable =  false
    easeFunction = null

    startAngle = null
    target = null

    endTime = 0L
    startTime = 0L

    lockType = LockType.NONE
    smoothLockTime = 0
  }

  @SubscribeEvent
  fun onRenderOverlay(event: RenderGameOverlayEvent) {
    if (!this.enabled) return

    if (this.endTime >= System.currentTimeMillis()) {
      this.interpolate(this.startAngle!!, this.target!!.getAngle())
      return
    }
    if (lockType == LockType.NONE) {
      this.disable()
      return
    }

    if (lockType == LockType.INSTANT) {
      val angChange = AngleUtil.calculateNeededAngleChange(AngleUtil.PLAYER_ANGLE, this.target!!.getAngle())
      player.rotationYaw += angChange.yaw
      player.rotationPitch += angChange.pitch
    } else {
      this.easeTo(this.target!!, 200, LockType.SMOOTH, this.smoothLockTime)
    }
  }
}

enum class LockType { NONE, INSTANT, SMOOTH }