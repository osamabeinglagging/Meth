package dev.macrohq.meth.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.meth.pathfinding.AStarPathfinder
import net.minecraft.util.BlockPos

object PathingUtil {
  val isDone get() = !pathExec.enabled
  var hasFailed = false
    private set

  fun goto(pos: BlockPos, sneak: Boolean = false, forceEnable: Boolean = false) {
//    println("GOGOTOOT")
    hasFailed = false
    runAsync {
//      println("GENENNENE")
      val path = AStarPathfinder(player.getStandingOnCeil(), pos).findPath(1000)
      if (path.isEmpty()) {
        hasFailed = true
        Logger.log("Could not find path!!")
      } else {
        pathExec.enable(path, sneak)
      }
    }
  }

  fun stop() {
    hasFailed = false
    pathExec.disable()
  }
}