package dev.macrohq.meth.config.hud

import TablistUtil
import dev.macrohq.meth.util.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class CommissionMacroStatusHUD : Gui() {

  @SubscribeEvent
  fun onRenderOverlay(event: RenderGameOverlayEvent) {
    if (event.isCancelable || event.type != RenderGameOverlayEvent.ElementType.ALL || !config.commStatusHUD || !macroHandler.enabled) return

    // Size Calculation
    val scaledResolution = ScaledResolution(mc)
    val textSize = 50
    val fontColor = 0xFFFFFF
    val h = scaledResolution.scaledHeight

    val textStartHeight = (h - textSize) / 2
    val textEndHeight = (h + textSize) / 2

    // Uptime Calculation
    val timePassedInSeconds = (System.currentTimeMillis() - macroHandler.startTime) / 1000
    val hoursPassed = (timePassedInSeconds/3600).toInt()
    val minutesPassed = ((timePassedInSeconds % 3600) / 60).toInt()
    val secondsPassed = ((timePassedInSeconds % 3600) % 60).toInt()

    val commPerHour = ((commissionMacro.commissionFinished / timePassedInSeconds.toFloat())*3600f).toInt()

    val name = "Name: ${macroHandler.activeMacro.macroName}"
    val uptime = "Uptime: ${hoursPassed}h ${minutesPassed}m ${secondsPassed}s"
    val commsFinished = "Comm Finished: ${commissionMacro.commissionFinished}"
    val commPerHourText = "Per Hour: $commPerHour"
    var currentComm = CommUtil.getCommission()?.commName?: "Null"

    val size = maxOf(fontRenderer.getStringWidth(name),
              fontRenderer.getStringWidth(uptime),
              fontRenderer.getStringWidth(commsFinished),
              fontRenderer.getStringWidth(commPerHourText),
              fontRenderer.getStringWidth(currentComm))

    drawRect(5, textStartHeight - 5, size+15, textEndHeight + 5, 0x90000000.toInt())
    drawString(fontRenderer, name, 10, textStartHeight, fontColor)
    drawString(fontRenderer, uptime, 10, textStartHeight + 10, fontColor)
    drawString(fontRenderer, commsFinished, 10, textStartHeight + 20, fontColor)
    drawString(fontRenderer, commPerHourText, 10, textStartHeight + 30, fontColor)
    drawString(fontRenderer, currentComm, 10, textStartHeight + 40, fontColor)
  }
}