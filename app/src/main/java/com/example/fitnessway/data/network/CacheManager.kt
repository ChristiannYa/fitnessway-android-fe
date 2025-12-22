package com.example.fitnessway.data.network

import com.example.fitnessway.util.Constants
import com.example.fitnessway.util.Formatters.logcat
import okhttp3.Cache

class CacheManager(private val cache: Cache) {
    fun evictUrl(url: String) {
        try {
            // Returns an iterator of all urls currently stored in the cache.
            // It lets us loop through all cached urls
            val urlIterator = cache.urls()

            // While loop is used because we have to loop through all of the urls that
            // `urlIterator` provides
            while (urlIterator.hasNext()) {
                val cachedUrl = urlIterator.next()
                if (cachedUrl.contains(url)) {
                    urlIterator.remove()
                }
            }

        } catch (e: Exception) {
            // We will just log the error instead of showing it in the UI because a cache error
            // is independent of the app's functionality.
            logcat(
                message = "evictUrl -> Failed to evict cache for $url: ${e.message}",
                level = Constants.LogLevel.ERROR
            )
        }
    }

    // @TODO: This should be used for clearing the cached data when logging out
    fun clearAll() {
        try {
            cache.evictAll()
        } catch (e: Exception) {
            logcat(
                message = "clearAll -> Failed to clear all cache: ${e.message}",
                level = Constants.LogLevel.ERROR
            )
        }
    }
}