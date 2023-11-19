package dev.macrohq.meth.util

import kotlin.random.Random

object StringUtil {
  private val funnyRealityNoJoke = listOf(
    "ratcoon is racist",
    "tom is lazy",
    "i cant study",
    "help me",
    "im stupid please help",
    "/wdr ${player.name} cheater",
    "cheater get banned",
    "why do you cheat",
    "youre getting reported",
    "nirox is the goat",
    "nirox hates kotlin",
    "kotlin is good",
    "i love cpp",
    "macrohq on top",
    "youtube/@macrohq",
    "gg/macro-hq-url-here",
    "macrohq.dev",
    "report bugs",
    "thanks may2bee",
    "yuro should dance",
    "pay me $200 to get your name here"
  )

  val randomFunny: String
    get() = funnyRealityNoJoke[Random.nextInt(funnyRealityNoJoke.size-1)].lowercase()
}