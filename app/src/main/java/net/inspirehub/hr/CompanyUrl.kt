package net.inspirehub.hr

import android.content.Context

/**
 * Reads the company URL every API call is built on.
 *
 * Ktor fills in its own defaults for a URL with no scheme and no host, so a blank
 * or half saved company URL is not an error there - the request quietly goes to
 * http://localhost and comes back as "connection refused", which says nothing
 * about the real problem. This fails early with a message that does.
 */
fun resolveCompanyUrl(context: Context): String {

    val url = SharedPrefManager(context).getCompanyUrl()?.trim().orEmpty()

    if (url.isEmpty()) {
        throw IllegalStateException(
            "Company URL is missing - no company has been saved for this employee yet"
        )
    }

    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        throw IllegalStateException(
            "Company URL has no scheme: $url"
        )
    }

    return url.trimEnd('/')
}
