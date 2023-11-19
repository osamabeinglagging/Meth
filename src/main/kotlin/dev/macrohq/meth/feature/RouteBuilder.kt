package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import org.lwjgl.input.Keyboard

object RouteBuilder {
  private var tempBlock: BlockPos? = null
  var route = mutableListOf<BlockPos>()

  @SubscribeEvent
  fun onInput(event: KeyInputEvent) {
    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPADENTER)) {
      tempBlock = if (tempBlock == null) {
        if (route.isEmpty()) {
          player.getStandingOnFloor()
        } else {
          route[route.size - 1].down()
        }
      } else if (route.isNotEmpty() && tempBlock!! == route[route.size - 1].down()) {
        RenderUtil.filledBox.remove(tempBlock!!)
        player.getStandingOnFloor()
      } else {
        route.add(tempBlock!!)
        log("Added to route!")
        null
      }
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
      if (tempBlock != null) {
        RenderUtil.filledBox.remove(tempBlock)
        tempBlock = null
      } else if (route.size > 0) {
        val blockToRemove = route[route.size - 1]
        route.remove(blockToRemove)
        RenderUtil.filledBox.remove(blockToRemove)
      }
    }
    if (tempBlock != null) {
      if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7)) {
        val distance = 24
        val playerEye = tempBlock!!.toVec3()
        val lookVec = player.lookVec
        val endPos = Vec3(
          playerEye.xCoord + lookVec.xCoord * distance,
          playerEye.yCoord + lookVec.yCoord * distance,
          playerEye.zCoord + lookVec.zCoord * distance
        )
        RenderUtil.filledBox.remove(tempBlock!!)
        tempBlock = BlockPos(endPos.xCoord, endPos.yCoord, endPos.zCoord)
        log("TempBlock: $tempBlock")
      }
      if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
        RenderUtil.filledBox.remove(tempBlock!!)
        tempBlock = BlockUtil.getRelativeBlock(0, 0, 1, tempBlock!!)
      } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
        RenderUtil.filledBox.remove(tempBlock!!)
        tempBlock = BlockUtil.getRelativeBlock(0, 0, -1, tempBlock!!)
      } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
        RenderUtil.filledBox.remove(tempBlock!!)
        tempBlock = BlockUtil.getRelativeBlock(-1, 0, 0, tempBlock!!)
      } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
        RenderUtil.filledBox.remove(tempBlock!!)
        tempBlock = BlockUtil.getRelativeBlock(1, 0, 0, tempBlock!!)
      } else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
        RenderUtil.filledBox.remove(tempBlock!!)
        tempBlock = BlockUtil.getRelativeBlock(0, 1, 0, tempBlock!!)
      } else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) {
        RenderUtil.filledBox.remove(tempBlock!!)
        tempBlock = BlockUtil.getRelativeBlock(0, -1, 0, tempBlock!!)
      }
    }
    if (!RenderUtil.filledBox.contains(tempBlock) && tempBlock != null) {
      RenderUtil.filledBox.add(tempBlock!!)
    }
  }
}