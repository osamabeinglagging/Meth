package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoAotv {
  private var failed = false
  private var enabled = false
  private var succeeded = false
  private var state = State.STARTING
  private var timer = Timer(0)
  private var nextBlock: BlockPos? = null
  private var timeLimit = Timer(0)
  private var route = mutableListOf<BlockPos>()

  enum class State {
    STARTING, FINDING_NEXT_BLOCK, LOOKING_AT_NEXT_BLOCK, LOOK_VERIFY, USING_AOTV, AOTV_VERIFY, WALK_TO_END, FINAL_BLOCK_VERIFY
  }

  fun enable(route: List<BlockPos>) {
    this.enabled = true
    this.failed = false
    this.nextBlock = null
    this.succeeded = false
    this.state = State.STARTING
    this.timer = Timer(0)
    this.route = route.toMutableList()
    this.timeLimit = Timer(config.autoAotvTimeLimit)

    log("Enabling Auto Aotv Macro")
//    info("Enabling Auto Aotv Macro.")
  }

  fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.nextBlock = null
    this.state = State.STARTING
    this.timer = Timer(0)
    this.timeLimit = Timer(0)

    log("Disabling Auto Aotv Macro")
//    info("Disabling Auto Aotv Macro.")
  }

  fun succeeded() = !enabled && succeeded
  fun failed() = !enabled && failed

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.enabled) return

    if (this.timeLimit.isDone) {
      this.disable();
      this.succeeded = false; this.failed = true
      return
    }

    when (this.state) {
      State.STARTING -> {
        InventoryUtil.holdItem(CommUtil.getAOT())
        this.state = State.FINDING_NEXT_BLOCK
      }

      State.FINDING_NEXT_BLOCK -> {
        this.nextBlock = if (this.nextBlock != null) this.route[this.route.indexOf(nextBlock) + 1] else this.route[0]
        this.state = State.LOOKING_AT_NEXT_BLOCK

        if (this.nextBlock == this.route.last() && config.commTransport == 0) {
          this.state = State.WALK_TO_END
          this.timer = Timer(200)
        }

        log("AutoAotv - Finding Next Block")
      }

      State.LOOKING_AT_NEXT_BLOCK -> {
        val time = if (player.onGround && !world.isAirBlock(this.nextBlock!!)) config.autoAotvEtherwarpLookTime
        else config.autoAotvFlyLookTime
        RotationUtil.lock(this.nextBlock!!, time, true)
        this.state = State.LOOK_VERIFY

        log("AutoAotv - Looking at Next Block. Time: ${time}")
      }

      State.LOOK_VERIFY -> {
        if (!AngleUtil.angleDifference(this.nextBlock!!, 1f, 1f)) return
        this.state = State.USING_AOTV

        if (!world.isAirBlock(this.nextBlock) && config.commTransport != 0) {
          this.timer = Timer(config.autoAotvEtherwarpSneakTime)
          KeyBindUtil.holdSneak()
        }

        log("AutoAotv - Verifying if looking at next block.")
      }

      State.USING_AOTV -> {
        if (!this.timer.isDone) return

        KeyBindUtil.rightClick()
        RotationUtil.stop()

        this.timer = Timer(config.autoAotvTeleportTimeLimit)
        this.state = State.AOTV_VERIFY

        log("AutoAotv - Using Aotv.")
      }

      State.AOTV_VERIFY -> {
        if (timer.isDone) {
          this.succeeded = false; this.failed = true
          this.disable(); return
        }

        if (player.distanceToBlock(player.lastTickPositionCeil()) > 4
          || player.getStandingOnFloor() == this.nextBlock!!
          || player.distanceToBlock(nextBlock!!) < 3
        ) {
          this.timer = Timer(0)
          KeyBindUtil.releaseSneak()

          val dist = if (config.autoAotvAOT == 0) 5 else 7

          this.state = if (player.distanceToBlock(this.nextBlock!!) > dist) {
            State.LOOKING_AT_NEXT_BLOCK
          } else if (this.nextBlock == this.route.last()
            && player.distanceToBlock(this.nextBlock!!) < 1
          ) State.FINAL_BLOCK_VERIFY
          else State.FINDING_NEXT_BLOCK
        }

        log("AutoAotv - Verifying Aotv.")
      }

      State.WALK_TO_END -> {
        if (!(player.onGround && timer.isDone)) return

        PathingUtil.goto(this.nextBlock!!)
        this.timer = Timer(config.autoAotvWalkTimeLimit)
        this.state = State.FINAL_BLOCK_VERIFY

        log("AutoAotv - Walking to end.")
      }

      State.FINAL_BLOCK_VERIFY -> {
        if ((PathingUtil.hasFailed || this.timer.isDone) && config.commTransport == 0) {
          PathingUtil.stop(); RotationUtil.stop()
          this.failed = true; this.succeeded = false
          this.disable(); return
        }

        if ((player.distanceToBlock(this.nextBlock!!) < 1 && player.onGround) ||
          (PathingUtil.isDone && config.commTransport == 0)) {
          PathingUtil.stop(); RotationUtil.stop()
          this.succeeded = true; this.failed = false
          this.disable();

          log("AutoAotv - Done Aotv'ing")
        }

        log("AutoAotv - Verifying final block.")
      }
    }
  }
}