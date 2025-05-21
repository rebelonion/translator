# TRANSLATOR

<img src="https://sloc.xyz/github/rebelonion/translator" alt="lines of code"/> <img src="https://img.shields.io/github/languages/code-size/rebelonion/translator" alt="code size"/> [![](https://jitpack.io/v/rebelonion/translator.svg)](https://jitpack.io/#rebelonion/translator) [![](https://jitpack.io/v/rebelonion/translator/month.svg)](https://jitpack.io/#rebelonion/translator)<br> 

*A simple and free Google Translate library for Kotlin/JVM and Java.*

Fork with the following improvements:
- Updated to Java 17 and Kotlin 2.1.0
- Replaced Ktor with OkHttp for more lightweight dependency management
- Simplified codebase while maintaining the same API

## What?
A library that uses Google Translate and [OkHttp](https://square.github.io/okhttp/) to translate text.

The discovery of an unofficial API endpoint and correct parameters are not my findings, they are from [Py-Googletrans](https://github.com/ssut/py-googletrans), a great library with a similar goal, but for Python.
## Why?
Google Translate is one of the better translators out there, but their API is not free. This uses an unofficial API endpoint that does not require an API token.

I was looking for a free Google Translate library for Kotlin, but all I found was [Py-Googletrans](https://github.com/ssut/py-googletrans). This is merely a simpler Py-Googletrans, but in Kotlin.
## How?
### Adding to your project
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
    implementation 'com.github.rebelonion:translator:1.1.2'
}
```
### Usage
```kotlin
val translator = Translator()
val translation = translator.translate("Bush's translator is so cool!", Language.RUSSIAN, Language.AUTO)
println(translation.translatedText) // Переводчик Буша такой классный!
println(translation.pronunciation) // Perevodchik Busha takoy klassnyy!
println(translation.sourceLanguage) // English
```
- If you are calling from a non `suspend` function, you can use `translateBlocking(...)`
- If you want to receive a `Result<Translation>`, you can use `translateCatching(...)`
- If you want to configure your own `OkHttpClient`, you can pass it to `Translator(client)`


- Operator function `Language.invoke()` can be used to resolve a `Language` enum from a language name, code, or partial name or code:
```kotlin
Language.ENGLISH == Language("english") == Language("en") == Language("eng")
```
### Java interoperability
```java
Translator translator = new Translator();
Translation translation = translator.translateBlocking("...", Language.Companion.INSTANCE.invoke("spanish"));
translation.getTranslatedText();
```
Only `translateBlocking(...)` should be called from Java code. The other methods use Kotlin specific language features.

## Credits
- This would not be possible without [Py-Googletrans](https://github.com/ssut/py-googletrans)
- [OkHttp](https://square.github.io/okhttp/) is a lightweight and efficient HTTP client for JVM and Android
- The original version used [Ktor](https://github.com/ktorio/ktor)
- Google Translate, but they should be cool and make their API free :)

## Note
I am not affiliated with Google, and this is using an **unofficial** API. This may cease to work at any point in time, and while I will try my best to keep this project up and running, you should not rely on it working all of the time.
