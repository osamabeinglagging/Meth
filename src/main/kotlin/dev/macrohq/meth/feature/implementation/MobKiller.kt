package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.helper.Target
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class MobKiller: AbstractFeature() {
  override val featureName: String = "MobKiller"
  override val isPassiveFeature: Boolean = false

  private var state = State.STARTING
  private var timer = Timer(0)
  private var currentEntity: Entity? = null
  private var blackListTimer = Timer(0)
  private var entities = mutableListOf<Entity>()
  private var blackList = mutableListOf<Entity>()

  companion object {
    private var instance: MobKiller? = null
    fun getInstance(): MobKiller {
      if (instance == null) {
        instance = MobKiller()
      }
      return instance!!
    }
  }

  enum class State {
    STARTING, CALCULATING, LOOKING, LOOKING_VERIFY, ATTACKING,
  }

  fun enable(forceEnable: Boolean = false) {
    this.enabled = true
    this.entities.clear()
    this.blackList.clear()
    randomMovement.enable()
    this.currentEntity = null
    this.state = State.STARTING
    this.forceEnable = forceEnable
    this.timer = Timer(0)
    this.blackListTimer = Timer(0)

    info("Enabled.")
  }

  override fun disable() {
    if (!this.enabled) return

    autoRotation.disable()
    this.enabled = false
    this.entities.clear()
    this.blackList.clear()
    randomMovement.disable()
    this.currentEntity = null
    this.state = State.STARTING
    this.timer = Timer(0)

    info("Disabled.")
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.canEnable()) return

    if (this.blackListTimer.isDone) {
      this.blackList.clear()
      this.blackListTimer = Timer(2500)

      log("Mob Blacklist Cleared")
    }

    if (this.currentEntity != null && (this.currentEntity as EntityOtherPlayerMP).health <= 0) {
      this.blackList.add(this.currentEntity!!)
      this.entities.remove(this.currentEntity!!)
      this.currentEntity = null
      this.state = State.CALCULATING
    }

    when (this.state) {
      State.STARTING -> {
        InventoryUtil.setHotbarSlotForItem(CommUtil.getWeapon())
        this.timer = Timer(config.mobKillerWaitAfterAttackTime)
        this.state = State.CALCULATING
      }

      State.CALCULATING -> {
        if (!this.timer.isDone) return

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

        log("Finding Mobs.")
      }

      State.LOOKING -> {
        autoRotation.easeTo(Target(this.currentEntity!!), config.mobKillerMobLookTime, LockType.INSTANT)
        this.state = State.LOOKING_VERIFY

        log("Looking at Mob.")
      }

      State.LOOKING_VERIFY -> {
        if (AngleUtil.isWithinAngleThreshold(this.currentEntity!!, 1f, 1f)) {
          this.state = State.ATTACKING
        }

        log("Verifying Looking at Mob.")
      }

      State.ATTACKING -> {
        KeyBindUtil.rightClick()
        this.entities.remove(this.currentEntity)
        this.blackList.add(this.currentEntity!!)
        autoRotation.disable()
        this.state = State.STARTING

        log("Attacking Mob.")
      }
    }
  }
}