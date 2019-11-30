package hr.mister11.importcontrol.rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPackageDirective

class ImportControlRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = "ImportControl",
        severity = Severity.Maintainability,
        description = "ImportControl",
        debt = Debt.TWENTY_MINS
    )

    private lateinit var currentPackage: String
    private lateinit var configuration: Configuration

    override fun preVisit(root: KtFile) {
        super.preVisit(root)
        if (::configuration.isInitialized.not()) {
            this.configuration = parseConfiguration()
        }
    }

    private fun parseConfiguration(): Configuration {
        val basePackage = valueOrNull<String>("base_package") ?: throw RuntimeException("Missing base package property.")
        val globallyAllowedPackages = valueOrDefault("allow", emptyList<String>())
        val subpackages = valueOrDefault("subpackage", emptyMap<String, Map<String, List<String>>>())
        return Configuration(
            basePackage = basePackage,
            globallyAllowedPackages = globallyAllowedPackages,
            subpackageConfigurations = subpackages.map { (subpackageName, subpackageProps) ->
                SubpackageConfiguration(
                    subpackageName = subpackageName,
                    subpackageAllowedImports = subpackageProps.getOrDefault("allow", emptyList())
                )
            }
        )
    }

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)
        val importFullName = importDirective.importPath?.pathStr
        importFullName?.let {
            if (isGlobalAllow(it).not()) {
               if (isSubpackageAllow(it).not()) {
                   report(CodeSmell(
                       issue = issue,
                       entity = Entity.from(importDirective),
                       message = "Bla"
                   ))
               }
            }
        }
    }

    private fun isGlobalAllow(importPath: String): Boolean {
        return this.configuration.globallyAllowedPackages.any { Regex(".*${it}.*").matches(importPath) }
    }

    private fun isSubpackageAllow(importPath: String): Boolean {

        val currentPackageRules = findCurrentPackageRules()
        if (currentPackageRules.isEmpty()) {
            return true
        }

        return currentPackageRules
            .map { Regex("${this.configuration.basePackage}\\.${it}.*") }
            .any { it.matches(importPath) }
    }

    private fun findCurrentPackageRules(): List<String> {
        val currentPackageTokens = currentPackage.split(".")
        val definedSubpackages = this.configuration.subpackageConfigurations.map { it.subpackageName }
        return currentPackageTokens
            .reversed()
            .firstOrNull { packageToken -> definedSubpackages.contains(packageToken) }
            ?.let { packageToken ->
                this.configuration.subpackageConfigurations.find { config -> config.subpackageName == packageToken }
            }
            ?.subpackageAllowedImports
            .orEmpty()
    }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        currentPackage = directive.qualifiedName
    }

    data class Configuration(
        val basePackage: String,
        val globallyAllowedPackages: List<String>,
        val subpackageConfigurations: List<SubpackageConfiguration>
    )

    data class SubpackageConfiguration(
        val subpackageName: String,
        val subpackageAllowedImports: List<String>
    )
}