package com.kaizensundays.fusion.kappa.plugin

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.Test

/**
 * Created: Saturday 1/6/2024, 5:39 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class MavenPluginArchTest {

    private fun classes() = ClassFileImporter().importPackages("com.kaizensundays.fusion.kappa.plugin")

    @Test
    fun pluginCore() {

        val classes = classes()

        ArchRuleDefinition.noClasses()
            .that().resideInAPackage("..kappa.plugin.core")
            .and().haveNameNotMatching(".*Test.*")
            .should().accessClassesThat().resideOutsideOfPackages(
                "java..",
                "kotlin..",
                "kotlinx..",
                "com.kaizensundays.fusion..",
            )
            .check(classes)

    }

}