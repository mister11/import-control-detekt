
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.junit.jupiter.api.Test
import rules.ImportKontrolRule

class ImportKontrolRuleTest {

    @Test
    fun test() {
        val rule = ImportKontrolRule(yamlConfig("config.yml"))

        val findings = rule.lint(source)

        assertThat(findings).isEmpty()
    }

    private val source = """
        package org.test.interfaces
        
        import org.test.domain
        
        fun main() {
        }
    """.trimIndent()
}