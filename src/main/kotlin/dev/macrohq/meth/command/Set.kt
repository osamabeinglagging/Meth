package dev.macrohq.meth.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.meth.feature.implementation.RouteBuilder
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import net.minecraft.block.BlockStairs
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import kotlin.math.abs


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
    val state = world.getBlockState(start!!)
    RenderUtil.filledBox.clear()
    RenderUtil.markers.clear()
    RenderUtil.markers.add(start!!)
    RenderUtil.markers.add(player.getStandingOnCeil())
    info("canWalk: ${BlockUtil.canWalkOn(player.getStandingOnCeil(), start!!)}")
  }

  @SubCommand
  private fun start() {
    if (start != null) {
      RenderUtil.filledBox.remove(start!!)
    }
    start = player.getStandingOnFloor()
    RenderUtil.filledBox.add(start!!)
  }

  @SubCommand
  private fun log() {
    val route = RouteBuilder.route
    RenderUtil.markers.clear()
    route.forEach { RenderUtil.markers.add(it.block) }
    autoAotv.enable(route, true)
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
  private fun route() {
    if (RouteBuilder.enabled) {
      RouteBuilder.disable()
    } else {
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
    RouteBuilder.route.clear()
    autoRotation.disable()
  }
}