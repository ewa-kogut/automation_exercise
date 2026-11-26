---
name: check-readme
description: After something is added, changed or removed in the repository, check that README.md and the other docs (docs/test-cases.md, the AGENTS.md layout, the CLAUDE.md skill list) still match the code. Reports what is missing or out of date; with "fix" it also updates the docs. Use when the user asks "is the README up to date", "check the docs", "did I forget the README", or before a commit that adds classes, tests, settings, tools or skills.
argument-hint: "[fix] to also update the docs; [base branch] to compare against, develop by default"
allowed-tools: Bash(git status:*), Bash(git diff:*), Bash(git log:*), Bash(git ls-files:*), Bash(git merge-base:*), Read, Grep, Glob
---

# Check README

`AGENTS.md` (*Documentation*) says that the docs must follow the code. This skill checks that they do,
for the changes on the current branch and in the working tree.

## Rules

- Without `fix`: change nothing, only report.
- With `fix`: edit only documentation: `README.md`, `docs/test-cases.md`, the *Layout* list in
  `AGENTS.md` and the skill list in `CLAUDE.md`. Never change code, tests or configuration to match
  the docs. If the docs look right and the code looks wrong, report it instead.
- Never commit, push, stage or switch branches.
- Never print secrets. Write placeholders such as `<API token>`, never real values, even when an
  environment variable or a local file holds one.
- Write in the style of the section you edit: the same table columns, the same tone and the same
  level of detail as the rows next to it.

## 1. Collect the changes

1. Base: the argument that is not `fix`, otherwise `develop`.
2. `git status --short`: changed, staged and untracked files.
3. `git diff $(git merge-base <base> HEAD)` and `git diff --cached`: everything that differs from the
   base, committed on this branch or not. Read untracked files in full.
4. Group the changed files: new, renamed, deleted, changed. Renames and deletions matter as much as
   additions, because they leave stale names in the docs.

If nothing differs from the base, say so and stop.

## 2. Checks

Do each check that applies to the changes. Skip the others, and don't review parts of the docs that
the changes don't touch.

| What changed | What must be in the docs |
| --- | --- |
| A file or class added, renamed or deleted | The *Project Structure* tree in `README.md`: an entry in the folder's order (alphabetical, unless the folder follows another order, such as `.claude/skills` following the skill table), with a short `#` comment aligned with its neighbours. A deleted or renamed file has no old entry left. For `src/main` packages, also the *Layout* list in `AGENTS.md` |
| A test added, or its `@Tag` ID, `@DisplayName` or variants changed | The *Current Test Coverage* table in `README.md`, one row per ID, in ID order, and the case in `docs/test-cases.md`: steps, expected results, and every difference from the site's scenario with the reason. The IDs in the code, the table and `docs/test-cases.md` are the same set. Parameterised variant labels in the docs match the code |
| A test or feature that finishes a *Roadmap* item | The item moved to **Done** |
| A new setting: `Config.get`/`Config.getRequired` key, a `*.properties` key, a `-D` option or an environment variable | The *Configuration* section, or the section of the tool it belongs to. Secrets are described as environment variables, never with a value |
| A dependency or plugin in `pom.xml` | *Tech Stack*, when it is a new tool or library the reader should know about. Version bumps only when the README mentions the version |
| `Jenkinsfile`: a stage or parameter | The *Jenkins* section and its parameter table |
| `.github/workflows/` | The *GitHub Actions* section and the tree |
| `docker/` | *Local Tools (Docker)*: the table row, port and start command |
| `.claude/skills/` | The *Claude Code Skills* table in `README.md`, the tree, and the skill list in `CLAUDE.md` |
| `.mcp.json` | The *MCP Servers* section: every server and every environment variable it reads |
| A JUnit extension or `META-INF/services` | The tree comments, and *Framework Design* or the tool's section when the behaviour changes what the reader sees |

Then look for stale references: class, method, file, setting and command names in `backticks` in
`README.md`, `AGENTS.md` and `docs/test-cases.md` that the changes removed or renamed. Search for the
old name with Grep. In example commands, a `-Dtest=` class or `#method` must still exist.

## 3. Report

Finish with a table, most important first:

| Section | File | What is missing or out of date | Suggested text or fix |
| --- | --- | --- | --- |

Then one line with the verdict:
- **Docs up to date**: nothing found.
- **Small gaps**: wording, order, alignment.
- **Docs out of date**: a missing class, test ID, setting or skill, or a reference to something that
  no longer exists.

With `fix`, make the changes after the table, then list each edited file with a one-line summary and
show `git diff --stat` for the docs. Say which findings were not fixed and why.
