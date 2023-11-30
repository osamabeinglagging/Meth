package dev.macrohq.meth.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.data.OptionSize
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import dev.macrohq.meth.util.macroHandler

class MethConfig : Config(Mod("Meth", ModType.UTIL_QOL), "meth.json") {
  /*
  * General
  */
  @KeyBind(
    name = "Toggle Macro", category = "General"
  )
  var macroToggleKeybind = OneKeyBind(UKeyboard.KEY_GRAVE)

  @Dropdown(
    name = "Available Macros", category = "General",
    options = ["Commission macro"]
  )
  var chosenMacro = 0

  @Switch(
    name = "Debug Mode", category = "General"
  )
  val debugMode = true

  @Switch(
    name = "Ungrab Mouse", category = "General"
  )
  val ungrabMouse = false

  @Switch(
    name = "Location HUD", category = "General"
  )
  val showLocation = false

  @Switch(
    name = "Auto reconnect", category = "General"
  )
  val autoReconnect = true

  /*
  * Commission Macro
  */

  @Dropdown(
    name = "Transport Method", category = "Commission Macro",
    options = ["Etherwarpless", "Etherwarp", "Fly + Etherwarp + Walk", "Hybrid"]
  )
  val commTransport = 1

  @Dropdown(
    name = "Mining Tool", category = "Commission Macro",
    options = ["Gemstone Gauntlet", "Titanium Drill", "Pickonimbus"]
  )
  var commTool = 0

  @Switch(
    name = "Use Pigeon", category = "Commission Macro"
  )
  var commUsePigeon = false


  @Switch(
    name = "Status HUD", category = "Commission Macro"
  )
  val commStatusHUD = false

  @Slider(
    name = "Wait After Comm Completion", category = "Commission Macro",
    min = 100f, max = 2000f, step = 50
  )
  var commWaitAfterCompletion = 1000

  /*
  * Mithril Miner
  */

  @Slider(
    name = "Tick Glide Tick Offset", category = "Mithril Miner",
    min = 1f, max = 10f, step = 1
  )
  var mithrilMinerTickGlideOffset = 3

  @Slider(
    name = "Speed Boost Activation Wait Time", category = "Mithril Miner", subcategory = "Time",
    min = 100f, max = 1000f, step = 50
  )
  var mithrilMinerMSBWaitTime = 100

  @Slider(
    name = "Block Look Time", category = "Mithril Miner", subcategory = "Time",
    min = 100f, max = 2000f, step = 50
  )
  var mithrilMinerLookTime = 200

  @Slider(
    name = "Look Time Limit", category = "Mithril Miner", subcategory = "Time",
    min = 100f, max = 3000f, step = 50
  )
  var mithrilMinerLookTimeLimit = 1500

  /*
  * Mob Killer
  */
  @Dropdown(
    name = "Primary Weapon", category = "Commission Macro", subcategory = "Mob Killer",
    options = ["Frozen Scythe", "Aurora Staff", "Juju Shortbow", "Terminator"]
  )
  var mobKillerWeapon = 0

  @Slider(
    name = "Mob Look Time", category = "Commission Macro", subcategory = "Mob Killer",
    min = 100f, max = 1000f, step = 50
  )
  var mobKillerMobLookTime = 500

  @Slider(
    name = "Attack Delay", category = "Commission Macro", subcategory = "Mob Killer",
    min = 100f, max = 1000f, step = 50
  )
  var mobKillerWaitAfterAttackTime = 100

  /*
  * Failsafes
  */

  @Slider(
    name = "Time to wait after World Unloads", category = "Failsafes", subcategory = "Time",
    min = 1000f, max = 10000f, step = 1000
  )
  var failsafeWaitAfterWorldUnload = 5000

  @Slider(
    name = "Time to wait Before Warping to Location", category = "Failsafes", subcategory = "Time",
    min = 1000f, max = 10000f, step = 1000
  )
  var failsafeWaitBeforeWarp = 2000

  @Slider(
    name = "Time to wait Before Fixing Inventory", category = "Failsafes", subcategory = "Time",
    min = 1000f, max = 10000f, step = 1000
  )
  var failsafeWaitBeforeInventoryFix = 2000

  @Slider(
    name = "Time to wait Before Warping Out(Fix Mana)", category = "Failsafes", subcategory = "Time",
    min = 1000f, max = 10000f, step = 1000
  )
  var failsafeWaitBeforeWarpOut = 2000

  /*
  * Features
  */

