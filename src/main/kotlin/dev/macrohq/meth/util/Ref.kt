package dev.macrohq.meth.util

import dev.macrohq.meth.Meth
import dev.macrohq.meth.config.hud.CommissionMacroStatusHUD
import dev.macrohq.meth.feature.implementation.*
import dev.macrohq.meth.feature.implementation.failsafe.Failsafe
import dev.macrohq.meth.macro.MacroHandler
import dev.macrohq.meth.macro.macros.CommissionMacro
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator

val mc = Minecraft.getMinecraft()
val player
  get() = mc.thePlayer
val playerController
  get() = mc.playerController
val world
  get() = mc.theWorld
val tessellator = Tessellator.getInstance()
val worldRenderer
  get() = tessellator.worldRenderer
val meth = Meth.instance
val gameSettings
  get() = mc.gameSettings
val fontRenderer
  get() = mc.fontRendererObj
val config
  get() = meth.config
val pathExec
  get() = meth.pathExec
val worldScanner
  get() = meth.worldScanner

val autoAotv = AutoAotv.getInstance()
val mithrilMiner = MithrilMiner.getInstance()
val autoCommission = AutoCommission.getInstance()
val randomMovement = RandomMovement.getInstance()
val mobKiller = MobKiller.getInstance()
val autoRotation = AutoRotation.getInstance()
val locationUtil = LocationTracker.getInstance()
val infoBarUtil = InfoBarTracker.getInstance()
val commissionMacro = CommissionMacro.getInstance()
val autoWarp = AutoWarp.getInstance()
val autoInventory = AutoInventory.getInstance()
val macroHandler = MacroHandler.getInstance()
val ping = Ping.getInstance()
val statusHUD = CommissionMacroStatusHUD.getInstance()
val failsafe = Failsafe.getInstance()
val movementLogger = MovementLogger.getInstance()