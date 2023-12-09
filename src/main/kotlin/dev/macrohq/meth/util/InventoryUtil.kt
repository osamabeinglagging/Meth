package dev.macrohq.meth.util

import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraft.util.StringUtils

//
//object InventoryUtil {
//
//  fun areItemsInInventory(items: MutableList<String>): Boolean {
//    val inventory = player.inventoryContainer.inventorySlots
//    for (slot in inventory) {
//      if (slot == null) continue
//      if (!slot.hasStack) continue
//      val name = slot.stack.displayName
//      if (items.any { name.contains(it) }) items.removeIf { name.contains(it) }
//      if (items.isEmpty()) break
//    }
//    return items.isEmpty()
//  }
//
//  fun areItemsInHotbar(items: MutableList<String>): Boolean {
//    for (i in 0..7) {
//      val stack = player.inventory.getStackInSlot(i) ?: continue
//      val name = stack.displayName
//      if (items.any { name.contains(it) }) items.removeIf { name.contains(it) }
//      if (items.isEmpty()) break
//    }
//    return items.isEmpty()
//  }
//
//  fun getItemSlotInInventoryContainer(name: String): Int? {
//    for (slot in player.inventoryContainer.inventorySlots) {
//      if (slot == null) continue
//      if (!slot.hasStack) continue
//      if (slot.stack.displayName.lowercase(Locale.getDefault())
//          .contains(name.lowercase(Locale.getDefault()))
//      ) {
//        return slot.slotIndex
//      }
//    }
//    return null
//  }
//
//  fun availableHotbarSlotIndex(itemsToIgnore: MutableList<String>): MutableList<Int> {
//    val slots = mutableListOf<Int>()
//    for (i in 0..7) {
//      val item = player.inventory.getStackInSlot(i)
//      if (item == null) slots.add(i)
//      else if (!itemsToIgnore.any { item.displayName.contains(it) }) slots.add(i)
//    }
//    return slots
//  }
//
//  fun getOpenContainerSlots(): List<Slot?> {
//    val inventory = mutableListOf<Slot?>()
//    player.openContainer.inventorySlots.forEach { inventory.add(it) }
//    return inventory
//  }
//
//  fun getGUIInventorySlots(): List<Slot?> {
//    val inventory = getOpenContainerSlots()
//    return inventory.slice(0..inventory.size - 37)
//  }
//
//  fun getGUIInventoryItemStack(): List<ItemStack?> {
//    val inventory = getOpenContainerItemStack()
//    return inventory.slice(0..inventory.size - 37)
//  }
//
//  fun getOpenContainerItemStack(): List<ItemStack?> {
//    val inventory = mutableListOf<ItemStack?>()
//    getOpenContainerSlots().forEach { inventory.add(it!!.stack) }
//    return inventory
//  }
//
//  fun holdItem(name: String): Boolean {
//    if (getHotbarSlotForItem(name) != -1) {
//      player.inventory.currentItem = getHotbarSlotForItem(name)
//      return true
//    }
//    return false
//  }
//
//  fun getIndexInGUI(name: String): Int {
//    return getGUIInventorySlots().find { it?.stack?.displayName?.contains(name) == true }?.slotIndex ?: -1
//  }
//
//  fun getHotbarSlotForItem(name: String): Int {
//    val inventory = player.inventory
//    for (i in 0..7) {
//      val currItem = inventory.getStackInSlot(i)
//      if (currItem != null && currItem.displayName.contains(name, true)) return i
//    }
//    return -1
//  }
//
//  fun clickSlot(slot: Int, button: Int = 0, clickType: Int = 0): Boolean {
//    if (player.openContainer !is ContainerChest || slot == -1 || player.openContainer.getSlot(slot) == null || !player.openContainer.getSlot(
//        slot
//      ).hasStack
//    ) return false
//    mc.playerController.windowClick(player.openContainer.windowId, slot, button, clickType, player)
//    return true
//  }
//
//  fun sendItemToHotbarSlot(name: String, targetSlot: Int) {
//    val sourceSlot = getItemSlotInInventoryContainer(name)!!
//    mc.playerController.windowClick(player.inventoryContainer.windowId, sourceSlot, targetSlot, 2, player)
//  }
//
//  fun openInventory() {
//    mc.displayGuiScreen(GuiInventory(player))
//  }
//
//  fun closeGUI() {
//    if (player.openContainer != null) player.closeScreen()
//  }
//
//  fun getGUIName(): String? {
//    return StringUtils.stripControlCodes(player.openContainer.inventorySlots[0].inventory.name) ?: null
//  }
//
//  fun getMiningSpeed(): Int {
//    return Regex("mining speed (\\d,*\\d*)").find(getLore(getGUIInventoryItemStack()[13]!!))?.groupValues?.last()
//      ?.replace(",", "")?.toInt() ?: 0
//  }
//
//  fun getSpeedBoostMultiplier(): Int {
//    return Regex("\\+(\\d+)%").find(getLore(getGUIInventoryItemStack()[29]!!))?.groupValues?.last()?.toInt() ?: 0
//  }
//
//  fun getCommissionItemSlot(): Int {
//    val inv = getGUIInventorySlots().filterNotNull().filter { it.hasStack && it.hasStack }
//    val comm = inv.firstOrNull { getLore(it.stack).contains("completed") }
//    return comm?.slotIndex ?: -1
//  }
//
//  fun getLore(item: ItemStack): String {
//    val base = item.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
//    var lore = ""
//    for (i in 0..base.tagCount()) {
//      lore += StringUtils.stripControlCodes(base.getStringTagAt(i).lowercase(Locale.getDefault()).trim()) + " "
//    }
//    return lore
//  }
//
//  fun changeGuiName(name: String) {
//    if (mc.currentScreen is GuiChest) {
//      val inventory = ((mc.currentScreen as GuiChest) as IGuiChest).getLowerChestInventory();
//      if (inventory is InventoryBasic) {
//        val basic = (inventory as IInventoryBasic)
//        if (basic.getInventoryTitle()[0].isUpperCase()) basic.setInventoryTitle(name)
//      }
//    }
//  }
//}


