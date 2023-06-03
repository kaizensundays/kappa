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
@Disabled
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

        noClasses()
            .that().resideInAPackage("..kappa.service")
            .should()
            .accessClassesThat().haveNameMatching(".*KtorServer")
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
            .should()
            .accessClassesThat().resideInAPackage("..kappa.service")
            .check(classes)

        noClasses()
            .that()
            .resideInAPackage("com.kaizensundays.fusion.kappa")
            .and().haveNameNotMatching(".*(KtorServer|Context|IntegrationTest).*")
            .should()
            .accessClassesThat().resideInAPackage("..kappa.service")
            .check(classes)

    }

}