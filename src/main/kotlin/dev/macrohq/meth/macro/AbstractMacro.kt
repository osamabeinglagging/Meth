package dev.macrohq.meth.macro

import dev.macrohq.meth.feature.implementation.LocationTracker
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

abstract class AbstractMacro {
  abstract val macroName: String
  abstract val location: Pair<LocationTracker.Island, LocationTracker.SubLocation>
  abstract val subMacroActive: Boolean
  abstract fun enable(softEnable: Boolean = false);
  abstract fun disable(softDisable: Boolean = false);
  abstract fun toggle();
  abstract fun necessaryItems(): MutableList<String>;
  abstract fun onTick(event: TickEvent.ClientTickEvent);
  abstract fun onWorldRenderEvent(event: RenderWorldLastEvent);
  abstract fun onChat(event: ClientChatReceivedEvent);
}