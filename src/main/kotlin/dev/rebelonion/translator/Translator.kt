package dev.rebelonion.translator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * [A translator that uses Google's translate API.](https://github.com/therealbush/translator)
 *
 * @author bush, py-googletrans + contributors
 * @since 1.0.0
 *
 * @property client The HTTP client to use for requests to Google's API.
 *                  This should be kept as default unless you know what you are doing.
 */
class Translator(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()
            chain.proceed(requestWithUserAgent)
        }
        .build()
) {

    /**
     * Translates the given string to the desired language.
     * ```
     * translate("Text to be translated", Language.RUSSIAN)
     * translate("Text to be translated", ALBANIAN, ENGLISH)
     * ```
     * @param text   The text to be translated.
     * @param target The language to translate [text] to.
     * @param source The language of [text]. By default, this is [Language.AUTO].
     *
     * @return A [Translation] containing the translated text and other related data.
     * @throws TranslationException If the HTTP request could not be completed.
     *
     * @see translateCatching
     * @see translateBlocking
     * @see translateBlockingCatching
     *
     * @see Translation
     */
    suspend fun translate(
        text: String,
        target: Language,
        source: Language = Language.AUTO
    ): Translation {
        require(target != Language.AUTO) {
            "The target language cannot be Language.AUTO!"
        }
        
        val httpUrl = "https://translate.googleapis.com/translate_a/single".toHttpUrl()
            .newBuilder()
            .apply {
                // Add constant parameters
                addQueryParameter("client", "gtx")
                dtParams.forEach { addQueryParameter("dt", it) }
                addQueryParameter("ie", "UTF-8")
                addQueryParameter("oe", "UTF-8")
                addQueryParameter("otf", "1")
                addQueryParameter("ssel", "0")
                addQueryParameter("tsel", "0")
                addQueryParameter("tk", "bushissocool")
                
                // Add translation-specific parameters
                addQueryParameter("sl", source.code)
                addQueryParameter("tl", target.code)
                addQueryParameter("hl", target.code)
                addQueryParameter("q", text)
            }
            .build()
            
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()
            
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).await()
        }
        
        if (!response.isSuccessful) {
            throw TranslationException("Error caught from HTTP request: ${response.code}")
        }
        
        val responseBody = response.body?.string() 
            ?: throw TranslationException("Response body was null")
            
        return Translation(target, text, responseBody, httpUrl)
    }
    
    private suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }
            
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(TranslationException("Exception caught from HTTP request", e))
            }
        })
    }

    /**
     * Translates the given string to the desired language,
     * and returns a [Result] containing the [Translation].
     * ```
     * translateCatching("Text to be translated", Language.RUSSIAN)
     * translateCatching("Text to be translated", ALBANIAN, ENGLISH)
     * ```
     * @param text   The text to be translated.
     * @param target The language to translate [text] to.
     * @param source The language of [text]. By default, this is [Language.AUTO].
     *
     * @return A [Result] containing the [Translation], or a [TranslationException]
     *         If the HTTP request could not be completed.
     *
     * @see translate
     * @see translateBlocking
     * @see translateBlockingCatching
     *
     * @see Translation
     */
    suspend fun translateCatching(
        text: String,
        target: Language,
        source: Language = Language.AUTO
    ): Result<Translation> = runCatching { translate(text, target, source) }

    /**
     * Translates the given string to the desired language,
     * blocking the current thread until completion.
     * ```
     * translateBlocking("Text to be translated", Language.RUSSIAN)
     * translateBlocking("Text to be translated", ALBANIAN, ENGLISH)
     * ```
     * @param text   The text to be translated.
     * @param target The language to translate [text] to.
     * @param source The language of [text]. By default, this is [Language.AUTO].
     *
     * @return A [Translation] containing the translated text and other related data.
     * @throws TranslationException If the HTTP request could not be completed.
     *
     * @see translate
     * @see translateCatching
     * @see translateBlockingCatching
     */
    @JvmOverloads
    fun translateBlocking(
        text: String,
        target: Language,
        source: Language = Language.AUTO
    ): Translation = runBlocking { translate(text, target, source) }

    /**
     * Translates the given string to the desired language,
     * blocking the current thread until completion and
     * returning a [Result] containing the [Translation].
     * ```
     * translateBlockingCatching("Text to be translated", Language.RUSSIAN)
     * translateBlockingCatching("Text to be translated", ALBANIAN, ENGLISH)
     * ```
     * @param text   The text to be translated.
     * @param target The language to translate [text] to.
     * @param source The language of [text]. By default, this is [Language.AUTO].
     *
     * @return A [Result] containing the [Translation], or a [TranslationException]
     *         If the HTTP request could not be completed.
     *
     * @see translate
     * @see translateCatching
     * @see translateBlocking
     *
     * @see Translation
     */
    fun translateBlockingCatching(
        text: String,
        target: Language,
        source: Language = Language.AUTO
    ): Result<Translation> = runBlocking { translateCatching(text, target, source) }
}

// I didn't find these myself, check out https://github.com/ssut/py-googletrans
private val dtParams = arrayOf("at", "bd", "ex", "ld", "md", "qca", "rw", "rm", "ss", "t")

/**
 * Indicates an exception/error relating to the translation's HTTP request.
 */
class TranslationException(message: String, cause: Throwable? = null) : Exception(message, cause)
