package dev.macrohq.meth.pathfinding

import dev.macrohq.meth.feature.helper.Angle
import dev.macrohq.meth.feature.helper.Target
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.pow
import kotlin.math.sqrt

class PathExec {
  var enabled = false
  private var route = mutableListOf<BlockPos>()
  var next: BlockPos? = null
  private var offPathTime = 0

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !enabled) return

    if (!isPlayerOnPath()) {
      offPathTime++
      if (offPathTime > 200) {
        log("offPath")
        disable()
        return
      }
    } else {
      offPathTime = 0
      if (playerPointOnPath() == route.last()) {
        disable(); return
      }
      next = route[route.indexOf(playerPointOnPath()) + 1]
      autoRotation.easeTo(
        Target(Angle(AngleUtil.getAngle(next!!).yaw, 20f)),
        300)
//      RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(next!!).yaw, 20f), 300)
    }

    val movement = MovementHelper.closestKeysetsToBlock(next!!)
    KeyBindUtil.movement(movement[0], movement[1], movement[2], movement[3])
    gameSettings.keyBindSprint.setPressed(true)
    gameSettings.keyBindJump.setPressed(shouldJump())
  }

  fun enable(path: List<BlockPos>) {
    disable()
    route = path.toMutableList()
    enabled = true
  }

  fun disable() {
    route.clear()
    enabled = false
    offPathTime = 0
    next = null
    KeyBindUtil.movement()
    gameSettings.keyBindSprint.setPressed(false)
    gameSettings.keyBindJump.setPressed(false)
  }

  private fun isPlayerOnPath() = playerPointOnPath() != null

  private fun playerPointOnPath(): BlockPos? {
    val playerPos = player.getStandingOnCeil()
    for (block in route) {
      if (playerPos.x == block.x && playerPos.z == block.z) return block
    }
    return null
  }

  private fun shouldJump() = player.onGround && (next!!.y + 0.5 - player.posY) >= 0.5 && (sqrt(
    (player.posX - next!!.x).pow(
      2.0
    ) + (player.posZ - next!!.z).pow(2.0)
  ) < 2)

}