package by.tsyd.jprof.ny2018

import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.Channel
import java.util.concurrent.CountDownLatch

/**
 * @author Dmitry Tsydzik.
 * @since 12/30/17.
 */
object Module {
    val linkChannel = Channel<Link>()

    val urlChannel = BroadcastChannel<String>(100)

    val documentChannel = BroadcastChannel<UrlDocument>(100)

    val finishedLatch = CountDownLatch(1)

    val urlFilter = UrlFilter(linkChannel, urlChannel)

    val urlFetcher = UrlFetcher(urlChannel, documentChannel)

    val linkExtractor = LinkExtractor(documentChannel, linkChannel, urlChannel)

    val counter = Counter(documentChannel, finishedLatch)

}