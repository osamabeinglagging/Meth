package dev.macrohq.meth.feature.implementation.failsafe.react

import dev.macrohq.meth.feature.helper.MovementData
import dev.macrohq.meth.feature.helper.Target
import dev.macrohq.meth.feature.implementation.failsafe.Failsafe
import dev.macrohq.meth.feature.implementation.failsafe.check.MacroCheck
import dev.macrohq.meth.util.*

object CheckReact {
  var timer = Timer(0)
  // Player Failsafe
  var playerFailsafeState = PlayerFailsafe.ROTATE_AND_SHIFT

  enum class PlayerFailsafe{
    ROTATE_AND_SHIFT, VERIFY
  }

  fun reactToCheck(){
    when(failsafe.whatCheck){
      Failsafe.Check.PLAYER_CHECK -> {
        playerFailsafe()
      }
      null -> {
        macroHandler.disable()

        MacroCheck.note("Disabled because it's null check")
      }
    }
  }

  // Player Failsafe
  private fun playerFailsafe(){
    when(playerFailsafeState){
      PlayerFailsafe.ROTATE_AND_SHIFT -> {
        autoRotation.easeTo(Target(MacroCheck.playerFailsafeTogglingPlayer!!), 400)
        movementLogger.replay(MovementData.getRandomMovement, 500)

        playerFailsafeState = PlayerFailsafe.VERIFY

        MacroCheck.log("Rotating and Dancing")
      }
      PlayerFailsafe.VERIFY -> {
        if(!movementLogger.succeeded()) return

        autoRotation.disable()
        failsafe.resetFailsafeSession()

        MacroCheck.note("Looked and dance hopefully that worked.")
      }
    }
  }
}