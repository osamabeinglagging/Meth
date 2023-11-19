package dev.macrohq.meth.feature

import dev.macrohq.meth.pathfinding.AStarPathfinder
import dev.macrohq.meth.util.*
import dev.macrohq.meth.util.Logger.log
import dev.macrohq.meth.util.Logger.note
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Failsafe {
  var failSafeAllowance = false
  private var fixing = false

  var somethingBroken = false
  var macroChecked = false

  private var whatsBroken: Broken? = null
  var whatFailsafe: Failsafe? = null

  private var timer = Timer(0)

  // Player Failsafe
  private var playerFailsafeToggled = 0
  private var playerFailsafeChecking = false
  private var playerFailsafeTime = Timer(0)
  private var playerFailsafeAnnoyer: Entity? = null
  var playerFailsafeState = PlayerFailsafeState.FINDING_PLAYER

  enum class Broken {
    ITEMS_NOT_IN_INVENTORY, ITEMS_NOT_IN_HOTBAR, NOT_AT_LOCATION, WARP_OUT
  }

  enum class Failsafe {
    ROTATION_FAILSAFE, HOTBAR_SWAP_FAILSAFE, BEDROCK_BOX_FAILSAFE, PLAYER_FAILSAFE,
  }

  enum class PlayerFailsafeState {
    FINDING_PLAYER, VERIFYIG_PLAYER, MOVING_AWAY, ROTATING_AND_REACTING, FINISHING
  }

//  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (player == null || world == null || !macroHandler.enabled) return

    if (!fixing && !macroChecked && !somethingBroken) {
      resetFailsafeInstance()
      checkMacroNecessities()
      checkMacroChecks()
    }

    if (somethingBroken) {
      fixStuff()
      return
    }
    if (macroChecked) {
      reactToMacroCheck()
      return
    }
  }

//  @SubscribeEvent
  fun onWorldChange(event: WorldEvent.Unload) {
    if (!macroHandler.enabled || fixing) return

    failSafeAllowance = false
    somethingBroken = true
    whatsBroken = Broken.NOT_AT_LOCATION
    timer = Timer(config.failsafeWaitAfterWorldUnload)

    log("World Change Unload")
  }

