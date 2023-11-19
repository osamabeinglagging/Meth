package dev.macrohq.meth.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(Minecraft.class)
public interface MinecraftInvoker {
    @Invoker("clickMouse")
    void invokeClickMouse();
    @Invoker("rightClickMouse")
    void invokeRightClickMouse();
}
