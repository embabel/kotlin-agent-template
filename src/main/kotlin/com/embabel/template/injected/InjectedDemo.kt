package com.embabel.template.injected

import com.embabel.agent.api.common.Ai
import jakarta.validation.constraints.Pattern
import org.springframework.stereotype.Component

/**
 * Demonstrate injection of Embabel's OperationContext into a Spring component.
 *
 * @param ai Embabel AI helper, injected by Spring
 */
@Component
class InjectedDemo(private val ai: Ai) {

    /**
     * Demonstrates use of JSR-380 validation annotations on record fields
     * to constrain generated content.
     */
    data class Animal(
        val name: String,
        @field:Pattern(regexp = ".*ox.*", message = "Species must contain 'ox'")
        val species: String,
    )

    fun inventAnimal(): Animal {
        return ai
            .withDefaultLlm()
            .withId("invent-animal")
            .creating(Animal::class.java)
            .withExample("good example", Animal("Fluffox", "Magicox"))
            .withExample("bad example: does not pass validation", Animal("Sparky", "Dragon"))
            .fromPrompt(
                """
                You just woke up in a magical forest.
                Invent a fictional animal.
                The animal should have a name and a species.
                """.trimIndent(),
            )
    }
}