//  @SubscribeEvent
  fun onWorldLoad(event: WorldEvent.Load) {
    if (!macroHandler.enabled || fixing) return

    failSafeAllowance = false
    somethingBroken = true
    whatsBroken = Broken.NOT_AT_LOCATION
  }

  private fun checkMacroNecessities() {
    if (locationUtil.currentIsland != macroHandler.location.first && locationUtil.currentSubLocation != macroHandler.location.second) {
      failSafeAllowance = false; somethingBroken = true; macroChecked = false
      whatsBroken = Broken.NOT_AT_LOCATION
      timer = Timer(config.failsafeWaitBeforeWarp)

      note("Failsafe - Not At Location")
      return
    }
    if (!InventoryUtil.areItemsInInventory(macroHandler.necessaryItems())) {
      failSafeAllowance = false; somethingBroken = true; macroChecked = false
      whatsBroken = Broken.ITEMS_NOT_IN_INVENTORY

      note("Failsafe - Items Not In Inventory")
      return
    }
    if (!InventoryUtil.areItemsInHotbar(macroHandler.necessaryItems()) && !autoInventory.enabled) {
      failSafeAllowance = false; somethingBroken = true; macroChecked = false
      whatsBroken = Broken.ITEMS_NOT_IN_HOTBAR
      timer = Timer(config.failsafeWaitBeforeInventoryFix)

      note("Failsafe - Items Not In Hotbar")
      return
    }

    // Commission Macro Specific Checks
    if (macroHandler.activeMacro == commissionMacro) {
      if (locationUtil.isInSkyBlock && infoBarUtil.manaPercentage == 0f) {
        failSafeAllowance = false; somethingBroken = true; macroChecked = false
        whatsBroken = Broken.WARP_OUT
        timer = Timer(config.failsafeWaitBeforeWarpOut)

        note("Failsafe - Ran out of mana.")
        return
      }
    }
  }

  private fun fixStuff() {
    when (whatsBroken) {
      Broken.ITEMS_NOT_IN_INVENTORY -> {
        Logger.error("Items not in inventory. Required: ${macroHandler.necessaryItems()}")
        macroHandler.disable() // disable macro
        resetFailsafeInstance()
      }

      Broken.ITEMS_NOT_IN_HOTBAR -> {
        if (!timer.isDone) return

        if (fixing && autoInventory.succeeded()) {
          note("Failsafe - Fixed Stuff. SubMacro Active: ${macroHandler.subMacroActive}")
          if (!macroHandler.subMacroActive) macroHandler.enable(true)
          resetFailsafeInstance()
          return
        }
        if (!(fixing || autoInventory.enabled)) {
          note("Failsafe - Fixing item not in Hotbar")
          autoInventory.sendItemsToHotbar(commissionMacro.necessaryItems(), true)
          fixing = true
        }
      }

      Broken.NOT_AT_LOCATION -> {
        if (!timer.isDone) return
        if (fixing && autoWarp.succeeded()) {
          resetFailsafeInstance(); macroHandler.enable(true);

          note("Failsafe - At Location.")
          return
        }
        if (!fixing) {
          fixing = true;
          UnGrabUtil.unGrabMouse(); macroHandler.disable(true);
          autoWarp.enable(null, macroHandler.location.second, true)
          note("Failsafe - Going Back to Macro Spot.")
        }
      }

      Broken.WARP_OUT -> {
        if (!timer.isDone) return

        if (fixing && autoWarp.succeeded()) {
          resetFailsafeInstance(); macroHandler.enable(true); playerFailsafeToggled = 0

          note("Failsafe - Warped out")
          return
        }
        if (!fixing) {
          fixing = true;
          macroHandler.disable(true)
          UnGrabUtil.unGrabMouse()
          autoWarp.enable(LocationUtil.Island.THE_HUB, null, true)
          note("Failsafe - Warping out")
        }
      }

      null -> {
        log("ITS NULL")
        macroHandler.disable()
        resetFailsafeInstance()
      }
    }
  }

  private fun checkMacroChecks() {
    // Macro Checks HERERERERERER
    if (macroHandler.activeMacro == commissionMacro) {
      if (macroHandler.subMacroActive) {
        // Nagger Checker
        when (playerFailsafeState) {
          PlayerFailsafeState.FINDING_PLAYER -> {
            playerFailsafeAnnoyer = world.loadedEntityList.filterIsInstance<EntityOtherPlayerMP>().toMutableList()
              .also { it.sortBy { player.getDistanceToEntity(it) } }
              .firstOrNull { player.getDistanceToEntity(it) < 1.5 }

            playerFailsafeChecking = playerFailsafeAnnoyer != null

            if (playerFailsafeChecking) {
              playerFailsafeTime = Timer(3000)
              playerFailsafeState = PlayerFailsafeState.VERIFYIG_PLAYER

              log("Failsafe - Nagger Found. state = $playerFailsafeState")
            }
          }

          PlayerFailsafeState.VERIFYIG_PLAYER -> {
            log("playerverifytimer: ${timer.isDone}")
            if (!timer.isDone) return

            playerFailsafeChecking = player.getDistanceToEntity(playerFailsafeAnnoyer) < 2
            log("distance: ${player.getDistanceToEntity(playerFailsafeAnnoyer)}")
            if (!playerFailsafeChecking) {
              playerFailsafeTime = Timer(0)

              log("Failsafe - Annoyer Left")
              return
            }

            failSafeAllowance = false
            playerFailsafeToggled++
            note("Failsafe - Nagger Detected")

            if (playerFailsafeToggled > 3) {
              somethingBroken = true; macroChecked = false
              whatsBroken = Broken.WARP_OUT
              timer = Timer(config.failsafeWaitBeforeWarpOut)

              log("Failsafe - Warping out cuz people dont like you.")
              return
            }

            somethingBroken = false; macroChecked = true
            whatFailsafe = Failsafe.PLAYER_FAILSAFE
            playerFailsafeState = PlayerFailsafeState.MOVING_AWAY
          }

          else -> {}
        }
      }
    }
  }

  private fun reactToMacroCheck() {
    when (whatFailsafe) {
      Failsafe.ROTATION_FAILSAFE -> TODO()
      Failsafe.HOTBAR_SWAP_FAILSAFE -> TODO()
      Failsafe.BEDROCK_BOX_FAILSAFE -> TODO()
      Failsafe.PLAYER_FAILSAFE -> {
        when (playerFailsafeState) {
          PlayerFailsafeState.MOVING_AWAY -> {
            fixing = true
            val nearestBlock = BlockUtil.neighbourGenerator(player.getStandingOnFloor(), 1).filter {
              AStarPathfinder.Node(it, null).isWalkable() && it != player.getStandingOnFloor() && player.getStandingOnFloor()
                .distanceSq(it) > 1 && BlockUtil.isBlockAllowedMithril(it) && playerFailsafeAnnoyer!!.getDistanceSqToCenter(it) > 1
            }.minByOrNull {
              BlockUtil.neighbourGenerator(it.up().up(), 1)
                .filter { !world.isAirBlock(it) }.size + player.distanceToBlock(it)
            }!!

            PathingUtil.goto(nearestBlock)
            playerFailsafeState = PlayerFailsafeState.ROTATING_AND_REACTING

            note("Failsafe - Walking Away")
          }

          PlayerFailsafeState.ROTATING_AND_REACTING -> {
            log("Rotating: isDone ${PathingUtil.isDone}")
            if (!PathingUtil.isDone) return
            PathingUtil.stop()
            RotationUtil.stop()
            val angle = RotationUtil.Rotation(0f, 0f)
//            RotationUtil.ease(AngleUtil.getAngles(playerFailsafeAnnoyer!!), 500)
            RotationUtil.ease(angle, 300, true)
            movementLogger.replay(MovementData.getRandomMovement, 500)
            playerFailsafeState = PlayerFailsafeState.FINISHING
          }

          PlayerFailsafeState.FINISHING -> {
            if (!movementLogger.succeeded()) return
            playerFailsafeState = PlayerFailsafeState.FINDING_PLAYER
            RotationUtil.stop()
            resetFailsafeInstance()
          }

          else -> {}
        }
      }

      null -> {
        log("FailsafeReact = null")
        macroHandler.disable()
      }
    }
  }

  fun resetFailsafeInstance() {
    whatsBroken = null
    whatFailsafe = null

    somethingBroken = false
    macroChecked = false

    fixing = false
    timer = Timer(0)
    failSafeAllowance = true

  }

  fun resetFailsafe() {
    failSafeAllowance = false
    fixing = false

    somethingBroken = false
    macroChecked = false

    whatsBroken = null
    whatFailsafe = null

    timer = Timer(0)

    // Player Failsafe
    playerFailsafeToggled = 0
    playerFailsafeChecking = false
    playerFailsafeTime = Timer(0)
    playerFailsafeAnnoyer = null
    playerFailsafeState = PlayerFailsafeState.FINDING_PLAYER
  }
}