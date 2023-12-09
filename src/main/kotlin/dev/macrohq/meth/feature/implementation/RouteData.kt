package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.helper.RouteNode
import dev.macrohq.meth.feature.helper.TransportMethod
import dev.macrohq.meth.macro.macros.CommissionMacro.CommissionType
import dev.macrohq.meth.macro.macros.CommissionMacro.CommissionType.*
import dev.macrohq.meth.util.config
import net.minecraft.util.BlockPos

object RouteData {

  /*New Start*/
  val LAVA_ETHERWARP = listOf(
    RouteNode(BlockPos(0, 165, -12), TransportMethod.ETHERWARP),
    RouteNode(BlockPos(29, 206, -6), TransportMethod.ETHERWARP),
    RouteNode(BlockPos(56, 222, -30), TransportMethod.ETHERWARP),
  )

  val LAVA_ETHERWARPLESS_1 = listOf(
    RouteNode(BlockPos(4, 160, -43)),
    RouteNode(BlockPos(9, 175, -12)),
    RouteNode(BlockPos(27, 206, -13)),
    RouteNode(BlockPos(54, 218, -12)),
    RouteNode(BlockPos(55, 226, -32)),
    RouteNode(BlockPos(56, 222, -30), TransportMethod.WALK)
  )
  val LAVA_ETHERWARPLESS_2 = listOf(
    RouteNode(BlockPos(0, 160, -30), TransportMethod.FLY),
    RouteNode(BlockPos(15, 196, -10), TransportMethod.FLY),
    RouteNode(BlockPos(46, 216, -9), TransportMethod.FLY),
    RouteNode(BlockPos(55, 226, -32), TransportMethod.FLY),
    RouteNode(BlockPos(56, 222, -30), TransportMethod.WALK),
  )
  val LAVA_ETHERWARPLESS_3 = listOf(
    RouteNode(BlockPos(3, 162, -20), TransportMethod.FLY),
    RouteNode(BlockPos(31, 215, -9), TransportMethod.FLY),
    RouteNode(BlockPos(48, 217, -10), TransportMethod.FLY),
    RouteNode(BlockPos(55, 226, -32), TransportMethod.FLY),
    RouteNode(BlockPos(56, 222, -30), TransportMethod.WALK),
  )

  val LAVA_HYBRID_1 = listOf(
    RouteNode(BlockPos(8, 166, -22), TransportMethod.FLY),
    RouteNode(BlockPos(30, 208, -10), TransportMethod.FLY),
    RouteNode(BlockPos(38, 197, -8), TransportMethod.FLY),
    RouteNode(BlockPos(44, 197, -12), TransportMethod.WALK),
    RouteNode(BlockPos(52, 232, -15), TransportMethod.FLY),
    RouteNode(BlockPos(53, 224, -31), TransportMethod.FLY),
    RouteNode(BlockPos(55, 222, -30), TransportMethod.WALK),
  )


  /*New End*/

  val LAVA_FLY_ETHERWARP_1 = listOf(
    RouteNode(BlockPos(4, 160, -43)),
    RouteNode(BlockPos(9, 175, -12)),
    RouteNode(BlockPos(27, 206, -13)),
    RouteNode(BlockPos(54, 218, -12)),
    RouteNode(BlockPos(56, 222, -30), TransportMethod.ETHERWARP)
  )

  private val CEANNA_AOTV = listOf(
    BlockPos(7, 158, -23), BlockPos(44, 146, 18), BlockPos(43, 134, 21)
  )
  private val CEANNA_ETHERWARP = listOf(
    BlockPos(6, 148, -12), BlockPos(32, 152, -1), BlockPos(41, 135, 18), BlockPos(41, 134, 21)
  )
  private val CEANNA_ETHERWARPLESS = listOf(
    BlockPos(12, 158, -23),
    BlockPos(40, 148, 13),
    BlockPos(43, 134, 21),
    BlockPos(42, 134, 20)
  )

