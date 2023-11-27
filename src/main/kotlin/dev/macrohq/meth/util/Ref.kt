package dev.macrohq.meth.util

import dev.macrohq.meth.Meth
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator

val mc
  get() = Minecraft.getMinecraft()
val player
  get() = mc.thePlayer
val world
  get() = mc.theWorld
val tessellator
  get() = Tessellator.getInstance()
val worldRenderer
  get() = tessellator.worldRenderer
val meth
  get() = Meth.instance
val gameSettings
  get() = mc.gameSettings
val fontRenderer
  get() = mc.fontRendererObj
val config
  get() = meth.config
val autoAotv
  get() = meth.autoAotv
val mithrilMiner
  get() = meth.mithrilMiner
val autoCommission
  get() = meth.autoCommission
val randomMovement
  get() = meth.randomMovement
val mobKiller
  get() = meth.mobKiller
val pathExec
  get() = meth.pathExec
val debugHUD
  get() = meth.debugHUD
val worldScanner
  get() = meth.worldScanner
val locationUtil
  get() = meth.locationUtil
val infoBarUtil
  get() = meth.infoBarUtil
val commissionMacro
  get() = meth.commissionMacro
val autoWarp
  get() = meth.autoWarp
val autoInventory
  get() = meth.autoInventory
val macroHandler
  get() = meth.macroHandler
val ping
  get() = meth.ping
val statusHUD
  get() = meth.commissionMacroStatusHUD
val failsafe
  get() = meth.failsafeNew
val movementLogger
  get() = meth.movementLogger