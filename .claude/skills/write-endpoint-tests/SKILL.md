---
name: write-endpoint-tests
description: Generate MockMvc endpoint tests for a Spring Boot service's controller by delegating to the endpoint-test-writer subagent. Use when the user asks to write, add or regenerate tests for REST endpoints in any backend service of this repository.
---

# Write endpoint tests

Delegates endpoint test generation to the `endpoint-test-writer` subagent.

## Arguments

`<service> [Entity]` — for example `profile UserProfile`, or just `profile`.

Resolve them before spawning anything:

- **No arguments** — list the backend service directories (those containing a `pom.xml` at their root) and ask which one to target.
- **Service only** — find the controllers under that service's `src/main/java`. One controller: use it. Several: ask which.
- **Both given** — proceed.

Do not guess a target. A wrong guess costs a full agent run.

## What to do

Spawn the `endpoint-test-writer` subagent with the Agent tool. The prompt must carry:

- the target service directory and the controller or entity to cover
- the absolute path to the repository root
- an instruction to follow its own recon → write → run → classify workflow, fixing only failures that stop tests from executing

Run it in the background so the user is not blocked.

## After it reports

Relay to the user:

- **failing tests and why they fail** — lead with this. A red test usually means a production bug, which is worth more than the test itself
- which files were created or changed
- any expectation the agent corrected mid-run, and the source line it cited
- the final build result (test count, failures)

Red tests are an expected outcome here, not a failure of the run. Never suggest relaxing an assertion to get a green build — the user reviews and decides.

Do not paste the test sources into your reply. The user reviews those in their editor; your job is to summarise what happened and surface findings.
