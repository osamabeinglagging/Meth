package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.util.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.random.Random

class RandomMovement: AbstractFeature() {
  override val featureName: String = "RandomMovement"
  override val isPassiveFeature: Boolean = true

  private var timer = Timer(0)
  private var state = State.STARTING

  companion object {
    private var instance: RandomMovement? = null
    fun getInstance(): RandomMovement {
      if (instance == null) {
        instance = RandomMovement()
      }
      return instance!!
    }
  }

  enum class State {
    STARTING,
    MOVING,
    STOP_MOVING,
    WAITING
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.canEnable()) return

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

  override fun disable() {
    enabled = false
    state = State.STARTING
    KeyBindUtil.movement()
  }
}