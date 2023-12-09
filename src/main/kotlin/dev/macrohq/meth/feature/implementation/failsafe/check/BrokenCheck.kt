package dev.macrohq.meth.feature.implementation.failsafe.check

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.implementation.failsafe.Failsafe
import dev.macrohq.meth.feature.implementation.failsafe.react.BrokenReact
import dev.macrohq.meth.util.*

object BrokenCheck: AbstractFeature() {
  override val featureName: String = "Failsafe"
  override val isPassiveFeature: Boolean = true

  override fun disable() {}
  fun checkForBrokenStuff() {
    // Not At Location Check - Local Check for Specific Macros
    if (locationUtil.currentIsland != macroHandler.activeMacro.location.first
      && locationUtil.currentSubLocation != macroHandler.activeMacro.location.second
    ) {

      when (macroHandler.activeMacro) {
        commissionMacro -> activateBroken(Failsafe.Broken.NOT_AT_LOCATION, time = config.failsafeWaitBeforeWarp)
      }

      note("Not At Location")
      return
    }

    // Items not in Inventory - Global Check for All Macros
    if (!InventoryUtil.areItemsInPlayerInventory(macroHandler.activeMacro.necessaryItems())) {

      activateBroken(Failsafe.Broken.ITEMS_NOT_IN_INVENTORY, time = 0)

      note("Items Not In Inventory.")
      return
    }

    // Items not in Hotbar - Global Check for All Macros
    if (!InventoryUtil.areItemsInPlayerHotbar(macroHandler.activeMacro.necessaryItems())) {

      activateBroken(Failsafe.Broken.ITEMS_NOT_IN_HOTBAR, time = config.failsafeWaitBeforeInventoryFix)

      note("Items Not In Hotbar")
      return
    }

    // Out of Mana - Local Check for Commission Macro
    if (locationUtil.isInSkyBlock && infoBarUtil.manaPercentage <= 0f && macroHandler.activeMacro == commissionMacro) {

      activateBroken(Failsafe.Broken.WARP_OUT, time = config.failsafeWaitBeforeWarpOut)

      note("Out of Mana")
      return
    }
  }

  fun activateBroken(broken: Failsafe.Broken, allowance: Boolean = false, time: Int) {
    failsafe.gotChecked = false
    BrokenReact.timer = Timer(time)
    failsafe.whatBroken = broken
    failsafe.somethingBroken = true
    failsafe.failsafeAllowance = allowance
  }
}