  /*    * Auto Aotv *    */
  @Dropdown(
    name = "Aspect of the", category = "Features", subcategory = "Auto Aotv",
    options = ["End", "Void"]
  )
  val autoAotvAOT = 1

  @Slider(
    name = "Auto Aotv Time Limit", category = "Features", subcategory = "Auto Aotv",
    min = 1000f, max = 50000f, step = 10000
  )
  var autoAotvTimeLimit = 30000

  @Slider(
    name = "Aotv Look Time", category = "Features", subcategory = "Auto Aotv",
    min = 100f, max = 1000f, step = 50
  )
  var autoAotvFlyLookTime = 200

  @Slider(
    name = "Aotv Etherwarp Look Time", category = "Features", subcategory = "Auto Aotv",
    min = 100f, max = 1000f, step = 50
  )
  var autoAotvEtherwarpLookTime = 400

  @Slider(
    name = "Etherwarp Sneak Time (Before Clicking)", category = "Features", subcategory = "Auto Aotv",
    min = 100f, max = 1000f, step = 50
  )
  var autoAotvEtherwarpSneakTime = 300

  @Slider(
    name = "Teleport Time Limit", category = "Features", subcategory = "Auto Aotv",
    min = 100f, max = 5000f, step = 100
  )
  var autoAotvTeleportTimeLimit = 3000

  @Slider(
    name = "Walk Time Limit", category = "Features", subcategory = "Auto Aotv",
    min = 100f, max = 10000f, step = 100
  )
  var autoAotvWalkTimeLimit = 5000

  /*    * AutoCommission SubCategory *    */

  @Slider(
    name = "Auto Commission Time Limit", category = "Commission Macro", subcategory = "Auto Commission",
    min = 1000f, max = 50000f, step = 10000
  )
  var autoCommissionTimeLimit = 30000

  @Slider(
    name = "Ceanna Look Time", category = "Commission Macro", subcategory = "Auto Commission",
    min = 100f, max = 1000f, step = 50
  )
  var autoCommissionCeannaLookTime = 300

  @Slider(
    name = "GUI Open Time Limit", category = "Commission Macro", subcategory = "Auto Commission",
    min = 100f, max = 5000f, step = 100
  )
  var autoCommisionGuiOpenTimeLimit = 3000

  @Slider(
    name = "Time for Commission Claim", category = "Commission Macro", subcategory = "Auto Commission",
    min = 100f, max = 1000f, step = 50
  )
  var autoCommissionClickTime = 500

  @Slider(
    name = "Time to wait in GUI", category = "Commission Macro", subcategory = "Auto Commission",
    min = 100f, max = 2000f, step = 100
  )
  var autoCommissionInGUITime = 1000

  /*    * Auto Inventory *    */
  @Slider(
    name = "Auto Inventory Time Limit", category = "Features", subcategory = "Auto Inventory",
    min = 1000f, max = 50000f, step = 1000
  )
  var autoInventoryTimeLimit = 10000

  @Slider(
    name = "Auto Inventory Click Time", category = "Features", subcategory = "Auto Inventory",
    min = 100f, max = 1000f, step = 100
  )
  var autoInventoryClickTime = 200

  @Slider(
    name = "GUI Sleep Time",
    category = "Features",
    subcategory = "Auto Inventory",
    min = 100f,
    max = 2000f,
    step = 100
  )
  var autoInventoryInGUITime = 1000

  /*    * Auto Warp *    */
  @Slider(
    name = "Auto Warp Time Limit", category = "Features", subcategory = "Auto Warp",
    min = 1000f, max = 50000f, step = 1000
  )
  var autoWarpTimeLimit = 30000

  @Slider(
    name = "Time to Wait Before Each Warp", category = "Features", subcategory = "Auto Warp",
    min = 100f, max = 5000f, step = 100
  )
  var autoWarpTime = 3000

  @Slider(
    name = "Time to Wait Before Restarting after Error", category = "Features", subcategory = "Auto Warp",
    min = 100f, max = 20000f, step = 1000
  )
  var autoWarpErrorWaitTime = 10000

  /*
  * Webhook
  */

  @Text(
    name = "Webhook URL", placeholder = "https://discord.com/api/v10/webhooks/",
    category = "Webhook", multiline = false, size = OptionSize.SINGLE
  )
  var webhookUrl = ""


  init {
    initialize()
    this.registerKeyBind(macroToggleKeybind) { macroHandler.toggle() }
    this.addDependency("commAOT", "Aspect of the") { commTransport == 0 }
  }
}