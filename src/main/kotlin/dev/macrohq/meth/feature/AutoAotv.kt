package dev.macrohq.meth.feature

import dev.macrohq.meth.feature.helper.RouteNode
import dev.macrohq.meth.feature.helper.TransportMethod
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoAotv {
  private var failed = false
  private var enabled = false
  private var succeeded = false
  private var state = State.STARTING
  private var timer = Timer(0)
  private var currentNodeIndex = 0
  private var nextNode: RouteNode? = null
  private var timeLimit = Timer(0)
  private var route = mutableListOf<RouteNode>()

  enum class State {
    STARTING, FINDING_NEXT_BLOCK, LOOKING_AT_NEXT_BLOCK, LOOK_VERIFY, AOTV_OR_ETHERWARP, AOTV_OR_ETHERWARP_VERIFY, WALK, WALK_VERIFY, END_VERIFY
  }

  fun enable(route: List<RouteNode>) {
    this.enabled = true
    this.failed = false
    this.nextNode = null
    this.succeeded = false
    this.timer = Timer(0)
    this.state = State.STARTING
    this.route = route.toMutableList()
    this.timeLimit = Timer(config.autoAotvTimeLimit)

    log("Enabling Auto Aotv Macro")
  }

  fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.nextNode = null
    this.currentNodeIndex = 0
    this.state = State.STARTING
    this.timer = Timer(0)
    this.timeLimit = Timer(0)
    KeyBindUtil.releaseSneak()

    log("Disabling Auto Aotv Macro")
  }

  private fun getTime(node: RouteNode): Int {
    var time = config.autoAotvFlyLookTime
    if(node.transportMethod == TransportMethod.ETHERWARP && player.onGround){
      time = config.autoAotvEtherwarpLookTime
    }
    return time
  }

  private fun setFailed(failed: Boolean = true) {
    // Failed = true == Task Failed
    // Failed = False == Task Succeeded
    this.failed = failed
    this.succeeded = !failed
  }

  fun succeeded() = !enabled && succeeded
  fun failed() = !enabled && failed

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.enabled) return
  println("inOnTick")

    if (this.timeLimit.isDone) {
      println("TimeLimit")
      log("[AutoAotv] - TimeLimit Ended")
      this.disable();
      this.setFailed()
      return
    }

    when (this.state) {

      State.STARTING -> {
        this.state = State.FINDING_NEXT_BLOCK
        // Should Check if HotBar Swap Macro Check is Enabled or not and then swap
        InventoryUtil.holdItem(CommUtil.getAOT())
      }

      State.FINDING_NEXT_BLOCK -> {
        if (this.currentNodeIndex == this.route.size) {
          this.state = State.END_VERIFY
          return
        }

        this.state = State.LOOKING_AT_NEXT_BLOCK
        this.nextNode = this.route[currentNodeIndex]
        this.currentNodeIndex++

        if(this.nextNode!!.transportMethod == TransportMethod.WALK){
          this.state = State.WALK
        }

        log("[AutoAotv] - Finding Next Block")
      }

      State.LOOKING_AT_NEXT_BLOCK -> {
        this.state = State.LOOK_VERIFY
        val time = this.getTime(this.nextNode!!)
        RotationUtil.lock(this.nextNode!!.block, time, true)

        log("[AutoAotv] - Looking at Next Block. Time: $time")
      }

      State.LOOK_VERIFY -> {
        if (!AngleUtil.angleDifference(this.nextNode!!.block, 1f, 1f)) return
        this.state = State.AOTV_OR_ETHERWARP

        val transportMethod = this.nextNode!!.transportMethod

        when (transportMethod) {
          TransportMethod.FLY -> {
            this.state = State.AOTV_OR_ETHERWARP
          }

          TransportMethod.ETHERWARP -> {
            KeyBindUtil.holdSneak()
            this.state = State.AOTV_OR_ETHERWARP
            this.timer = Timer(config.autoAotvEtherwarpSneakTime)
          }

          TransportMethod.WALK -> {
            this.state = State.WALK
          }
        }

        log("[AutoAotv] - Verifying if looking at next block.")
      }

      State.AOTV_OR_ETHERWARP -> {
        if (!this.timer.isDone) return

        KeyBindUtil.rightClick()
        RotationUtil.stop()

        this.timer = Timer(config.autoAotvTeleportTimeLimit)
        this.state = State.AOTV_OR_ETHERWARP_VERIFY

        log("[AutoAotv] - Using Aotv.")
      }

      State.AOTV_OR_ETHERWARP_VERIFY -> {
        if (this.timer.isDone) {
          this.setFailed()
          println("Verify")
          this.disable();
          println("Disable Called From AOTV_OR_ETHERWARP_VERIFY")
          return
        }

        val playerDistanceTraveledFromLastTick = player.distanceToBlock(player.lastTickPositionCeil())
        val playerStandingOnNextNode = player.getStandingOnFloor() == this.nextNode!!.block
        val playerDistanceToNextBlock = player.distanceToBlock(nextNode!!.block)
        val shouldCrashIntoNextBlock = !world.isAirBlock(this.nextNode!!.block)
            && this.nextNode!!.transportMethod == TransportMethod.FLY

        if (playerDistanceTraveledFromLastTick < 4 && !playerStandingOnNextNode && playerDistanceToNextBlock > 3) return

        this.timer = Timer(0)
        KeyBindUtil.releaseSneak()

        if (playerDistanceToNextBlock > 7 || (shouldCrashIntoNextBlock && !player.onGround)) {
          this.state = State.LOOKING_AT_NEXT_BLOCK
        } else {
          this.state = State.FINDING_NEXT_BLOCK
        }

        log("[AutoAotv] - Verifying Aotv.")
      }

      State.WALK -> {
        if (!(player.onGround && timer.isDone)) return

        PathingUtil.goto(this.nextNode!!.block)
        this.state = State.WALK_VERIFY
        this.timer = Timer(config.autoAotvWalkTimeLimit)

        log("[AutoAotv] - Walking to end.")
      }

      State.WALK_VERIFY -> {
        if (PathingUtil.hasFailed || this.timer.isDone) {
          PathingUtil.stop(); RotationUtil.stop()
          this.setFailed()
          println("Walk Failed")
          this.disable()
          return
        }

        val playerDistanceToNextNode = player.distanceToBlock(this.nextNode!!.block)

        if (playerDistanceToNextNode < 1 && player.onGround || PathingUtil.isDone) {
          PathingUtil.stop(); RotationUtil.stop()
          this.state = State.FINDING_NEXT_BLOCK

          log("[AutoAotv] - Done Walking.")
        }
      }

      State.END_VERIFY -> {
        if (this.timer.isDone) {
          this.setFailed()
          println("End Verify Failed")
          this.disable()
          return
        }

        log("[AutoAotv] - Verifying final block.")

        val playerDistanceToNextNode = player.distanceToBlock(this.nextNode!!.block)
        val playerStandingOnFinalNode = player.getStandingOnFloor() == this.route.last().block

        if (playerDistanceToNextNode > 1 && !playerStandingOnFinalNode) return

        this.setFailed(false)
        this.disable()

        log("[AutoAotv] - Done Aotv'ing")

      }
    }
  }
}
