package dev.macrohq.meth.feature.failsafe.check

import dev.macrohq.meth.feature.failsafe.FailsafeNew
import dev.macrohq.meth.feature.failsafe.react.BrokenReact
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.note

object BrokenCheck {
  fun checkForBrokenStuff() {
    // Not At Location Check - Local Check for Specific Macros
    if (locationUtil.currentIsland != macroHandler.activeMacro.location.first
      && locationUtil.currentSubLocation != macroHandler.activeMacro.location.second
    ) {

      when (macroHandler.activeMacro) {
        commissionMacro -> this.activateBroken(FailsafeNew.Broken.NOT_AT_LOCATION, time = config.failsafeWaitBeforeWarp)
      }

      note("Failsafe - Not At Location")
      return
    }

    // Items not in Inventory - Global Check for All Macros
    if (!InventoryUtil.areItemsInInventory(macroHandler.activeMacro.necessaryItems())) {

      this.activateBroken(FailsafeNew.Broken.ITEMS_NOT_IN_INVENTORY, time = 0)

      note("Failsafe - Items Not In Inventory.")
      return
    }

    // Items not in Hotbar - Global Check for All Macros
    if (!InventoryUtil.areItemsInHotbar(macroHandler.activeMacro.necessaryItems())) {

      this.activateBroken(FailsafeNew.Broken.ITEMS_NOT_IN_HOTBAR, time = config.failsafeWaitBeforeInventoryFix)

      note("Failsafe - Items Not In Hotbar")
      return
    }

    // Out of Mana - Local Check for Commission Macro
    if (locationUtil.isInSkyBlock && infoBarUtil.manaPercentage <= 0f && macroHandler.activeMacro == commissionMacro) {

      this.activateBroken(FailsafeNew.Broken.WARP_OUT, time = config.failsafeWaitBeforeWarpOut)

      note("Failsafe - Out of Mana")
      return
    }
  }

  fun activateBroken(broken: FailsafeNew.Broken, allowance: Boolean = false, time: Int) {
    failsafe.gotChecked = false
    BrokenReact.timer = Timer(time)
    failsafe.whatBroken = broken
    failsafe.somethingBroken = true
    failsafe.failsafeAllowance = allowance
  }
}