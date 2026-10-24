#!/usr/bin/env bash
set -euo pipefail

# Execute no terminal do seu computador: a sessão do agente não pode gravar em ~/.codex.
codex mcp add supabase --url 'https://mcp.supabase.com/mcp?project_ref=clcfbwdtiesbjmaropra&features=docs%2Caccount%2Cdatabase%2Cdebugging%2Cdevelopment%2Cfunctions%2Cbranching'
codex mcp login supabase
codex mcp list
