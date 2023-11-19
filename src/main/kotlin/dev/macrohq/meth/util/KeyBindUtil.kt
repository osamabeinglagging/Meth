package dev.macrohq.meth.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.meth.mixin.MinecraftInvoker
import net.minecraft.client.settings.KeyBinding
import kotlin.random.Random

object KeyBindUtil {

  private var leftClicking = false
  private var rightClicking = false

  fun leftClick() = (mc as MinecraftInvoker).invokeClickMouse()
  fun rightClick() = (mc as MinecraftInvoker).invokeRightClickMouse()
  fun holdLeftClick() {
    leftClick()
    gameSettings.keyBindAttack.setPressed(true)
  }
  fun releaseLeftClick() = gameSettings.keyBindAttack.setPressed(false)
  fun holdSneak() = gameSettings.keyBindSneak.setPressed(true)
  fun releaseSneak() = gameSettings.keyBindSneak.setPressed(false)

  fun leftClick(clicksPerSecond: Int) {
    if (leftClicking) return
    leftClicking = true
    runAsync {
      while (leftClicking) {
        leftClick()
        Thread.sleep(900 / clicksPerSecond.toLong() + Random.nextLong(0, 100))
      }
    }
  }


  fun rightClick(clicksPerSecond: Int) {
    if (rightClicking) return
    rightClicking = true
    runAsync {
      while (rightClicking) {
        rightClick()
        Thread.sleep(900 / clicksPerSecond.toLong() + Random.nextLong(0, 100))
      }
    }
  }

  fun stopClicking() {
    leftClicking = false
    rightClicking = false
  }


  fun jump() {
    if (!player.onGround) return
    gameSettings.keyBindJump.setPressed(true)
    runAsync {
      Thread.sleep(100)
      gameSettings.keyBindJump.setPressed(false)
    }
  }

  fun onRenderWorldLast() {
    if (mc.currentScreen != null) KeyBinding.unPressAllKeys()
  }

  fun movement(
    forward: Boolean = false,
    backward: Boolean = false,
    left: Boolean = false,
    right: Boolean = false,
    jump: Boolean = false,
    sneak: Boolean = false,
    sprint: Boolean = false,
    attack: Boolean = false
  ) {
    gameSettings.keyBindForward.setPressed(forward)
    gameSettings.keyBindBack.setPressed(backward)
    gameSettings.keyBindLeft.setPressed(left)
    gameSettings.keyBindRight.setPressed(right)
    gameSettings.keyBindJump.setPressed(jump)
    gameSettings.keyBindSneak.setPressed(sneak)
    gameSettings.keyBindSprint.setPressed(sprint)
    if(attack){
      this.holdLeftClick()
    }
  }
}
