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
//    if(worldScanner.enabled){
//      worldScanner.disable()
//    }else{
//      worldScanner.enable()
//    }
//    player.setAngles()
//    EntityRenderer
//    val ar = AutoRotation()
//    val degrees = ar.pixelsForDegree(1f, 1f)
//    ar.moveMouse(degrees.first, degrees.second)
//    val cluster = mutableListOf<BlockPos>()
//    val blocks = mutableListOf<BlockPos>()
//    generateClusterAndExpand(RaytracingUtil.getBlockLookingAt(10f)!!, cluster, blocks)
//    RenderUtil.markers.apply { clear() }.addAll(cluster)

//    val velocity = 20f / 20f
//    val velocity = (velocity * velocity + velocity * 2) / 3f
//    if (velocity > 1) velocity = 1
    val target = world.getLoadedEntityList().apply { sortBy { player.getDistanceToEntity(it) } }.first { it != player && it is EntityLivingBase }

    val velocity = 1f
    val d = player.getPositionEyes(1f).distanceTo(target.positionVector.addVector(.5,1.0,.5))
    val posX = (target.posX + (target.posX - target.lastTickPosX) * d - player.posX)
    val posY = (target.posY + (target.posY - target.prevPosY) * d + target.height * 0.5 - player.posY - player.eyeHeight)
    val posZ = (target.posZ + (target.posZ - target.prevPosZ) * d - player.posZ)

    val neededYaw = Math.toDegrees(atan2(posZ, posX)).toFloat() - 90
    player.rotationYaw = neededYaw

    val hDistance = sqrt(posX * posX + posZ * posZ)
    val hDistanceSq = hDistance * hDistance
    val g = 0.006f
    val velocitySq: Float = velocity * velocity
    val velocityPow4 = velocitySq * velocitySq
    val neededPitch = -Math.toDegrees(atan((velocitySq - sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq))) / (g * hDistance))).toFloat()
    player.rotationPitch = neededPitch
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
      info("Groupin shit")
      val clusters = mutableListOf<MutableList<BlockPos>>()
      while (blocks.isNotEmpty()) {
        val cluster = mutableListOf<BlockPos>()
        generateClusterAndExpand(blocks.first(), cluster, blocks)
        info("Exited")
        clusters.add(cluster)
      }
      info("Cluster: ${clusters.size}")
      RenderUtil.markers.clear()
      clusters.forEach { RenderUtil.markers.addAll(it) }
    }
  }

  private fun neighbors(blockPos: BlockPos): MutableList<BlockPos> {
//    return BlockUtil.neighbourGenerator(blockPos, 1)
//      .filter { it != blockPos && world.getBlockState(it).block == Blocks.stained_glass && it != blockPos }
//      .toMutableList()
//    ItemBow

    val neighbours = mutableListOf<BlockPos>()
    val parentColor = world.getBlockState(blockPos).getValue(BlockStainedGlass.COLOR)
    for (x in 1 downTo -1) {
      for (y in 1 downTo -1) {
        for (z in 1 downTo -1) {
          val block = blockPos.add(x, y, z)
          if (world.getBlockState(block).block != Blocks.stained_glass && world.getBlockState(block).block != Blocks.stained_glass_pane) continue
          if (world.getBlockState(block).getValue(BlockStainedGlass.COLOR) != parentColor) continue
          if(x == 0 && z == 0 && y == 1) neighbours.add(0, block)
          neighbours.add(block)
        }
      }
    }
    return neighbours
  }

  private fun generateClusterAndExpand(
    block: BlockPos,
    cluster: MutableList<BlockPos>,
    blocks: MutableList<BlockPos>
  ) {
    cluster.add(block)
    blocks.remove(block)
    val neighbors = neighbors(block)
    neighbors.removeAll(cluster)
    cluster.addAll(neighbors)

//    neighbors(block).apply { removeAll(cluster) }.forEach {
//    if(neighbors(block).size == 0) return
    if(neighbors.size == 0) return
      generateClusterAndExpand(neighbors(block).first(), cluster, blocks)
//    }
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