package dev.macrohq.meth.feature.implementation.failsafe.check

import TablistUtil
import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.implementation.failsafe.Failsafe
import dev.macrohq.meth.feature.implementation.failsafe.react.CheckReact
import dev.macrohq.meth.util.*
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity

object MacroCheck: AbstractFeature() {
  override val featureName: String = "Feature"
  override val isPassiveFeature: Boolean = true
  override fun disable() {}

  var timer = Timer(0)

  // Player Failsafe
  var playerFailsafeToggleCount = 0
  var playerFailsafeTogglingPlayer: Entity? = null
  var playerFailsafeCheckState = PlayerCheck.LOOKING_FOR_PLAYER

  enum class PlayerCheck {
    LOOKING_FOR_PLAYER, VERIFYING_PLAYER_CHECK
  }

  fun checkForChecks() {
    checkForPlayers()
  }

  private fun checkForPlayers() {
    if (!macroHandler.activeMacro.subMacroActive) return

    when (playerFailsafeCheckState) {
      PlayerCheck.LOOKING_FOR_PLAYER -> {
        val playerBB = player.entityBoundingBox.expand(3.0, 3.0, 3.0)

        playerFailsafeTogglingPlayer = world.getEntitiesInAABBexcluding(
          player, playerBB
        ) { entity ->
          entity!!.canBeCollidedWith() && entity is EntityOtherPlayerMP
                  && TablistUtil.getAllPlayers().any { it.contains(entity.name) }
        }
          .firstOrNull { player.getDistanceToEntity(it) < 1.5 }

        if (playerFailsafeTogglingPlayer != null) {
          playerFailsafeCheckState = PlayerCheck.VERIFYING_PLAYER_CHECK
          timer = Timer(3000)

          log("Annoying Player Found. name: ${playerFailsafeTogglingPlayer!!.name}")
        }
      }

      PlayerCheck.VERIFYING_PLAYER_CHECK -> {
        log("Verifying Player.")
        if (!timer.isDone) return

        if (player.getDistanceToEntity(playerFailsafeTogglingPlayer) < 1.5) {
          playerFailsafeToggleCount++
          if (playerFailsafeToggleCount > 3) {
            BrokenCheck.activateBroken(Failsafe.Broken.WARP_OUT, time = 500)
            playerFailsafeToggleCount = 0

            note("Player wants to be benc1ark. Warping out.")
            return
          }

          activateCheck(Failsafe.Check.PLAYER_CHECK, time = 1000)

          note("Enabling Player Failsafe")
          return
        }

        failsafe.resetFailsafeSession()
        log("Annoying Player Left.")
      }
    }
  }

  fun activateCheck(broken: Failsafe.Check, allowance: Boolean = false, time: Int) {
    failsafe.gotChecked = true
    CheckReact.timer = Timer(time)
    failsafe.whatCheck = broken
    failsafe.somethingBroken = false
    failsafe.failsafeAllowance = allowance
  }
}