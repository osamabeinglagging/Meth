package dev.macrohq.meth.util

import dev.macrohq.meth.util.idkappropriatename.IGuiChest
import dev.macrohq.meth.util.idkappropriatename.IInventoryBasic
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.InventoryBasic
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.StringUtils
import java.util.*

object InventoryUtil {

  fun areItemsInInventory(items: MutableList<String>): Boolean {
    val inventory = player.inventoryContainer.inventorySlots
    for (slot in inventory) {
      if (slot == null) continue
      if (!slot.hasStack) continue
      val name = slot.stack.displayName
      if (items.any { name.contains(it) }) items.removeIf { name.contains(it) }
      if (items.isEmpty()) break
    }
    return items.isEmpty()
  }

  fun areItemsInHotbar(items: MutableList<String>): Boolean {
    for (i in 0..7) {
      val stack = player.inventory.getStackInSlot(i) ?: continue
      val name = stack.displayName
      if (items.any { name.contains(it) }) items.removeIf { name.contains(it) }
      if (items.isEmpty()) break
    }
    return items.isEmpty()
  }

  fun getItemSlotInInventoryContainer(name: String): Int? {
    for (slot in player.inventoryContainer.inventorySlots) {
      if (slot == null) continue
      if (!slot.hasStack) continue
      if (slot.stack.displayName.lowercase(Locale.getDefault())
          .contains(name.lowercase(Locale.getDefault()))
      ) {
        return slot.slotIndex
      }
    }
    return null
  }

  fun availableHotbarSlotIndex(itemsToIgnore: MutableList<String>): MutableList<Int> {
    val slots = mutableListOf<Int>()
    for (i in 0..7) {
      val item = player.inventory.getStackInSlot(i)
      if (item == null) slots.add(i)
      else if (!itemsToIgnore.any { item.displayName.contains(it) }) slots.add(i)
    }
    return slots
  }

  fun getOpenContainerSlots(): List<Slot?> {
    val inventory = mutableListOf<Slot?>()
    player.openContainer.inventorySlots.forEach { inventory.add(it) }
    return inventory
  }

  fun getGUIInventorySlots(): List<Slot?> {
    val inventory = getOpenContainerSlots()
    return inventory.slice(0..inventory.size - 37)
  }

  fun getGUIInventoryItemStack(): List<ItemStack?> {
    val inventory = getOpenContainerItemStack()
    return inventory.slice(0..inventory.size - 37)
  }

  fun getOpenContainerItemStack(): List<ItemStack?> {
    val inventory = mutableListOf<ItemStack?>()
    getOpenContainerSlots().forEach { inventory.add(it!!.stack) }
    return inventory
  }

  fun holdItem(name: String): Boolean {
    if (getHotbarSlotForItem(name) != -1) {
      player.inventory.currentItem = getHotbarSlotForItem(name)
      return true
    }
    return false
  }

  fun getIndexInGUI(name: String): Int {
    return getGUIInventorySlots().find { it?.stack?.displayName?.contains(name) == true }?.slotIndex ?: -1
  }

  fun getHotbarSlotForItem(name: String): Int {
    val inventory = player.inventory
    for (i in 0..7) {
      val currItem = inventory.getStackInSlot(i)
      if (currItem != null && currItem.displayName.contains(name, true)) return i
    }
    return -1
  }

  fun clickSlot(slot: Int, button: Int = 0, clickType: Int = 0): Boolean {
    if (player.openContainer !is ContainerChest || slot == -1 || player.openContainer.getSlot(slot) == null || !player.openContainer.getSlot(
        slot
      ).hasStack
    ) return false
    mc.playerController.windowClick(player.openContainer.windowId, slot, button, clickType, player)
    return true
  }

  fun sendItemToHotbarSlot(name: String, targetSlot: Int) {
    val sourceSlot = getItemSlotInInventoryContainer(name)!!
    mc.playerController.windowClick(player.inventoryContainer.windowId, sourceSlot, targetSlot, 2, player)
  }

  fun openInventory() {
    mc.displayGuiScreen(GuiInventory(player))
  }

  fun closeGUI() {
    if (player.openContainer != null) player.closeScreen()
  }

  fun getGUIName(): String? {
    return StringUtils.stripControlCodes(player.openContainer.inventorySlots[0].inventory.name) ?: null
  }

  fun getMiningSpeed(): Int {
    return Regex("mining speed (\\d,*\\d*)").find(getLore(getGUIInventoryItemStack()[13]!!))?.groupValues?.last()
      ?.replace(",", "")?.toInt() ?: 0
  }

  fun getSpeedBoostMultiplier(): Int {
    return Regex("\\+(\\d+)%").find(getLore(getGUIInventoryItemStack()[29]!!))?.groupValues?.last()?.toInt() ?: 0
  }

  fun getCommissionItemSlot(): Int {
    val inv = getGUIInventorySlots().filterNotNull().filter { it.hasStack && it.hasStack }
    val comm = inv.firstOrNull { getLore(it.stack).contains("completed") }
    return comm?.slotIndex ?: -1
  }

  fun getLore(item: ItemStack): String {
    val base = item.tagCompound.getCompoundTag("display").getTagList("Lore", 8)
    var lore = ""
    for (i in 0..base.tagCount()) {
      lore += StringUtils.stripControlCodes(base.getStringTagAt(i).lowercase(Locale.getDefault()).trim()) + " "
    }
    return lore
  }

  fun changeGuiName(name: String) {
    if (mc.currentScreen is GuiChest) {
      val inventory = ((mc.currentScreen as GuiChest) as IGuiChest).getLowerChestInventory();
      if (inventory is InventoryBasic) {
        val basic = (inventory as IInventoryBasic)
        if (basic.getInventoryTitle()[0].isUpperCase()) basic.setInventoryTitle(name)
      }
    }
  }
}