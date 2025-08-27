package com.example.waf3.core.template

import com.example.waf3.core.model.TestCategory
import com.example.waf3.core.model.TestDefinition
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

interface UITemplateRenderer {
    fun parseTemplate(json: String): UIConfiguration
    fun renderTests(config: UIConfiguration): List<TestDefinition>
    fun applyTheme(theme: ThemeConfig)
    fun localizeStrings(locale: String, strings: Map<String, String>)
}

@Serializable
data class UIConfiguration(
    val tests: List<TemplateTest>,
    val theme: ThemeConfig? = null,
    val strings: Map<String, String> = emptyMap(),
)

@Serializable
data class TemplateTest(
    val id: String,
    val name: String,
    val category: String,
    val description: String = "",
    val defaultMethod: String = "GET",
    val defaultPathSuffix: String = "",
    val defaultHeaders: Map<String, String> = emptyMap(),
    val defaultBody: String? = null,
)

@Serializable
data class ThemeConfig(
    val primary: String? = null,
    val background: String? = null,
)

class DefaultUITemplateRenderer : UITemplateRenderer {
    private val json = Json { ignoreUnknownKeys = true }

    override fun parseTemplate(json: String): UIConfiguration {
        return this.json.decodeFromString(UIConfiguration.serializer(), json)
    }

    override fun renderTests(config: UIConfiguration): List<TestDefinition> {
        return config.tests.map {
            TestDefinition(
                id = it.id,
                name = it.name,
                category = it.category.toTestCategory(),
                description = it.description,
                defaultMethod = it.defaultMethod,
                defaultPathSuffix = it.defaultPathSuffix,
                defaultHeaders = it.defaultHeaders,
                defaultBody = it.defaultBody,
            )
        }
    }

    override fun applyTheme(theme: ThemeConfig) {
        // Hook for runtime theming via composition locals if needed later
    }

    override fun localizeStrings(locale: String, strings: Map<String, String>) {
        // Hook: could populate a string provider used by UI
    }
}

private fun String.toTestCategory(): TestCategory = when (lowercase()) {
    "waf_detection", "waf" -> TestCategory.WAF_DETECTION
    "bot_detection", "bot" -> TestCategory.BOT_DETECTION
    "http_tls", "http", "tls" -> TestCategory.HTTP_TLS
    "cdn" -> TestCategory.CDN
    "ddos_lite", "ddos" -> TestCategory.DDOS_LITE
    else -> TestCategory.WAF_DETECTION
}


