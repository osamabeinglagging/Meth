package dev.macrohq.meth.util.event

import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.Event

open class PacketReceiveEvent(val packet: Packet<*>): Event()