  private val LAVA_AOTV = listOf(
    BlockPos(4, 160, -43), BlockPos(9, 175, -12), BlockPos(27, 206, -13), BlockPos(54, 218, -12), BlockPos(56, 222, -30)
  )
  private val LAVA_ETHERWARPP = listOf(
    BlockPos(0, 165, -12), BlockPos(30, 206, -6), BlockPos(56, 222, -30)
  )
  private val LAVA_ETHERWARPLESSs = listOf(
    BlockPos(4, 160, -43),
    BlockPos(9, 175, -12),
    BlockPos(27, 206, -13),
    BlockPos(54, 218, -12),
    BlockPos(55, 226, -32),
    BlockPos(56, 222, -30),
  )

  private val CLIFFSIDE_AOTV = listOf(
    BlockPos(20, 183, 19),
    BlockPos(71, 173, 41),
    BlockPos(93, 144, 51),
  )
  private val CLIFFSIDE_ETHERWARP = listOf(
    BlockPos(0, 165, -12), BlockPos(31, 177, 13), BlockPos(56, 161, 59), BlockPos(91, 145, 51), BlockPos(93, 144, 51)
  )
  private val CLIFFSIDE_ETHERWARPLESS = listOf(
    BlockPos(8, 164, -23),
    BlockPos(20, 186, 19),
    BlockPos(68, 173, 39),
    BlockPos(93, 144, 49),
    BlockPos(93, 144, 51),
  )

  private val RAMPART_AOTV = listOf(
    BlockPos(1, 159, -24),
    BlockPos(-33, 181, 1),
    BlockPos(-64, 230, -20),
    BlockPos(-84, 200, -27),
    BlockPos(-73, 169, -59),
    BlockPos(-70, 169, -59),
  )
  private val RAMPART_ETHERWARP = listOf(
    BlockPos(0, 165, -12),
    BlockPos(-48, 174, -31),
    BlockPos(-70, 158, -40),
    BlockPos(-79, 177, -55),
    BlockPos(-70, 169, -59)
  )
  private val RAMPART_ETHERWARPLESS = listOf(
    BlockPos(-12, 160, -23),
    BlockPos(-50, 194, -3),
    BlockPos(-63, 223, -15),
    BlockPos(-84, 204, -28),
    BlockPos(-74, 169, -57),
    BlockPos(-71, 169, -59)
  )

  private val ROYAL_AOTV = listOf(
    BlockPos(10, 156, -23),
    BlockPos(46, 146, 24),
    BlockPos(114, 156, 46),
    BlockPos(134, 159, 58),
    BlockPos(166, 168, 23),
    BlockPos(165, 161, 17),
  )
  private val ROYAL_ETHERWARP = listOf(
    BlockPos(0, 165, -12),
    BlockPos(31, 177, 13),
    BlockPos(56, 161, 59),
    BlockPos(106, 156, 37),
    BlockPos(139, 166, 65),
    BlockPos(165, 161, 23),
    BlockPos(165, 161, 17),
  )
  private val ROYAL_ETHERWARPLESS = listOf(
    BlockPos(6, 154, -21),
    BlockPos(56, 139, 35),
    BlockPos(112, 156, 42),
    BlockPos(131, 161, 61),
    BlockPos(164, 170, 33),
    BlockPos(163, 161, 21),
    BlockPos(164, 161, 18),
  )

  private val UPPER_AOTV = listOf(
    BlockPos(-5, 157, -38),
    BlockPos(-19, 169, -23),
    BlockPos(-50, 181, -32),
    BlockPos(-66, 164, -39),
    BlockPos(-86, 162, -50),
    BlockPos(-112, 173, -71),
    BlockPos(-111, 166, -74),
  )
  private val UPPER_ETHERWARP = listOf(
    BlockPos(0, 165, -12),
    BlockPos(-48, 174, -31),
    BlockPos(-84, 156, -49),
    BlockPos(-116, 171, -76),
    BlockPos(-111, 166, -74),
  )
  private val UPPER_ETHERWARPLESS = listOf(
    BlockPos(-10, 160, -23),
    BlockPos(-56, 184, -29),
    BlockPos(-63, 165, -38),
    BlockPos(-91, 162, -53),
    BlockPos(-111, 170, -69),
    BlockPos(-111, 166, -74),
  )

