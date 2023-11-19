package dev.macrohq.meth.feature

import dev.macrohq.meth.util.event.PacketReceiveEvent
import dev.macrohq.meth.util.failsafe
import dev.macrohq.meth.util.macroHandler
import dev.macrohq.meth.util.player
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.network.play.server.S37PacketStatistics
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Ping {
  var serverPing = 100
  private var lastCheckTime = 0L
  private var packetSent = false

  @SubscribeEvent
  fun onPacketReceiveEvent(event: PacketReceiveEvent) {
    if (!packetSent) return
    if (event.packet is S37PacketStatistics) {
      serverPing = (System.currentTimeMillis() - lastCheckTime).toInt()
      packetSent = false
    }
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!macroHandler.enabled || !failsafe.failsafeAllowance) return
    sendPing()
  }

  private fun sendPing() {
    if (System.currentTimeMillis() - lastCheckTime < 10000) return
    player.sendQueue.networkManager.sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS))
    lastCheckTime = System.currentTimeMillis()
    packetSent = true
  }
}