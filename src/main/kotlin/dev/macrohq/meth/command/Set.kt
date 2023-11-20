package dev.macrohq.meth.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.meth.feature.RouteBuilder
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import net.minecraft.block.BlockStainedGlass
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos

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
    if(worldScanner.enabled){
      worldScanner.disable()
    }else{
      worldScanner.enable()
    }
  }

  @SubCommand
  private fun scan() {
    info(world.getBlockState(player.getStandingOnFloor()).getValue(BlockStainedGlass.COLOR))
  }

  @SubCommand
  private fun group() {
    runAsync {
      info("Groupin shit")
      val clusters = mutableListOf<MutableList<BlockPos>>()
      while (blocks.isNotEmpty()) {
        val cluster = mutableListOf<BlockPos>()
        generateClusterAndExpand(blocks.first(), cluster, blocks)
        clusters.add(cluster)
      }
      info("Cluster: ${clusters.size}")
      RenderUtil.markers.clear()
      clusters.forEach { RenderUtil.markers.addAll(it) }
    }
  }

  private fun neighbors(blockPos: BlockPos): MutableList<BlockPos> {
    return BlockUtil.neighbourGenerator(blockPos, 1)
      .filter { world.getBlockState(it).block == Blocks.stained_glass && it != blockPos }
      .toMutableList()
  }

  private fun generateClusterAndExpand(
    block: BlockPos,
    cluster: MutableList<BlockPos>,
    blocks: MutableList<BlockPos>
  ) {
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
  }
}