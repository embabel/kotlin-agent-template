<img align="left" src="https://github.com/embabel/embabel-agent/blob/main/embabel-agent-api/images/315px-Meister_der_Weltenchronik_001.jpg?raw=true" width="180">

# Generated Agent Project

![Build](https://github.com/embabel/kotlin-agent-template/actions/workflows/maven.yml/badge.svg)

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white) ![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)

<br clear="left"/>


Starting point for your own agent development using the [Embabel framework](https://github.com/embabel/embabel-agent).

Uses Spring Boot 3.5.9 and Embabel 0.3.1.

Add your magic here!

Illustrates:

- A simple agent using the Embabel `@Agent` and `@Action` annotations
- Unit tests for an agent verifying prompts and hyperparameters

> For the Java equivalent, see
> our [Java agent template](https://github.com/embabel/java-agent-template).

# Running

Run the shell script to start Embabel under Spring Shell:

```bash
./scripts/shell.sh
```

There is a single example
agent, [WriteAndReviewAgent](./src/main/kotlin/com/embabel/template/agent/WriteAndReviewAgent.kt).
It uses one LLM with a high temperature and creative persona to write a story based on your input,
then another LLM with a low temperature and different persona to review the story.

When the Embabel shell comes up, invoke the story agent like this:

```
x "Tell me a story about...[your topic]"
```

## Suggested Next Steps

To get a feel for working with Embabel, try the following:

- Modify the prompts in `WriteAndReviewAgent`.
- Experiment with different models and hyperparameters by modifying `withLlm` calls.
- Integrate your own services, injecting them with Spring. All Embabel `@Agent` classes are Spring beans.
- Run the tests with `mvn test` and modify them to experiment with prompt verification.

To see tool support, check out the more
complex [Embabel Agent API Examples](https://github.com/embabel/embabel-agent-examples) repository.

## Model support

Embabel integrates with any LLM supported by Spring AI.

See [LLM integration guide](docs/llm-docs.md) (work in progress).

Also see [Spring AI models](https://docs.spring.io/spring-ai/reference/api/index.html).

## Testing

This repository includes unit tests demonstrating how to test Embabel agents.

### Running Tests

```bash
mvn test
```

### Unit Tests

Unit tests use Embabel's `FakeOperationContext` and `FakePromptRunner` to test agent actions in isolation without
calling actual LLMs.

See [WriteAndReviewAgentTest.kt](./src/test/kotlin/com/embabel/template/agent/WriteAndReviewAgentTest.kt) for examples
of:

- Creating a fake context with `FakeOperationContext.create()`
- Setting up expected responses with `context.expectResponse()`
- Verifying prompt content contains expected values
- Inspecting LLM invocations via `promptRunner.llmInvocations`

```kotlin
val context = FakeOperationContext.create()
context.expectResponse(Story("Once upon a time..."))

val story = agent.craftStory(userInput, context.ai())

val prompt = context.llmInvocations.first().messages.first().content
assertTrue(prompt.contains("knight"))
```

## Contributors

[![Embabel contributors](https://contrib.rocks/image?repo=embabel/kotlin-agent-template)](https://github.com/embabel/kotlin-agent-template/graphs/contributors)