  private val ICE_AOTV = listOf(
    BlockPos(-8, 166, -25),
    BlockPos(-5, 193, 34),
    BlockPos(0, 130, 129),
    BlockPos(3, 130, 177),
  )
  private val ICE_ETHERWARP = listOf(
    BlockPos(0, 165, -12),
    BlockPos(-18, 194, 21),
    BlockPos(8, 161, 63),
    BlockPos(8, 151, 113),
    BlockPos(0, 127, 142),
    BlockPos(3, 130, 177),
  )
  private val ICE_ETHERWARPLESS = listOf(
    BlockPos(-8, 166, -25),
    BlockPos(-5, 193, 34),
    BlockPos(0, 130, 128),
    BlockPos(0, 130, 141),
    BlockPos(-2, 138, 177),
    BlockPos(-1, 133, 177),
  )

  private val GOBLIN_AOTV = listOf(
    BlockPos(-8, 166, -25),
    BlockPos(-5, 193, 34),
    BlockPos(0, 130, 129),
    BlockPos(0, 131, 153),
    BlockPos(-60, 169, 153),
    BlockPos(-68, 154, 150),
  )
  private val GOBLIN_ETHERWARP = listOf(
    BlockPos(0, 165, -12),
    BlockPos(-18, 194, 21),
    BlockPos(8, 161, 63),
    BlockPos(8, 151, 113),
    BlockPos(0, 127, 142),
    BlockPos(-46, 148, 169),
    BlockPos(-56, 165, 135),
    BlockPos(-68, 154, 150),
  )
  private val GOBLIN_ETHERWARPLESS = listOf(
    BlockPos(-8, 166, -25),
    BlockPos(-5, 193, 34),
    BlockPos(0, 130, 128),
    BlockPos(0, 130, 155),
    BlockPos(-46, 167, 155),
    BlockPos(-65, 155, 148),
    BlockPos(-67, 154, 149),
  )

  fun getRoute(comm: CommissionType, fly: Int = config.commTransport): List<BlockPos> {
    return when (comm) {
      MITHRIL_MINER, TITANIUM_MINER, LAVA_TITANIUM, LAVA_MITHRIL -> {
        properRoute(LAVA_AOTV, LAVA_ETHERWARPP, LAVA_ETHERWARPLESSs, fly)
      }

      UPPER_MITHRIL, UPPER_TITANIUM -> {
        properRoute(UPPER_AOTV, UPPER_ETHERWARP, UPPER_ETHERWARPLESS, fly)
      }

      ROYAL_MITHRIL, ROYAL_TITANIUM -> {
        properRoute(ROYAL_AOTV, ROYAL_ETHERWARP, ROYAL_ETHERWARPLESS, fly)
      }

      CLIFFSIDE_MITHRIL, CLIFFSIDE_TITANIUM -> {
        properRoute(CLIFFSIDE_AOTV, CLIFFSIDE_ETHERWARP, CLIFFSIDE_ETHERWARPLESS, fly)
      }

      RAMPARTS_MITHRIL, RAMPARTS_TITANIUM -> {
        properRoute(RAMPART_AOTV, RAMPART_ETHERWARP, RAMPART_ETHERWARPLESS, fly)
      }

      GOBLIN_SLAYER -> {
        properRoute(GOBLIN_AOTV, GOBLIN_ETHERWARP, GOBLIN_ETHERWARPLESS, fly)
      }

      ICE_WALKER_SLAYER -> {
        properRoute(ICE_AOTV, ICE_ETHERWARP, ICE_ETHERWARPLESS, fly)
      }

      COMMISSION_CLAIM -> {
        properRoute(CEANNA_AOTV, CEANNA_ETHERWARP, CEANNA_ETHERWARPLESS, fly)
      }
    }
  }

  private fun properRoute(
    flyRoute: List<BlockPos>, tpRoute: List<BlockPos>, etherwarpLessRoute: List<BlockPos>, commTransport: Int
  ): List<BlockPos> {
    return when (commTransport) {
      0 -> etherwarpLessRoute
      1 -> flyRoute
      2 -> tpRoute
      else -> tpRoute
    }
  }
}