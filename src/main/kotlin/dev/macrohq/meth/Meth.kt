package dev.macrohq.meth

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.meth.command.Set
import dev.macrohq.meth.config.MethConfig
import dev.macrohq.meth.feature.FeatureManager
import dev.macrohq.meth.feature.implementation.*
import dev.macrohq.meth.macro.MacroHandler
import dev.macrohq.meth.macro.macros.CommissionMacro
import dev.macrohq.meth.pathfinding.PathExec
import dev.macrohq.meth.util.*
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod(modid = "meth", name = "Meth", version = "%%VERSION%%")
class Meth {
  companion object {
    @Mod.Instance("meth")
    lateinit var instance: Meth private set
  }

  lateinit var config: MethConfig private set
  lateinit var pathExec: PathExec private set
  lateinit var worldScanner: WorldScanner private set
  lateinit var macroHandler: MacroHandler private set
  lateinit var commissionMacro: CommissionMacro private set
  var oTree: OctTree? = null

  @Mod.EventHandler
  fun onInit(event: FMLInitializationEvent) {
    config = MethConfig()
    pathExec = PathExec()
    worldScanner = WorldScanner()

    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(pathExec)
    MinecraftForge.EVENT_BUS.register(statusHUD)

    MinecraftForge.EVENT_BUS.register(RouteBuilder)
    MinecraftForge.EVENT_BUS.register(Testing)

    MacroHandler.getInstance().loadMacros().forEach{ macro -> MinecraftForge.EVENT_BUS.register(macro)}
    FeatureManager.getInstance().loadFeatures().forEach{ feature -> MinecraftForge.EVENT_BUS.register(feature)}

    CommandManager.register(Set()) // You dont need this punk
  }

  @SubscribeEvent
  fun onRenderWorldLast(event: RenderWorldLastEvent) {
    RenderUtil.onRenderWorldLast(event)
    KeyBindUtil.onRenderWorldLast()
//    InventoryUtil.changeGuiName(StringUtil.randomFunny)
  }
}