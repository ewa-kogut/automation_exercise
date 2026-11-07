---
name: check-secrets
description: Before a commit or push, check the changes, the files about to be committed and the local test reports for passwords, keys, tokens, real email addresses and personal paths. Reports what it finds and changes nothing. Use when the user asks "is it safe to commit", "check for secrets", "can I push this", or before sharing reports.
argument-hint: "[reports] to also check target/ reports and screenshots"
allowed-tools: Bash(git status:*), Bash(git diff:*), Bash(git ls-files:*), Bash(git check-ignore:*), Grep, Glob
---

# Check secrets

Find anything private before it leaves this computer. This repository is public: whatever is pushed
can be read by anyone, and it stays in the Git history even after it is deleted.

## Rules

- **Never print a value you find.** Report the file, the line and the kind of finding, and mask the
  value, e.g. `j***@gmail.com`, `password=<hidden>`.
- Never open `src/main/resources/config-local.properties` or `.env`, and never print environment
  variables. They hold the real credentials.
- Change nothing: no edits, no `git add`, no `git rm`, no commits. Only report.
- The patterns in this file, and in `AGENTS.md` and `README.md` examples such as `you@example.com`,
  are descriptions, not findings. Don't report them.

## 1. What would be committed

1. `git status --short`: list new, changed and deleted files, including untracked ones.
2. Files that must never be committed. Report any of these that are tracked, staged or untracked and
   not ignored:
   - `config-local.properties`, `.env`
   - anything in `target/`, `allure-results/`, `allure-report/`, `screenshots/`
   - `reportportal.lock`, `reportportal.sync`
   - `*.png`, `*.html` or `*.log` outside `docs/` and `src/`
   Use `git check-ignore -v <file>` to confirm that an ignored file really is ignored.

## 2. Search the changes

Search the output of `git diff` and `git diff --cached`, and the content of untracked files, for:

| Kind | What to look for |
| --- | --- |
| Email addresses | Any address except `@example.com`, `noreply@anthropic.com` and documented placeholders such as `you@example.com` |
| Passwords | `password`, `passwd`, `pwd` or `secret` followed by `=` or `:` and a value. Ignore code that reads a setting, e.g. `Config.getRequired("test.user.password")`, and fixed fake test passwords such as `Test-password-1` |
| Keys and tokens | `ghp_`, `github_pat_`, `AKIA`, `xox`, `BEGIN ... PRIVATE KEY`, long hex or base64 strings, and UUIDs next to words like `key` or `token` (ReportPortal API keys are UUIDs) |
| Personal paths | `C:\Users\<name>` or `C:/Users/<name>` in committed files |

## 3. Reports (only with the `reports` argument)

Search `target/surefire-reports/`, `target/allure-results/` and `target/screenshots/*.html` for the same
patterns, and for `test.user.email=` and `test.user.password=`. Screenshots (`*.png`) can't be
searched: say that they may show the test account's email address, so they must not be shared.

## 4. Report

Finish with a table:

| File | Line | Kind | Masked value | What to do |
| --- | --- | --- | --- | --- |

Then one line with the verdict:
- **Safe to commit**: nothing found.
- **Check first**: findings that are probably harmless, e.g. a personal path. Say why.
- **Do not commit**: credentials, keys, tokens or real email addresses. Say how to remove them. If they
  were already pushed, say that removing them from the latest commit is not enough: the password or
  key has to be changed, because the old value stays in the Git history.
