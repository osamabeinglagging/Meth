package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.player
import dev.macrohq.meth.util.world
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.event.entity.player.ArrowLooseEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object Testing {
  var testing = false
  var movement = ""
  var i = 1
  val chunks = mutableListOf<Chunk>()

  @SubscribeEvent
  fun onTick(event: TickEvent.ClientTickEvent) {
    if (player == null || world == null || !testing) return
//    info("TICK: ${mc.netHandler.getPlayerInfo(player.uniqueID).responseTime}")
  }

  @SubscribeEvent
  fun onInput(event: KeyInputEvent) {
//        if(Keyboard.isKeyDown(Keyboard.KEY_GRAVE)){
//            if(meth.oTree != null) {
//                var block = RaytracingUtil.getBlockLookingAt(10f)
//                if(block == null){
//                    block = player.getStandingOnFloor()
//                }
//                info("inserted: ${meth.oTree!!.insert(Leaf(block, world.isAirBlock(block)))}")
//                RenderUtil.aabbs.clear()
//                RenderUtil.markers.clear()
//                meth.oTree!!.draw()
//            }
//        }
  }

  @SubscribeEvent
  fun onChunkLoad(event: ChunkEvent.Load) {
//        info("RAN")
//        chunks.add(event.chunk)
  }

  @SubscribeEvent
  fun onBow(event: ArrowLooseEvent){
    info("charge: ${event.charge}")
  }
}