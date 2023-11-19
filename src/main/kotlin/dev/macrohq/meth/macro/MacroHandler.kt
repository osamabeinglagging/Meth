package dev.macrohq.meth.macro

import dev.macrohq.meth.util.UnGrabUtil
import dev.macrohq.meth.util.commissionMacro
import dev.macrohq.meth.util.config
import dev.macrohq.meth.util.failsafe
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class MacroHandler : Macro() {

  val activeMacro: Macro = activeMacro()
  var enabled = false
  var startTime = 0L

  override val macroName = "Macro Handler"
  override val location get() = activeMacro.location
  override val subMacroActive get() = activeMacro.subMacroActive

  override fun enable(softEnable: Boolean) {
    this.activeMacro.enable(softEnable)

    if(softEnable) return
    failsafe.resetFailsafe()
    this.enabled = true
    this.startTime = System.currentTimeMillis()
  }

  override fun disable(softDisable: Boolean) {
    this.activeMacro.disable(softDisable)

    if(softDisable) return
    failsafe.resetFailsafe()
    this.enabled = false
    this.startTime = 0L
  }

  override fun toggle() {
    if (!this.enabled) this.enable()
    else this.disable()
  }

  override fun necessaryItems(): MutableList<String> {
    return this.activeMacro.necessaryItems()
  }

  @SubscribeEvent
  override fun onTick(event: TickEvent.ClientTickEvent) {
    if (!this.enabled) return
    this.activeMacro.onTick(event)
  }

  @SubscribeEvent
  override fun onWorldRenderEvent(event: RenderWorldLastEvent) {
    if (!this.enabled) return
    this.activeMacro.onWorldRenderEvent(event)
  }

  @SubscribeEvent
  override fun onChat(event: ClientChatReceivedEvent) {
    if (!this.enabled) return
    this.activeMacro.onChat(event)
  }

  private fun activeMacro(): Macro {
    return when (config.chosenMacro) {
      0 -> commissionMacro
      else -> commissionMacro
    }
  }
}