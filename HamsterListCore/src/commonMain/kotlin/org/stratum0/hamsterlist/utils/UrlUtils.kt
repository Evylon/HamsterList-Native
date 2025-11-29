package org.stratum0.hamsterlist.utils

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.parseUrl
import org.stratum0.hamsterlist.models.HamsterList

/**
 * Parse a url from the given [urlString].
 * If the parsing failed, parsing is tried again with an added HTTPS scheme.
 * This allows for user input urls like "example.com".
 * @return the parsed url or null, if the [urlString] could not be parsed.
 */
fun parseUrlLenient(urlString: String): Url? {
    return if (urlString.startsWith(URLProtocol.HTTP.name)) {
        parseUrl(urlString)
    } else {
        parseUrl(URLProtocol.HTTPS.name + "://" + urlString)
    }
}

fun parseHamsterListFromUrl(urlString: String): HamsterList? {
    return parseUrlLenient(urlString)?.let { url ->
        if (url.segments.size != 1) return null
        HamsterList(
            listId = url.segments.first(),
            serverHostName = URLBuilder(
                protocol = URLProtocol.HTTPS,
                host = url.host,
                port = url.port
            ).buildString()
        )
    }
}
