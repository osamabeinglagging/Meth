package dev.macrohq.meth.util

import com.google.gson.Gson
import net.minecraft.util.ChatComponentText
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

object Logger {
  private var lastDebugMessage = ""

  fun info(message: Any) {
    this.send("a§l$message")
  }

  fun note(message: Any) {
    this.send("e$message")
  }

  fun error(message: Any) {
    this.send("c$message")
  }

  fun log(message: Any) {
    if (!config.debugMode || message == this.lastDebugMessage) return
    this.lastDebugMessage = message.toString()
    this.send("7$message");
  }

  private fun send(message: String) {
    player.addChatMessage(ChatComponentText("§4[§6Meth§4] §8» §$message"))
  }
}

class Webhook() {
  private val client = OkHttpClient()
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

  fun send(jsonBody: String){

  }

  fun mapFromResponse(response: Response): Map<*, *> = Gson().fromJson(response.body!!.string(), Map::class.java)

  fun postWebhook(jsonBody: String, url: String): Response {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
      .url(url)
      .headers(headers.toHeaders())
      .post(requestBody)
      .build()

    return client.newCall(request).execute()
  }

  fun patchWebhook(jsonBody: String, url: String): Response {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
      .url(url)
      .headers(headers.toHeaders())
      .patch(requestBody)
      .build()

    return client.newCall(request).execute()
  }

  fun statusBody(uptime: Int,
                 commissionsCompleted: Int,
                 commissionPerHour: Int,
                 currentCommission: String,
                 lastFailsafeTrigger: Int,
                 lastTask: Int): String {
    return """
    {
      "content": null,
      "embeds": [
      {
        "title": "📢 Status Update",
        "description": "❓ **General Information**
        \n**👤** **Username:** ${player.name}
        \n**⌛** **Uptime:** <t:$uptime:R>
        \n**✔️** **Completed:** $commissionsCompleted
        \n**⏳** **Commission Rate:** $commissionPerHour/h
        \n**⚡** **Current Commission:** $currentCommission
        \n\n🛡️ **Failsafe Information**
        \n**🚨** **Last Trigger:** <t:$lastFailsafeTrigger:R>
        \n\n⌛ **Timestamp Information**
        \n**🚨** **Last Failsafe Trigger:** <t:$lastFailsafeTrigger:R>
        \n**🔄** **Last Webhook Update:** <t:${System.currentTimeMillis()/1000}:R>
        \n**📋** **Last Task:** <t:$lastTask:R>",
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
    """.trimIndent()
  }

  fun failsafeBody(failsafeName: String, counterMeasure: String, failsafeTime: Int): String {
    return """
        {
            "content": null,
            "embeds": [
                {
                    "title": "🆘 Failsafe Trigger",
                    "description": "```diff\n- 🚨 <Failsafe Information>```
                    \n**`🛡️`** **`Failsafe Triggered:`** $failsafeName
                    \n**`🧯`** **`Countermeasures Employed:`** $counterMeasure
                    \n\n```diff
                    \n- ⌛ <Timestamp Information>```
                    \n**`🚨`** **`Last Failsafe Trigger:`** <t:$failsafeTime:R>
                    \n**`🔄`** **`Last Webhook Update:`** <t:${System.currentTimeMillis() / 1000}:R>
                    "color": 16711680,
                    "footer": {
                        "text": "💊   Meth"
                    }
                }
            ],
            "attachments": []
        }
    """.trimIndent()
  }
}
