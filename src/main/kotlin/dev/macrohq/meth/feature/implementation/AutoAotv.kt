package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.helper.RouteNode
import dev.macrohq.meth.feature.helper.Target
import dev.macrohq.meth.feature.helper.TransportMethod
import dev.macrohq.meth.util.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.max

class AutoAotv : AbstractFeature() {
  override val featureName = "AutoAotv"
  override val isPassiveFeature = false

  private var state = State.STARTING
  private var timer = Timer(0)
  private var currentNodeIndex = 0
  private var nextNode: RouteNode? = null
  private var timeLimit = Timer(0)
  private var route = mutableListOf<RouteNode>()

  companion object {
    private var instance: AutoAotv? = null
    fun getInstance(): AutoAotv {
      if (instance == null) {
        instance = AutoAotv()
      }
      return instance!!
    }
  }

  enum class State {
    STARTING, FINDING_NEXT_BLOCK, LOOKING_AT_NEXT_BLOCK, LOOK_VERIFY, AOTV_OR_ETHERWARP, AOTV_OR_ETHERWARP_VERIFY, WALK, WALK_VERIFY, END_VERIFY
  }

  fun enable(route: List<RouteNode>, forceEnable: Boolean = false) {
    if(route.isEmpty()){
      log("Not enabling aotv due to route being empty.")
      this.setSucceeded(false)
      return
    }

    this.enabled = true
    this.failed = false
    this.nextNode = null
    this.success = false
    this.timer = Timer(0)
    this.state = State.STARTING
    this.forceEnable = forceEnable
    this.route = route.toMutableList()
    this.timeLimit = Timer(config.autoAotvTimeLimit)

    log("Enabling Auto Aotv Macro")
  }

  override fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.nextNode = null
    this.forceEnable = false
    this.currentNodeIndex = 0
    this.state = State.STARTING
    this.timer = Timer(0)
    this.timeLimit = Timer(0)
    KeyBindUtil.releaseSneak()

    log("Disabling Auto Aotv Macro")
  }

  private fun getRotationTime(node: RouteNode, nodeIndex: Int): Int {
    var time = config.autoAotvFlyLookTime
    val previousNode = this.route[max(nodeIndex - 1, 0)]
    log("NextTransport: ${node.transportMethod}")
    log("prevnodetransport: ${previousNode.transportMethod}")
    if (node.transportMethod == TransportMethod.ETHERWARP && previousNode.transportMethod == TransportMethod.ETHERWARP) {
      time = config.autoAotvEtherwarpLookTime
    }
    return time
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.canEnable()) return

    if (this.timeLimit.isDone) {
      log("Aotv Didn't finish Before Set Time.")
      this.disable()
      this.setSucceeded(false)
      return
    }

    when (this.state) {

      State.STARTING -> {
        this.state = State.FINDING_NEXT_BLOCK
        // Should Check if HotBar Swap Macro Check is Enabled or not and then swap
        InventoryUtil.setHotbarSlotForItem(CommUtil.getAOT())
      }

      State.FINDING_NEXT_BLOCK -> {
        if (this.currentNodeIndex == this.route.size) {
          this.state = State.END_VERIFY
          this.timer = Timer(250)
          return
        }

        this.state = State.LOOKING_AT_NEXT_BLOCK
        this.nextNode = this.route[currentNodeIndex]
        this.currentNodeIndex++

        if (this.nextNode!!.transportMethod == TransportMethod.WALK || this.nextNode!!.transportMethod == TransportMethod.SNEAK_WALK) {
          this.state = State.WALK
        }

        log("Finding Next Block")
      }

      State.LOOKING_AT_NEXT_BLOCK -> {
        this.state = State.LOOK_VERIFY
        val time = this.getRotationTime(this.nextNode!!, this.currentNodeIndex-1)
        autoRotation.easeTo(Target(this.nextNode!!.block), time, lockType = LockType.SMOOTH)

        log("Looking at Next Block. Time: $time")
      }

      State.LOOK_VERIFY -> {
        if (!AngleUtil.isWithinAngleThreshold(this.nextNode!!.block, 1f, 1f)) return
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

          TransportMethod.WALK, TransportMethod.SNEAK_WALK -> {
            this.state = State.WALK
          }
        }

        log("Verifying if looking at next block.")
      }

      State.AOTV_OR_ETHERWARP -> {
        if (!this.timer.isDone) return

        KeyBindUtil.rightClick()
        autoRotation.disable()

        this.timer = Timer(config.autoAotvTeleportTimeLimit)
        this.state = State.AOTV_OR_ETHERWARP_VERIFY

        log("Using Aotv.")
      }

      State.AOTV_OR_ETHERWARP_VERIFY -> {
        if (this.timer.isDone) {
          this.setSucceeded(false)
          this.disable();
          return
        }

        val playerDistanceTraveledFromLastTick = player.distanceToBlock(player.lastTickPositionCeil())
        val isPlayerStandingOnNextNode = player.getStandingOnFloor() == this.nextNode!!.block
        val playerDistanceToNextBlock = player.distanceToBlock(nextNode!!.block)
        val shouldCrashIntoNextBlock = !world.isAirBlock(this.nextNode!!.block)
            && this.nextNode!!.transportMethod == TransportMethod.FLY && player.distanceToBlock(this.nextNode!!.block) > 2

        if (playerDistanceTraveledFromLastTick < 4 && !isPlayerStandingOnNextNode && playerDistanceToNextBlock > 3) return

        this.timer = Timer(0)
        KeyBindUtil.releaseSneak()

        if (playerDistanceToNextBlock > 7 || (shouldCrashIntoNextBlock && !player.onGround)) {
          this.state = State.LOOKING_AT_NEXT_BLOCK
        } else {
          this.state = State.FINDING_NEXT_BLOCK
        }

        log("Verifying Aotv.")
      }

      State.WALK -> {
        if (!(player.onGround && timer.isDone)) return

        PathingUtil.goto(this.nextNode!!.block, this.nextNode!!.transportMethod == TransportMethod.SNEAK_WALK)
        this.state = State.WALK_VERIFY
        this.timer = Timer(config.autoAotvWalkTimeLimit)

        log("Walking to end.")
      }

      State.WALK_VERIFY -> {
        if (PathingUtil.hasFailed || this.timer.isDone) {
          PathingUtil.stop()
          autoRotation.disable()
          this.setSucceeded(false)
          this.disable()

          log("Walk Failed")
          return
        }

        val playerDistanceToNextNode = player.distanceToBlock(this.nextNode!!.block)

        if ((playerDistanceToNextNode < .5 && player.onGround) || PathingUtil.isDone) {
          PathingUtil.stop()
          autoRotation.disable()
          this.timer = Timer(0)
          this.state = State.FINDING_NEXT_BLOCK

          log("Done Walking.")
        }
      }

      State.END_VERIFY -> {
        if (this.timer.isDone) {
          this.setSucceeded(false)
          this.disable()
          log("End Verify Failed")
          return
        }

        log("Verifying final block.")

        val playerDistanceToNextNode = player.distanceToBlock(this.nextNode!!.block)
        val playerStandingOnFinalNode = player.getStandingOnFloor() == this.route.last().block

        if (playerDistanceToNextNode > 1 && !playerStandingOnFinalNode) return

        this.setSucceeded()
        this.disable()

        log("Done Aotv'ing")
      }
    }
  }
}
