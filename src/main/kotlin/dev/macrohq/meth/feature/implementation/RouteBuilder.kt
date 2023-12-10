package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.helper.RouteNode
import dev.macrohq.meth.feature.helper.TransportMethod
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import org.lwjgl.input.Keyboard

object RouteBuilder {
  var enabled = false
  private var tempBlock: BlockPos? = null
  private var createNode = false
  private val keys = mutableMapOf<Int, Boolean>()
  var route = mutableListOf<RouteNode>()

  @SubscribeEvent
  fun onInput(event: KeyInputEvent) {
    if (!enabled) return

    if (Keyboard.isKeyDown(Keyboard.KEY_NUMPADENTER)) {
      RenderUtil.filledBox.remove(tempBlock)
      val selectedBlock = (route.lastOrNull()?.block ?: player.getStandingOnFloor().up()).down()
      when (tempBlock) {
        null -> {
          tempBlock = selectedBlock
        }

        selectedBlock -> {
          tempBlock = player.getStandingOnFloor()
        }

        else -> {
          createNode = true
          log("press any:")
          log("1. Fly")
          log("2. Walk")
          log("3. Sneak Walk")
          log("4. Etherwarp")
        }
      }
      RenderUtil.filledBox.add(tempBlock!!)
      return
    }

    if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
      if (tempBlock == null && route.size > 0) {
        val last = route.last().block
        route.removeLast()
        RenderUtil.filledBox.remove(last)
      }
      if (tempBlock != null) {
        RenderUtil.filledBox.remove(tempBlock)
        tempBlock = null
      }
      return
    }

    if(Keyboard.isKeyDown(Keyboard.KEY_MULTIPLY)){
      if(tempBlock != null) return

      val last = route.last().block
      route.removeLast()
      tempBlock = last
      return
    }

    if (tempBlock == null) return

    if (createNode) {
      val transportMethod: TransportMethod = when {
        Keyboard.isKeyDown(Keyboard.KEY_1) -> TransportMethod.FLY
        Keyboard.isKeyDown(Keyboard.KEY_2) -> TransportMethod.WALK
        Keyboard.isKeyDown(Keyboard.KEY_3) -> TransportMethod.SNEAK_WALK
        Keyboard.isKeyDown(Keyboard.KEY_4) -> TransportMethod.ETHERWARP
        Keyboard.isKeyDown(Keyboard.KEY_DIVIDE) -> {
          createNode = false
          log("Exiting Save.")
          null
        }

        else -> {
          log("Press Forward Slash to Exit.")
          null
        }
      } ?: return

      route.add(RouteNode(tempBlock!!, transportMethod))
      log("Added ${tempBlock!!} to route.")
      createNode = false
      tempBlock = null
      return
    }

    RenderUtil.filledBox.remove(tempBlock!!)
    tempBlock = getOffset(tempBlock!!)
    RenderUtil.filledBox.add(tempBlock!!)
  }

  private fun getOffset(block: BlockPos): BlockPos {
    val offset: DoubleArray = when {
      Keyboard.isKeyDown(Keyboard.KEY_LEFT) -> doubleArrayOf(-1.0, 0.0, 0.0)
      Keyboard.isKeyDown(Keyboard.KEY_RIGHT) -> doubleArrayOf(1.0, 0.0, 0.0)
      Keyboard.isKeyDown(Keyboard.KEY_UP) -> doubleArrayOf(0.0, 0.0, 1.0)
      Keyboard.isKeyDown(Keyboard.KEY_DOWN) -> doubleArrayOf(0.0, 0.0, -1.0)
      Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5) -> doubleArrayOf(0.0, 1.0, 0.0)
      Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2) -> doubleArrayOf(0.0, -1.0, 0.0)
      Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7) -> {
        val distance = 24
        val playerEye = tempBlock!!.toVec3()
        val lookVec = player.lookVec
        val endPos = Vec3(
          playerEye.xCoord + lookVec.xCoord * distance,
          playerEye.yCoord + lookVec.yCoord * distance,
          playerEye.zCoord + lookVec.zCoord * distance
        )
        return BlockPos(endPos.xCoord, endPos.yCoord, endPos.zCoord)
      }

      else -> doubleArrayOf(0.0, 0.0, 0.0)
    }
    return BlockUtil.getRelativeBlock(offset[0].toInt(), offset[1].toInt(), offset[2].toInt(), block)
  }

  private fun handleKey() {
    for (key in 0 until Keyboard.KEYBOARD_SIZE) {
      keys[key] = Keyboard.isKeyDown(key)
    }
  }

  private fun get(key: Int): Boolean {
    return keys.getOrDefault(key, false)
  }

  fun enable() {
    enabled = true

    route = mutableListOf()
    tempBlock = null
    createNode = false
    keys.clear()

    log("[RouteBuilder] - Enabled")
  }

  fun disable() {
    route = mutableListOf()
    enabled = false
    tempBlock = null
    createNode = false
    keys.clear()

    log("[RouteBuilder] - Disabled")
  }
}