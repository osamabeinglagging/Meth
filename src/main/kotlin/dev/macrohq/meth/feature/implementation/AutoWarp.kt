package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoWarp : AbstractFeature() {
  override val featureName: String = "AutoWarp"
  override val isPassiveFeature: Boolean = false

  private var failCounter = 0
  private var notOnSkyBlock = false
  private var timer = Timer(0)
  private var timeLimit = Timer(0)
  private var island: LocationTracker.Island? = null
  private var subLocation: LocationTracker.SubLocation? = null

  companion object {
    private var instance: AutoWarp? = null
    fun getInstance(): AutoWarp {
      if (instance == null) {
        instance = AutoWarp()
      }
      return instance!!
    }
  }

  fun enable(
    island: LocationTracker.Island? = null, subLocation: LocationTracker.SubLocation? = null, forceEnable: Boolean = false
  ) {
    this.failed = false
    this.enabled = true
    this.failCounter = 0
    this.timer = Timer(0)
    this.success = false
    this.forceEnable = forceEnable
    this.island = island
    this.subLocation = subLocation
    this.timeLimit = Timer(config.autoWarpTimeLimit)

    log("Enabled")
  }

  override fun disable() {
    if (!this.enabled) return

    this.failCounter = 0
    this.island = null
    this.enabled = false
    this.subLocation = null
    this.timer = Timer(0)
    this.timeLimit = Timer(0)

    log("Disabled")
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.canEnable()) return
    if (!failsafe.failsafeAllowance && !forceEnable) {
      this.disable()
      return
    }

    if (this.failCounter > 10 || timeLimit.isDone) {
      this.setSucceeded(false)
      this.disable()

      log("Could not warp to destination.")
      return
    }

    if (this.isDone()) {
      this.setSucceeded()
      this.disable()

      log("Warped to Desired Location Successfully");
      return
    }

    if (!this.timer.isDone) return
    this.failCounter++
    this.timer = Timer(config.autoWarpTime)

    val currentIsland = locationUtil.currentIsland
    val currentSubLocation = locationUtil.currentSubLocation

    if (this.notOnSkyBlock) {
      this.notOnSkyBlock = false
      player.sendChatMessage(getIslandWarpCommand(LocationTracker.Island.LOBBY))

      log("Not on SkyBlock error.")
      return
    }

    if (!locationUtil.isInSkyBlock) {
      if (locationUtil.currentIsland == LocationTracker.Island.LIMBO) {
        player.sendChatMessage(getIslandWarpCommand(LocationTracker.Island.LOBBY))
      } else {
        player.sendChatMessage("/play sb")
      }

      log("Player is not on SkyBlock")
      return
    }

    if (this.island != null && currentIsland != this.island) {
      player.sendChatMessage(getIslandWarpCommand(this.island!!))

      log("Player is not at desired island.")
      return
    }

    if (this.subLocation != null && currentSubLocation != this.subLocation) {
      player.sendChatMessage(getSubLocationWarpCommand(this.subLocation!!))

      log("Player is not at desired location.")
      return
    }
  }

  @SubscribeEvent
  fun onChat(event: ClientChatReceivedEvent) {
    if (!this.canEnable()) return
    if (event.type.toInt() != 0) return
    val message = event.message.unformattedText

    val cannotJoinSB = "Cannot join SkyBlock for a moment!"
    val couldntWarp = "Couldn't warp you! Try again later."
    val sendingCommandsTooFast = "You are sending commands too fast! Please slow down."
    val notOnSkyBlock = "Oops! You are not on SkyBlock so we couldn't warp you!"
    val noWarpScroll = "You haven't unlocked this fast travel destination!"

    if (message.contains(cannotJoinSB) || message.contains(couldntWarp) || message.contains(sendingCommandsTooFast)) {
      this.timer = Timer(config.autoWarpErrorWaitTime)
    }
    if (message.contains(notOnSkyBlock)) {
      this.notOnSkyBlock = true
    }
    if (message.contains(noWarpScroll)) {
      this.setSucceeded(false)
      this.disable()

      info("Please use the ${this.island!!.name} and ${this.subLocation!!.name} travel scrolls to unlock this destination.")
    }
  }

  fun isDone(): Boolean {
    val currentIsland = locationUtil.currentIsland
    val currentSubLocation = locationUtil.currentSubLocation

    return (this.island == null || currentIsland == this.island) &&
        (this.subLocation == null || currentSubLocation == this.subLocation)
  }

  private fun getIslandWarpCommand(island: LocationTracker.Island): String {
    return when (island) {
      LocationTracker.Island.PRIVATE_ISLAND -> "/is"
      LocationTracker.Island.THE_HUB -> "/warp hub"
      LocationTracker.Island.THE_PARK -> "/warp park"
      LocationTracker.Island.THE_FARMING_ISLANDS -> "/warp barn"
      LocationTracker.Island.SPIDER_DEN -> "/warp spider"
      LocationTracker.Island.THE_END -> "/warp end"
      LocationTracker.Island.CRIMSON_ISLE -> "/warp isle"
      LocationTracker.Island.GOLD_MINE -> "/warp gold"
      LocationTracker.Island.DEEP_CAVERNS -> "/warp deep"
      LocationTracker.Island.DWARVEN_MINES -> "/warp mines"
      LocationTracker.Island.CRYSTAL_HOLLOWS -> "/warp ch"
      LocationTracker.Island.JERRY_WORKSHOP -> "/savethejerries"
      LocationTracker.Island.DUNGEON_HUB -> "/warp dhub"
      LocationTracker.Island.LOBBY -> "/l"
      LocationTracker.Island.GARDEN -> "/warp garden"
      LocationTracker.Island.LIMBO -> ""
      LocationTracker.Island.DUNGEON -> ""
      LocationTracker.Island.RIFT -> ""
    }
  }

  private fun getSubLocationWarpCommand(subLocation: LocationTracker.SubLocation): String {
    return when (subLocation) {
      LocationTracker.SubLocation.The_Forge -> "/warp forge"
      else -> ""
    }
  }
}