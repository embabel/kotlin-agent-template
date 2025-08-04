/*
 * Copyright 2024-2025 Embabel Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.embabel.template.agent

import com.embabel.agent.api.annotation.*
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.api.common.create
import com.embabel.agent.domain.io.UserInput
import com.embabel.agent.domain.library.HasContent
import com.embabel.agent.prompt.persona.Persona
import com.embabel.common.ai.model.LlmOptions
import com.embabel.common.ai.model.ModelSelectionCriteria.Companion.Auto
import com.embabel.common.core.types.Timestamped
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val StoryTeller = Persona(
    name = "Roald Dahl",
    persona = "A creative storyteller who loves to weave imaginative tales that are a bit unconventional",
    voice = "Quirky",
    objective = "Create memorable stories that captivate the reader's imagination.",
)

val Reviewer = Persona(
    name = "Media Book Review",
    persona = "New York Times Book Reviewer",
    voice = "Professional and insightful",
    objective = "Help guide readers toward good stories",
)

data class Story(
    val text: String,
)

data class ReviewedStory(
    val story: Story,
    val review: String,
    val reviewer: Persona,
) : HasContent, Timestamped {

    override val timestamp: Instant
        get() = Instant.now()

    override val content: String
        get() = """
            # Story
            ${story.text}

            # Review
            $review

            # Reviewer
            ${reviewer.name}, ${
            timestamp.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))
        }
        """.trimIndent()
}


@Agent(
    description = "Generate a story based on user input and review it",
)
@Profile("!test")
class WriteAndReviewAgent(
    @param:Value("\${storyWordCount:100}") private val storyWordCount: Int,
    @param:Value("\${reviewWordCount:100}") private val reviewWordCount: Int,
) {

    @Action
    fun craftStory(userInput: UserInput, context: OperationContext): Story =
        context.promptRunner().withLlm(LlmOptions(criteria = Auto, temperature = 0.9))
            .withPromptContributor(StoryTeller)
            .create<Story>("""
            Craft a short story in $storyWordCount words or less.
            The story should be engaging and imaginative.
            Use the user's input as inspiration if possible.
            If the user has provided a name, include it in the story.

            # User input
            ${userInput.content}
        """.trimIndent())

    @AchievesGoal(
        description = "The user has been greeted",
        export = Export(remote = true, name = "writeAndReviewStory")
    )
    @Action
    fun reviewStory(userInput: UserInput, story: Story, context: OperationContext): ReviewedStory {
        val review = context.promptRunner(
            LlmOptions(criteria = Auto)
        ).withPromptContributor(Reviewer)
            .generateText(
                """
            You will be given a short story to review.
            Review it in $reviewWordCount words or less.
            Consider whether or not the story is engaging, imaginative, and well-written.
            Also consider whether the story is appropriate given the original user input.

            # Story
            ${story.text}

            # User input that inspired the story
            ${userInput.content}
        """.trimIndent()
            )
        return ReviewedStory(
            story = story,
            review = review,
            reviewer = Reviewer,
        )
    }

}
