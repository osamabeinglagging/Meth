package dev.macrohq.meth.feature.failsafe.react

import dev.macrohq.meth.feature.MovementData
import dev.macrohq.meth.feature.failsafe.FailsafeNew
import dev.macrohq.meth.feature.failsafe.check.MacroCheck
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import dev.macrohq.meth.util.Logger.note

object CheckReact {
  var timer = Timer(0)
  // Player Failsafe
  var playerFailsafeState =PlayerFailsafe.ROTATE_AND_SHIFT
  enum class PlayerFailsafe{
    ROTATE_AND_SHIFT, VERIFY
  }
  fun reactToCheck(){
    when(failsafe.whatCheck){
      FailsafeNew.Check.PLAYER_CHECK -> {
        this.playerFailsafe()
      }
      null -> {
        macroHandler.disable()

        note("Disabled because it's null check")
      }
    }
  }

  // Player Failsafe
  private fun playerFailsafe(){
    when(this.playerFailsafeState){
      PlayerFailsafe.ROTATE_AND_SHIFT -> {
        RotationUtil.ease(AngleUtil.getAngles(MacroCheck.playerFailsafeTogglingPlayer!!), 400)
        movementLogger.replay(MovementData.getRandomMovement, 500)

        this.playerFailsafeState = PlayerFailsafe.VERIFY

        log("Rotating and Dancing")
      }
      PlayerFailsafe.VERIFY -> {
        if(!movementLogger.succeeded()) return

        RotationUtil.stop()
        failsafe.resetFailsafeSession()

        note("Looked and dance hopefully that worked.")
      }
    }
  }
}