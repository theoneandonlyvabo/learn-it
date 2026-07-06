package com.learnit.app.data.remote

import com.learnit.app.BuildConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolves a topic to a stock photo URL via Unsplash. Results are cached in memory for the
 * process lifetime so the dashboard doesn't re-hit the API on every recomposition.
 *
 * ponytail: in-memory cache only — lost on process death, re-fetched next launch. Fine for a
 * handful of decks. Persist the URL on the deck if the free-tier rate limit (50 req/hr) bites.
 */
@Singleton
class DeckImageProvider @Inject constructor(
    private val api: UnsplashApiService
) {
    private val cache = mutableMapOf<String, String?>()
    private val mutex = Mutex()

    suspend fun imageUrlFor(topic: String): String? {
        val key = topic.trim()
        if (key.isBlank() || BuildConfig.UNSPLASH_ACCESS_KEY.isBlank()) return null

        mutex.withLock { if (cache.containsKey(key)) return cache[key] }

        val url = runCatching {
            api.search(query = key, authorization = "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
                .results.firstOrNull()?.urls?.small
        }.getOrNull()

        mutex.withLock { cache[key] = url }
        return url
    }
}
