package dev.macrohq.meth.mixin;

import dev.macrohq.meth.util.idkappropriatename.IInventoryBasic;
import net.minecraft.inventory.InventoryBasic;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryBasic.class)
public abstract class MixinInventoryBasic implements IInventoryBasic {

    @Accessor
    @Override
    public abstract @NotNull String getInventoryTitle();

    @Accessor
    @Override
    public abstract void setInventoryTitle(@NotNull String inventoryTitle);
}
