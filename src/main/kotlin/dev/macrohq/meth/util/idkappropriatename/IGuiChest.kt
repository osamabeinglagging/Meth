package dev.macrohq.meth.util.idkappropriatename

import net.minecraft.inventory.IInventory

interface IGuiChest {
  fun getLowerChestInventory(): IInventory;
}