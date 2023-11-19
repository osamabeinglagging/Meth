package dev.macrohq.meth.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.meth.feature.RouteBuilder
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import scala.Mutable
import java.util.Collections.addAll

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
//    WebhookUtil.send(
//      WebhookUtil.statusBody(1698425140, 71, 20, "Kill Tom", 1698425140, 1698425140),
//      WebhookUtil.WebhookType.GENERAL_INFO
//    )
//    RenderUtil.markers.add(player.getStandingOnFloor())
//    blocks.forEach {
//      RenderUtil.markers.add(it)
//    }
//    info("First: ${blocks.first()}")
//    RenderUtil.markers.clear()
//    RenderUtil.markers.addAll(
//      BlockUtil.neighbourGenerator(player.getStandingOnFloor(), 1)
//        .filter { world.getBlockState(it).block == Blocks.stained_glass }.toMutableList()
//    )
    info("SET")
    RenderUtil.markers.clear()
    RenderUtil.markers.add(player.getStandingOnFloor().add(20,0,0))
    PathingUtil.goto(player.getStandingOnFloor().add(20,0,0))
  }

  @SubCommand
  private fun scan() {
    runAsync {
      Logger.info("Scan started")
      blocks.clear()
      for (y in 0..256) {
        for (x in player.chunkCoordX * 16..player.chunkCoordX * 16 + 15) {
          for (z in player.chunkCoordZ * 16..player.chunkCoordZ * 16 + 15) {
            val block = BlockPos(x, y, z)
            if (world.getBlockState(block).block == Blocks.stained_glass) {
              blocks.add(block)
            }
          }
        }
      }
      info("Blocks: ${blocks.size}")
//      RenderUtil.markers.clear()
//      RenderUtil.markers.addAll(blocks)
    }
  }

  @SubCommand
  private fun group() {
    runAsync {
      val clusters = mutableListOf<MutableList<BlockPos>>()
      while(blocks.isNotEmpty()) {
        val cluster = mutableListOf<BlockPos>()
        generateClusterAndExpand(blocks.first(), cluster, blocks)
        clusters.add(cluster)
      }
      RenderUtil.markers.clear()
      clusters.forEach{RenderUtil.markers.addAll(it)}
    }
  }

  private fun neighbors(blockPos: BlockPos): MutableList<BlockPos> {
    return BlockUtil.neighbourGenerator(blockPos, 1).filter { world.getBlockState(it).block == Blocks.stained_glass && it != blockPos}
      .toMutableList()
  }

  private fun generateClusterAndExpand(block: BlockPos, cluster: MutableList<BlockPos>, blocks: MutableList<BlockPos>) {
    cluster.add(block)
    blocks.remove(block)
    neighbors(block).apply { removeAll(cluster) }.forEach {
      generateClusterAndExpand(it, cluster, blocks)
    }
  }

  @SubCommand
  private fun log() {
    entities = world.loadedEntityList.toMutableList()
    log("Logged entities")
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
  private fun f() {
//    failsafe.somethingBroken = false; failsafe.macroChecked = true
//    failsafe.whatFailsafe = Failsafe.Failsafe.PLAYER_FAILSAFE
  }

  @SubCommand
  private fun start() {
    if (start != null) RenderUtil.markers.remove(start)
    start = player.getStandingOnFloor()
    RenderUtil.markers.add(player.getStandingOnFloor())
  }

  @SubCommand
  private fun end() {
    var str = ""
    str += "\tval LAVA = listOf(\n"
    for (block in RouteBuilder.route) {
      str += "\t\tBlockPos(${block.x}, ${block.y}, ${block.z}),\n"
    }
    str += "\t)"
    println(str)
  }

  @SubCommand
  private fun clear() {
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
  }
}