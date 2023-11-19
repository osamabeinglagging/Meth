package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class MobKiller {
  private var enabled = false
  private var state = State.STARTING
  private var timer = Timer(0)
  private var currentEntity: Entity? = null
  private var blackListTimer = Timer(0)
  private var entities = mutableListOf<Entity>()
  private var blackList = mutableListOf<Entity>()

  enum class State {
    STARTING, CALCULATING, LOOKING, LOOKING_VERIFY, ATTACKING,
  }

  fun enable() {
    this.enabled = true
    this.entities.clear()
    this.blackList.clear()
    randomMovement.enable()
    this.currentEntity = null
    this.state = State.STARTING
    this.timer = Timer(0)
    this.blackListTimer = Timer(0)

    info("MobKiller Enabled.")
  }

  fun disable() {
    if (!this.enabled) return

    RotationUtil.stop()
    this.enabled = false
    this.entities.clear()
    this.blackList.clear()
    randomMovement.disable()
    this.currentEntity = null
    this.state = State.STARTING
    this.timer = Timer(0)

    info("MobKiller Disabled.")
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.enabled || !failsafe.failsafeAllowance) return

    if (this.blackListTimer.isDone) {
      this.blackList.clear()
      this.blackListTimer = Timer(2500)

      log("MobKiller - Mob Blacklist Cleared")
    }

    if (this.currentEntity != null) {
      if ((this.currentEntity as EntityOtherPlayerMP).health <= 0) {
        this.blackList.add(this.currentEntity!!)
        this.entities.remove(this.currentEntity!!)
        this.currentEntity = null
        this.state = State.CALCULATING
      }
    }

    when (this.state) {
      State.STARTING -> {
        InventoryUtil.holdItem(CommUtil.getWeapon())
        this.timer = Timer(config.mobKillerWaitAfterAttackTime)
        this.state = State.CALCULATING
      }

      State.CALCULATING -> {
        if(!this.timer.isDone) return

        if (this.entities.size == 0) {
          this.entities = CommUtil.getCommissionMob().toMutableList()
          this.entities.removeAll(this.blackList)
        }
        if (this.entities.size > 0) {
          this.currentEntity = this.entities[0]
          RenderUtil.entites.clear()
          RenderUtil.entites.add(this.currentEntity!!)
          this.state = State.LOOKING
        }

        log("MobKiller - Calculating Mobs.")
      }

      State.LOOKING -> {
        RotationUtil.lock(this.currentEntity!!, config.mobKillerMobLookTime)
        this.state = State.LOOKING_VERIFY

        log("MobKiller - Looking at Mob.")
      }

      State.LOOKING_VERIFY -> {
        if (AngleUtil.angleDifference(this.currentEntity!!, 1f, 1f)) {
          this.state = State.ATTACKING
        }

        log("MobKiller - Verifying Looking at Mob.")
      }

      State.ATTACKING -> {
        KeyBindUtil.rightClick()
        this.entities.remove(this.currentEntity)
        this.blackList.add(this.currentEntity!!)
        RotationUtil.stop()
        this.state = State.STARTING

        log("MobKiller - Attacking Mob.")
      }
    }
  }
}