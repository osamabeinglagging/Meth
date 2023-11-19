package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoWarp {
  var enabled = false
  private var failed = 0
  private var success = false
  private var forceEnable = false
  private var failedToWarp = false
  private var notOnSkyBlock = false
  private var timer = Timer(0)
  private var timeLimit = Timer(0)
  private var island: LocationUtil.Island? = null
  private var subLocation: LocationUtil.SubLocation? = null
  fun enable(
    island: LocationUtil.Island? = null, subLocation: LocationUtil.SubLocation? = null, force: Boolean = false
  ) {
    this.failed = 0
    this.enabled = true
    this.success = false
    this.island = island
    this.forceEnable = force
    this.timer = Timer(0)
    this.timeLimit = Timer(config.autoWarpTimeLimit)
    this.subLocation = subLocation

    log("Auto Warp Enabled")
  }

  fun disable() {
    if(!this.enabled) return

    this.failed = 0
    this.island = null
    this.enabled = false
    this.subLocation = null
    this.timer = Timer(0)
    this.timeLimit = Timer(0)

    log("Auto Warp Disabled")
  }

  fun failed(): Boolean = !this.enabled && this.failedToWarp
  fun succeeded(): Boolean = !this.enabled && this.success

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || event.phase == TickEvent.Phase.END || !this.enabled || !(failsafe.failsafeAllowance || this.forceEnable)) return

    if (this.failed > 10 || timeLimit.isDone) {
      this.failedToWarp = true
      this.disable()
      return
    }

    if (this.isDone()) {
      this.success = true;
      this.disable();

      log("AutoWarp - Warped to Desired Location Successfully");
      return
    }

    if (!this.timer.isDone) return
    this.failed++

    val currentIsland = locationUtil.currentIsland
    val currentSubLocation = locationUtil.currentSubLocation

    if (this.notOnSkyBlock) {
      this.notOnSkyBlock = false
      player.sendChatMessage(getIslandWarpCommand(LocationUtil.Island.LOBBY))
      this.timer = Timer(config.autoWarpTime)

      log("AutoWarp - Not on SkyBlock error.")
      return
    }

    if (!locationUtil.isInSkyBlock) {
      this.timer = if (locationUtil.currentIsland == LocationUtil.Island.LIMBO) {
        player.sendChatMessage(getIslandWarpCommand(LocationUtil.Island.LOBBY))
        Timer(config.autoWarpTime)
      } else {
        player.sendChatMessage("/play sb")
        Timer(config.autoWarpTime)
      }

      log("AutoWarp - Player is not on SkyBlock")
      return
    }

    if (this.island != null && currentIsland != this.island) {
      player.sendChatMessage(getIslandWarpCommand(this.island!!))
      timer = Timer(config.autoWarpTime)

      log("AutoWarp - Player is not at desired island.")
      return
    }

    if (this.subLocation != null && currentSubLocation != this.subLocation) {
      player.sendChatMessage(getSubLocationWarpCommand(this.subLocation!!))
      this.timer = Timer(config.autoWarpTime)

      log("AutoWarp - Player is not at desired location.")
      return
    }
  }

  @SubscribeEvent
  fun onChat(event: ClientChatReceivedEvent) {
    if (event.type.toInt() != 0) return
    val message = event.message.unformattedText

    val cannotJoinSB = "Cannot join SkyBlock for a moment!"
    val couldntWarp = "Couldn't warp you! Try again later."
    val sendingCommandsTooFast = "You are sending commands too fast! Please slow down."
    val notOnSkyBlock = "Oops! You are not on SkyBlock so we couldn't warp you!"

    if (message.contains(cannotJoinSB) || message.contains(couldntWarp) || message.contains(sendingCommandsTooFast)) {
      this.timer = Timer(config.autoWarpErrorWaitTime)
    }
    if (message.contains(notOnSkyBlock)) {
      this.notOnSkyBlock = true
    }
  }

  fun isDone(): Boolean {
    val currentIsland = locationUtil.currentIsland
    val currentSubLocation = locationUtil.currentSubLocation
    if (this.island == null && this.subLocation == null) {
      return true
    }
    if (this.island != null && this.subLocation != null) {
      return currentIsland == this.island && currentSubLocation == this.subLocation
    }
    if (this.island != null) {
      return currentIsland == this.island
    }
    if (this.subLocation != null) {
      return currentSubLocation == this.subLocation
    }
    return true
  }

  private fun getIslandWarpCommand(island: LocationUtil.Island): String {
    return when (island) {
      LocationUtil.Island.PRIVATE_ISLAND -> "/is"
      LocationUtil.Island.THE_HUB -> "/warp hub"
      LocationUtil.Island.THE_PARK -> "/warp park"
      LocationUtil.Island.THE_FARMING_ISLANDS -> "/warp barn"
      LocationUtil.Island.SPIDER_DEN -> "/warp spider"
      LocationUtil.Island.THE_END -> "/warp end"
      LocationUtil.Island.CRIMSON_ISLE -> "/warp isle"
      LocationUtil.Island.GOLD_MINE -> "/warp gold"
      LocationUtil.Island.DEEP_CAVERNS -> "/warp deep"
      LocationUtil.Island.DWARVEN_MINES -> "/warp mines"
      LocationUtil.Island.CRYSTAL_HOLLOWS -> "/warp ch"
      LocationUtil.Island.JERRY_WORKSHOP -> "/savethejerries"
      LocationUtil.Island.DUNGEON_HUB -> "/warp dhub"
      LocationUtil.Island.LOBBY -> "/l"
      LocationUtil.Island.GARDEN -> "/warp garden"
      LocationUtil.Island.LIMBO -> ""
      LocationUtil.Island.DUNGEON -> ""
      LocationUtil.Island.RIFT -> ""
    }
  }

  private fun getSubLocationWarpCommand(subLocation: LocationUtil.SubLocation): String {
    return when (subLocation) {
      LocationUtil.SubLocation.The_Forge -> "/warp forge"
      else -> ""
    }
  }
}