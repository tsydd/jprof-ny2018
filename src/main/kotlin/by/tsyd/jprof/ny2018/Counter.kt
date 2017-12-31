package by.tsyd.jprof.ny2018

import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern

/**
 * @author Dmitry Tsydzik.
 * @since 12/30/17.
 */
class Counter(
        documentChannel: BroadcastChannel<UrlDocument>,
        private val finishedLatch: CountDownLatch
) {
    private val pattern = "java".toPattern(Pattern.CASE_INSENSITIVE)
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    var totalCounter: Int = 0

    init {
        val subscription = documentChannel.openSubscription()
        launch {
            for ((url, document) in subscription) {
                if (document == null) {
                    continue
                }
                val count = countJava(document.body().text())
                if (count > 0) {
                    logger.info("Found {} occurrence(s) of 'java' in {}", count, url)
                    totalCounter += count
                }
            }
            logger.info("Finished counter")
            finishedLatch.countDown()
        }
    }

    private fun countJava(str: String): Int {
        val matcher = pattern.matcher(str)
        var count = 0
        while (matcher.find()) {
            count++
        }
        return count
    }
}