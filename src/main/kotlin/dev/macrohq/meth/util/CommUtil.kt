package dev.macrohq.meth.util

import TablistUtil
import dev.macrohq.meth.macro.macros.CommissionMacro
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import kotlin.math.abs

object CommUtil {
  fun getTool(): String {
    return when (config.commTool) {
      0 -> "Gemstone Gauntlet"
      1 -> "Titanium Drill"
      2 -> "Pickonimbus"
      else -> ""
    }
  }

  fun getWeapon(): String {
    return when (config.mobKillerWeapon) {
      0 -> "Frozen Scythe"
      1 -> "Aurora Staff"
      2 -> "Juju Shortbow"
      3 -> "Terminator"
      else -> "Hello, Im Underwater, Please help me wowoowowowow"
    }
  }

  fun getCeanna(): Entity {
    var entPos = Vec3(42.50, 134.50, 22.50)
    val ceanna = world.loadedEntityList.find { it.name.contains("Ceanana") }
    if (ceanna != null) entPos = ceanna.positionVector
    return world.loadedEntityList.filterIsInstance<EntityOtherPlayerMP>()
      .minBy { it.positionVector.distanceTo(entPos) }
  }

  fun getCommission(): CommissionMacro.CommissionType? {
    var commissionName: CommissionMacro.CommissionType? = null
    for (comm in CommissionMacro.CommissionType.entries) {
      for (text in TablistUtil.getTabList().reversed()) {
        if (text.contains(comm.commName) && !text.contains("Golden") && !text.contains("Raid")) {
          commissionName = comm
          if (text.contains("DONE")) {
            commissionName = CommissionMacro.CommissionType.COMMISSION_CLAIM
          }
        }
      }
      if (commissionName != null) break
    }
    return commissionName
  }

  fun getCommissionMob() = if (getCommission() == CommissionMacro.CommissionType.GOBLIN_SLAYER) {
    getAllGoblins()
  } else getAllIceWalkers()

  fun getAllIceWalkers(): List<Entity> {
    return world.loadedEntityList.filterIsInstance<EntityOtherPlayerMP>().filter {
      player.canEntityBeSeen(it) && it.name == "Ice Walker" && player.getDistanceToEntity(it) < 64
    }.sortedBy {
      player.getDistanceToEntity(it) + abs(AngleUtil.getYawChange(it.positionVector)) + abs(
        AngleUtil.getPitchChange(it.positionVector)
      )
    }
  }

  fun getAllGoblins(): List<EntityOtherPlayerMP> {
    return world.loadedEntityList.filterIsInstance<EntityOtherPlayerMP>().filter {
      player.canEntityBeSeen(it) && it.name.contains("Goblin") && player.getDistanceToEntity(it) < 30
    }.sortedBy {
      player.getDistanceToEntity(it) + abs(AngleUtil.getYawChange(it.positionVector)) + abs(
        AngleUtil.getPitchChange(it.positionVector)
      )
    }
  }

  fun getAOT(): String { // aspect of the
    return when (config.autoAotvAOT) {
      0 -> "Aspect of the End"
      1 -> "Aspect of the Void"
      else -> "hello your comuter has vairasi"
    }
  }
}