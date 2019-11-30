
import hr.mister11.importcontrol.rules.ImportControlRule
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test

class ImportControlRuleTest {

    private val rule = ImportControlRule(yamlConfig("config.yml"))

    @Test
    fun samePackageTest() {
        val findings = rule.lint(samePackageSource)
        assertThat(findings).isEmpty()
    }

    @Test
    fun allowedPackageTest() {
        val findings = rule.lint(allowedPackageSource)
        assertThat(findings).isEmpty()
    }

    @Test
    fun disallowedPackageTest() {
        val findings = rule.lint(disallowedPackageSource)
        assertThat(findings).size().isOne
        assertThat(findings[0]).`is`(object : Condition<Finding>() {
            override fun matches(value: Finding?): Boolean {
                return value?.issue?.id == "ImportControl"
            }
        })
    }
}