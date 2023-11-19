package dev.macrohq.meth.config.hud

import dev.macrohq.meth.util.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class DebugHUD : Gui() {
  var macroName = "Null"
  var state = "Null"
  var timer = "Timer: Null"
  val extraInfo = mutableListOf<String>()
  var status = "Null"

  @SubscribeEvent
  fun renderOverlay(event: RenderGameOverlayEvent) {
    if (event.isCancelable || event.type != RenderGameOverlayEvent.ElementType.ALL || !config.debugMode || !macroHandler.enabled) return
    val scaledResolution = ScaledResolution(mc)
    val textSize = 60 + (extraInfo.size * 10)
    val fontColor = 0xFFFFFF
    val h = scaledResolution.scaledHeight

    var textStartHeight = (h - textSize) / 2
    val textEndHeight = (h + textSize) / 2

    drawRect(5, textStartHeight - 5, 150, textEndHeight + 5, 0x90000000.toInt())

    drawString(fontRenderer, "Name: $macroName", 10, textStartHeight, fontColor)
    drawString(fontRenderer, "State: $state", 10, textStartHeight + 10, fontColor)
    drawString(fontRenderer, "Rotating: ${!RotationUtil.done}", 10, textStartHeight + 20, fontColor)
    drawString(fontRenderer, timer, 10, textStartHeight + 30, fontColor)
    drawString(fontRenderer, "Status: $status", 10, textStartHeight + 40, fontColor)
    drawString(fontRenderer, "Ping: ${ping.serverPing}", 10, textStartHeight + 50, fontColor)
    textStartHeight += 50
    for (text in extraInfo) {
      textStartHeight += 10
      drawString(fontRenderer, text, 10, textStartHeight, fontColor)
    }
  }

  fun clear() {
    macroName = "Null"
    state = "Null"
    timer = "Timer: Null"
    extraInfo.clear()
    status = "Null"
  }
}

