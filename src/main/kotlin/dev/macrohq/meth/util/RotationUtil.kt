package dev.macrohq.meth.util

import cc.polyfrost.oneconfig.utils.Multithreading.runAsync
import dev.macrohq.meth.util.Logger.info
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import kotlin.math.pow

object RotationUtil {
  private var startRotation = Rotation(0f, 0f)
  private var endRotation = Rotation(0f, 0f)
  private var startTime = 0L
  private var endTime = 0L
  private var entityToLockOn: Entity? = null
  private var blockToLockOn: BlockPos? = null
  private var isOverridden = false
  private var locking = false
  var done = true

  fun ease(rotation: Rotation, durationMillis: Int, override: Boolean = false) {
    if (isOverridden) return
    isOverridden = override
    done = false
    startRotation = Rotation(player.rotationYaw, player.rotationPitch)
    val neededChange = AngleUtil.getNeededChange(startRotation, rotation)
    endRotation = Rotation(startRotation.yaw + neededChange.yaw, startRotation.pitch + neededChange.pitch)
    startTime = System.currentTimeMillis()
    endTime = startTime + durationMillis
  }

  fun lock(entity: Entity, durationMillis: Int, override: Boolean = false) {
    if (isOverridden) return
    done = false
    ease(
      AngleUtil.getAngles(
        entity.positionVector.addVector(
          0.0, 0.8, 0.0
        )
      ), durationMillis, override
    )
    entityToLockOn = entity
    blockToLockOn = null
  }

  fun lock(blockPos: BlockPos, durationMillis: Int, override: Boolean = false) {
    if (isOverridden) return
    done = false
    ease(AngleUtil.getAngles(BlockUtil.getClosestSidePos(blockPos)), durationMillis, override)
    entityToLockOn = null
    blockToLockOn = blockPos
  }

  fun onRenderWorldLast() {
    if (done) return
    if (System.currentTimeMillis() <= endTime) {
      player.rotationYaw = interpolate(startRotation.yaw, endRotation.yaw)
      player.rotationPitch = interpolate(startRotation.pitch, endRotation.pitch)
      return
    }
    player.rotationYaw = endRotation.yaw
    player.rotationPitch = endRotation.pitch
    if (!locking && ((entityToLockOn != null && entityToLockOn!!.isEntityAlive) || blockToLockOn != null)) {
      locking = true
      runAsync {
        while (locking) {
          if (entityToLockOn != null) {
            endRotation = AngleUtil.getAngles(entityToLockOn!!.positionVector.addVector(0.0, 0.8, 0.0))
            if ((entityToLockOn as EntityOtherPlayerMP).health <= 0) break
          } else if (blockToLockOn != null) {
            endRotation = AngleUtil.getAngles(BlockUtil.getClosestSidePos(blockToLockOn!!))
          }
          player.rotationYaw = endRotation.yaw
          player.rotationPitch = endRotation.pitch
        }
        stop()
      }
    } else stop()
    done = true
  }

  private fun interpolate(start: Float, end: Float): Float {
    val spentMillis = (System.currentTimeMillis() - startTime).toFloat()
    val relativeProgress = spentMillis / (endTime - startTime)
    return (end - start) * easeOutCubic(relativeProgress) + start
  }

  private fun easeOutCubic(number: Float): Float {
    return (1.0 - (1.0 - number).pow(3.0)).toFloat()
  }

  fun stop() {
    isOverridden = false
    done = true
    entityToLockOn = null
    blockToLockOn = null
    locking = false
  }

  data class Rotation(var yaw: Float, var pitch: Float)
}
