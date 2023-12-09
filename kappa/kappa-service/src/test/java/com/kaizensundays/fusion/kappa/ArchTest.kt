package com.kaizensundays.fusion.kappa

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * Created: Sunday 8/21/2022, 1:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ArchTest {

    private fun classes() = ClassFileImporter().importPackages("com.kaizensundays.fusion.kappa")

    @Disabled
    fun privateFieldsAndFunctionsInTest() {

        val classes = classes()

        arrayOf(

            methods()
                .that()
                .areNotAnnotatedWith(Test::class.java)
                .and()
                .areNotAnnotatedWith(BeforeEach::class.java)
                .and()
                .areDeclaredInClassesThat().haveNameMatching(".*Test")
                .and()
                .areDeclaredInClassesThat().haveNameNotMatching(".*IntegrationTest")
                .should().bePrivate(),

            ).forEach { rule -> rule.check(classes) }


    }

    @Test
    fun serviceLayer() {

        val classes = classes()

        noClasses().that().resideInAPackage("..kappa.service")
            .and().haveNameNotMatching(".*Test.*")
            .should().accessClassesThat().resideOutsideOfPackages(
                "java..",
                "kotlin..",
                "kotlinx..",
                "org.slf4j..",
                "org.apache.commons..",
                "org.apache.maven..",
                "com.fasterxml..",
                "javax.cache..",
                "com.kaizensundays.fusion..",
            )
            .check(classes)

        noClasses().that().resideInAPackage("..kappa.messages")
            .and().haveNameNotMatching(".*Test.*")
            .should().accessClassesThat().resideOutsideOfPackages(
                "java..",
                "kotlin..",
                "kotlinx..",
                "com.kaizensundays.fusion.kappa.event..",
                "com.kaizensundays.fusion.kappa.messages..",
            )
            .check(classes)

        noClasses()
            .that()
            .resideInAPackage("..kappa.cache")
            .should()
            .accessClassesThat().resideInAPackage("..kappa.service")
            .check(classes)

        noClasses()
            .that()
            .resideInAPackage("..kappa.os")
            .and().haveNameNotMatching(".*(Test|CommandBuilder|Os)")
            .should()
            .accessClassesThat().resideInAPackage("..kappa.service")
            .check(classes)

    }

}