package by.tsyd.jprof.ny2018

import kotlinx.coroutines.experimental.runBlocking

/**
 * @author Dmitry Tsydzik.
 * @since 12/30/17.
 */
fun main(args: Array<String>) {
    runBlocking {
        Module.linkChannel.send(Link("", "https://jprof.by/"))
    }

    Module.finishedLatch.await()
    println("Total 'java' count: ${Module.counter.totalCounter}")
}