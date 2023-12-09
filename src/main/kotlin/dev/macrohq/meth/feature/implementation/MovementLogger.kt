package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.helper.Angle
import dev.macrohq.meth.feature.helper.Target
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class MovementLogger: AbstractFeature() {
  override val featureName: String = "MovementLogger"
  override val isPassiveFeature: Boolean = true

  private var replay = false
  private var lastTickYaw = 0f
  private var lastTickPitch = 0f
  private var timerEnded = false
  private var timer = Timer(0)
  private var currentMovementToReplayIndex = 0
  private var movements = mutableListOf<Movement>()
  private var movementsToReplay = mutableListOf<Movement>()

  companion object {
    private var instance: MovementLogger? = null
    fun getInstance(): MovementLogger {
      if (instance == null) {
        instance = MovementLogger()
      }
      return instance!!
    }
  }

  fun enable(time: Int) {
    this.enabled = true
    this.success = false
    this.failed = false
    this.timer = Timer(time)

    info("Starting Movement Logger.")
  }

  override fun disable() {
    if (!(this.canEnable() || this.replay)) return

    this.replay = false
    this.enabled = false
    this.lastTickYaw = 0f
    this.lastTickPitch = 0f
    this.timerEnded = false
    this.timer = Timer(0)
    this.currentMovementToReplayIndex = 0
    autoRotation.disable()

    info("Stopping Movement Logger.")
  }

  fun print(): String {
    var movement = ""
    this.movements.forEach { movement += it.toString() }
    this.movementsToReplay = movements.toMutableList()
    return movement
  }

  fun replay(moves: String, time: Int) {
    this.replay = true
    this.timer = Timer(time)
    this.currentMovementToReplayIndex = 0
    this.movementsToReplay.clear()

    moves.split(";")
      .toMutableList()
      .apply { removeIf { it == "" } }
      .forEach {
        this.movementsToReplay.add(Movement(it))
      }

    info("Starting Movement Replay")
  }

  fun clear() = this.movements.clear()

  @SubscribeEvent
  fun onTickRecord(event: ClientTickEvent) {
    if (player == null || world == null || !this.enabled || event.phase == TickEvent.Phase.END) return
    if (!this.timer.isDone) return

    if (!this.timerEnded && this.timer.isDone) {
      this.timerEnded = true
      this.lastTickYaw = player.rotationYaw
      this.lastTickPitch = player.rotationPitch

      note("Started Movement Logger.")
    }

    val movement = "${gameSettings.keyBindForward.isKeyDown}," +
            "${gameSettings.keyBindBack.isKeyDown}," +
            "${gameSettings.keyBindLeft.isKeyDown}," +
            "${gameSettings.keyBindRight.isKeyDown}," +
            "${gameSettings.keyBindJump.isKeyDown}," +
            "${gameSettings.keyBindSneak.isKeyDown}," +
            "${gameSettings.keyBindSprint.isKeyDown}," +
            "${gameSettings.keyBindAttack.isKeyDown}," +
            "${player.rotationYaw - this.lastTickYaw}," +
            "${player.rotationPitch - this.lastTickPitch};"

    this.lastTickYaw = player.rotationYaw
    this.lastTickPitch = player.rotationPitch
    movements.add(Movement(movement))
  }

  @SubscribeEvent
  fun onTickReplay(event: ClientTickEvent) {
    if (player == null || world == null || !this.replay || event.phase == TickEvent.Phase.END) return
    if (!this.timer.isDone) return

    if (!this.timerEnded && this.timer.isDone) {
      this.timerEnded = true
      note("Started Movement Replay.")
    }

    this.movementsToReplay[this.currentMovementToReplayIndex].replay()
    this.currentMovementToReplayIndex++
    if (this.currentMovementToReplayIndex == movementsToReplay.size) this.disable()
  }

  @SubscribeEvent
  fun onWorldUnload(event: WorldEvent.Unload){
    this.disable()
  }

  @SubscribeEvent
  fun onWorldUnload(event: WorldEvent.Load){
    this.disable()
  }
}

class Movement(
  private var forward: Boolean,
  private var backward: Boolean,
  private var left: Boolean,
  private var right: Boolean,
  private var jump: Boolean,
  private var sneak: Boolean,
  private var sprint: Boolean,
  private var attack: Boolean,
  private var yawChange: Float,
  private var pitchChange: Float
) {
  constructor(data: String) : this(false, false, false, false, false, false, false, false, 0f, 0f) {
    this.decode(data)
  }

  fun replay() {
    KeyBindUtil.movement(
      this.forward,
      this.backward,
      this.left,
      this.right,
      this.jump,
      this.sneak,
      this.sprint,
      this.attack
    )
    autoRotation.easeTo(
      Target(Angle(
        player.rotationYaw + this.yawChange,
        player.rotationPitch + this.pitchChange
      )), 50
    )
    log("yawc: ${this.yawChange}, pitchc: ${this.pitchChange}")
  }

  private fun decode(data: String) {
    val brokenData = data.split(",")
    this.forward = brokenData[0].toBoolean()
    this.backward = brokenData[1].toBoolean()
    this.left = brokenData[2].toBoolean()
    this.right = brokenData[3].toBoolean()
    this.jump = brokenData[4].toBoolean()
    this.sneak = brokenData[5].toBoolean()
    this.sprint = brokenData[6].toBoolean()
    this.attack = brokenData[7].toBoolean()
    this.yawChange = brokenData[8].toFloat()
    this.pitchChange = brokenData[9].removeSuffix(";").toFloat()
  }

  override fun toString() = "$forward,$backward,$left,$right,$jump,$sneak,$sprint,$attack,$yawChange,$pitchChange;"
}