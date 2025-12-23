package com.embabel.template

import com.embabel.agent.api.invocation.AgentInvocation
import com.embabel.agent.core.AgentPlatform
import com.embabel.agent.domain.io.UserInput
import com.embabel.template.agent.ReviewedStory
import com.embabel.template.injected.InjectedDemo
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class DemoShell(
    private val injectedDemo: InjectedDemo,
    private val agentPlatform: AgentPlatform,
) {

    @ShellMethod("Demo")
    fun demo(): String {
        // Illustrate calling an agent programmatically,
        // as most often occurs in real applications.
        val reviewedStory = AgentInvocation
            .create(agentPlatform, ReviewedStory::class.java)
            .invoke(UserInput("Tell me a story about caterpillars"))
        return reviewedStory.content
    }

    @ShellMethod("Invent an animal")
    fun animal(): String {
        return injectedDemo.inventAnimal().toString()
    }
}
