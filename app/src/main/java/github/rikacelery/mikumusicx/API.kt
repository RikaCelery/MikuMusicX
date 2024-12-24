package github.rikacelery.mikumusicx

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.Jsoup

object API : AutoCloseable {
    private class LRUCache<K, V>(
        private val capacity: Int,
    ) {
        private val cache =
            object : LinkedHashMap<K, V>(capacity, 0.75f, true) {
                override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean = size > capacity
            }

        operator fun get(key: K): V? = cache[key]

        operator fun set(
            key: K,
            value: V,
        ) {
            cache[key] = value
        }

        override fun toString(): String = cache.toString()
    }

    val client =
        HttpClient(OkHttp) {
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                retryOnException()
            }
        }

    private val cache = LRUCache<Long, String>(500)

    suspend fun fetchCover(musicId: Long): String {
        if (cache.get(musicId) != null) {
            return cache.get(musicId)!!
        }
        val resp =
            client
                .get("https://music.163.com/song") {
                    parameter("id", musicId)
                }.bodyAsText()
        val html = Jsoup.parse(resp)
        val jsonScript = html.selectFirst("script[type=\"application/ld+json\"]")
        requireNotNull(jsonScript)
        val json = Json.parseToJsonElement(jsonScript.html())
        val cover =
            json.jsonObject["images"]!!
                .jsonArray
                .first()
                .jsonPrimitive.content
        cache[musicId] = cover
        return cover
    }

    override fun close() {
        client.close()
    }
}
