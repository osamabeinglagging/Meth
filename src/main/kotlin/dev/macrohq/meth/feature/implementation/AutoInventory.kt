package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.util.*
import net.minecraft.inventory.ContainerChest
import net.minecraft.util.StringUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.util.LinkedList

class AutoInventory : AbstractFeature() {
  override val featureName: String = "AutoInventory"
  override val isPassiveFeature: Boolean = false

  private var timer = Timer(0)
  private var timeLimit = Timer(0)

  // Send To Hotbar
  private var elementsToSwap = LinkedList<String>()
  private var availableSlots = LinkedList<Int>()
  private var itemsToHotbarState = ItemsToHotbarState.STARTING

  // Get Speed and Boost
  private var miningSpeed = 0
  private var miningSpeedBoost = 0
  private var speedAndBoostState = SpeedAndBoostState.STARTING

  private var mainState = MainStates.NONE

  companion object {
    private var instance: AutoInventory? = null
    fun getInstance(): AutoInventory {
      if (instance == null) {
        instance = AutoInventory()
      }
      return instance!!
    }
  }

  enum class MainStates {
    NONE, ITEMS_TO_HOTBAR, SPEED_AND_BOOST
  }

  enum class ItemsToHotbarState {
    STARTING, SWAP_SLOTS, CLOSE_INVENTORY
  }

  enum class SpeedAndBoostState {
    STARTING, GET_VALUES
  }

  fun sendItemsToHotbar(items: MutableList<String>, force: Boolean = false) {
    this.enabled = true
    this.failed = false
    this.success = false
    this.forceEnable = force
    this.timer = Timer(1000)
    this.mainState = MainStates.ITEMS_TO_HOTBAR
    this.timeLimit = Timer(config.autoInventoryTimeLimit)
    this.itemsToHotbarState = ItemsToHotbarState.STARTING

    this.elementsToSwap.clear()
    for (item in items) {
      if (InventoryUtil.getHotbarSlotForItem(item) == -1) this.elementsToSwap.add(item)
    }
    this.availableSlots = availableHotbarSlotIndex(items)

    log("Sending Items to Hotbar")
  }

  override fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.elementsToSwap.clear()
    this.mainState = MainStates.NONE
    this.timeLimit = Timer(0)
    this.itemsToHotbarState = ItemsToHotbarState.STARTING
    this.speedAndBoostState = SpeedAndBoostState.STARTING

    log("Disabling AutoInventory.")
  }

  fun getSpeedAndBoost(forceEnable: Boolean = false) {
    this.enabled = true
    this.failed = false
    this.success = false
    this.miningSpeed = 0
    this.miningSpeedBoost = 0
    this.forceEnable = forceEnable
    this.timer = Timer(500)
    this.mainState = MainStates.SPEED_AND_BOOST
    this.timeLimit = Timer(config.autoInventoryTimeLimit)
    this.speedAndBoostState = SpeedAndBoostState.STARTING

    log("Getting Speed And Boost")
  }

  fun getSpeedBoost() = Pair(miningSpeed, miningSpeedBoost)

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.canEnable()) return
    if (!failsafe.failsafeAllowance && !forceEnable) {
      this.disable()
      return
    }

    if (this.timeLimit.isDone) {
      this.setSucceeded(false)
      this.disable()
      return
    }

    // Move Items to Hotbar
    if (this.mainState == MainStates.ITEMS_TO_HOTBAR) {
      this.autoInventoryItemsToHotbar()
    }

    // Get Mining Speed and Mining Speed Boost
    if (this.mainState == MainStates.SPEED_AND_BOOST) {
      this.autoInventoryGetSpeedBoost()
    }
  }

  // Items to Hotbar
  private fun autoInventoryItemsToHotbar() {
    if (this.elementsToSwap.isEmpty() || this.availableSlots.isEmpty()) {
      this.itemsToHotbarState = ItemsToHotbarState.CLOSE_INVENTORY
    }

    when (this.itemsToHotbarState) {

      ItemsToHotbarState.STARTING -> {
        InventoryUtil.openPlayerInventory()

        this.timer = Timer(config.autoInventoryClickTime)
        this.itemsToHotbarState = ItemsToHotbarState.SWAP_SLOTS
      }

      ItemsToHotbarState.SWAP_SLOTS -> {
        if (!this.timer.isDone) return

        InventoryUtil.sendItemIntoHotbar(
          InventoryUtil.getContainerSlotForItem(this.elementsToSwap.poll()),
          this.availableSlots.poll()
        )

        this.timer = Timer(config.autoInventoryClickTime)
      }

      ItemsToHotbarState.CLOSE_INVENTORY -> {
        InventoryUtil.closeOpenGUI()

        this.setSucceeded(this.elementsToSwap.isNotEmpty())
        this.disable()
      }
    }
  }
  private fun availableHotbarSlotIndex(items: MutableList<String>): LinkedList<Int> {
    val slots = LinkedList<Int>()
    for (i in 0..7) {
      val item = player.inventory.getStackInSlot(i)
      if (item == null || !items.contains(StringUtils.stripControlCodes(item.displayName))) slots.add(i)
    }
    return slots
  }


  // Get Speed Boost
  private fun autoInventoryGetSpeedBoost() {
    when (this.speedAndBoostState) {
      SpeedAndBoostState.STARTING -> {
        if (!this.timer.isDone) return

        if (this.miningSpeed == 0 || this.miningSpeedBoost == 0) {
          if (this.miningSpeed == 0) {
            player.sendChatMessage("/sbmenu")
          } else {
            player.sendChatMessage("/hotm")
          }
          this.speedAndBoostState = SpeedAndBoostState.GET_VALUES
        }
        else {
          this.setSucceeded()
          this.disable()
          return
        }
        this.timer = Timer((config.autoInventoryInGUITime) + ping.serverPing)
      }

      SpeedAndBoostState.GET_VALUES -> {
        if (!timer.isDone) return

        this.speedAndBoostState = SpeedAndBoostState.STARTING
        if (player.openContainer !is ContainerChest ||
          (InventoryUtil.getContainerSlotForItem("SkyBlock Profile") == -1
              && InventoryUtil.getContainerSlotForItem("Mining Speed Boost") == -1)
        ) {
          this.disable()
          this.setSucceeded(false)

          Logger.error("Could not open SkyBlock Menu/HOTM Menu. Disabling")
          return
        }

        if (this.miningSpeed == 0) {
          val profileSlot = InventoryUtil.getContainerSlotForItem("SkyBlock Profile")
          val profileLore = InventoryUtil.getLore(profileSlot)
          val regex = Regex("mining speed (\\d,*\\d*)")

          this.miningSpeed = regex.find(profileLore)?.groupValues?.last()?.replace(",", "")?.toInt() ?: 0

          log("Speed: ${this.miningSpeed}")
        } else if (this.miningSpeedBoost == 0) {
          val boostSlot = InventoryUtil.getContainerSlotForItem("Mining Speed Boost")
          val boostLore = InventoryUtil.getLore(boostSlot)
          val regex = Regex("\\+(\\d+)%")

          this.miningSpeedBoost = regex.find(boostLore)?.groupValues?.last()?.replace(",", "")?.toInt() ?: 0

          log("Boost: ${this.miningSpeedBoost}")
        }

        InventoryUtil.closeOpenGUI()

      }
    }
  }
}
