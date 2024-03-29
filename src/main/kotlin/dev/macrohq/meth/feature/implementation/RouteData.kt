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
            // might/should change
            RouteNode(BlockPos(8, 166, -22), TransportMethod.FLY),
            RouteNode(BlockPos(30, 208, -10), TransportMethod.FLY),
            RouteNode(BlockPos(38, 197, -8), TransportMethod.FLY),
            RouteNode(BlockPos(44, 197, -12), TransportMethod.WALK),
            RouteNode(BlockPos(52, 232, -15), TransportMethod.FLY),
            RouteNode(BlockPos(53, 224, -31), TransportMethod.FLY),
            RouteNode(BlockPos(56, 222, -30), TransportMethod.WALK),
    )

    val LAVA_HYBRID_2 = listOf(
            RouteNode(BlockPos(0, 165, -12), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(28, 206, -6), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(31, 206, -6), TransportMethod.SNEAK_WALK),
            RouteNode(BlockPos(51, 206, -6), TransportMethod.WALK),
            RouteNode(BlockPos(54, 228, -18), TransportMethod.FLY),
            RouteNode(BlockPos(54, 224, -32), TransportMethod.FLY),
            RouteNode(BlockPos(56, 222, -30), TransportMethod.WALK),
    )

    val LAVA_HYBRID_3 = listOf(
            RouteNode(BlockPos(8, 166, -25), TransportMethod.FLY),
            RouteNode(BlockPos(28, 206, -6), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(30, 206, -6), TransportMethod.WALK),
            RouteNode(BlockPos(42, 206, -6), TransportMethod.WALK),
            RouteNode(BlockPos(54, 228, -16), TransportMethod.FLY),
            RouteNode(BlockPos(53, 224, -31), TransportMethod.FLY),
            RouteNode(BlockPos(55, 222, -30), TransportMethod.WALK),
    )
    private val CEANNA_ETHERWARP = listOf(
            RouteNode(BlockPos(6, 148, -12), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(32, 152, -1), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(41, 135, 18), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(41, 134, 21), TransportMethod.ETHERWARP)
    )


    val CEANNA_HYBRID_1 = listOf(
            RouteNode(BlockPos(6, 153, -21), TransportMethod.FLY),
            RouteNode(BlockPos(23, 143, -4), TransportMethod.FLY),
            RouteNode(BlockPos(39, 135, 15), TransportMethod.WALK),
            RouteNode(BlockPos(41, 135, 18), TransportMethod.WALK),
            RouteNode(BlockPos(42, 134, 20), TransportMethod.WALK),
    )
    val CEANNA_HYBRID_2 = listOf(
            RouteNode(BlockPos(3, 162, -33), TransportMethod.FLY),
            RouteNode(BlockPos(14, 144, -12), TransportMethod.FLY),
            RouteNode(BlockPos(39, 135, 15), TransportMethod.WALK),
            RouteNode(BlockPos(42, 134, 19), TransportMethod.WALK),
    )
    val CEANNA_HYBRID_3 = listOf(
            RouteNode(BlockPos(10, 158, -23), TransportMethod.FLY),
            RouteNode(BlockPos(28, 140, 2), TransportMethod.FLY),
            RouteNode(BlockPos(41, 134, 19), TransportMethod.WALK),
    )

    val CEANNA_ETHERWARPLESS_1 = listOf(
            RouteNode(BlockPos(10, 160, -23), TransportMethod.FLY),
            RouteNode(BlockPos(36, 148, 11), TransportMethod.FLY),
            RouteNode(BlockPos(41, 134, 22), TransportMethod.FLY),
    )

    val CEANNA_ETHERWARPLESS_2 = listOf(
            RouteNode(BlockPos(10, 156, -23), TransportMethod.FLY),
            RouteNode(BlockPos(23, 155, -2), TransportMethod.FLY),
            RouteNode(BlockPos(40, 148, 15), TransportMethod.FLY),
            RouteNode(BlockPos(41, 134, 21), TransportMethod.FLY),
    )

    val CEANNA_ETHERWARPLESS_3 = listOf(
            RouteNode(BlockPos(10, 154, -23), TransportMethod.FLY),
            RouteNode(BlockPos(39, 150, 15), TransportMethod.FLY),
            RouteNode(BlockPos(41, 134, 21), TransportMethod.FLY),
    )

    val CLIFFSIDE_ETHERWARP = listOf(
            RouteNode(BlockPos(0, 165, -12), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(31, 177, 13), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(56, 161, 59), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(91, 145, 51), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(93, 144, 51), TransportMethod.ETHERWARP)
    )

    val CLIFFSIDE_ETHERWARPLESS_1 = listOf(
            RouteNode(BlockPos(8, 164, -23), TransportMethod.FLY),
            RouteNode(BlockPos(20, 186, 19), TransportMethod.FLY),
            RouteNode(BlockPos(68, 173, 39), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 49), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 51), TransportMethod.WALK)
    )

    val CLIFFSIDE_ETHERWARPLESS_2 = listOf(
            RouteNode(BlockPos(20, 184, 19), TransportMethod.FLY),
            RouteNode(BlockPos(62, 189, 43), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 49), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 51), TransportMethod.WALK),
    )

    val CLIFFSIDE_ETHERWARPLESS_3 = listOf(
            RouteNode(BlockPos(12, 156, -22), TransportMethod.FLY),
            RouteNode(BlockPos(46, 142, 23), TransportMethod.FLY),
            RouteNode(BlockPos(64, 166, 42), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 49), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 51), TransportMethod.WALK),
    )

    val CLIFFSIDE_HYBRID_1 = listOf(
            RouteNode(BlockPos(10, 159, -23), TransportMethod.FLY),
            RouteNode(BlockPos(20, 143, -6), TransportMethod.FLY),
            RouteNode(BlockPos(40, 135, 15), TransportMethod.WALK),
            RouteNode(BlockPos(66, 163, 45), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 49), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 51), TransportMethod.WALK),
    )

    val CLIFFSIDE_HYBRID_2 = listOf(
            RouteNode(BlockPos(4, 156, -46), TransportMethod.FLY),
            RouteNode(BlockPos(4, 146, -38), TransportMethod.FLY),
            RouteNode(BlockPos(13, 144, -12), TransportMethod.WALK),
            RouteNode(BlockPos(40, 135, 14), TransportMethod.WALK),
            RouteNode(BlockPos(57, 135, 27), TransportMethod.WALK),
            RouteNode(BlockPos(66, 165, 42), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 49), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 51), TransportMethod.WALK),
    )

    val CLIFFSIDE_HYBRID_3 = listOf(
            RouteNode(BlockPos(10, 152, -21), TransportMethod.FLY),
            RouteNode(BlockPos(40, 145, 15), TransportMethod.FLY),
            RouteNode(BlockPos(68, 165, 51), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 49), TransportMethod.FLY),
            RouteNode(BlockPos(93, 144, 51), TransportMethod.WALK),
    )

    val ROYAL_ETHERWARP = listOf(
            RouteNode(BlockPos(9, 148, -9), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(52, 142, 14), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(84, 144, 44), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(107, 154, 38), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(130, 157, 38), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(172, 162, 22), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(164, 161, 18), TransportMethod.ETHERWARP),
    )

    val ROYAL_ETHERWARPLESS_1 = listOf(
            RouteNode(BlockPos(10, 158, -23), TransportMethod.FLY),
            RouteNode(BlockPos(38, 146, 15), TransportMethod.FLY),
            RouteNode(BlockPos(80, 155, 40), TransportMethod.FLY),
            RouteNode(BlockPos(114, 157, 41), TransportMethod.FLY),
            RouteNode(BlockPos(134, 162, 29), TransportMethod.FLY),
            RouteNode(BlockPos(166, 170, 35), TransportMethod.FLY),
            RouteNode(BlockPos(165, 160, 18), TransportMethod.FLY),
    )

    val ROYAL_ETHERWARPLESS_2 = listOf(
            RouteNode(BlockPos(24, 183, 19), TransportMethod.FLY),
            RouteNode(BlockPos(42, 179, 35), TransportMethod.FLY),
            RouteNode(BlockPos(82, 153, 41), TransportMethod.FLY),
            RouteNode(BlockPos(111, 156, 41), TransportMethod.FLY),
            RouteNode(BlockPos(128, 164, 55), TransportMethod.FLY),
            RouteNode(BlockPos(152, 166, 54), TransportMethod.FLY),
            RouteNode(BlockPos(164, 169, 33), TransportMethod.FLY),
            RouteNode(BlockPos(165, 160, 18), TransportMethod.FLY),
    )

    val ROYAL_ETHERWARPLESS_3 = listOf(
            RouteNode(BlockPos(3, 153, -45), TransportMethod.FLY),
            RouteNode(BlockPos(12, 155, -24), TransportMethod.FLY),
            RouteNode(BlockPos(24, 150, -4), TransportMethod.FLY),
            RouteNode(BlockPos(38, 142, 14), TransportMethod.FLY),
            RouteNode(BlockPos(56, 146, 29), TransportMethod.FLY),
            RouteNode(BlockPos(78, 152, 35), TransportMethod.FLY),
            RouteNode(BlockPos(101, 154, 40), TransportMethod.FLY),
            RouteNode(BlockPos(121, 158, 53), TransportMethod.FLY),
            RouteNode(BlockPos(144, 163, 58), TransportMethod.FLY),
            RouteNode(BlockPos(156, 171, 39), TransportMethod.FLY),
            RouteNode(BlockPos(168, 160, 22), TransportMethod.FLY),
            RouteNode(BlockPos(165, 160, 18), TransportMethod.WALK),
    )

    val ROYAL_HYBRID_1 = listOf(
            RouteNode(BlockPos(4, 154, -45), TransportMethod.FLY),
            RouteNode(BlockPos(8, 144, -23), TransportMethod.FLY),
            RouteNode(BlockPos(21, 143, -4), TransportMethod.WALK),
            RouteNode(BlockPos(41, 135, 16), TransportMethod.WALK),
            RouteNode(BlockPos(58, 147, 30), TransportMethod.FLY),
            RouteNode(BlockPos(81, 152, 38), TransportMethod.FLY),
            RouteNode(BlockPos(104, 155, 42), TransportMethod.FLY),
            RouteNode(BlockPos(113, 161, 38), TransportMethod.FLY),
            RouteNode(BlockPos(122, 154, 33), TransportMethod.FLY),
            RouteNode(BlockPos(151, 150, 33), TransportMethod.WALK),
            RouteNode(BlockPos(168, 171, 33), TransportMethod.FLY),
            RouteNode(BlockPos(168, 160, 19), TransportMethod.FLY),
            RouteNode(BlockPos(165, 160, 18), TransportMethod.WALK),
    )

    val RAMPART_ETHERWARP = listOf(
            RouteNode(BlockPos(0, 165, -12), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-48, 174, -31), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-70, 158, -40), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-79, 177, -55), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-70, 169, -59), TransportMethod.ETHERWARP)
    )

    val RAMPART_ETHERWARPLESS_1 = listOf(
            RouteNode(BlockPos(-3, 157, -46), TransportMethod.FLY),
            RouteNode(BlockPos(-12, 164, -25), TransportMethod.FLY),
            RouteNode(BlockPos(-27, 180, -15), TransportMethod.FLY),
            RouteNode(BlockPos(-43, 198, -17), TransportMethod.FLY),
            RouteNode(BlockPos(-59, 214, -24), TransportMethod.FLY),
            RouteNode(BlockPos(-78, 214, -36), TransportMethod.FLY),
            RouteNode(BlockPos(-75, 195, -48), TransportMethod.FLY),
            RouteNode(BlockPos(-75, 169, -56), TransportMethod.FLY),
            RouteNode(BlockPos(-71, 169, -59), TransportMethod.WALK),
    )
    val RAMPART_ETHERWARPLESS_2 = listOf(
            RouteNode(BlockPos(-4, 157, -46), TransportMethod.FLY),
            RouteNode(BlockPos(-12, 165, -25), TransportMethod.FLY),
            RouteNode(BlockPos(-33, 176, -23), TransportMethod.FLY),
            RouteNode(BlockPos(-54, 172, -32), TransportMethod.FLY),
            RouteNode(BlockPos(-74, 161, -43), TransportMethod.FLY),
            RouteNode(BlockPos(-78, 184, -48), TransportMethod.FLY),
            RouteNode(BlockPos(-74, 169, -56), TransportMethod.FLY),
            RouteNode(BlockPos(-71, 169, -59), TransportMethod.WALK),
    )

    val RAMPART_HYBRID_1 = listOf(
            RouteNode(BlockPos(-4, 154, -46), TransportMethod.FLY),
            RouteNode(BlockPos(-7, 144, -24), TransportMethod.FLY),
            RouteNode(BlockPos(-43, 136, -2), TransportMethod.WALK),
            RouteNode(BlockPos(-63, 145, 10), TransportMethod.FLY),
            RouteNode(BlockPos(-71, 146, 10), TransportMethod.FLY),
            RouteNode(BlockPos(-82, 163, -2), TransportMethod.FLY),
            RouteNode(BlockPos(-82, 172, -24), TransportMethod.FLY),
            RouteNode(BlockPos(-76, 179, -47), TransportMethod.FLY),
            RouteNode(BlockPos(-75, 169, -57), TransportMethod.FLY),
            RouteNode(BlockPos(-71, 169, -59), TransportMethod.WALK),
    )

    val UPPER_ETHERWARP = listOf(
            RouteNode(BlockPos(0, 165, -12), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-48, 174, -31), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-84, 156, -49), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-116, 171, -76), TransportMethod.ETHERWARP),
            RouteNode(BlockPos(-111, 166, -74), TransportMethod.ETHERWARP)
    )
    /*New End*/
    private val CEANNA_ETHERWARPp = listOf(
            BlockPos(6, 148, -12), BlockPos(32, 152, -1), BlockPos(41, 135, 18), BlockPos(41, 134, 21)
    )

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
    private val CLIFFSIDE_ETHERWARPp = listOf(
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
    private val RAMPART_ETHERWARPp = listOf(
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
    private val ROYAL_ETHERWARPp = listOf(
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
    private val UPPER_ETHERWARPp = listOf(
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
                properRoute(UPPER_AOTV, UPPER_ETHERWARPp, UPPER_ETHERWARPLESS, fly)
            }

            ROYAL_MITHRIL, ROYAL_TITANIUM -> {
                properRoute(ROYAL_AOTV, this.ROYAL_ETHERWARPp, ROYAL_ETHERWARPLESS, fly)
            }

            CLIFFSIDE_MITHRIL, CLIFFSIDE_TITANIUM -> {
                properRoute(CLIFFSIDE_AOTV, CLIFFSIDE_ETHERWARPp, CLIFFSIDE_ETHERWARPLESS, fly)
            }

            RAMPARTS_MITHRIL, RAMPARTS_TITANIUM -> {
                properRoute(RAMPART_AOTV, RAMPART_ETHERWARPp, RAMPART_ETHERWARPLESS, fly)
            }

            GOBLIN_SLAYER -> {
                properRoute(GOBLIN_AOTV, GOBLIN_ETHERWARP, GOBLIN_ETHERWARPLESS, fly)
            }

            ICE_WALKER_SLAYER -> {
                properRoute(ICE_AOTV, ICE_ETHERWARP, ICE_ETHERWARPLESS, fly)
            }

            COMMISSION_CLAIM -> {
                properRoute(CEANNA_AOTV, CEANNA_ETHERWARPp, CEANNA_ETHERWARPLESS, fly)
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