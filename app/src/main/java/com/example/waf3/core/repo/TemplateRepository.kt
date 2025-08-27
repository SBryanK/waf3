package com.example.waf3.core.repo

import android.content.Context
import com.example.waf3.core.template.DefaultUITemplateRenderer
import com.example.waf3.core.template.UIConfiguration
import com.example.waf3.core.model.TestDefinition

class TemplateRepository(private val context: Context) {
    private val renderer = DefaultUITemplateRenderer()

    fun loadConfiguration(): UIConfiguration {
        val json = runCatching {
            context.assets.open("ui_template.json").bufferedReader().use { it.readText() }
        }.getOrElse { DEFAULT_JSON }
        return renderer.parseTemplate(json)
    }

    fun getTests(): List<TestDefinition> = renderer.renderTests(loadConfiguration())

    companion object {
        private const val DEFAULT_JSON = """
            {
              "tests": [
                {"id":"sql_injection_get","name":"SQL Injection - GET","category":"waf_detection","defaultPathSuffix":"?q=1' OR '1'='1"},
                {"id":"xss_get","name":"XSS Payload - GET","category":"waf_detection","defaultPathSuffix":"?q=<script>alert(1)</script>"},
                {"id":"lfi_get","name":"Local File Inclusion - GET","category":"waf_detection","defaultPathSuffix":"?file=../../../etc/passwd"},
                {"id":"ua_suspicious","name":"Suspicious User-Agent","category":"bot_detection","defaultHeaders":{"User-Agent":"python-requests/2.28.0"}},
                {"id":"https_redirect","name":"HTTPS Redirect","category":"http_tls"},
                {"id":"cache_bypass","name":"Cache Bypass","category":"cdn","defaultPathSuffix":"?nocache=ts"}
              ]
            }
        """
    }
}