/**
 * Utility class providing various inventory-related functions.
 */
object InventoryUtil {

  /**
   * Checks if the specified items are present in the player's inventory.
   *
   * @param items List of item names to check.
   * @return True if all items are found; false otherwise.
   */
  fun areItemsInPlayerInventory(items: MutableList<String>): Boolean {
    val inventory = player.inventoryContainer.inventorySlots
    for (slotObj in inventory) {
      if (!slotObj.hasStack) continue
      items.removeIf { StringUtils.stripControlCodes(slotObj.stack.displayName).contains(it) }
      if (items.isEmpty()) return true
    }
    return false
  }

  /**
   * Checks if the specified items are present in the player's hotbar.
   *
   * @param items List of item names to check.
   * @return True if all items are found; false otherwise.
   */
  fun areItemsInPlayerHotbar(items: MutableList<String>): Boolean {
    for (i in 0..7) {
      val stackObj = player.inventory.getStackInSlot(i) ?: continue
      items.removeIf { StringUtils.stripControlCodes(stackObj.displayName).contains(it) }
      if (items.isEmpty()) return true
    }
    return false
  }

  /**
   * Retrieves the display name of the currently open GUI, if it is a chest container.
   *
   * If the currently open container is not an instance of [ContainerChest], an empty string is returned.
   * Otherwise, the unformatted text of the lower chest inventory's display name is returned.
   *
   * @return The display name of the open chest GUI or an empty string if the current GUI is not a chest container.
   */
  fun getOpenContainerDisplayName(container: Container = player.openContainer): String {
    return if (container !is ContainerChest) ""
    else (container as ContainerChest).lowerChestInventory.displayName.unformattedText
  }

  /**
   * Searches the player's hotbar for an item with a display name containing the specified [itemName].
   *
   * @param itemName The name to search for, case-insensitive.
   * @return The index of the hotbar slot containing the matching item, or -1 if no match is found.
   */
  fun getHotbarSlotForItem(itemName: String): Int {
    for (i in 0..7) {
      if (player.inventory.getStackInSlot(i)?.displayName?.contains(itemName, ignoreCase = true) == true) return i
    }
    return -1
  }

  /**
   * Sets the player's current hotbar slot to the one containing an item with a display name
   * containing the specified [itemName], if found.
   *
   * @param itemName The name to search for, case-insensitive.
   * @return `true` if a matching item is found and the hotbar slot is set; `false` otherwise.
   */
  fun setHotbarSlotForItem(itemName: String): Boolean {
    val slot = getHotbarSlotForItem(itemName)
    if (slot == -1) return false

    player.inventory.currentItem = slot
    return true
  }

  /**
   * Searches the slots of the currently open container for an item with a display name
   * containing the specified [itemName], case-insensitive.
   *
   * @param itemName The name to search for.
   * @return The slot number of the matching item, or -1 if no match is found.
   */
  fun getContainerSlotForItem(itemName: String): Int {
    for (slot in player.openContainer.inventorySlots) {
      if (slot == null || !slot.hasStack) continue
      if (slot.stack.displayName.contains(itemName, ignoreCase = true)) return slot.slotNumber
    }
    return -1
  }

