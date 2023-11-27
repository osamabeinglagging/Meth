package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoInventory {
  var enabled = false
  private var timer = Timer(0)
  private var timeLimit = Timer(0)

  // Send To Hotbar
  private var elementsToSwap = mutableListOf<String>()
  private var availableSlots = mutableListOf<Int>()
  private var itemsToHotbarState = ItemsToHotbarState.STARTING

  // Get Speed and Boost
  private var miningSpeed = 0
  private var miningSpeedBoost = 0
  private var speedAndBoostState = SpeedAndBoostState.STARTING

  private var failed = false
  private var succeeded = false
  private var forceEnable = false

  private var mainState = MainStates.NONE

  enum class MainStates {
    NONE, ITEMS_TO_HOTBAR, SPEED_AND_BOOST
  }

  enum class ItemsToHotbarState {
    STARTING, SWAP_SLOTS, CLOSE_INVENTORY
  }

  enum class SpeedAndBoostState {
    STARTING, GET_SPEED, GET_SPEED_BOOST_MULTIPLIER
  }

  fun sendItemsToHotbar(items: MutableList<String>, force: Boolean = false) {
    this.enabled = true
    this.succeeded = false
    this.failed = false
    this.forceEnable = force
    this.timer = Timer(1000)
    this.mainState = MainStates.ITEMS_TO_HOTBAR
    this.timeLimit = Timer(config.autoInventoryTimeLimit)
    this.itemsToHotbarState = ItemsToHotbarState.STARTING

    this.elementsToSwap.clear()
    for (item in items) {
      if (InventoryUtil.getHotbarSlotForItem(item) == -1) this.elementsToSwap.add(item)
    }
    items.removeAll(this.elementsToSwap)
    this.availableSlots = InventoryUtil.availableHotbarSlotIndex(items)

    log("[AutoInventory] - Sending Items to Hotbar")
  }

  fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.failed = false
    this.elementsToSwap.clear()
    this.mainState = MainStates.NONE
    this.timeLimit = Timer(0)
    this.itemsToHotbarState = ItemsToHotbarState.STARTING
    this.speedAndBoostState = SpeedAndBoostState.STARTING

    log("[AutoInventory] - Disabling AutoInventory.")
  }

  fun getSpeedAndBoost(forceEnable: Boolean = false) {
    this.enabled = true
    this.succeeded = false
    this.failed = false
    this.miningSpeed = 0
    this.miningSpeedBoost = 0
    this.forceEnable = forceEnable
    this.timer = Timer(500)
    this.mainState = MainStates.SPEED_AND_BOOST
    this.timeLimit = Timer(config.autoInventoryTimeLimit)
    this.speedAndBoostState = SpeedAndBoostState.STARTING

    log("[AutoInventory] - Getting Speed And Boost")
  }

  fun getSpeedBoost() = Pair(miningSpeed, miningSpeedBoost)

  fun setFailed(failed: Boolean = false) {
    this.failed = failed
    this.succeeded = !failed
  }

  fun failed(): Boolean = !this.enabled && this.failed
  fun succeeded(): Boolean = !this.enabled && this.succeeded

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if(player == null || world == null || !this.enabled) return
    if (!failsafe.failsafeAllowance && !forceEnable) {
      this.disable()
      return
    }

    if (this.timeLimit.isDone) {
      this.setFailed()
      this.disable()
      return
    }

    // Move Items to Hotbar
    if (this.mainState == MainStates.ITEMS_TO_HOTBAR) {

      when (this.itemsToHotbarState) {

        ItemsToHotbarState.STARTING -> {
          InventoryUtil.openInventory()

          this.timer = Timer(config.autoInventoryClickTime)
          this.itemsToHotbarState = ItemsToHotbarState.SWAP_SLOTS

          if (this.elementsToSwap.isEmpty()) this.itemsToHotbarState = ItemsToHotbarState.CLOSE_INVENTORY
        }

        ItemsToHotbarState.SWAP_SLOTS -> {
          if (!this.timer.isDone) return

          InventoryUtil.sendItemToHotbarSlot(this.elementsToSwap[0], this.availableSlots[0])

          this.elementsToSwap.removeAt(0)
          this.availableSlots.removeAt(0)

          this.timer = Timer(config.autoInventoryClickTime)
          if (this.elementsToSwap.isEmpty() || this.availableSlots.isEmpty()) {
            this.itemsToHotbarState = ItemsToHotbarState.CLOSE_INVENTORY
          }
        }

        ItemsToHotbarState.CLOSE_INVENTORY -> {
          InventoryUtil.closeGUI()

          this.setFailed(this.elementsToSwap.isNotEmpty())
          this.disable()
        }
      }
    }


    // Get Mining Speed and Mining Speed Boost
    if (this.mainState == MainStates.SPEED_AND_BOOST) {

      when (this.speedAndBoostState) {
        SpeedAndBoostState.STARTING -> {
          if (!this.timer.isDone) return

          when {
            this.miningSpeed == 0 -> {
              player.sendChatMessage("/sbmenu")
              this.speedAndBoostState = SpeedAndBoostState.GET_SPEED
            }

            this.miningSpeedBoost == 0 -> {
              player.sendChatMessage("/hotm")
              this.speedAndBoostState = SpeedAndBoostState.GET_SPEED_BOOST_MULTIPLIER
            }

            else -> {
              this.succeeded = true
              this.disable()
              return
            }
          }
          this.timer = Timer((config.autoInventoryInGUITime) + ping.serverPing)
        }

        SpeedAndBoostState.GET_SPEED -> {
          if (!timer.isDone) return

          this.speedAndBoostState = SpeedAndBoostState.STARTING
          if (player.openContainer !is ContainerChest || InventoryUtil.getIndexInGUI("SkyBlock Profile") == -1) {
            this.disable()
            this.succeeded = false

            Logger.error("AutoInventory - Could not open SkyBlock Menu. Disabling")
            return
          }

          this.miningSpeed = InventoryUtil.getMiningSpeed()
          InventoryUtil.closeGUI()

          log("AutoInventory - Mining Speed: ${this.miningSpeed}")
        }

        SpeedAndBoostState.GET_SPEED_BOOST_MULTIPLIER -> {
          if (!this.timer.isDone) return

          this.speedAndBoostState = SpeedAndBoostState.STARTING
          if (player.openContainer !is ContainerChest && InventoryUtil.getIndexInGUI("Mining Speed Boost") == -1) {
            this.disable()
            this.succeeded = false

            Logger.error("AutoInventory - Could not open HOTM Tree. Disabling")
            return
          }

          this.miningSpeedBoost = InventoryUtil.getSpeedBoostMultiplier()
          InventoryUtil.closeGUI()

          log("AutoInventory - Mining Speed Boost: ${this.miningSpeedBoost}")
        }
      }
    }
  }
}