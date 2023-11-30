package dev.macrohq.meth

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.meth.command.Set
import dev.macrohq.meth.config.hud.DebugHUD
import dev.macrohq.meth.config.MethConfig
import dev.macrohq.meth.config.hud.CommissionMacroStatusHUD
import dev.macrohq.meth.feature.*
import dev.macrohq.meth.feature.failsafe.FailsafeNew
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

  lateinit var ping: Ping private set
  lateinit var config: MethConfig private set
  lateinit var autoAotv: AutoAotv private set
  lateinit var pathExec: PathExec private set
  lateinit var debugHUD: DebugHUD private set
  lateinit var autoWarp: AutoWarp private set
  lateinit var mobKiller: MobKiller private set
  lateinit var failsafeNew: FailsafeNew private set
  lateinit var infoBarUtil: InfoBarUtil private set
  lateinit var autoRotation: AutoRotation private set
  lateinit var worldScanner: WorldScanner private set
  lateinit var locationUtil: LocationUtil private set
  lateinit var mithrilMiner: MithrilMiner private set
  lateinit var macroHandler: MacroHandler private set
  lateinit var autoInventory: AutoInventory private set
  lateinit var autoCommission: AutoCommission private set
  lateinit var movementLogger: MovementLogger private set
  lateinit var randomMovement: RandomMovement private set
  lateinit var commissionMacro: CommissionMacro private set
  lateinit var commissionMacroStatusHUD: CommissionMacroStatusHUD private set
  var oTree: OctTree? = null

  @Mod.EventHandler
  fun onInit(event: FMLInitializationEvent) {
    ping = Ping()
    config = MethConfig()
    autoAotv = AutoAotv()
    pathExec = PathExec()
    debugHUD = DebugHUD()
    autoWarp = AutoWarp()
    mobKiller = MobKiller()
    infoBarUtil = InfoBarUtil()
    failsafeNew = FailsafeNew()
    autoRotation = AutoRotation()
    worldScanner = WorldScanner()
    locationUtil = LocationUtil()
    mithrilMiner = MithrilMiner()
    autoInventory = AutoInventory()
    autoCommission = AutoCommission()
    randomMovement = RandomMovement()
    movementLogger = MovementLogger()
    commissionMacro = CommissionMacro()
    macroHandler = MacroHandler()
    commissionMacroStatusHUD = CommissionMacroStatusHUD()

    MinecraftForge.EVENT_BUS.register(ping)
    MinecraftForge.EVENT_BUS.register(autoAotv)
    MinecraftForge.EVENT_BUS.register(pathExec)
    MinecraftForge.EVENT_BUS.register(debugHUD)
    MinecraftForge.EVENT_BUS.register(autoWarp)
    MinecraftForge.EVENT_BUS.register(mobKiller)
    MinecraftForge.EVENT_BUS.register(failsafe)
    MinecraftForge.EVENT_BUS.register(locationUtil)
    MinecraftForge.EVENT_BUS.register(failsafeNew)
    MinecraftForge.EVENT_BUS.register(infoBarUtil)
    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(mithrilMiner)
    MinecraftForge.EVENT_BUS.register(autoRotation)
    MinecraftForge.EVENT_BUS.register(macroHandler)
    MinecraftForge.EVENT_BUS.register(autoInventory)
    MinecraftForge.EVENT_BUS.register(randomMovement)
    MinecraftForge.EVENT_BUS.register(movementLogger)
    MinecraftForge.EVENT_BUS.register(autoCommission)
    MinecraftForge.EVENT_BUS.register(commissionMacroStatusHUD)

    MinecraftForge.EVENT_BUS.register(RouteBuilder)
    MinecraftForge.EVENT_BUS.register(Testing)
    CommandManager.register(Set()) // You dont need this punk
  }

  @SubscribeEvent
  fun onRenderWorldLast(event: RenderWorldLastEvent) {
    RotationUtil.onRenderWorldLast()
    RenderUtil.onRenderWorldLast(event)
    KeyBindUtil.onRenderWorldLast()
    InventoryUtil.changeGuiName(StringUtil.randomFunny)
  }
}