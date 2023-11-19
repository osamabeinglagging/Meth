import com.google.common.collect.ComparisonChain
import com.google.common.collect.Ordering
import dev.macrohq.meth.util.player
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.util.StringUtils
import net.minecraft.world.WorldSettings
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.regex.Pattern

object TablistUtil {
  // Copied form MightyMiner Because I don't want to figure out how it works
  // Thanks Nirox <3
  private val playerOrdering: Ordering<NetworkPlayerInfo> = Ordering.from(PlayerComparator())

  private val pattern = Pattern.compile("§([0-9]|[a-z])")

  @SideOnly(Side.CLIENT)
  private class PlayerComparator : Comparator<NetworkPlayerInfo> {
    override fun compare(o1: NetworkPlayerInfo, o2: NetworkPlayerInfo): Int {
      val team1 = o1.playerTeam
      val team2 = o2.playerTeam
      return ComparisonChain.start().compareTrueFirst(
          o1.gameType != WorldSettings.GameType.SPECTATOR, o2.gameType != WorldSettings.GameType.SPECTATOR
        ).compare(
          team1?.registeredName ?: "", team2?.registeredName ?: ""
        ).compare(o1.gameProfile.name, o2.gameProfile.name).result()
    }
  }

  fun getTabList(): List<String> {
    val players = playerOrdering.sortedCopy(player.sendQueue.playerInfoMap)
    val result = mutableListOf<String>()

    for (info in players) {
      val name = Minecraft.getMinecraft().ingameGUI.tabList.getPlayerName(info)
      result.add(StringUtils.stripControlCodes(name))
    }
    return result
  }

  fun getAllPlayers(): MutableList<String>{
    val players = mutableSetOf<String>()
    val pattern = Regex("\\[\\d+]\\s\\w+")
    for(text in this.getTabList()){
      if(pattern.containsMatchIn(text)) players.add(text)
    }
    return players.toMutableList()
  }
}
