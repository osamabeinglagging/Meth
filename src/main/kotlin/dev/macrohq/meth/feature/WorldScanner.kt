package dev.macrohq.meth.feature;

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.meth.feature.routebuilder.Vein
import dev.macrohq.meth.mixin.ChunkProviderClientAccessor
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import dev.macrohq.meth.util.Logger.note
import net.minecraft.block.BlockStainedGlass
import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

class WorldScanner {
  // Create a queue, add chunks to queue as they load, load chunks into list a from a thread.
  private val colorInt = hashMapOf(
    EnumDyeColor.RED to 0,
    EnumDyeColor.PURPLE to 1,
    EnumDyeColor.LIME to 2,
    EnumDyeColor.BLUE to 3,
    EnumDyeColor.ORANGE to 4,
    EnumDyeColor.YELLOW to 5
  )
  var enabled = false
  private var chunkQueue: Queue<Chunk> = LinkedList()
  private var processedChunks = mutableListOf<Chunk>()
  private var foundVeins = hashMapOf<Int, MutableList<Vein>>()

  @SubscribeEvent
  fun onChunkLoad(event: ChunkEvent.Load) {
    if (!this.enabled) return

    this.chunkQueue.add(event.chunk)
  }

  private fun startProcessThread() {
    runAsync {
      while (this.enabled) {
        if (this.chunkQueue.isEmpty()) continue

        val currentChunk = chunkQueue.poll()
        if (this.processedChunks.contains(currentChunk)) continue
        val time1 = System.currentTimeMillis()

        this.processedChunks.add(currentChunk)
        val currentChunkGemstones = mutableListOf<BlockPos>()

        for (y in 30..190) {
          for (x in (currentChunk.xPosition * 16)..(currentChunk.xPosition * 16) + 15) {
            for (z in (currentChunk.zPosition * 16)..(currentChunk.zPosition * 16) + 15) {
              val block = BlockPos(x, y, z)
              if ((world.getBlockState(block).block != Blocks.stained_glass && world.getBlockState(block).block != Blocks.stained_glass_pane)
                || !this.colorInt.contains(world.getBlockState(block).getValue(BlockStainedGlass.COLOR))
              ) continue
              currentChunkGemstones.add(block)
            }
          }
        }

        while (currentChunkGemstones.isNotEmpty()) {
          val currentClusterGemstones = mutableListOf<BlockPos>()
          this.generateClustersAndExpand(
            currentChunkGemstones.first(),
            currentClusterGemstones,
            currentChunkGemstones
          )

          val chunkCoordinate = this.encodeChunkCoordinate(currentChunk.xPosition, currentChunk.zPosition)

          this.foundVeins.getOrPut(chunkCoordinate) { mutableListOf() }.add(
            Vein(
              colorInt[world.getBlockState(currentClusterGemstones.first()).getValue(BlockStainedGlass.COLOR)]!!,
              findCenterBlockFromCluster(currentClusterGemstones)
            )
          )
        }
        log("Time to process chunk: ${System.currentTimeMillis() - time1}ms")
      }
    }
  }

  fun enable() {
    this.enabled = true
    val loadedChunks = (world.chunkProvider as ChunkProviderClientAccessor).chunkListing
    this.chunkQueue.addAll(loadedChunks)
    this.startProcessThread()
  }

  fun disable() {
    info("WorldScanner Disabled")
    this.enabled = false
  }

  fun clear() {
    this.chunkQueue.clear()
    this.processedChunks.clear()
    this.foundVeins.clear()
  }

  private fun findCenterBlockFromCluster(cluster: MutableList<BlockPos>): BlockPos {
    var maxX = Int.MIN_VALUE
    var maxY = Int.MIN_VALUE
    var maxZ = Int.MIN_VALUE

    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    var minZ = Int.MAX_VALUE

    cluster.forEach { pos ->
      maxX = maxOf(maxX, pos.x)
      maxY = maxOf(maxY, pos.y)
      maxZ = maxOf(maxZ, pos.z)

      minX = minOf(minX, pos.x)
      minY = minOf(minY, pos.y)
      minZ = minOf(minZ, pos.z)
    }

    val centerX = (minX + maxX) / 2
    val centerY = (minY + maxY) / 2
    val centerZ = (minZ + maxZ) / 2

    return BlockPos(centerX, centerY, centerZ)
  }

  private fun findNeighbours(blockPos: BlockPos): MutableList<BlockPos> {
    val neighbours = mutableListOf<BlockPos>()
    val parentColor = world.getBlockState(blockPos).getValue(BlockStainedGlass.COLOR)
    for (x in -1..1) {
      for (y in -1..1) {
        for (z in -1..1) {
          val block = blockPos.add(x, y, z)
          if (world.getBlockState(block).block != Blocks.stained_glass && world.getBlockState(block).block != Blocks.stained_glass_pane) continue
          if (world.getBlockState(block).getValue(BlockStainedGlass.COLOR) != parentColor) continue

          neighbours.add(block)
        }
      }
    }
    return neighbours
  }

  private fun generateClustersAndExpand(
    block: BlockPos,
    cluster: MutableList<BlockPos>,
    blocks: MutableList<BlockPos>
  ) {
    cluster.add(block)
    blocks.remove(block)
    findNeighbours(block).apply { removeAll(cluster) }.forEach {
      generateClustersAndExpand(it, cluster, blocks)
    }
  }

  private fun encodeChunkCoordinate(chunkX: Int, chunkY: Int): Int = (chunkX shl 8) and chunkY
}