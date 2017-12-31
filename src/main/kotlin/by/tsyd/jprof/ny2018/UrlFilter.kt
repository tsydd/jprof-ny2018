package by.tsyd.jprof.ny2018

import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Dmitry Tsydzik.
 * @since 12/30/17.
 */
class UrlFilter(
        private val linkChannel: Channel<Link>,
        private val urlChannel: BroadcastChannel<String>
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val visitedUrls: MutableSet<String> = HashSet()
    private val urlPrefix = "https://jprof.by"

    init {
        launch {
            for ((parent, url_) in linkChannel) {
                var url = url_

                if (url.startsWith('#') || url.startsWith("javascript:")) {
                    continue
                }

                if (url.startsWith('/')) {
                    url = urlPrefix + url
                }

                if ("://" !in url) {
                    url = parent + url
                }

                if (!url.startsWith(urlPrefix)) {
                    logger.debug("Skip {}. Bad prefix. Parent: {}", url, parent)
                    continue
                }

                val extension = url.substringAfterLast('/')
                        .substringAfterLast('.', "")

                if (extension.isNotEmpty()) {
                    logger.debug("Skip {}. Has extension", url)
                    continue
                }

                if (!url.endsWith('/')) {
                    url += '/'
                }

                val added = visitedUrls.add(url)
                if (added) {
                    logger.info("New url: {}", url)
                    urlChannel.send(url)
                }
            }
            urlChannel.close()
        }
    }
}