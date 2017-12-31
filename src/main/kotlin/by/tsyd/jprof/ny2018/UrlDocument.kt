package by.tsyd.jprof.ny2018

import org.jsoup.nodes.Document

/**
 * @author Dmitry Tsydzik.
 * @since 12/30/17.
 */
data class UrlDocument(
        val url: String,
        val document: Document?
)