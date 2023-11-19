package dev.macrohq.meth.mixin;

import dev.macrohq.meth.util.idkappropriatename.IGuiChest;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(GuiChest.class)
public abstract class MixinGuiChest implements IGuiChest {

    @Accessor
    @Override
    public abstract IInventory getLowerChestInventory();
}
