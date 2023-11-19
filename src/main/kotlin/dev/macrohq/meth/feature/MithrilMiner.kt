package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import dev.macrohq.meth.util.Logger.note
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class MithrilMiner {
  private var failed = false
  private var enabled = false
  private var miningSpeed = 0
  private var miningTicks = 0
  private var noMithrilTimer = 0
  private var miningSpeedBoost = 0
  private var titaniumPriority = false
  private var isSpeedBoostActive = false
  private var isSpeedBoostAvailable = true
  private var pointOnBlock: Vec3? = null
  private var currentBlock: BlockPos? = null
  private var timer = Timer(0)
  private var state: State = State.STARTING

  enum class State {
    STARTING, GET_SPEED_AND_BOOST, SPEED_AND_BOOST_VERIFY, HANDLE_MSB, CHECKING, LOOKING, LOOKING_VERIFY, BREAKING, BREAKING_VERIFY
  }

  fun enable(miningSpeed: Int = 0, miningSpeedBoost: Int = 0, titaniumPriority: Boolean = false) {
    this.enabled = true
    this.failed = false
    this.miningSpeed = miningSpeed
    this.miningSpeedBoost = miningSpeedBoost
    this.titaniumPriority = titaniumPriority

    log("Mithril Miner Enabled.")
  }

  fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.miningSpeed = 0
    this.miningSpeedBoost = 0
    this.state = State.STARTING
    KeyBindUtil.releaseLeftClick()
    this.titaniumPriority = false
    this.isSpeedBoostActive = false
    this.isSpeedBoostAvailable = true
    this.pointOnBlock = null
    this.currentBlock = null

    log("Mithril Miner Disabled.")
  }

  fun succeeded() = !this.enabled && !this.failed
  fun failed() = !this.enabled && this.failed

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.enabled || !failsafe.failsafeAllowance) return

    when (this.state) {
      State.STARTING -> {
        if (!InventoryUtil.holdItem(CommUtil.getTool())) {
          this.disable(); return
        }
        this.state = State.CHECKING

        if (this.miningSpeed == 0 || this.miningSpeedBoost == 0) {
          this.state = State.GET_SPEED_AND_BOOST
        } else if (this.isSpeedBoostAvailable) {
          this.state = State.HANDLE_MSB
          this.timer = Timer(config.mithrilMinerMSBWaitTime)
          KeyBindUtil.releaseLeftClick()
        }
      }

      State.GET_SPEED_AND_BOOST -> {
        autoInventory.getSpeedAndBoost()
        this.state = State.SPEED_AND_BOOST_VERIFY

        log("MithrilMiner - Getting speed and boost")
      }

      State.SPEED_AND_BOOST_VERIFY -> {
        if (autoInventory.failed()) {
          this.failed = true
          this.disable()
          return
        }

        if (autoInventory.succeeded()) {
          val (miningSpeed, miningSpeedBoost) = autoInventory.getSpeedBoost()
          this.miningSpeed = miningSpeed
          this.miningSpeedBoost = miningSpeedBoost
          state = State.STARTING
        }
      }

      State.HANDLE_MSB -> {
        if (!this.timer.isDone) return

        KeyBindUtil.rightClick()
        this.state = State.STARTING
        this.isSpeedBoostAvailable = false
      }

      State.CHECKING -> {
        val availableMithrilBlocks = BlockUtil.validMithril(this.titaniumPriority)
        if (this.currentBlock != null) availableMithrilBlocks.remove(this.currentBlock)

        if (availableMithrilBlocks.size < 6) {
          this.noMithrilTimer++
          if (noMithrilTimer > 200) {
            log("MithrilMiner - Cannot find Mithril Blocks.")
            this.failed = true
            this.disable()
          }
          if (availableMithrilBlocks.isEmpty()) return
        }

        this.currentBlock = availableMithrilBlocks[0]

        if (availableMithrilBlocks.size > 6) {
          this.noMithrilTimer = 0
        }
        RenderUtil.markers.clear()
        RenderUtil.markers.add(currentBlock!!)
        this.state = State.LOOKING
      }

      State.LOOKING -> {
        this.pointOnBlock = BlockUtil.bestPointOnBlock(this.currentBlock!!)
        RotationUtil.ease(AngleUtil.getAngles(this.pointOnBlock!!), config.mithrilMinerLookTime)

        this.timer = Timer(config.mithrilMinerLookTimeLimit)
        this.state = State.LOOKING_VERIFY
      }

      State.LOOKING_VERIFY -> {
        if (this.timer.isDone || !BlockUtil.isBlockAllowedMithril(this.currentBlock!!)) {
          this.state = State.STARTING
          return
        }
        if (AngleUtil.angleDifference(this.pointOnBlock!!, 1f, 1f)
          || this.currentBlock == RaytracingUtil.getBlockLookingAt(10f)
        ) {
          this.timer = Timer(0)
          this.state = State.BREAKING

          log("MithrilMiner - Look Done")
        }
      }

      State.BREAKING -> {
        val speed = if (this.isSpeedBoostActive) this.miningSpeed * (this.miningSpeedBoost / 100) else this.miningSpeed
        this.miningTicks = BlockUtil.getBreakTicks(currentBlock!!, speed)
        KeyBindUtil.holdLeftClick()
        this.state = State.BREAKING_VERIFY

        log("MithrilMiner - MiningTicks: ${this.miningTicks}")
      }

      State.BREAKING_VERIFY -> {
        this.miningTicks--
        if (this.miningTicks < 0 ||
          !BlockUtil.isBlockAllowedMithril(currentBlock!!) ||
          currentBlock != RaytracingUtil.getBlockLookingAt(5f)
        ) {
          timer = Timer(0)
          this.state = State.STARTING
        }

        log("MithrilMiner - Breaking Block Verify.")
      }
    }
  }

  @SubscribeEvent
  fun onChatReceive(event: ClientChatReceivedEvent) {
    if (event.type.toInt() != 0) return
    val msg = event.message.unformattedText
    if (msg.contains("Mining Speed Boost is now available!")) {
      note("Boost Available")
      this.isSpeedBoostAvailable = true
    }
    if (msg.contains("You used your Mining Speed Boost Pickaxe Ability!")) {
      note("Boost Active")
      this.isSpeedBoostActive = true
      this.isSpeedBoostAvailable = false
    }
    if (msg.contains("Your Mining Speed Boost has expired!") || (!this.isSpeedBoostActive && msg.contains("This ability is on cooldown for"))) {
      note("Boost Ended or not Available!")
      this.isSpeedBoostActive = false
      this.isSpeedBoostAvailable = false
    }
  }
}