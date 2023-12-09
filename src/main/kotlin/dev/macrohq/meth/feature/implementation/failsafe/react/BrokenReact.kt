package dev.macrohq.meth.feature.implementation.failsafe.react

import dev.macrohq.meth.feature.implementation.LocationTracker
import dev.macrohq.meth.feature.implementation.failsafe.Failsafe
import dev.macrohq.meth.feature.implementation.failsafe.check.BrokenCheck
import dev.macrohq.meth.util.*

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
      Failsafe.Broken.ITEMS_NOT_IN_INVENTORY -> {
        fixItemsNotInInventory()
      }

      Failsafe.Broken.ITEMS_NOT_IN_HOTBAR -> {
        fixItemsNotInHotbar()
      }

      Failsafe.Broken.NOT_AT_LOCATION -> {
        fixNotAtLocation()
      }

      Failsafe.Broken.WARP_OUT -> {
        fixWarpOut()
      }

      Failsafe.Broken.DISCONNECTED -> {
        fixDisconnect()
      }

      null -> {
        macroHandler.disable()

        BrokenCheck.note("Disabled because it's null broken")
      }

    }
  }

  // Fix Item Not In Inventory
  private fun fixItemsNotInInventory() {
    macroHandler.disable()
    BrokenCheck.note("Cannot find all of these items in your inventory.")
    macroHandler.activeMacro.necessaryItems()
      .forEach { BrokenCheck.note("${macroHandler.activeMacro.necessaryItems().indexOf(it) + 1}. $it") }
  }

  // Fix Item Not In Hotbar
  private fun fixItemsNotInHotbar() {
    if (!timer.isDone) return

    when (itemsNotInHotbarState) {
      ItemsNotInHotbar.ACTIVATING_AUTOINV -> {
        failsafe.fixingStuff = true
        itemsNotInHotbarState = ItemsNotInHotbar.VERIFYING_AUTOINV
        if (!macroHandler.subMacroActive) macroHandler.disable(true)
        autoInventory.sendItemsToHotbar(macroHandler.activeMacro.necessaryItems(), true)

        BrokenCheck.note("Failsafe - Fixing item not in Hotbar.")
      }

      ItemsNotInHotbar.VERIFYING_AUTOINV -> {
        if (autoInventory.failed()) {
          macroHandler.disable()

          BrokenCheck.note("Failsafe - Failed to Fix Inventory")
          return
        }
        if (autoInventory.succeeded()) {
          if (!macroHandler.subMacroActive) macroHandler.enable(true)

          failsafe.resetFailsafeSession()

          BrokenCheck.note("Failsafe - Fixed Inventory")
        }
      }
    }
  }

  // Fix not at location
  private fun fixNotAtLocation() {
    if (!timer.isDone) return

    when (notAtLocationState) {
      NotAtLocation.ACTIVATING_AUTOWARP -> {
        failsafe.fixingStuff = true
        notAtLocationState = NotAtLocation.VERIFYING_AUTOWARP
        macroHandler.disable(true)
        autoWarp.enable(null, macroHandler.location.second, true)

        BrokenCheck.note("Failsafe - Going back to Location.")
      }

      NotAtLocation.VERIFYING_AUTOWARP -> {
        if (autoWarp.failed()) {
          macroHandler.disable()

          BrokenCheck.note("Failsafe - Failed to warp.")
          return
        }
        if (autoWarp.succeeded()) {
          macroHandler.enable(true)
          failsafe.resetFailsafeSession()

          BrokenCheck.note("Failsafe - Warped at Location")
        }
      }
    }
  }

  // Fix warp out
  private fun fixWarpOut() {
    if (!timer.isDone) return

    when (warpOutState) {
      WarpOut.ACTIVATING_AUTOWARP -> {
        failsafe.fixingStuff = true
        warpOutState = WarpOut.VERIFYING_AUTOWARP
        macroHandler.disable(true)
        autoWarp.enable(LocationTracker.Island.THE_HUB, null, true)

        BrokenCheck.note("Failsafe - Warping Out")
      }

      WarpOut.VERIFYING_AUTOWARP -> {
        if (autoWarp.failed()) {
          macroHandler.disable()

          BrokenCheck.note("Failsafe - Failed to warp out.")
          return
        }
        if (autoWarp.succeeded()) {
          macroHandler.enable(true)
          failsafe.resetFailsafeSession()

          BrokenCheck.note("Failsafe - Warped Out")
        }
      }
    }
  }

  // Fix disconnect
  private fun fixDisconnect(){

  }
}