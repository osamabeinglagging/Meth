package dev.macrohq.meth.feature.failsafe.check

import TablistUtil
import dev.macrohq.meth.feature.failsafe.FailsafeNew
import dev.macrohq.meth.feature.failsafe.react.CheckReact
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import dev.macrohq.meth.util.Logger.note
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity

object MacroCheck {
  var timer = Timer(0)

  // Player Failsafe
  var playerFailsafeToggleCount = 0
  var playerFailsafeTogglingPlayer: Entity? = null
  var playerFailsafeCheckState = PlayerCheck.LOOKING_FOR_PLAYER

  enum class PlayerCheck {
    LOOKING_FOR_PLAYER, VERIFYING_PLAYER_CHECK
  }

  fun checkForChecks() {
    this.checkForPlayers()
  }

  private fun checkForPlayers() {
    if (!macroHandler.activeMacro.subMacroActive) return

    when (this.playerFailsafeCheckState) {
      PlayerCheck.LOOKING_FOR_PLAYER -> {
//        log("Failsafe - Looking for Player")
        val playerBB = player.entityBoundingBox.expand(3.0, 3.0, 3.0)

        playerFailsafeTogglingPlayer = world.getEntitiesInAABBexcluding(
          player, playerBB
        ) { entity ->
          entity!!.canBeCollidedWith() && entity is EntityOtherPlayerMP
                  && TablistUtil.getAllPlayers().any { it.contains(entity.name) }
        }
          .firstOrNull { player.getDistanceToEntity(it) < 1.5 }

        if (this.playerFailsafeTogglingPlayer != null) {
          this.playerFailsafeCheckState = PlayerCheck.VERIFYING_PLAYER_CHECK
          this.timer = Timer(3000)

          log("Annoying Player Found. name: ${playerFailsafeTogglingPlayer!!.name}")
        }
      }

      PlayerCheck.VERIFYING_PLAYER_CHECK -> {
        log("Verifying Player.")
        if (!this.timer.isDone) return

        if (player.getDistanceToEntity(this.playerFailsafeTogglingPlayer) < 1.5) {
          this.playerFailsafeToggleCount++
          if (this.playerFailsafeToggleCount > 3) {
            BrokenCheck.activateBroken(FailsafeNew.Broken.WARP_OUT, time = 500)
            this.playerFailsafeToggleCount = 0

            note("Player wants to be benc1ark. Warping out.")
            return
          }

          this.activateCheck(FailsafeNew.Check.PLAYER_CHECK, time = 1000)

          note("Enabling Player Failsafe")
          return
        }

        failsafe.resetFailsafeSession()
        log("Annoying Player Left.")
      }
    }
  }

  fun activateCheck(broken: FailsafeNew.Check, allowance: Boolean = false, time: Int) {
    failsafe.gotChecked = true
    CheckReact.timer = Timer(time)
    failsafe.whatCheck = broken
    failsafe.somethingBroken = false
    failsafe.failsafeAllowance = allowance
  }
}