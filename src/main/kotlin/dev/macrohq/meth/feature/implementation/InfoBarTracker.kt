package dev.macrohq.meth.feature.implementation

import dev.macrohq.meth.feature.AbstractFeature
import dev.macrohq.meth.feature.IFeature
import dev.macrohq.meth.util.locationUtil
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class InfoBarTracker: AbstractFeature() {
  override val featureName: String = "InfoBarTracker"
  override val isPassiveFeature: Boolean = true

  private val manaPattern = Regex("(\\d*)/(\\d*)✎")
  private val healthPattern = Regex("(\\d*)/(\\d*)")

  companion object{
    private var instance: InfoBarTracker? = null
    fun getInstance(): InfoBarTracker {
      if(instance == null) instance = InfoBarTracker()
      return instance!!
    }
  }

  var maxMana = 0
  var currentMana = 0
  var manaPercentage = 0f

  var maxHealth = 0
  var currentHealth = 0
  var healthPercentage = 0f

  override fun disable(){}

  @SubscribeEvent
  fun onChat(event: ClientChatReceivedEvent) {
    if (!locationUtil.isInSkyBlock) return
    if (event.type.toInt() != 2) return

    val infoBar = StringUtils.stripControlCodes(event.message.unformattedText.replace(",",""))

    if (infoBar.contains("NOT ENOUGH MANA")) {
      this.currentMana = 0
      this.maxMana = 0
      this.manaPercentage = 0f
    }
    else if (manaPattern.containsMatchIn(infoBar)) {
      val match = manaPattern.find(infoBar)!!

      this.maxMana = match.groupValues[2].toInt()
      this.currentMana = match.groupValues[1].toInt()

      this.manaPercentage = (currentMana.toFloat() / maxMana.toFloat()) * 100
    }

    if (healthPattern.containsMatchIn(infoBar)) {
      val match = healthPattern.find(infoBar)!!

      this.maxHealth = match.groupValues[2].replace(",", "").toInt()
      this.currentHealth = match.groupValues[1].replace(",", "").toInt()

      this.healthPercentage = (currentHealth / maxHealth).toFloat() * 100
    }
  }
}