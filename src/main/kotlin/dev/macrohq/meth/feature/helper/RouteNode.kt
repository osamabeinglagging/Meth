package dev.macrohq.meth.feature.helper

import net.minecraft.util.BlockPos

enum class TransportMethod { FLY, WALK, SNEAK_WALK, ETHERWARP }
data class RouteNode(val block: BlockPos, val transportMethod: TransportMethod = TransportMethod.FLY)