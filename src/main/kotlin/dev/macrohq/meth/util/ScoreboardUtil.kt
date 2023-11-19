package dev.macrohq.meth.util

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.StringUtils
import java.util.*
import java.util.stream.Collectors

object ScoreboardUtil {
  fun getScoreboardLines(): List<String> {
    val lines = mutableListOf<String>()
    val scoreboard = mc.theWorld.scoreboard ?: return lines
    val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return lines
    var scores = scoreboard.getSortedScores(objective)
    val list = scores.stream().filter { input: Score? ->
        input != null && input.playerName != null && !input.playerName.startsWith("#")
      }.collect(Collectors.toList())
    scores = if (list.size > 15) {
      Lists.newArrayList(Iterables.skip(list, scores.size - 15))
    } else {
      list
    }
    for (score in scores) {
      val team = scoreboard.getPlayersTeam(score.playerName)
      lines.add(ScorePlayerTeam.formatPlayerName(team, score.playerName))
    }
    val newlines = mutableListOf<String>()
    lines.forEach {
      val stripped = StringUtils.stripControlCodes(it).toCharArray()
      val strBuilder = StringBuilder()
      for (c in stripped) {
        if (c.code in 21..126) strBuilder.append(c)
      }
      newlines.add(strBuilder.toString())
    }
    return newlines
  }

  fun getScoreboardTitle(): String {
    return StringUtils.stripControlCodes(world.scoreboard.getObjectiveInDisplaySlot(1).displayName)
      .lowercase(Locale.getDefault())
  }
}