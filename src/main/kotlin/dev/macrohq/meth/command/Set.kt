package dev.macrohq.meth.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.meth.feature.AutoRotation
import dev.macrohq.meth.feature.LockType
import dev.macrohq.meth.feature.RouteBuilder
import dev.macrohq.meth.feature.RouteData
import dev.macrohq.meth.feature.helper.RouteNode
import dev.macrohq.meth.feature.helper.TransportMethod
import dev.macrohq.meth.pathfinding.AStarPathfinder
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import net.minecraft.block.BlockStainedGlass
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.target


@Command(value = "set", aliases = ["pft", "s"])
class Set {
  private var start: BlockPos? = null
  private var end: BlockPos? = null
  private var ll = mutableListOf<BlockPos>()
  private var entities = mutableListOf<Entity>()
  var count = 0
  val blocks = mutableListOf<BlockPos>()

  @Main
  private fun main() {
//    val ent = world.getLoadedEntityList().apply { sortBy { player.getDistanceToEntity(it) } }[1]
//    PathingUtil.goto(start!!, true)
//    autoRotation.easeToBlock(start!!, 300, relativeChange = true)
//    autoRotation.easeToAngle(AngleUtil.getAngle(start!!), 300, relativeChange = true)
//    RotationUtil.ease(AngleUtil.getAngles(start!!), 300)
    val renderer = mc.renderManager.getEntityRenderObject<Entity>(player)
    val model = (renderer as RenderLiving).getMainModel()
    val biped = model as ModelBiped
    val arm = biped.bipedRightArm
    
  }

  @SubCommand
  private fun start(){
    if(start != null){
      RenderUtil.filledBox.remove(start!!)
    }
    start = player.getStandingOnFloor()
    RenderUtil.filledBox.add(start!!)
  }

  @SubCommand
  private fun log() {
//    entities = world.loadedEntityList.toMutableList()
//    log("Logged entities")
    autoAotv.enable(RouteBuilder.route)
  }

  @SubCommand
  private fun record() {
    movementLogger.enable(5000)
  }

  @SubCommand
  private fun stop() {
    movementLogger.disable()
  }

  @SubCommand
  private fun replay() {
//    movementLogger.replay(movementLogger.moveme5000)
  }

  @SubCommand
  private fun clearmove() {
    info("Clearing")
    movementLogger.clear()
  }

  @SubCommand
  private fun end() {
    var str = ""
    str += "\tval LAVA = listOf(\n"
    for (block in RouteBuilder.route) {
      str += "\t\tRouteNode(BlockPos(${block.block.x}, ${block.block.y}, ${block.block.z}), TransportMethod.${block.transportMethod}),\n"
    }
    str += "\t)"
    println(str)
  }

  @SubCommand
  private fun route(){
    if(RouteBuilder.enabled){
      RouteBuilder.disable()
    }else{
      RouteBuilder.enable()
    }
  }

  @SubCommand
  private fun clear() {
    worldScanner.clear()
    movementLogger.disable()
    PathingUtil.stop()
    autoAotv.disable()
    failsafe.resetFailsafe()
    autoInventory.disable()
    macroHandler.disable()
    RenderUtil.lines.clear()
    commissionMacro.disable()
    meth.oTree = null
    RenderUtil.aabbs.clear()
    RenderUtil.points.clear()
    pathExec.disable()
    RenderUtil.entites.clear()
    mobKiller.disable()
    randomMovement.disable()
    autoCommission.disable()
    RenderUtil.filledBox.clear()
    RenderUtil.markers.clear()
    RouteBuilder.route.clear()
    mithrilMiner.disable()
    RotationUtil.stop()
    RouteBuilder.route.clear()
    autoRotation.stop()
  }
}