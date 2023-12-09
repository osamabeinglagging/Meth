package dev.macrohq.meth.feature

import dev.macrohq.meth.util.Logger
import dev.macrohq.meth.util.failsafe

abstract class AbstractFeature : IFeature {
  override var forceEnable = false
  override var failed: Boolean = false
  override var success: Boolean = false
  override var enabled: Boolean = false

  override fun canEnable() = (failsafe.failsafeAllowance || this.forceEnable) && this.enabled
  override fun setSucceeded(succeeded: Boolean) {
    this.failed = !succeeded
    this.success = succeeded
  }

  override fun succeeded() = !this.enabled && this.success
  override fun failed() = !this.enabled && this.failed

  fun log(message: String) {
    Logger.log("[${this.featureName} - $message]")
  }

  fun note(message: String) {
    Logger.note("[${this.featureName} - $message")
  }

  fun error(message: String) {
    Logger.error("[${this.featureName} - $message")
  }
}