  /**
   * Clicks a slot in the currently open inventory/GUI.
   *
   * @param slot Slot number of the item you want to interact with.
   * @param mouseButton Left, right, or middle click. Use constants from [MouseButton].
   * @param mode Interaction mode. Use constants from [ClickMode].
   * @return Whether the slot was successfully clicked.
   */
  fun clickSlot(slot: Int, mouseButton: Int = MouseButton.LEFT, mode: Int = ClickMode.PICKUP): Boolean {
    if (mc.currentScreen == null || player.openContainer == null
      || slot == -1 || mouseButton == -1
    ) return false

    playerController.windowClick(player.openContainer.windowId, slot, mouseButton, mode, player)
    return true
  }

  /**
   * Shift-clicks the item from the specified initial slot into the container to quickly move it.
   *
   * @param slot The index of the slot containing the item to be shift-clicked.
   * @return `true` if the shift-click operation is successful; `false` otherwise.
   */
  fun shiftClickIntoContainer(slot: Int): Boolean {
    return clickSlot(slot, MouseButton.LEFT, ClickMode.QUICK_MOVE)
  }

  /**
   * Sends an item from the specified source slot to the specified hotbar slot using a swap operation.
   *
   * @param sourceSlot The index of the slot containing the item to be sent to the hotbar.
   * @param hotbarSlot The index of the hotbar slot where the item will be placed.
   * @return `true` if the item is successfully sent to the hotbar; `false` otherwise.
   */
  fun sendItemIntoHotbar(sourceSlot: Int, hotbarSlot: Int): Boolean {
    if (hotbarSlot !in 0..7) return false
    return clickSlot(sourceSlot, hotbarSlot, ClickMode.SWAP)
  }

  /**
   * Throws the item from the specified source slot by simulating a left-click with the throw action.
   *
   * @param sourceSlot The index of the slot containing the item to be thrown.
   * @return `true` if the throw action is successful; `false` otherwise.
   */
  fun throwItem(sourceSlot: Int): Boolean {
    return clickSlot(sourceSlot, MouseButton.LEFT, ClickMode.THROW)
  }

  /**
   * Swaps items between the specified source and target slots in the player's open container.
   *
   * @param sourceSlot The index of the source slot containing the item to be swapped.
   * @param targetSlot The index of the target slot for swapping the item.
   * @return `true` if the swap operation is successful; `false` otherwise.
   */
  fun swapSlots(sourceSlot: Int, targetSlot: Int): Boolean {
    if (player.openContainer == null || sourceSlot == -1 || targetSlot == -1) return false

    val sourceSlotObj = player.openContainer.getSlot(sourceSlot)
    val targetSlotObj = player.openContainer.getSlot(targetSlot)

    if (sourceSlotObj == null || targetSlotObj == null || !sourceSlotObj.hasStack) return false

    val s1 = clickSlot(sourceSlot, mode = ClickMode.PICKUP)
    val s2 = clickSlot(targetSlot, mode = ClickMode.PICKUP)
    val s3 = if (targetSlotObj.hasStack) clickSlot(sourceSlot, ClickMode.PICKUP) else true

    return s1 && s2 && s3
  }

  /**
   * Opens the player's inventory GUI.
   * This function displays the player's inventory screen using Minecraft's `GuiInventory` class.
   */
  fun openPlayerInventory() {
    mc.displayGuiScreen(GuiInventory(player))
  }

  /**
   * Closes the currently open GUI screen, if any.
   * This function checks if the player has an open container and closes it.
   */
  fun closeOpenGUI() {
    if (player.openContainer != null) player.closeScreen()
  }

  /**
   * Retrieves the lore of an ItemStack.
   *
   * @param sourceSlot The container slot to retrieve the lore from.
   * @return The formatted lore as a single string.
   */
  fun getLore(sourceSlot: Int): String {
    val stack = player.openContainer.getSlot(sourceSlot).stack ?: return ""
    val base = stack.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
    var lore = ""
    for (i in 0..base.tagCount()) {
      lore += StringUtils.stripControlCodes(base.getStringTagAt(i).lowercase().trim()) + " "
    }
    return lore
  }
}

/**
 * Constants representing mouse buttons for inventory interactions.
 */
object MouseButton {
  const val LEFT = 0
  const val RIGHT = 1
  const val MIDDLE = 2
}

/**
 * Constants representing different modes of inventory interactions.
 */
object ClickMode {
  const val PICKUP = 0
  const val QUICK_MOVE = 1
  const val SWAP = 2
  const val CLONE = 3
  const val THROW = 4
  const val QUICK_CRAFT = 5
  const val PICKUP_ALL = 6
}