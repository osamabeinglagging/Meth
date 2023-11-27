package dev.macrohq.meth.feature

import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.error
import dev.macrohq.meth.util.Logger.log
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoCommission {
  private var failed = false
  private var enabled = false
  private var succeeded = false
  private var claimFailure = 0
  private var state = State.STARTING
  private var timer = Timer(0)
  private var timeLimit = Timer(0)
  private var ceannaPos = Vec3(42.50, 135.70, 22.50)

  enum class State {
    STARTING, LOOKING, LOOKING_VERIFY, OPENING_GUI, GUI_VERIFY, CLAIMING_COMM, FINISHING
  }

  fun enable() {
    this.enabled = true
    this.failed = false
    this.succeeded = false
    this.state = State.STARTING
    this.timer = Timer(0)
    this.timeLimit = Timer(config.autoCommissionTimeLimit)

    log("Auto Commission Claim Enabled")
  }

  fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.state = State.STARTING
    this.timer = Timer(0)
    this.timeLimit = Timer(0)

    log("Auto Commission Claim Disabled")
  }

  private fun setFailed(failed: Boolean = false) {
    this.failed = failed
    this.succeeded = !failed
  }

  fun succeeded() = !enabled && succeeded
  fun failed() = !enabled && failed

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.enabled || !failsafe.failsafeAllowance) return

    if (this.timeLimit.isDone) {
      log("[AutoCommission] - TimeLimit Ended.")
      this.disable()
      this.setFailed()
      return
    }

    if (this.claimFailure > 2) {
      error("[AutoCommission] - Failed to Find Commission.")
      this.disable()
      return
    }

    when (this.state) {
      State.STARTING -> {
        this.state = State.LOOKING
        var itemToHold = CommUtil.getTool()

        if (config.commUsePigeon) {
          this.timer = Timer(300)
          this.state = State.OPENING_GUI
          itemToHold = "Royal Pigeon"
        }

        if(!InventoryUtil.holdItem(itemToHold)){
          this.setFailed()
          this.disable()

          log("[AutoCommission] - Cannot find item to hold.")
          return
        }

        log("[AutoCommission] - Starting AutoCommission Claim")
      }

      State.LOOKING -> {
        RotationUtil.lock(CommUtil.getCeanna(), config.autoCommissionCeannaLookTime, true)
        this.state = State.LOOKING_VERIFY

        log("AutoCommission - Looking at Ceanna")
      }

      State.LOOKING_VERIFY -> {
        if (!AngleUtil.angleDifference(CommUtil.getCeanna(), 2f, 2f)) return

        RotationUtil.stop()
        this.timer = Timer(300)
        this.state = State.OPENING_GUI

        log("AutoCommission - Look verify at Ceanna")
      }

      State.OPENING_GUI -> {
        if (!timer.isDone) return

        if (config.commUsePigeon) {
          KeyBindUtil.rightClick()
        } else {
          mc.playerController.interactWithEntitySendPacket(player, CommUtil.getCeanna())
        }

        this.state = State.GUI_VERIFY
        this.timer = Timer(config.autoCommissionInGUITime)

        log("AutoCommission - Opening GUI")
      }

      State.GUI_VERIFY -> {
        if (mc.currentScreen is GuiChest && player.openContainer is ContainerChest) {
          this.state = State.CLAIMING_COMM
          this.timer = Timer(config.autoCommissionClickTime)
          return
        }

        if (this.timer.isDone) {
          log("AutoCommission - Failed to open GUI.")
          this.succeeded = false; this.failed = true;
          this.disable(); return
        }

        log("AutoCommission - Verifying GUI")
      }

      State.CLAIMING_COMM -> {
        if (!this.timer.isDone) return

        val comm = InventoryUtil.getCommissionItemSlot()
        if (comm == -1) {
          log("AutoCommission - Could not find Commission");
          this.claimFailure++
          this.timer = Timer(config.autoCommissionInGUITime)
          this.state = State.GUI_VERIFY
          return
        }

        this.claimFailure = 0
        InventoryUtil.clickSlot(comm)
        this.state = State.FINISHING
        this.timer = Timer(config.autoCommissionInGUITime)

        log("AutoCommission - Claiming Commission")
      }

      State.FINISHING -> {
        if (!this.timer.isDone) return

        InventoryUtil.closeGUI()
        this.setFailed(false)
        this.disable()

        log("AutoCommission - Finishing AutoCommission.")
      }
    }
  }
}