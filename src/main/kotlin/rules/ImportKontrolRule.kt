package rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.utils.addToStdlib.indexOfOrNull

class ImportKontrolRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = "ImportKontrol",
        severity = Severity.Maintainability,
        description = "ImportKontrol",
        debt = Debt.TWENTY_MINS
    )

    private lateinit var basePackage: String
    private var currentPackage: String? = null

    override fun preVisit(root: KtFile) {
        super.preVisit(root)
        basePackage = valueOrNull("base_package") ?: throw RuntimeException("Missing base package key!")
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
        val globalAllows = valueOrDefault("allow", emptyList<String>())
        return globalAllows.any { it == importPath }
    }

    private fun isSubpackageAllow(importPath: String): Boolean {
        val importPathWithoutBase = importPath.replace(basePackage, "").substring(1)
        val subpackage = importPathWithoutBase
            .substring(0, importPathWithoutBase.indexOfOrNull('.') ?: importPathWithoutBase.length)
        val subpackages = valueOrDefault("subpackage", emptyMap<String, Map<String, List<String>>>())
        val subpackageMap = subpackages.getOrDefault("domain", emptyMap())
        val subpackageAllows = subpackageMap.getOrDefault("allow", emptyList())
        return subpackageAllows.any { it == subpackage }
    }
    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        currentPackage = directive.qualifiedName
    }
}