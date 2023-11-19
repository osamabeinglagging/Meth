package dev.macrohq.meth.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import com.google.gson.Gson
import dev.macrohq.meth.util.Logger.log
import dev.macrohq.meth.util.Logger.note
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

object WebhookUtil {
  private const val fileLocation = "config/meth/webhookConfig.json"
  private val client = OkHttpClient()
  private val gson = Gson()
  private val headers = mapOf(
    "authority" to "discord.com",
    "accept" to "application/json",
    "accept-language" to "en",
    "content-type" to "application/json",
    "origin" to "https://discohook.org",
    "referer" to "https://discohook.org/",
    "sec-ch-ua" to "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"",
    "sec-ch-ua-mobile" to "?0",
    "sec-ch-ua-platform" to "\"Windows\"",
    "sec-fetch-dest" to "empty",
    "sec-fetch-mode" to "cors",
    "sec-fetch-site" to "cross-site",
    "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36"
  )

  enum class WebhookType {
    GENERAL_INFO, FAILSAFE
  }

  private fun getWebhookId(webhookType: WebhookType): String {
    return if (webhookType == WebhookType.GENERAL_INFO) "generalMessageID" else "failsafeMessageID"
  }

  fun send(jsonBody: String, webhookType: WebhookType) {
    runAsync {
      val ids = JsonUtil.getMap(fileLocation)
      val webhookId = getWebhookId(webhookType)

      if (!ids.keys.contains(webhookId)) {
        val response = postWebhook(jsonBody, config.webhookUrl.removeSuffix("/") + "?wait=true")
        if (response.code != 200) {
          handleFailedWebhook(response)
        }
        val map = gson.fromJson(response.body!!.string(), Map::class.java)
        val mapData = mapOf(
          getWebhookId(webhookType) to map!!["id"].toString()
        )
        JsonUtil.updateJson(fileLocation, mapData)
      } else {
        val url = "${config.webhookUrl.removeSuffix("/")}/messages/${ids[webhookId]}"
        val response = patchWebhook(jsonBody, url)

        if (response.code != 200) {
          handleFailedWebhook(response)
        }
      }
    }
  }

  fun mapFromResponse(response: Response): Map<*, *> = Gson().fromJson(response.body!!.string(), Map::class.java)

  private fun postWebhook(jsonBody: String, url: String): Response {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
      .url(url)
      .headers(headers.toHeaders())
      .post(requestBody)
      .build()

    return client.newCall(request).execute()
  }

  private fun patchWebhook(jsonBody: String, url: String): Response {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
      .url(url)
      .headers(headers.toHeaders())
      .patch(requestBody)
      .build()

    return client.newCall(request).execute()
  }

  fun statusBody(
    uptime: Int,
    commissionsCompleted: Int,
    commissionPerHour: Int,
    currentCommission: String,
    lastFailsafeTrigger: Int,
    lastTask: Int
  ): String {
    return """
        {
          "content": null,
          "embeds": [
          {
            "title": "📢 Status Update",
            "description": "```md\n❓ <General Information>```\n**`👤`** **`Username:`** ${player.name}\n**`⌛`** **`Uptime:`** <t:${uptime}:R>\n**`✔️`** **`Completed:`** ${commissionsCompleted}\n**`⏳`** **`Commission Rate:`** $commissionPerHour per hour\n**`⚡`** **`Current Commission:`** $currentCommission \n\n```md\n🛡️ <Failsafe Information>```\n**`🚨`** **`Last Trigger:`** <t:${lastFailsafeTrigger}:R>\n\n```md\n⌛ <Timestamp Information>```\n**`🚨`** **`Last Failsafe Trigger:`** <t:${lastFailsafeTrigger}:R>\n**`🔄`** **`Last Webhook Update:`** <t:${System.currentTimeMillis()/1000}:R>\n**`📋`** **`Last Task:`** <t:${lastTask}:R>",
            "color": 65362,
            "footer": {
            "text": "💊 Meth"
          },
            "thumbnail": {
            "url": "https://mc-heads.net/head/${player.uniqueID}"
          }
          }
          ],
          "attachments": []
        }
        """
  }

  fun failsafeBody(failsafeName: String, counterMeasure: String, failsafeTime: Int): String {
    return """
    {
      "content": null,
      "embeds": [
      {
        "title": "🆘 Failsafe Trigger",
        "description": "```diff\n- 🚨 <Failsafe Information>```\n**`🛡️`** **`Failsafe Triggered:`** ${failsafeName}!\n**`🧯`** **`Countermeasures Employed:`** ${counterMeasure}\n\n```diff\n- ⌛ <Timestamp Information>```\n**`🚨`** **`Last Failsafe Trigger:`** <t:${failsafeTime}:R>\n**`🔄`** **`Last Webhook Update:`** <t:${System.currentTimeMillis() / 1000}:R>\n**`📋`** **`Last Task:`** <t:${failsafeTime}:R>",
        "color": 16711680,
        "footer": {
        "text": "💊 Meth"
      }
      }
      ],
      "attachments": []
    }
    """
  }

  private fun handleFailedWebhook(response: Response) {
    note("Failed to send/confirm webhook. Code ${response.code}. Enable Log to View More Information on Next Webhook Update.")
    log(response)
  }
}
