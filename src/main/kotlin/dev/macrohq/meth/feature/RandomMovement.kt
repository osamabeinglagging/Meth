package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.random.Random

class RandomMovement {
  private var enabled = false
  private var timer = Timer(0)
  private var state = State.STARTING

  enum class State {
    STARTING,
    MOVING,
    STOP_MOVING,
    WAITING
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !enabled || !failsafe.failsafeAllowance) return

    when (state) {
      State.STARTING -> {
        state = State.WAITING
      }

      State.MOVING -> {
        val boolean = Array(4) { Random.nextBoolean() }
        KeyBindUtil.movement(boolean[0], boolean[1], boolean[2], boolean[3], sneak = true)
        timer = Timer(100)
        state = State.STOP_MOVING
      }

      State.STOP_MOVING -> {
        if (!timer.isDone) return
        KeyBindUtil.movement(sneak = true)
        timer = Timer(8000)
        state = State.WAITING
      }

      State.WAITING -> {
        if (!timer.isDone) return
        state = State.MOVING
      }
    }
  }

  fun enable() {
    enabled = true
  }

  fun disable() {
    enabled = false
    state = State.STARTING
    KeyBindUtil.movement()
  }

}