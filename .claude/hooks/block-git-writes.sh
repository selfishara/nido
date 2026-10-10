#!/usr/bin/env bash
# PreToolUse guardrail for Nido (see docs/permissions.md).
# Claude Code sends the tool call as JSON on stdin. Exit code 2 blocks the call
# and the message on stderr is shown to the agent.

input="$(cat)"

# 1. Sara runs git herself: block git commands that change history or branches.
if printf '%s' "$input" | grep -Eq '"command"[[:space:]]*:[[:space:]]*"[^"]*git[[:space:]]+(commit|push|merge|rebase|reset[[:space:]]+--hard|checkout[[:space:]]+-b|switch[[:space:]]+-c|branch[[:space:]]+-[dD])'; then
  echo "Blocked by Nido guardrail: Sara runs git herself. Suggest the git command instead of running it (docs/permissions.md)." >&2
  exit 2
fi

# 2. Never touch secret files.
if printf '%s' "$input" | grep -Eq '(^|[/"[:space:]])\.env([."[:space:]]|$)' && ! printf '%s' "$input" | grep -q '\.env\.example'; then
  echo "Blocked by Nido guardrail: .env files hold secrets and must not be read or written by agents (docs/permissions.md)." >&2
  exit 2
fi

exit 0
