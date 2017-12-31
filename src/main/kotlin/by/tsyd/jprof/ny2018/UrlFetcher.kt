package by.tsyd.jprof.ny2018

import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * @author Dmitry Tsydzik.
 * @since 12/30/17.
 */
class UrlFetcher(
        urlChannel: BroadcastChannel<String>,
        private val documentChannel: BroadcastChannel<UrlDocument>
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val httpClient = OkHttpClient()

    init {
        val urlChannelSubscription = urlChannel.openSubscription()
        launch {
            for (url in urlChannelSubscription) {
                logger.info("Received {}", url)
                launch {
                    fetch(url)
                }
            }
            logger.info("Closing documentChannel")
            httpClient.dispatcher().executorService().shutdown()
            documentChannel.close()
        }
    }

    private suspend fun fetch(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()
        try {
            val response = httpClient.newCall(request).await()
            if (!response.isSuccessful) {
                logger.info("Skip {}", response)
                documentChannel.send(UrlDocument(url, null))
                return
            }
            response.body()
                    ?.string()
                    ?.let(Jsoup::parse)
                    ?.let { documentChannel.send(UrlDocument(url, it)) }
        } catch (e: Exception) {
            logger.error("Failed to fetch {}", url, e)
            documentChannel.send(UrlDocument(url, null))
        }
    }

    private suspend fun Call.await(): Response = suspendCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }
        })
    }
}