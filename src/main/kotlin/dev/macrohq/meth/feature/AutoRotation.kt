package dev.macrohq.meth.feature

import dev.macrohq.meth.feature.helper.Angle
import dev.macrohq.meth.util.*
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AutoRotation {
  private var enabled = false
  private var easeFunction: ((Float) -> Float)? = null

  private var targetVecPos: Vec3? = null
  private var targetEntity: Pair<Entity, Float>? = null

  private var startAngle: Angle? = null
  private var targetAngle: Angle? = null
    get() {
      if (this.targetEntity != null) {
        return this.setTargetAngle(this.targetEntity!!.first.positionVector.addVector(0.0, 1.5, 0.0))
      }
      if (this.targetVecPos != null) {
        return this.setTargetAngle(this.targetVecPos!!)
      }
      return field
    }
  private var angleChange: Angle? = null

  private var endTime = 0L
  private var startTime = 0L

  private var relativeChange = false
  private var lastFrameRelativeAngle = Angle(0f, 0f)

  private var lockType: LockType = LockType.NONE
  private var smoothLockTime = 0

  fun easeToAngle(
    angle: Angle,
    time: Int,
    easeFunction: (Float) -> Float = EaseUtil.easingFunctions.random(),
    relativeChange: Boolean = false
  ) {
    this.enabled = true
    this.relativeChange = relativeChange
    this.easeFunction = easeFunction

    this.startAngle = Angle(player.rotationYaw, player.rotationPitch)
    this.angleChange = AngleUtil.getNeededAngleChange(startAngle!!, angle)
    this.targetAngle = Angle(startAngle!!.yaw + angleChange!!.yaw, startAngle!!.pitch + angleChange!!.pitch)

    this.startTime = System.currentTimeMillis()
    this.endTime = this.startTime + time
  }

  fun easeToBlock(
    blockPos: BlockPos,
    time: Int = 500,
    easeFunction: (Float) -> Float = EaseUtil.easingFunctions.random(),
    relativeChange: Boolean = false,
    lockType: LockType = LockType.NONE,
    smoothLockTime: Int = 0
  ) {
    this.easeToVec(BlockUtil.getClosestSidePos(blockPos), time, easeFunction, relativeChange, lockType, smoothLockTime)
  }

  fun easeToEntity(
    entity: Entity,
    height: Float = 1.5f,
    time: Int = 500,
    easeFunction: (Float) -> Float = EaseUtil.easingFunctions.random(),
    relativeChange: Boolean = false,
    lockType: LockType = LockType.NONE,
    smoothLockTime: Int = 0
  ) {
    this.targetEntity = Pair(entity, height)
    this.defaultInit(easeFunction, this.targetAngle!!, time)

    this.relativeChange = relativeChange
    this.lockType = lockType
    this.smoothLockTime = smoothLockTime
  }

  fun easeToVec(
    endVec: Vec3,
    time: Int = 500,
    easeFunction: (Float) -> Float = EaseUtil.easingFunctions.random(),
    relativeChange: Boolean = false,
    lockType: LockType = LockType.NONE,
    smoothLockTime: Int = 0
  ) {
    this.targetVecPos = endVec
    this.defaultInit(easeFunction, this.targetAngle!!, time)

    this.relativeChange = relativeChange
    this.lockType = lockType
    this.smoothLockTime = smoothLockTime
  }

  private fun defaultInit(easeFunction: (Float) -> Float, targetAngle: Angle, time: Int) {
    this.enabled = true

    this.easeFunction = easeFunction
    this.startAngle = Angle(player.rotationYaw, player.rotationPitch)
    this.angleChange = AngleUtil.getNeededAngleChange(startAngle!!, targetAngle)

    this.startTime = System.currentTimeMillis()
    this.endTime = this.startTime + time
  }

  private fun setTargetAngle(endVec: Vec3): Angle {
    val dummyAngle = AngleUtil.getAngle(endVec)
    return Angle((player.rotationYaw.toInt() / 360) * 360 + dummyAngle.yaw, dummyAngle.pitch)
  }

  private fun changeAngle(yawChange: Float, pitchChange: Float) {
    val newYawChange = yawChange / 0.15f
    val newPitchChange = pitchChange / 0.15f
    player.setAngles(newYawChange, newPitchChange)
  }

  private fun interpolate(startAngle: Angle, endAngle: Angle) {
    val timeProgress = (System.currentTimeMillis() - this.startTime).toFloat() / (this.endTime - this.startTime)
    val totalAngleProgress = this.easeFunction!!(timeProgress)

    val currentYawProgress: Float
    val currentPitchProgress: Float
    val yawProgressThisFrame: Float
    val pitchProgressThisFrame: Float

    if (!this.relativeChange) {
      currentYawProgress = (player.rotationYaw - startAngle.yaw) / (endAngle.yaw - startAngle.yaw)
      currentPitchProgress = (player.rotationPitch - startAngle.pitch) / (endAngle.pitch - startAngle.pitch)

      yawProgressThisFrame = (endAngle.yaw - startAngle.yaw) * (totalAngleProgress - currentYawProgress)
      pitchProgressThisFrame = (endAngle.pitch - startAngle.pitch) * (totalAngleProgress - currentPitchProgress)
    } else {
      currentYawProgress = (endAngle.yaw - startAngle.yaw) * totalAngleProgress
      currentPitchProgress = (endAngle.pitch - startAngle.pitch) * totalAngleProgress

      yawProgressThisFrame = currentYawProgress + startAngle.yaw - player.rotationYaw
      pitchProgressThisFrame = currentPitchProgress + startAngle.pitch - player.rotationPitch
    }

    this.changeAngle(
      AngleUtil.reduceTrailingPointsTo(yawProgressThisFrame, 2),
      -AngleUtil.reduceTrailingPointsTo(pitchProgressThisFrame, 2)
    )
  }

  fun stop() {
    enabled = false
    easeFunction = null

    targetVecPos = null
    targetEntity = null

    startAngle = null
    targetAngle = null
    angleChange = null

    endTime = 0L
    startTime = 0L

    relativeChange = false
    lastFrameRelativeAngle = Angle(0f, 0f)

    lockType = LockType.NONE
    smoothLockTime = 0
  }

  @SubscribeEvent
  fun onRenderOverlay(event: RenderGameOverlayEvent) {
    if (!this.enabled) return

    if (this.endTime >= System.currentTimeMillis()) {
      this.interpolate(this.startAngle!!, this.targetAngle!!)
      return
    }
    if (lockType == LockType.NONE) {
      this.stop(); return
    }

    if (lockType == LockType.INSTANT) {
      player.rotationYaw = this.targetAngle!!.yaw
      player.rotationPitch = this.targetAngle!!.pitch
    } else {
      if (this.targetEntity != null) {
        this.easeToEntity(
          this.targetEntity!!.first,
          this.targetEntity!!.second,
          this.smoothLockTime,
          lockType = this.lockType,
          smoothLockTime = this.smoothLockTime
        )
      } else {
        this.easeToVec(
          this.targetVecPos!!,
          this.smoothLockTime,
          lockType = this.lockType,
          smoothLockTime = this.smoothLockTime
        )
      }
    }
  }
}

enum class LockType { NONE, INSTANT, SMOOTH }