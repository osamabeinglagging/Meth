package dev.macrohq.meth.feature.failsafe

import dev.macrohq.meth.feature.failsafe.check.BrokenCheck
import dev.macrohq.meth.feature.failsafe.check.MacroCheck
import dev.macrohq.meth.feature.failsafe.react.BrokenReact
import dev.macrohq.meth.feature.failsafe.react.CheckReact
import dev.macrohq.meth.util.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent

open class FailsafeNew {
  var failsafeAllowance = false
  var fixingStuff = false

  var gotChecked = false
  var somethingBroken = false

  var whatCheck: Check? = null
  var whatBroken: Broken? = null

  enum class Broken {
    ITEMS_NOT_IN_INVENTORY, ITEMS_NOT_IN_HOTBAR, NOT_AT_LOCATION, WARP_OUT, DISCONNECTED
  }

  enum class Check {
    PLAYER_CHECK
  }

  @SubscribeEvent
  fun onTickCheck(event: ClientTickEvent) {
    if (player == null || world == null || !macroHandler.enabled) return

    if (!this.fixingStuff && !this.somethingBroken && !this.gotChecked) {
      BrokenCheck.checkForBrokenStuff()
      MacroCheck.checkForChecks()
    }

    if (somethingBroken) {
      BrokenReact.fixWhatsBroken()
      return
    }
    if (gotChecked) {
      CheckReact.reactToCheck()
      return
    }
  }

  @SubscribeEvent
  fun onDisconnect(event: ClientDisconnectionFromServerEvent) {
    if (!config.autoReconnect) {
      macroHandler.disable()

      println("Failsafe - Disconnected.")
      return
    }
    BrokenCheck.activateBroken(Broken.DISCONNECTED, time = 5000)
  }

  fun resetFailsafeSession() {
    this.whatCheck = null
    this.whatBroken = null
    this.gotChecked = false
    this.fixingStuff = false
    this.somethingBroken = false
    this.failsafeAllowance = true
    BrokenReact.timer = Timer(0)
    CheckReact.timer = Timer(0)

    BrokenReact.notAtLocationState = BrokenReact.NotAtLocation.ACTIVATING_AUTOWARP
    BrokenReact.itemsNotInHotbarState = BrokenReact.ItemsNotInHotbar.ACTIVATING_AUTOINV
    BrokenReact.warpOutState = BrokenReact.WarpOut.ACTIVATING_AUTOWARP

    MacroCheck.playerFailsafeTogglingPlayer = null
    MacroCheck.playerFailsafeCheckState = MacroCheck.PlayerCheck.LOOKING_FOR_PLAYER

    CheckReact.playerFailsafeState = CheckReact.PlayerFailsafe.ROTATE_AND_SHIFT
  }

  fun resetFailsafe() {
    resetFailsafeSession()
    MacroCheck.playerFailsafeToggleCount = 0
  }
}