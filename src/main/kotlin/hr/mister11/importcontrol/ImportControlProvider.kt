package hr.mister11.importcontrol

import hr.mister11.importcontrol.rules.ImportControlRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class ImportControlProvider : RuleSetProvider {
    override val ruleSetId: String = "import-control"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ImportControlRule(config)
        )
    )
}