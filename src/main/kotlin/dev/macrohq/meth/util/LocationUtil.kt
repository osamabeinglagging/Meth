package dev.macrohq.meth.util

import TablistUtil
import net.minecraft.client.gui.Gui
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class LocationUtil: Gui() {
  enum class SubLocation(val locName: String) {
    // Dwarven Mines
    The_Forge("The Forge"),
    Forge_Basin("Forge Basin"),
    Palace_Bridge("Palace Bridge"),
    Royal_Palace("Royal Palace"),
    Aristocrat_Passage("Aristocrat Passage"),
    Hanging_Court("Hanging Court"),
    Divans_Gateway("Divan's Gateway"),
    Far_Reserve("Far Reserve"),
    Goblin_Burrows("Goblin Burrows"),
    Miners_Guild("Miner's Guild"),
    Great_Ice_Wall("Great Ice Wall"),
    The_Mist("The Mist"),
    C_and_C_Minecarts_Co("C&C Minecarts Co."),
    Grand_Library("Grand Library"),
    Barracks_of_Heroes("Barracks of Heroes"),
    Dwarven_Village("Dwarven Village"),
    The_Lift("The Lift"),
    Royal_Quarters("Royal Quarters"),
    Lava_Springs("Lava Springs"),
    Cliffside_Veins("Cliffside Veins"),
    Ramparts_Quarry("Rampart's Quarry"),
    Upper_Mines("Upper Mines"),
    Royal_Mines("Royal Mines"),

    // Hub Island
    Archery_Range("Archery Range"),
    Auction_House("Auction House"),
    Bank("Bank"),
    Bazaar_Alley("Bazaar Alley"),
    Blacksmiths_House("Blacksmith's House"),
    Builders_House("Builder's House"),
    Canvas_Room("Canvas Room"),
    Coal_Mine("Coal Mine"),
    Colosseum_Arena("Colosseum Arena"),
    Colosseum("Colosseum"),
    Community_Center("Community Center"),
    Election_Room("Election Room"),
    Farm("Farm"),
    Farmhouse("Farmhouse"),
    Fashion_Shop("Fashion Shop"),
    Fishermans_Hut("Fisherman's Hut"),
    Flower_House("Flower House"),
    Forest("Forest"),
    Graveyard("Graveyard"),
    Hexatorum("Hexatorum"),
    Library("Library"),
    Mountain("Mountain"),
    Museum("Museum"),
    Regalia_Room("Regalia Room"),
    Ruins("Ruins"),
    Shens_Auction("Shen's Auction"),
    Tavern("Tavern"),
    Thaumaturgist("Thaumaturgist"),
    Unincorporated("Unincorporated"),
    Village("Village"),
    Wilderness("Wilderness"),
    Wizard_Tower("Wizard Tower"),

    // Park
    Birch_Park("Birch Park"),
    Dark_Thicket("Dark Thicket"),
    Howling_Cave("Howling Cave"),
    Jungle_Island("Jungle Island"),
    Lonely_Island("Lonely Island"),
    Melodys_Plateau("Melody's Plateau"),
    Savanna_Woodland("Savanna Woodland"),
    Spruce_Woods("Spruce Woods"),
    Viking_Longhouse("Viking Longhouse"),

    // Farming Islands and Desert Settlement
    The_Barn("The Barn"),
    Windmill("Windmill"),
    Desert_Settlement("Desert Settlement"),
    Glowing_Mushroom_Cave("Glowing Mushroom Cave"),
    Jakes_House("Jake's House"),
    Mushroom_Desert("Mushroom Desert"),
    Mushroom_Gorge("Mushroom Gorge"),
    Oasis("Oasis"),
    Overgrown_Mushroom_Cave("Overgrown Mushroom Cave"),
    Shepherds_Keep("Shepherd's Keep"),
    Trappers_Den("Trapper's Den"),
    Treasure_Hunter_Camp("Treasure Hunter Camp"),

    // Gold Mine
    Gold_Mine("Gold Mine"),

    // Deep Caverns
    Diamond_Reserve("Diamond Reserve"),
    Gunpowder_Mines("Gunpowder Mines"),
    Lapis_Quarry("Lapis Quarry"),
    Obsidian_Sanctuary("Obsidian Sanctuary"),
    Pigmens_Den("Pigmen's Den"),
    Slimehill("Slimehill"),

    // Crystal Hollows
    Crystal_Hollows("Crystal Hollows"),
    Crystal_Nucleus("Crystal Nucleus"),
    Dragons_Lair("Dragon's Lair"),
    Fairy_Grotto("Fairy Grotto"),
    Goblin_Holdout("Goblin Holdout"),
    Goblin_Queens_Den("Goblin Queen's Den"),
    Jungle_Temple("Jungle Temple"),
    Jungle("Jungle"),
    Khazad_dum("Khazad-dûm"),
    Lost_Precursor_City("Lost Precursor City"),
    Magma_Fields("Magma Fields"),
    Mines_of_Divan("Mines of Divan"),
    Mithril_Deposits("Mithril Deposits"),
    Precursor_Remnants("Precursor Remnants"),

    // Spider's Den
    Arachnes_Burrow("Arachne's Burrow"),
    Arachnes_Sanctuary("Arachne's Sanctuary"),
    Archaeologists_Camp("Archaeologist's Camp"),
    Grandmas_House("Grandma's House"),
    Gravel_Mines("Gravel Mines"),
    Spider_Mound("Spider Mound"),

    // The End
    Dragons_Nest("Dragon's Nest"),
    The_End("The End"),
    Void_Sepulture("Void Sepulture"),
    Void_Slate("Void Slate"),
    Zealot_Bruiser_Hideout("Zealot Bruiser Hideout"),

    // Crimson Isle
    Auras_Lab("Aura's Lab"),
    Barbarian_Outpost("Barbarian Outpost"),
    Belly_of_the_Beast("Belly of the Beast"),
    Blazing_Volcano("Blazing Volcano"),
    Burning_Desert("Burning Desert"),
    Cathedral("Cathedral"),
    Chiefs_Hut("Chief's Hut"),
    Community_Center_Scarleton("Community Center (Scarleton)"),
    Courtyard("Courtyard"),
    Crimson_Fields("Crimson Fields"),
    Dojo("Dojo"),
    Dragontail_Auction_House("Dragontail Auction House"),
    Dragontail_Bank("Dragontail Bank"),
    Dragontail_Bazaar("Dragontail Bazaar"),
    Dragontail_Blacksmith("Dragontail Blacksmith"),
    Dragontail_Minion_Shop("Dragontail Minion Shop"),
    Dragontail_Townsquare("Dragontail Townsquare"),
    Dragontail("Dragontail"),
    Forgotten_Skull("Forgotten Skull"),
    Igrupans_Chicken_Coop("Igrupan's Chicken Coop"),
    Igrupans_House("Igrupan's House"),
    Mage_Council("Mage Council"),
    Mage_Outpost("Mage Outpost"),
    Magma_Chamber("Magma Chamber"),
    Matriarchs_Lair("Matriarch's Lair"),
    Mystic_Marsh("Mystic Marsh"),
    Odgers_Hut("Odger's Hut"),
    Plhlegblast_Pool("Plhlegblast Pool"),
    Ruins_of_Ashfang("Ruins of Ashfang"),
    Scarleton_Auction_House("Scarleton Auction House"),
    Scarleton_Bank("Scarleton Bank"),
    Scarleton_Bazaar("Scarleton Bazaar"),
    Scarleton_Blacksmith("Scarleton Blacksmith"),
    Scarleton_Minion_Shop("Scarleton Minion Shop"),
    Scarleton_Plaza("Scarleton Plaza"),
    Scarleton("Scarleton"),
    Smoldering_Tomb("Smoldering Tomb"),
    Stronghold("Stronghold"),
    The_Bastion("The Bastion"),
    The_Dukedom("The Dukedom"),
    The_Wasteland("The Wasteland"),
    Throne_Room("Throne Room"),

    // Winter Island
    Einarys_Emporium("Einary's Emporium"),
    Garys_Shack("Gary's Shack"),
    Glacial_Cave("Glacial Cave"),
    Hot_Springs("Hot Springs"),
    Jerry_Pond("Jerry Pond"),
    Jerrys_Workshop("Jerry's Workshop"),
    Mount_Jerry("Mount Jerry"),
    Reflective_Pond("Reflective Pond"),
    Sherrys_Showroom("Sherry's Showroom"),
    Sunken_Jerry_Pond("Sunken Jerry Pond"),
    Terrys_Shack("Terry's Shack"),

    // Dungeon
    The_Catacombs_Entrance("The Catacombs (Entrance)"),
    The_Catacombs_F1("The Catacombs (F1)"),
    The_Catacombs_F2("The Catacombs (F2)"),
    The_Catacombs_F3("The Catacombs (F3)"),
    The_Catacombs_F4("The Catacombs (F4)"),
    The_Catacombs_F5("The Catacombs (F5)"),
    The_Catacombs_F6("The Catacombs (F6)"),
    The_Catacombs_F7("The Catacombs (F7)"),

    // Rift
    Around_Colosseum("Around Colosseum"),
    Barrier_Street("Barrier Street"),
    Barry_Center("Barry Center"),
    Barry_HQ("Barry HQ"),
    Black_Lagoon("Black Lagoon"),
    Book_in_a_Book("Book in a Book"),
    Broken_Cage("Broken Cage"),
    Cake_House("Cake House"),
    Dolphin_Trainer("Dolphin Trainer"),
    Dreadfarm("Dreadfarm"),
    Déjà_Vu_Alley("Déjà Vu Alley"),
    Empty_Bank("Empty Bank"),
    Enigmas_Crib("Enigma's Crib"),
    Fairylosopher_Tower("Fairylosopher Tower"),
    Great_Beanstalk("Great Beanstalk"),
    Half_Eaten_Cave("Half-Eaten Cave"),
    Infested_House("Infested House"),
    Lagoon_Cave("Lagoon Cave"),
    Lagoon_Hut("Lagoon Hut"),
    Leeches_Lair("Leeches Lair"),
    Living_Cave("Living Cave"),
    Living_Stillness("Living Stillness"),
    Lonely_Terrace("Lonely Terrace"),
    Mirrorverse("Mirrorverse"),
    Murder_House("Murder House"),
    Otherside("Otherside"),
    Oubliette("Oubliette"),
    Photon_Pathway("Photon Pathway"),
    Pumpgrotto("Pumpgrotto"),
    Rift_Gallery_Entrance("Rift Gallery Entrance"),
    Rift_Gallery("Rift Gallery"),
    Shifted_Tavern("Shifted Tavern"),
    Stillgore_Chateau("Stillgore Château"),
    Taylors("Taylor's"),
    Village_Plaza("Village Plaza"),
    West_Village("West Village"),
    Your_Island("Your Island"),
    Wyld_Woods("Wyld Woods")
  }

  enum class Island(val islandName: String) {
    PRIVATE_ISLAND("Private Island"),
    THE_HUB("Hub"),
    THE_PARK("The Park"),
    THE_FARMING_ISLANDS("The Farming Islands"),
    GOLD_MINE("Gold Mine"),
    DEEP_CAVERNS("Deep Caverns"),
    DWARVEN_MINES("Dwarven Mines"),
    CRYSTAL_HOLLOWS("Crystal Hollows"),
    SPIDER_DEN("Spider's Den"),
    THE_END("The End"),
    CRIMSON_ISLE("Crimson Isle"),
    JERRY_WORKSHOP("Jerry's Workshop"),
    RIFT("Rift"),
    DUNGEON_HUB("Dungeon Hub"),
    LIMBO("UNKNOWN"),
    LOBBY("PROTOTYPE"),
    GARDEN("Garden"),
    DUNGEON("Dungeon")
  }

  var currentIsland: Island? = null
  var currentSubLocation: SubLocation? = null
  private var worldChangeInProgress = false

  val isInSkyBlock
    get() = currentIsland != Island.LOBBY && currentIsland != Island.LIMBO

  @SubscribeEvent
  fun onChat(event: ClientChatReceivedEvent) {
  }

  @SubscribeEvent
  fun onWorldLoad(event: WorldEvent.Load) {
    currentIsland = null
    currentSubLocation = null
    worldChangeInProgress = false
  }

  @SubscribeEvent
  fun onWorldUnload(event: WorldEvent.Unload) {
    currentIsland = null
    currentSubLocation = null
    worldChangeInProgress = true
  }

  @SubscribeEvent
  fun renderOverlay(event: RenderGameOverlayEvent){
    if (event.isCancelable || event.type != RenderGameOverlayEvent.ElementType.ALL || !config.showLocation) return
    // Size
    val islandNameSize = if(currentIsland != null) (8 + currentIsland!!.islandName.length) else 5
    val sublocNameSize = if(currentSubLocation != null) (8 + currentSubLocation!!.locName.length) else 5
    val size = maxOf(islandNameSize, sublocNameSize)

    val islandName = if(currentIsland != null) currentIsland!!.islandName else "Null"
    val sublocName = if(currentSubLocation != null) currentSubLocation!!.locName else "Null"

    drawRect(5, 5, size * 6, 33, 0x90000000.toInt())
    drawString(fontRenderer, "Island: $islandName", 10, 10, 0xFFFFFF)
    drawString(fontRenderer, "SubLoc: $sublocName", 10, 20, 0xFFFFFF)
  }

  @SubscribeEvent
  fun onTick(event: TickEvent.ClientTickEvent) {
    if (player == null || world == null) return

    val scoreBoard = ScoreboardUtil.getScoreboardLines().reversed()
    if (scoreBoard.isNotEmpty()) {
      outer@ for (line in scoreBoard) {
        for (loc in SubLocation.entries) {
          if (line.contains(loc.locName)) {
            currentSubLocation = loc
            break@outer
          }
        }
      }
    }

    for (line in TablistUtil.getTabList()) {
      val pattern = Regex("Area: (.+)")
      if (pattern.matches(line)) {
        val text = pattern.find(line)!!.groupValues[1]
        for (island in LocationUtil.Island.entries) {
          if (island.islandName == text) {
            currentIsland = island
            return
          }
        }
      }
    }
    if (ScoreboardUtil.getScoreboardLines().isNotEmpty()) {
      if (ScoreboardUtil.getScoreboardTitle() != "skyblock") {
        currentIsland = Island.LOBBY
      }
    } else {
      currentIsland = Island.LIMBO
    }
  }

}