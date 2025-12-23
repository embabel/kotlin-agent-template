package com.embabel.template.agent

import com.embabel.agent.domain.io.UserInput
import com.embabel.agent.test.unit.FakeOperationContext
import com.embabel.agent.test.unit.FakePromptRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * Unit tests for the WriteAndReviewAgent class.
 * Tests the agent's ability to craft and review stories based on user input.
 */
internal class WriteAndReviewAgentTest {

    /**
     * Tests the story crafting functionality of the WriteAndReviewAgent.
     * Verifies that the LLM call contains expected content and configuration.
     */
    @Test
    fun testCraftStory() {
        // Create agent with word limits: 200 min, 400 max
        val agent = WriteAndReviewAgent(200, 400)
        val context = FakeOperationContext.create()
        val promptRunner = context.promptRunner() as FakePromptRunner

        context.expectResponse(Story("One upon a time Sir Galahad . . "))

        agent.craftStory(
            UserInput("Tell me a story about a brave knight", Instant.now()),
            context
        )

        // Verify the prompt contains the expected keyword
        assertTrue(
            promptRunner.llmInvocations.first().messages.single().content.contains("knight"),
            "Expected prompt to contain 'knight'"
        )


        // Verify the temperature setting for creative output
        val actual = promptRunner.llmInvocations.first().interaction.llm.temperature
        assertEquals(
            0.7, actual!!, 0.01,
            "Expected temperature to be 0.7: Higher for more creative output"
        )
    }

    /**
     * Tests the story review functionality of the WriteAndReviewAgent.
     * Verifies that the review process includes expected prompt content.
     */
    @Test
    fun testReview() {
        // Create agent with word limits
        val agent = WriteAndReviewAgent(200, 400)

        // Set up test data
        val userInput = UserInput("Tell me a story about a brave knight", Instant.now())
        val story = Story("Once upon a time, Sir Galahad...")

        // Create fake context and set expected response
        val context = FakeOperationContext.create()
        context.expectResponse("A thrilling tale of bravery and adventure!")

        // Execute the review
        agent.reviewStory(userInput, story, context)

        // Verify the LLM invocation contains expected content
        val llmInvocation = context.llmInvocations.singleOrNull()
            ?: error("Expected a single LLM invocation, not ${context.llmInvocations.single()}")
        val prompt: String = llmInvocation.messages.first().content
        assertTrue(
            prompt.contains("knight"),
            "Expected prompt to contain 'knight'"
        )
        assertTrue(
            prompt.contains("review"),
            "Expected prompt to contain 'review'"
        )
    }
}
