package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.helper.Target
import dev.macrohq.meth.util.*
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoCommission : AbstractFeature() {
  override val featureName = "AutoCommission"
  override val isPassiveFeature = false

  private var claimFailure = 0
  private var state = State.STARTING
  private var timer = Timer(0)
  private var timeLimit = Timer(0)
  private var ceannaPos = Vec3(42.50, 135.70, 22.50)

  companion object {
    private var instance: AutoCommission? = null
    fun getInstance(): AutoCommission {
      if (instance == null) {
        instance = AutoCommission()
      }
      return instance!!
    }
  }

  enum class State {
    STARTING, LOOKING, LOOKING_VERIFY, OPENING_GUI, GUI_VERIFY, CLAIMING_COMM, FINISHING
  }

  fun enable(forceEnable: Boolean = false) {
    this.enabled = true
    this.failed = false
    this.success = false
    this.state = State.STARTING
    this.forceEnable = forceEnable
    this.timer = Timer(0)
    this.timeLimit = Timer(config.autoCommissionTimeLimit)

    log("Auto Commission Claim Enabled")
  }

  override fun disable() {
    if (!this.enabled) return

    this.enabled = false
    this.state = State.STARTING
    this.timer = Timer(0)
    this.timeLimit = Timer(0)

    log("Auto Commission Claim Disabled")
  }

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !this.canEnable()) return

    if (this.timeLimit.isDone || this.claimFailure > 2) {
      if(this.timeLimit.isDone){
        log("TimeLimit Ended.")
      }
      else{
        log("Cannot claim")
      }
      this.disable()
      this.setSucceeded(false)
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

        if (!InventoryUtil.setHotbarSlotForItem(itemToHold)) {
          this.setSucceeded(false)
          this.disable()

          log("Cannot find item to hold.")
          return
        }

        log("Starting Commission Claim")
      }

      State.LOOKING -> {
        autoRotation.easeTo(Target(CommUtil.getCeanna()), config.autoCommissionCeannaLookTime)
        this.state = State.LOOKING_VERIFY

        log("Looking at Ceanna")
      }

      State.LOOKING_VERIFY -> {
        if (!AngleUtil.isWithinAngleThreshold(CommUtil.getCeanna(), 2f, 2f)) return

        autoRotation.disable()
        this.timer = Timer(300)
        this.state = State.OPENING_GUI

        log("Look verify at Ceanna")
      }

      State.OPENING_GUI -> {
        if (!this.timer.isDone) return

        if (config.commUsePigeon) {
          KeyBindUtil.rightClick()
        } else {
          mc.playerController.interactWithEntitySendPacket(player, CommUtil.getCeanna())
        }

        this.state = State.GUI_VERIFY
        this.timer = Timer(config.autoCommissionInGUITime)

        log("Opening GUI")
      }

      State.GUI_VERIFY -> {
        if (mc.currentScreen is GuiChest || player.openContainer is ContainerChest) {
          this.state = State.CLAIMING_COMM
          this.timer = Timer(config.autoCommissionClickTime)
          return
        }

        if (this.timer.isDone) {
          log("Failed to open GUI.")
          this.setSucceeded(false)
          this.disable()
          return
        }

        log("Verifying GUI")
      }

      State.CLAIMING_COMM -> {
        if (!this.timer.isDone) return

        val comm = CommUtil.getCommissionSlot()
        if (comm == -1) {
          log("Could not find Commission");
          this.claimFailure++
          this.timer = Timer(config.autoCommissionInGUITime)
          this.state = State.GUI_VERIFY
          return
        }

        this.claimFailure = 0
        InventoryUtil.clickSlot(comm)
        this.state = State.FINISHING
        this.timer = Timer(config.autoCommissionInGUITime)

        log("Claiming Commission")
      }

      State.FINISHING -> {
        if (!this.timer.isDone) return

        InventoryUtil.closeOpenGUI()
        this.setSucceeded()
        this.disable()

        log("Finishing AutoCommission.")
      }
    }
  }
}