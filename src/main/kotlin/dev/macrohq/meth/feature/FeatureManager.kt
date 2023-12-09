package dev.macrohq.meth.feature

import dev.macrohq.meth.feature.implementation.*
import dev.macrohq.meth.feature.implementation.failsafe.Failsafe

class FeatureManager {
  private val features = mutableListOf<IFeature>()

  companion object {
    private var instance: FeatureManager? = null
    fun getInstance(): FeatureManager {
      if (instance == null) {
        instance = FeatureManager()
      }
      return instance!!
    }
  }

  fun loadFeatures(): List<IFeature> {
    val features = listOf(
      AutoAotv.getInstance(),
      AutoCommission.getInstance(),
      AutoInventory.getInstance(),
      AutoRotation.getInstance(),
      AutoWarp.getInstance(),
      MithrilMiner.getInstance(),
      MobKiller.getInstance(),
      MovementLogger.getInstance(),
      Ping.getInstance(),
      RandomMovement.getInstance(),
      Failsafe.getInstance(),
      InfoBarTracker.getInstance(),
      LocationTracker.getInstance()
    )
    this.features.addAll(features)
    return this.features
  }

  fun disableFeatures(disablePassiveFeatures: Boolean) {
    this.features.forEach { feature ->
      if(!feature.isPassiveFeature || (feature.isPassiveFeature && disablePassiveFeatures)){
        feature.disable()
      }
    }
  }
}