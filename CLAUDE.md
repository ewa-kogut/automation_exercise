@AGENTS.md

## Claude Code skills

Project skills live in `.claude/skills/`:
- `/run-and-heal [filter]` – run tests, investigate failures, fix broken test code, re-run.
- `/new-ui-test <number>` – implement a UI scenario from https://automationexercise.com/test_cases.
- `/new-api-test <number or description>` – implement an API scenario from
  https://automationexercise.com/api_list, or an extra case (API-15 and up).
- `/check-secrets [reports]` – before a commit or push, look for credentials, keys and personal data.
  Reports only, changes nothing.
