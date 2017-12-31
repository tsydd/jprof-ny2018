package by.tsyd.jprof.ny2018

import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentSkipListSet

/**
 * @author Dmitry Tsydzik.
 * @since 12/30/17.
 */
class LinkExtractor(
        documentChannel: BroadcastChannel<UrlDocument>,
        private val linkChannel: Channel<Link>,
        urlChannel: BroadcastChannel<String>
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val unprocessedUrls: MutableSet<String> = ConcurrentSkipListSet()

    init {
        val urlDocuments = documentChannel.openSubscription()
        val urlChannelSubscription = urlChannel.openSubscription()

        launch {
            for ((url, document) in urlDocuments) {
                document
                        ?.select("a[href]")
                        ?.asSequence()
                        ?.map { it.attr("href") }
                        ?.forEach { linkChannel.send(Link(url, it)) }

                unprocessedUrls.remove(url)

                if (unprocessedUrls.isEmpty()) {
                    logger.info("Closing urlChannel")
                    linkChannel.close()
                }
            }
        }

        launch {
            for (url in urlChannelSubscription) {
                unprocessedUrls.add(url)
            }
        }
    }
}