package dev.macrohq.meth.macro.macros

import dev.macrohq.meth.feature.implementation.LocationTracker
import dev.macrohq.meth.macro.AbstractMacro
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.info
import dev.macrohq.meth.util.Logger.log
import dev.macrohq.meth.util.Logger.note
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class CommissionMacro : AbstractMacro() {
  override var macroName = "Commission Macro"

  var enabled = false
  var commissionFinished = 0
  private var aotvFailure = 0
  private var miningSpeed = 0
  private var miningSpeedBoost = 0
  private var state = State.STARTING
  private var subMacroFailure = 0
  private var timer = Timer(0)
  private var currentCommission = CommissionType.COMMISSION_CLAIM
  override var subMacroActive: Boolean = false
  override val location = Pair(LocationTracker.Island.DWARVEN_MINES, LocationTracker.SubLocation.The_Forge)

  companion object{
    private var instance: CommissionMacro? = null
    fun getInstance(): CommissionMacro{
      if(instance == null) instance = CommissionMacro()
      return instance!!
    }
  }

  enum class CommissionType(val commName: String) {
    MITHRIL_MINER("Mithril Miner"), TITANIUM_MINER("Titanium Miner"), UPPER_MITHRIL("Upper Mines Mithril"), ROYAL_MITHRIL(
      "Royal Mines Mithril"
    ),
    LAVA_MITHRIL("Lava Springs Mithril"), CLIFFSIDE_MITHRIL("Cliffside Veins Mithril"), RAMPARTS_MITHRIL("Rampart's Quarry Mithril"), UPPER_TITANIUM(
      "Upper Mines Titanium"
    ),
    ROYAL_TITANIUM("Royal Mines Titanium"), LAVA_TITANIUM("Lava Springs Titanium"), CLIFFSIDE_TITANIUM("Cliffside Veins Titanium"), RAMPARTS_TITANIUM(
      "Rampart's Quarry Titanium"
    ),
    GOBLIN_SLAYER("Goblin Slayer"), ICE_WALKER_SLAYER("Ice Walker Slayer"), COMMISSION_CLAIM("Claim Commission")
  }

  enum class State {
    STARTING, CHECKING_COMMISSION, GET_SPEED_AND_BOOST, SPEED_AND_BOOST_VERIFY, TRANSPORTING, TRANSPORTING_VERIFY, TOGGLE_MACRO, COMMISSION_CLAIM, COMMISSION_CLAIM_VERIFY, MACRO_VERIFY, DISABLE_MACROS, WARP, WARP_VERIFY
  }

  override fun enable(softEnable: Boolean) {
    info("Enabling Commission Macro")

    this.timer = Timer(0)
    this.enabled = true
    this.state = State.STARTING

    if (softEnable) return

    UnGrabUtil.unGrabMouse()
    this.aotvFailure = 0
    this.miningSpeed = 0
    this.miningSpeedBoost = 0
    this.commissionFinished = 0
    this.subMacroFailure = 0
  }

  override fun disable(softDisable: Boolean) {

    this.timer = Timer(0)
    autoRotation.disable()
    this.disableMacros()
    this.enabled = false
    this.state = State.STARTING
    this.subMacroActive = false

    info("Disabling Commission Macro")

    if (softDisable) return

    UnGrabUtil.grabMouse()
    this.aotvFailure = 0
    this.miningSpeed = 0
    this.miningSpeedBoost = 0
    this.commissionFinished = 0
    this.subMacroFailure = 0
  }

  private fun disableMacros() {
    this.subMacroActive = false

    mithrilMiner.disable()
    mobKiller.disable()
    autoWarp.disable()
    autoAotv.disable()
    autoCommission.disable()
    autoInventory.disable()
  }

  override fun toggle() = if (!enabled) this.enable() else this.disable()

  override fun onTick(event: TickEvent.ClientTickEvent) {
    if (player == null || world == null || !this.enabled || !failsafe.failsafeAllowance) return
    if (this.aotvFailure > 5 || this.subMacroFailure > 2 || (this.aotvFailure > 2 && config.commTransport == 0)) {
      macroHandler.disable()

      note("Failed Aotv/Mithrilminer. Disabling")
      return
    }

    when (this.state) {
      State.STARTING -> {
        this.state = State.WARP

        if (this.miningSpeed == 0 || this.miningSpeedBoost == 0) {
          this.state = State.GET_SPEED_AND_BOOST
        } else if (locationUtil.currentSubLocation == this.location.second) {
          this.state = State.CHECKING_COMMISSION
          log("CommissionMacro - At Forge.")
        }

        log("CommissionMacro - Going to warp.")
      }

      State.GET_SPEED_AND_BOOST -> {
        InventoryUtil.setHotbarSlotForItem(CommUtil.getTool())
        autoInventory.getSpeedAndBoost()
        this.state = State.SPEED_AND_BOOST_VERIFY

        log("CommissionMacro - Getting speed and boost")
      }

      State.SPEED_AND_BOOST_VERIFY -> {
        if (autoInventory.failed()) {
          this.subMacroFailure++
          this.state = State.STARTING
          return
        }

        if (autoInventory.succeeded()) {
          val (miningSpeed, miningSpeedBoost) = autoInventory.getSpeedBoost()
          this.miningSpeed = miningSpeed
          this.miningSpeedBoost = miningSpeedBoost
          this.state = State.STARTING
        }
      }

      State.CHECKING_COMMISSION -> {
        val currentComm = CommUtil.getCommission()
        if (currentComm == null) this.state = State.STARTING
        this.currentCommission = currentComm!!
        this.state = State.TRANSPORTING

        if (config.commUsePigeon && this.currentCommission.commName.contains("Claim Commission")) {
          this.state = State.TOGGLE_MACRO
        }

        log("CommissionMacro - Getting Commission")
      }

      State.TRANSPORTING -> {
        val routeToChoose = if (this.aotvFailure < 3) config.commTransport else 2
//        autoAotv.enable(RouteData.getRoute(this.currentCommission, routeToChoose))
        this.state = State.TRANSPORTING_VERIFY

        log("CommissionMacro - Starting Transport.")
      }

      State.TRANSPORTING_VERIFY -> {
        if (autoAotv.failed()) {
          this.aotvFailure++
          this.state = State.STARTING
          this.timer = Timer(0)

          log("CommissionMacro - Failed to Transport.")
          return
        }
        if (autoAotv.succeeded()) {
          this.aotvFailure = 0
          this.timer = Timer(0)
          this.state = State.TOGGLE_MACRO

          log("CommissionMacro - Successfully Transported.")
        }
      }

      State.TOGGLE_MACRO -> {
        if(player.motionX != 0.0 || player.motionZ != 0.0) return

        if (this.currentCommission.commName.contains("Claim Commission")) {
          autoCommission.enable()
          this.state = State.COMMISSION_CLAIM_VERIFY

          return
        }

        this.subMacroActive = true
        UnGrabUtil.grabMouse()

        if (this.currentCommission.commName.contains("Slayer")) {
          mobKiller.enable()
        } else {
          mithrilMiner.enable(
            this.miningSpeed,
            this.miningSpeedBoost,
            this.currentCommission.commName.contains("Titanium")
          )
        }

        UnGrabUtil.unGrabMouse()
        this.state = State.MACRO_VERIFY
      }

      State.MACRO_VERIFY -> {
        if (mithrilMiner.failed()) {
          this.subMacroFailure++
          this.state = State.STARTING
          this.subMacroActive = false
        }
      }

      State.COMMISSION_CLAIM -> {}

      State.COMMISSION_CLAIM_VERIFY -> {
        if (autoCommission.failed()) {
          this.subMacroFailure++
          this.state = State.STARTING
          this.subMacroActive = false

          note("Cant claim commission.")
          return
        }
        if (autoCommission.succeeded()) {
          this.timer = Timer(0)
          this.commissionFinished++
          this.state = State.STARTING

          note("Successfully Claimed Commission")
        }
      }

      State.DISABLE_MACROS -> {
        if (!timer.isDone) return
        if (this.currentCommission.commName.contains("Slayer")) mobKiller.disable()
        else mithrilMiner.disable()
        this.timer = Timer(config.commWaitAfterCompletion)
        this.state = State.WARP
      }

      State.WARP -> {
        if (!this.timer.isDone) return

        autoWarp.enable(null, LocationTracker.SubLocation.The_Forge)
        this.timer = Timer(15000)
        this.state = State.WARP_VERIFY

        log("CommissionMacro - Starting Warp.")
      }

      State.WARP_VERIFY -> {
        if (!autoWarp.succeeded() && this.timer.isDone) {
          macroHandler.disable()

          log("CommissionMacro - Warp Failed.")
          return
        }

        if (autoWarp.succeeded()) {
          this.state = State.STARTING

          log("CommissionMacro - Warp Successful.")
        }
      }
    }
  }

  override fun onWorldRenderEvent(event: RenderWorldLastEvent) {
  }

  override fun onChat(event: ClientChatReceivedEvent) {
    if (event.type.toInt() != 0 || !enabled) return

    val commText = this.currentCommission.commName + " Commission Complete! Visit the King to claim your rewards!"
    val message = StringUtils.stripControlCodes(event.message.unformattedText)

    if (message.lowercase().contains(commText.lowercase())) {
      this.subMacroFailure = 0
      this.state = State.DISABLE_MACROS
      this.timer = Timer(config.commWaitAfterCompletion)
      this.subMacroActive = false

      log("CommissionMacro - Commission Completed.")
    }
  }

  override fun necessaryItems(): MutableList<String> {
    val items = mutableListOf(
      CommUtil.getAOT(), CommUtil.getWeapon(), CommUtil.getTool()
    )
    if (config.commUsePigeon) items.add("Royal Pigeon")
    return items
  }
}