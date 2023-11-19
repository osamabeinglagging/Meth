package dev.macrohq.meth.feature.failsafe.react

import dev.macrohq.meth.feature.failsafe.FailsafeNew
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.note

object BrokenReact {
  var timer = Timer(0)

  // Items not in Hotbar
  var itemsNotInHotbarState = ItemsNotInHotbar.ACTIVATING_AUTOINV

  // Not At Location
  var notAtLocationState = NotAtLocation.ACTIVATING_AUTOWARP

  // Warp Out
  var warpOutState = WarpOut.ACTIVATING_AUTOWARP

  enum class ItemsNotInHotbar {
    ACTIVATING_AUTOINV, VERIFYING_AUTOINV
  }

  enum class NotAtLocation {
    ACTIVATING_AUTOWARP, VERIFYING_AUTOWARP
  }

  enum class WarpOut {
    ACTIVATING_AUTOWARP, VERIFYING_AUTOWARP
  }

  fun fixWhatsBroken() {
    when (failsafe.whatBroken) {
      FailsafeNew.Broken.ITEMS_NOT_IN_INVENTORY -> {
        this.fixItemsNotInInventory()
      }

      FailsafeNew.Broken.ITEMS_NOT_IN_HOTBAR -> {
        this.fixItemsNotInHotbar()
      }

      FailsafeNew.Broken.NOT_AT_LOCATION -> {
        this.fixNotAtLocation()
      }

      FailsafeNew.Broken.WARP_OUT -> {
        this.fixWarpOut()
      }

      FailsafeNew.Broken.DISCONNECTED -> {
        this.fixDisconnect()
      }

      null -> {
        macroHandler.disable()

        note("Disabled because it's null broken")
      }

    }
  }

  // Fix Item Not In Inventory
  private fun fixItemsNotInInventory() {
    macroHandler.disable()
    note("Cannot find all of these items in your inventory.")
    macroHandler.activeMacro.necessaryItems()
      .forEach { note("${macroHandler.activeMacro.necessaryItems().indexOf(it) + 1}. $it") }
  }

  // Fix Item Not In Hotbar
  private fun fixItemsNotInHotbar() {
    if (!this.timer.isDone) return

    when (this.itemsNotInHotbarState) {
      ItemsNotInHotbar.ACTIVATING_AUTOINV -> {
        failsafe.fixingStuff = true
        this.itemsNotInHotbarState = ItemsNotInHotbar.VERIFYING_AUTOINV
        if (!macroHandler.subMacroActive) macroHandler.disable(true)
        autoInventory.sendItemsToHotbar(macroHandler.activeMacro.necessaryItems(), true)

        note("Failsafe - Fixing item not in Hotbar.")
      }

      ItemsNotInHotbar.VERIFYING_AUTOINV -> {
        if (autoInventory.failed()) {
          macroHandler.disable()

          note("Failsafe - Failed to Fix Inventory")
          return
        }
        if (autoInventory.succeeded()) {
          if (!macroHandler.subMacroActive) macroHandler.enable(true)

          failsafe.resetFailsafeSession()

          note("Failsafe - Fixed Inventory")
        }
      }
    }
  }

  // Fix not at location
  private fun fixNotAtLocation() {
    if (!this.timer.isDone) return

    when (notAtLocationState) {
      NotAtLocation.ACTIVATING_AUTOWARP -> {
        failsafe.fixingStuff = true
        this.notAtLocationState = NotAtLocation.VERIFYING_AUTOWARP
        macroHandler.disable(true)
        autoWarp.enable(null, macroHandler.location.second, true)

        note("Failsafe - Going back to Location.")
      }

      NotAtLocation.VERIFYING_AUTOWARP -> {
        if (autoWarp.failed()) {
          macroHandler.disable()

          note("Failsafe - Failed to warp.")
          return
        }
        if (autoWarp.succeeded()) {
          macroHandler.enable(true)
          failsafe.resetFailsafeSession()

          note("Failsafe - Warped at Location")
        }
      }
    }
  }

  // Fix warp out
  private fun fixWarpOut() {
    if (!this.timer.isDone) return

    when (warpOutState) {
      WarpOut.ACTIVATING_AUTOWARP -> {
        failsafe.fixingStuff = true
        this.warpOutState = WarpOut.VERIFYING_AUTOWARP
        macroHandler.disable(true)
        autoWarp.enable(LocationUtil.Island.THE_HUB, null, true)

        note("Failsafe - Warping Out")
      }

      WarpOut.VERIFYING_AUTOWARP -> {
        if (autoWarp.failed()) {
          macroHandler.disable()

          note("Failsafe - Failed to warp out.")
          return
        }
        if (autoWarp.succeeded()) {
          macroHandler.enable(true)
          failsafe.resetFailsafeSession()

          note("Failsafe - Warped Out")
        }
      }
    }
  }

  // Fix disconnect
  private fun fixDisconnect(){

  }
}