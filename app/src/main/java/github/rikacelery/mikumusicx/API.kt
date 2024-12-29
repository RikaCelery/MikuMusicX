package github.rikacelery.mikumusicx

import android.util.Log
import github.rikacelery.mikumusicx.domain.model.Song
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
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
                override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean =
                    size > capacity
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
            install(HttpRedirect) {
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                retryOnException()
            }
        }

    private val cache = LRUCache<String, String>(500)
    private val cache2 = LRUCache<String, Song>(500)

    suspend fun fetchInfo(musicId: String): Song {
        if (cache2.get(musicId) != null) {
            return cache2.get(musicId)!!
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
        val music = Song(
            title = json.jsonObject["title"]!!.jsonPrimitive.content,
            subtitle = html.selectFirst("meta[property=\"og:music:artist\"]")?.attr("content") ?: "",
            mediaId = musicId,
            imageUrl = cover,
            desc = html.selectFirst("meta[property=\"og:description\"]")?.attr("content") ?: "",
        )
        cache2[musicId] = music
        cache[musicId] = cover
        return music
    }

    suspend fun playable(id: String): Boolean {
        runCatching { client.head("https://music.163.com/song/media/outer/url?id=$id").status }.onSuccess {
            Log.i("Check", "$id, $it")
            return it == HttpStatusCode.OK || it == HttpStatusCode.Found
        }.onFailure {
            return false
        }
        error("")
    }

    suspend fun fetchCover(musicId: String): String {
        if (musicId.isBlank()){
            Log.e("FETCH", "musicId is blank")
            return ""
        }
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

    suspend fun redirect(link: String): String {
        var u = link
        val s1 = client.head(u)
        return s1.request.url.toString()
    }

    override fun close() {
        client.close()
    }
}
